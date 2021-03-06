/*
* Copyright 2008-2012 Jeff McWhirter/ramadda.org
*                     Don Murray/CU-CIRES
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this 
* software and associated documentation files (the "Software"), to deal in the Software 
* without restriction, including without limitation the rights to use, copy, modify, 
* merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
* permit persons to whom the Software is furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all copies 
* or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
* PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
* OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
* DEALINGS IN THE SOFTWARE.
*/

package org.ramadda.repository.harvester;


import org.ramadda.repository.*;
import org.ramadda.repository.auth.*;
import org.ramadda.repository.metadata.*;
import org.ramadda.repository.output.*;
import org.ramadda.repository.type.*;
import org.ramadda.util.HtmlUtils;
import org.ramadda.util.Utils;


import org.w3c.dom.*;


import ucar.unidata.sql.SqlUtil;
import ucar.unidata.ui.ImageUtils;
import ucar.unidata.util.DateUtil;

import ucar.unidata.util.IOUtil;
import ucar.unidata.util.LogUtil;
import ucar.unidata.util.Misc;


import ucar.unidata.util.StringUtil;
import ucar.unidata.util.TwoFacedObject;
import ucar.unidata.xml.XmlUtil;

import java.io.File;

import java.lang.reflect.*;



import java.net.*;




import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;



import java.util.regex.*;


/**
 * A harvester that looks at the local server file system
 *
 *
 * @version $Revision: 1.3 $
 */
public class PatternHarvester extends Harvester implements EntryInitializer {

    /** attribute id */
    public static final String ATTR_TYPE = "type";

    /** attribute id */
    public static final String ATTR_DATEFORMAT = "dateformat";

    /** attribute id */
    public static final String ATTR_FILEPATTERN = "filepattern";

    /** attribute id */
    public static final String ATTR_NOTFILEPATTERN = "notfilepattern";

    /** attribute id */
    public static final String ATTR_MOVETOSTORAGE = "movetostorage";


    /** _more_ */
    private static final int FILE_CHANGED_TIME_THRESHOLD_MS = 60 * 1000;

    /** _more_ */
    private String dateFormat = "yyyyMMdd_HHmm";

    /** _more_ */
    private List<SimpleDateFormat> sdf;
    //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");


    /** _more_ */
    private List<String> patternNames = new ArrayList<String>();


    /** _more_ */
    private String filePatternString = ".*";


    /** _more_ */
    private Pattern filePattern;

    /** _more_ */
    private String notfilePatternString = "";


    /** _more_ */
    private Pattern notfilePattern;


    /** _more_ */
    private boolean moveToStorage = false;

    /** _more_ */
    private List<FileInfo> dirs;

    /** _more_ */
    private HashSet<File> dirMap = new HashSet<File>();

    /** _more_ */
    private HashSet seenFiles = new HashSet();

    /** _more_ */
    private int entryCnt = 0;

    /** _more_ */
    private int newEntryCnt = 0;


    /** _more_ */
    private long lastRunTime = 0;

    /**
     * ctor
     *
     * @param repository _more_
     * @param id harvester id
     *
     * @throws Exception _more_
     */
    public PatternHarvester(Repository repository, String id)
            throws Exception {
        super(repository, id);
        if (groupTemplate.length() == 0) {
            groupTemplate = "${dirgroup}";
        }
    }

    /**
     * _more_
     *
     * @param repository _more_
     * @param element _more_
     *
     * @throws Exception _more_
     */
    public PatternHarvester(Repository repository, Element element)
            throws Exception {
        super(repository, element);
        if (groupTemplate.length() == 0) {
            groupTemplate = "${dirgroup}";
        }
        init();
    }


    /**
     * _more_
     *
     * @param entry _more_
     */
    public void initEntry(Entry entry) {}

    /**
     * _more_
     *
     * @param f _more_
     *
     * @return _more_
     */
    private boolean haveProcessedFile(String f) {
        if (seenFiles.contains(f)) {
            return true;
        }

        return false;
    }

    /**
     * _more_
     *
     * @param f _more_
     */
    private void putProcessedFile(String f) {
        //Limit the size to 10000
        if (seenFiles.size() > 10000) {
            seenFiles = new HashSet();
        }
        seenFiles.add(f);
    }

    /**
     * _more_
     *
     * @param element _more_
     *
     * @throws Exception _more_
     */
    protected void init(Element element) throws Exception {
        super.init(element);

        moveToStorage = XmlUtil.getAttribute(element, ATTR_MOVETOSTORAGE,
                                             moveToStorage);
        filePatternString = XmlUtil.getAttribute(element, ATTR_FILEPATTERN,
                filePatternString);


        notfilePatternString = XmlUtil.getAttribute(element,
                ATTR_NOTFILEPATTERN, notfilePatternString);


        filePattern    = null;
        notfilePattern = null;
        sdf            = null;
        init();
        dateFormat = XmlUtil.getAttribute(element, ATTR_DATEFORMAT,
                                          dateFormat);
        sdf = null;

    }


    /**
     * _more_
     *
     * @return _more_
     */
    public String getDescription() {
        return "Server File System";
    }

    /**
     * _more_
     *
     * @param element _more_
     *
     * @throws Exception _more_
     */
    public void applyState(Element element) throws Exception {
        super.applyState(element);
        element.setAttribute(ATTR_FILEPATTERN, filePatternString);
        element.setAttribute(ATTR_NOTFILEPATTERN, notfilePatternString);
        element.setAttribute(ATTR_MOVETOSTORAGE, "" + moveToStorage);
        element.setAttribute(ATTR_DATEFORMAT, dateFormat);
    }


    /**
     * _more_
     *
     * @param request _more_
     *
     * @throws Exception _more_
     */
    public void applyEditForm(Request request) throws Exception {
        super.applyEditForm(request);

        //Reset the seen files
        seenFiles         = new HashSet();

        lastRunTime       = 0;
        filePatternString = request.getUnsafeString(ATTR_FILEPATTERN,
                filePatternString);
        filePattern          = null;

        notfilePatternString = request.getUnsafeString(ATTR_NOTFILEPATTERN,
                notfilePatternString).trim();
        notfilePattern = null;

        sdf            = null;
        init();
        dateFormat = request.getUnsafeString(ATTR_DATEFORMAT, dateFormat);
        if (request.exists(ATTR_MOVETOSTORAGE)) {
            moveToStorage = request.get(ATTR_MOVETOSTORAGE, moveToStorage);
        } else {
            moveToStorage = false;
        }
    }

    /**
     * _more_
     *
     * @param request _more_
     * @param sb _more_
     *
     * @throws Exception _more_
     */
    public void createEditForm(Request request, StringBuffer sb)
            throws Exception {

        super.createEditForm(request, sb);
        String     extraLabel     = "";
        String     fileFieldExtra = "";

        List<File> rootDirs       = getRootDirs();
        for (File rootDir : rootDirs) {
            if ( !rootDir.exists()) {
                extraLabel = HtmlUtils.br()
                             + HtmlUtils.span(
                                 msg("Directory does not exist"),
                                 HtmlUtils.cssClass(
                                     CSS_CLASS_REQUIRED_LABEL));
                fileFieldExtra = HtmlUtils.cssClass(CSS_CLASS_REQUIRED);

                break;
            } else if ( !getStorageManager().isLocalFileOk(rootDir)) {
                String adminLink =
                    HtmlUtils.href(
                        getRepository().getUrlBase()
                        + "/userguide/admin.html#filesystemaccess", msg(
                            "More information"), " target=_HELP");
                extraLabel =
                    HtmlUtils.br()
                    + HtmlUtils
                        .span(msg(
                            "You need to add this directory to the file system access list"), HtmlUtils
                                .cssClass(CSS_CLASS_REQUIRED_LABEL)) + HtmlUtils.space(2) + adminLink;
                fileFieldExtra = HtmlUtils.cssClass(CSS_CLASS_REQUIRED);

                break;
            }
        }

        if (rootDirs.size() == 0) {
            extraLabel = HtmlUtils.br()
                         + HtmlUtils.span(
                             msg("Required"),
                             HtmlUtils.cssClass(CSS_CLASS_REQUIRED_LABEL));
            fileFieldExtra = HtmlUtils.cssClass(CSS_CLASS_REQUIRED);

        }

        sb.append(HtmlUtils.colspan(msgHeader("Look for files"), 2));

        StringBuffer inputText = new StringBuffer();
        for (File rootDir : rootDirs) {
            String path = rootDir.toString();
            path = path.replace("\\", "/");
            inputText.append(path);
            inputText.append("\n");
        }

        sb.append(
            HtmlUtils.formEntry(
                msgLabel("Under directories"),
                HtmlUtils.textArea(
                    ATTR_ROOTDIR, inputText.toString(), 5, 60,
                    fileFieldExtra.toString()) + extraLabel));


        sb.append(HtmlUtils.formEntry(msgLabel("That match pattern"),
                                      HtmlUtils.input(ATTR_FILEPATTERN,
                                          filePatternString,
                                          HtmlUtils.SIZE_60)));

        sb.append(
            HtmlUtils.formEntry(
                msgLabel("Exclude files that match pattern"),
                HtmlUtils.input(
                    ATTR_NOTFILEPATTERN, notfilePatternString,
                    HtmlUtils.SIZE_60)));

        sb.append(
            HtmlUtils.colspan(
                msgHeader("Then create an entry with") + HtmlUtils.space(2)
                + HtmlUtils.href(
                    getRepository().getUrlBase() + "/help/harvesters.html",
                    "(" + msg("Help") + ")", " target=_HELP"), 2));


        //        sb.append(
        //HtmlUtils.formEntry("",
        //msgLabel("Then create an entry with")));

        addBaseGroupSelect(ATTR_BASEGROUP, sb);

        sb.append(HtmlUtils.formEntry(msgLabel("Folder template"),
                                      HtmlUtils.input(ATTR_GROUPTEMPLATE,
                                          groupTemplate, HtmlUtils.SIZE_60)));

        sb.append(HtmlUtils.formEntry(msgLabel("Name template"),
                                      HtmlUtils.input(ATTR_NAMETEMPLATE,
                                          nameTemplate, HtmlUtils.SIZE_60)));
        sb.append(HtmlUtils.formEntry(msgLabel("Description template"),
                                      HtmlUtils.input(ATTR_DESCTEMPLATE,
                                          descTemplate, HtmlUtils.SIZE_60)));

        sb.append(HtmlUtils.formEntry(msgLabel("Entry type"),
                                      makeEntryTypeSelector(request,
                                          getTypeHandler())));



        sb.append(HtmlUtils.formEntry(msgLabel("Date format"),
                                      HtmlUtils.input(ATTR_DATEFORMAT,
                                          dateFormat, HtmlUtils.SIZE_60)));


        String moveNote =
            msg(
            "Note: This will move the files from their current location to RAMADDA's storage directory");
        sb.append(HtmlUtils.formEntry(msgLabel("Move file to storage"),
                                      HtmlUtils.checkbox(ATTR_MOVETOSTORAGE,
                                          "true",
                                          moveToStorage) + HtmlUtils.space(1)
                                              + moveNote));


        sb.append(
            HtmlUtils
                .formEntry(
                    msgLabel("Metadata"),
                    HtmlUtils
                        .checkbox(
                            ATTR_ADDMETADATA, "true",
                            getAddMetadata()) + HtmlUtils.space(1)
                                + msg("Add full metadata")
                                + HtmlUtils.space(4)
                                + HtmlUtils
                                    .checkbox(
                                        ATTR_ADDSHORTMETADATA, "true",
                                        getAddShortMetadata()) + HtmlUtils
                                            .space(1) + msg(
                                                "Just add spatial/temporal metadata")));

        sb.append(HtmlUtils.formEntry(msgLabel("User"),
                                      HtmlUtils.input(ATTR_USER,
                                          (getUserName() != null)
                                          ? getUserName().trim()
                                          : "", HtmlUtils.SIZE_30)));


    }

    /**
     * _more_
     *
     * @param request _more_
     * @param typeHandler _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public String makeEntryTypeSelector(Request request,
                                        TypeHandler typeHandler)
            throws Exception {
        List items = new ArrayList();
        items.add(new TwoFacedObject(msg("Find match"), TYPE_FINDMATCH));

        return repository.makeTypeSelect(items, request, false,
                                         getTypeHandler().getType(), false,
                                         null);
    }


    /**
     * _more_
     *
     * @return _more_
     */
    private List<SimpleDateFormat> getSDF() {
        if (sdf == null) {
            sdf = new ArrayList<SimpleDateFormat>();
            if ((dateFormat != null) && (dateFormat.length() > 0)) {
                for (String tok :
                        (List<String>) StringUtil.split(dateFormat, ",",
                            true, true)) {
                    sdf.add(new SimpleDateFormat(tok));
                }
            } else {
                sdf.add(new SimpleDateFormat("yyyyMMdd_HHmm"));
            }
            for (SimpleDateFormat format : sdf) {
                format.setTimeZone(RepositoryUtil.TIMEZONE_DEFAULT);
            }
        }

        return sdf;

    }


    /**
     * _more_
     */
    private void init() {

        if ((notfilePattern == null) && (notfilePatternString.length() > 0)) {
            notfilePattern = Pattern.compile(notfilePatternString);
        }
        boolean gotAttributeInPattern = false;


        if ((filePattern == null) && (filePatternString != null)
                && (filePatternString.length() > 0)) {
            String       tmp     = filePatternString;
            StringBuffer pattern = new StringBuffer();
            patternNames = new ArrayList<String>();
            while (true) {
                int openParenIdx = tmp.indexOf("(");
                if (openParenIdx < 0) {
                    pattern.append(tmp);
                    break;
                }
                int closeParenIdx = tmp.indexOf(")");
                if(closeParenIdx< openParenIdx ) {
                    pattern.append(tmp);
                    break;
                }
                int colonIdx = tmp.indexOf(":");
                if (colonIdx < 0) {
                    pattern.append(tmp);
                    break;
                }
                if(closeParenIdx< colonIdx) {
                    pattern.append(tmp.substring(0, closeParenIdx+1));
                    patternNames.add("");
                    tmp = tmp.substring(closeParenIdx+1);
                    continue;
                }
                pattern.append(tmp.substring(0, openParenIdx + 1));
                String name = tmp.substring(openParenIdx + 1, colonIdx);
                patternNames.add(name);
                gotAttributeInPattern = true;
                tmp = tmp.substring(colonIdx + 1);
            }
            //            System.err.println ("pattern:" + pattern);
            //            System.err.println ("pattern names:" + patternNames);
            if(!gotAttributeInPattern) {
                pattern = new StringBuffer(filePatternString);
                patternNames = new ArrayList<String>();
            }

            filePattern = Pattern.compile(pattern.toString());
            if (getTestMode()) {
                getRepository().getLogManager().logInfo("orig pattern:"
                        + "  " + filePatternString);
                getRepository().getLogManager().logInfo("pattern:" + "  "
                        + pattern);
                getRepository().getLogManager().logInfo("pattern names:"
                        + patternNames);
            }
        }
    }





    /**
     * _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public String getExtraInfo() throws Exception {
        String dirMsg = "";
        if (dirs != null) {
            if (dirs.size() == 0) {
                dirMsg = "No directories found<br>";
            } else {
                dirMsg = "Scanning:" + dirs.size() + " directories";
                String dirBlock = HtmlUtils.insetDiv(StringUtil.join("<br>",
                                      dirs), 0, 10, 0, 0);
                dirMsg = HtmlUtils.makeShowHideBlock(dirMsg, dirBlock, false);
            }
        }

        String entryMsg = "";
        if (entryCnt > 0) {
            entryMsg = "Found " + entryCnt + " file" + ((entryCnt == 1)
                    ? ""
                    : "s") + "<br>" + "Found " + newEntryCnt + " new file"
                           + ((newEntryCnt == 1)
                              ? ""
                              : "s") + "<br>";

        }
        List<File> rootDirs = getRootDirs();

        return "Directory:" + StringUtil.join(" ", rootDirs) + "<br>"
               + dirMsg + entryMsg + status + "<br>" + currentStatus;
    }

    /**
     * _more_
     *
     * @param dir _more_
     */
    private void removeDir(FileInfo dir) {
        dirs.remove(dir);
        dirMap.remove(dir.getFile());
    }

    /**
     * _more_
     *
     * @param dir _more_
     * @param rootDir _more_
     *
     * @return _more_
     */
    private FileInfo addDir(File dir, File rootDir) {
        FileInfo fileInfo = new FileInfo(dir, rootDir, true);
        dirs.add(fileInfo);
        dirMap.add(dir);

        return fileInfo;
    }

    /**
     * _more_
     *
     * @param dir _more_
     *
     * @return _more_
     */
    private boolean hasDir(File dir) {
        return dirMap.contains(dir);
    }




    /**
     * _more_
     *
     *
     * @param timestamp _more_
     * @throws Exception _more_
     */
    protected void runInner(int timestamp) throws Exception {
        logHarvesterInfo("******************* Starting ****************");
        if ( !canContinueRunning(timestamp)) {
            logHarvesterInfo("stopping in runInner");

            return;
        }

        entryCnt    = 0;
        newEntryCnt = 0;
        status = new StringBuffer("Looking for initial directory listing");
        long tt1 = System.currentTimeMillis();
        dirs = new ArrayList<FileInfo>();

        List<File> rootDirs = getRootDirs();
        for (File rootDir : rootDirs) {
            logHarvesterInfo("Looking for initial directory listing:"
                             + rootDir);
            if ( !rootDir.exists()) {
                logHarvesterInfo("Root directory does not exist:" + rootDir);
            }
            dirs.add(new FileInfo(rootDir));
            dirs.addAll(FileInfo.collectDirs(rootDir, this));
        }

        logHarvesterInfo("Found " + dirs.size()
                         + " directories under top-level dir");

        long tt2 = System.currentTimeMillis();
        status = new StringBuffer("");
        //        System.err.println("took:" + (tt2 - tt1) + " to find initial dirs:"
        //                           + dirs.size());

        for (FileInfo dir : dirs) {
            dirMap.add(dir.getFile());
        }

        int cnt = 0;

        while (canContinueRunning(timestamp)) {
            long t1 = System.currentTimeMillis();
            logHarvesterInfo("Start scanning");
            printTab = "\t";
            harvestEntries((cnt == 0), timestamp);
            printTab = "";
            logHarvesterInfo("Done scanning");
            lastRunTime = System.currentTimeMillis();
            long t2 = System.currentTimeMillis();
            cnt++;
            //            System.err.println("found:" + entries.size() + " files in:"
            //                               + (t2 - t1) + "ms");
            if ( !getMonitor()) {
                status.append("Done<br>");
                logHarvesterInfo("Ran one time only. Exiting loop");

                break;
            }

            status.append("Done... sleeping for " + getSleepMinutes()
                          + " minutes<br>");
            logHarvesterInfo("Sleeping for " + getSleepMinutes()
                             + " minutes");
            doPause();
            status = new StringBuffer();
        }
        logHarvesterInfo("***********  Done running **************");
    }


    /**
     * _more_
     *
     * @param firstTime _more_
     * @param timestamp _more_
     *
     *
     * @throws Exception _more_
     */
    private void harvestEntries(boolean firstTime, int timestamp)
            throws Exception {

        long           t1        = System.currentTimeMillis();
        List<Entry>    entries   = new ArrayList<Entry>();
        List<Entry>    needToAdd = new ArrayList<Entry>();
        List<FileInfo> tmpDirs   = new ArrayList<FileInfo>(dirs);
        entryCnt    = 0;
        newEntryCnt = 0;

        boolean checkIfDirHasChanged = true;

        //For now lets always look at each dir even if it hasn't changed
        //It seems as though sometimes files come in or have been changed
        //and we skip them then we miss them the next time through
        checkIfDirHasChanged = true;

        //Iterate by size because we can add new dirs to the list
        for (int fileIdx = 0; fileIdx < tmpDirs.size(); fileIdx++) {
            printTab = "\t";
            FileInfo dirInfo = tmpDirs.get(fileIdx);
            if ( !dirInfo.exists()) {
                logHarvesterInfo("Directory:" + dirInfo.getFile()
                                 + " * does not exist *");
                removeDir(dirInfo);

                continue;
            }
            boolean directoryChanged = dirInfo.hasChanged();
            if (checkIfDirHasChanged && !firstTime && !directoryChanged) {
                /*
                logHarvesterInfo("Directory:" + dirInfo.getFile()
                                 + "  * no change *");
                */
                continue;
            }



            File[] files = dirInfo.getFile().listFiles();
            dirInfo.clearAddedFiles();
            if ((files == null) || (files.length == 0)) {
                logHarvesterInfo("Directory:" + dirInfo.getFile()
                                 + " * no files *");

                continue;
            }

            logHarvesterInfo("Directory:" + dirInfo.getFile()
                             + "  found " + files.length +" files");

            for (File f : files) {
                logHarvesterInfo("File:" + f);
            }


            printTab = "\t\t";
            files    = IOUtil.sortFilesOnName(files);

            for (File f : files) {
                if (f.isDirectory()) {
                    //If this is a directory then check if we already have it 
                    //in the list. If not then add it to the main list and the local list
                    if ( !hasDir(f)) {
                        if(FileInfo.okToRecurse(f, this)) {
                            logHarvesterInfo("New directory:" + f);
                            tmpDirs.add(addDir(f, dirInfo.getRootDir()));
                        }
                    }
                    continue;
                }
                long fileTime = f.lastModified();
                //time diff threshold = 1 minute
                long now = System.currentTimeMillis();
                if ((now - fileTime) < FILE_CHANGED_TIME_THRESHOLD_MS) {
                    logHarvesterInfo("Skipping recently modified file:" + f
                                     + " milliseconds since modified:"
                                     + (now - fileTime));
                    System.err.println("file:" + f + " last modified:"
                                       + new Date(fileTime) + " " + fileTime
                                       + " now:" + new Date(now) + " " + now);

                    //Reset the state that gets set and checked in hasChanged so we can return to this dir
                    dirInfo.reset();

                    continue;
                }
                Entry entry = null;
                try {
                    entry = processFile(dirInfo, f);
                } catch (Exception exc) {
                    logHarvesterError("Error creating entry:" + f, exc);
                }
                if (entry == null) {
                    logHarvesterInfo("No entry created");
                    continue;
                }
                entries.add(entry);
                entryCnt++;
                if (getTestMode() && (entryCnt >= getTestCount())) {
                    return;
                }
                if ( !getTestMode()) {
                    //Check every time
                    //                    if (entries.size() > 1000) {
                    if (entries.size() > 0) {
                        List<Entry> nonUniqueOnes = new ArrayList<Entry>();
                        List<Entry> uniqueEntries =
                            getEntryManager().getUniqueEntries(entries,
                                nonUniqueOnes);
                        //                        System.err.println("non unique:" + nonUniqueOnes);
                        //                        System.err.println("unique:" + uniqueEntries);
                        for (Entry e : nonUniqueOnes) {
                            logHarvesterInfo("Entry already exists:"
                                             + e.getResource());
                        }
                        newEntryCnt += uniqueEntries.size();
                        needToAdd.addAll(uniqueEntries);
                        for (Entry newEntry : uniqueEntries) {
                            logHarvesterInfo("New entry:"
                                             + newEntry.getResource());
                            dirInfo.addFile(newEntry.getResource().getPath());
                        }
                        entries = new ArrayList();
                    }
                    if (needToAdd.size() > 1000) {
                        addEntries(needToAdd, timestamp);
                        needToAdd = new ArrayList<Entry>();
                    }
                }
                if ( !canContinueRunning(timestamp)) {
                    logHarvesterInfo("stopping harvest");

                    return;
                }
            }
        }

        if ( !canContinueRunning(timestamp)) {
            return;
        }

        //Uggh, cut-and-paste from above
        if ( !getTestMode()) {
            if (entries.size() > 0) {
                List<Entry> nonUniqueOnes = new ArrayList<Entry>();
                List<Entry> uniqueEntries =
                    getEntryManager().getUniqueEntries(entries,
                        nonUniqueOnes);
                for (Entry e : nonUniqueOnes) {
                    logHarvesterInfo("Entry already exists:"
                                     + e.getResource());
                }
                for (Entry newEntry : uniqueEntries) {
                    logHarvesterInfo("New entry:" + newEntry.getResource());
                }
                newEntryCnt += uniqueEntries.size();
                needToAdd.addAll(uniqueEntries);
            }
            addEntries(needToAdd, timestamp);
        }


    }


    /**
     * _more_
     *
     * @param entries _more_
     * @param timestamp _more_
     *
     * @throws Exception _more_
     */
    private void addEntries(List<Entry> entries, int timestamp)
            throws Exception {
        if (getTestMode() || (entries.size() == 0)) {
            return;
        }
        List<Entry> entriesToAdd = new ArrayList<Entry>();
        status.append("Initializing " + entries.size() + " new "
                      + ((entries.size() > 1)
                         ? "entries"
                         : "entry") + "<br>");
        int cnt = 0;
        for (Entry newEntry : entries) {
            if ( !canContinueRunning(timestamp)) {
                return;
            }
            newEntry.getTypeHandler().initializeNewEntry(newEntry);
            entriesToAdd.add(newEntry);
            cnt++;
            currentStatus = "Initialized " + cnt + " of " + entries.size()
                            + " entries";
        }
        currentStatus = "Done initializing entries";
        if (getAddMetadata() || getAddShortMetadata()) {
            currentStatus = "Adding metadata";
            int count = 0;
            for (Entry entry : entriesToAdd) {
                List<Entry> tmpList = new ArrayList<Entry>();
                tmpList.add(entry);
                count++;
                getEntryManager().addInitialMetadata(null, tmpList, true,
                        getAddShortMetadata());
                currentStatus = "Added metadata for " + count + " entries. "
                                + (entriesToAdd.size() - count)
                                + " more entries to process";
            }
        }
        currentStatus = "";
        logHarvesterInfo("Inserting " + entriesToAdd.size() + " new entries");
        status.append("Inserting entries<br>");
        getEntryManager().addNewEntries(getRequest(), entriesToAdd);
    }

    /**
     * _more_
     *
     * @param parentFile _more_
     * @param parentGroup _more_
     * @param dirToks _more_
     * @param makeGroup _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    private String getDirNames(File parentFile, Entry parentGroup,
                               List<String> dirToks, boolean makeGroup)
            throws Exception {
        //        if(dirToks.size()==0) return parentFile.toString();
        List names = new ArrayList();
        for (int i = 0; i < dirToks.size(); i++) {
            String filename = (String) dirToks.get(i);
            File   file     = new File(parentFile + "/" + filename);
            if(!file.exists()) {
                file     = new File(parentFile + "/" + filename.replaceAll(" ","_"));
            }
            Entry  template = getEntryManager().getTemplateEntry(file);
            String name     = ((template != null)
                               ? template.getName()
                               : filename);
            if(!Utils.stringDefined(name)) {
                name = filename;
            }
            if (makeGroup && (parentGroup != null)) {
                Entry group =
                    getEntryManager().findGroupFromName(getRequest(),
                        parentGroup.getFullName() + Entry.PATHDELIMITER
                        + name, getUser(), false);
                if (group == null) {
                    group = getEntryManager().makeNewGroup(parentGroup, name,
                            getUser(), template);
                    
                }
                parentGroup = group;
            }
            names.add(name);
            parentFile = file;
        }

        return StringUtil.join(Entry.PATHDELIMITER, names);
    }


    /**
     * _more_
     *
     * @param value _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    private Date parseDate(String value) throws Exception {
        Exception lastException = null;
        for (SimpleDateFormat sdf : getSDF()) {
            try {
                return sdf.parse(value);
            } catch (Exception exc) {
                lastException = exc;
            }
        }
        if (lastException != null) {
            throw lastException;
        }

        return null;
    }



    /**
     * _more_
     *
     * @param args _more_
     */
    public static void main(String[] args) {
        StringUtil.replaceDate("hello ${fromdate:yyyy-mm-dd}", "fromdate",
                               new Date());
    }

    /**
     * _more_
     *
     *
     * @param fileInfo _more_
     * @param f _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Entry processFile(FileInfo fileInfo, File f) throws Exception {

        //check if its a hidden file
        boolean isPlaceholder = f.getName().equals(".placeholder");

        if (f.getName().startsWith(".") && !isPlaceholder) {
            logHarvesterInfo("File is hidden file:" + f);
            return null;
        }

        if(f.getName().equals("ramadda.properties")) {
            return null;
        }

        String fileName = f.toString();
        fileName = fileName.replace("\\", "/");

        //Call init so we get the filePattern
        init();


        Matcher matcher = null;
        if (filePattern != null) {
            matcher = filePattern.matcher(fileName);
            if ( !matcher.find()) {
                debug("file:<i>" + fileName + "</i> does not match pattern");
                logHarvesterInfo("file:" + fileName
                                 + " does not match pattern");

                return null;
            }
        }
        if (notfilePattern != null) {
            Matcher matcher2 = notfilePattern.matcher(fileName);
            if (matcher2.find()) {
                logHarvesterInfo(
                    "excluding file because it matches the NOT pattern:"
                    + fileName);

                return null;
            }
        }


        debug("file:<i>" + fileName + "</i> matches pattern");

        return harvestFile(fileInfo, f, matcher);
    }


    /**
     * _more_
     *
     * @param fileInfo _more_
     * @param f _more_
     * @param matcher _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Entry harvestFile(FileInfo fileInfo, File f, Matcher matcher)
            throws Exception {

        boolean isPlaceholder = f.getName().equals(".placeholder");
        String  fileName      = f.toString();
        fileName = fileName.replace("\\", "/");

        if ( !getTestMode()) {

            /**
             * * TRY THIS
             * if (haveProcessedFile(fileName)) {
             *   logHarvesterInfo("Already processed file:" + fileName);
             *   debug("Already harvested file:" + fileName);
             *   return null;
             * }
             * putProcessedFile(fileName);
             */
        }

        TypeHandler typeHandler      = getTypeHandler();
        TypeHandler typeHandlerToUse = null;

        Entry       templateEntry    = getEntryManager().getTemplateEntry(f);
        if (templateEntry != null) {
            typeHandlerToUse = templateEntry.getTypeHandler();
        }


        if ((typeHandlerToUse == null)
                && typeHandler.getType().equals(TYPE_FINDMATCH)) {
            String path      = f.toString();
            String shortName = f.getName();
            for (TypeHandler otherTypeHandler :
                    getRepository().getTypeHandlers()) {
                if (otherTypeHandler.canHandleResource(path, shortName)) {
                    typeHandlerToUse = otherTypeHandler;

                    break;
                }
            }
        }

        if (typeHandlerToUse == null) {
            if ( !typeHandler.getType().equals(TYPE_FINDMATCH)) {
                typeHandlerToUse = typeHandler;
            } else {
                //Default to the generic file type
                typeHandlerToUse =
                    getRepository().getTypeHandler(TypeHandler.TYPE_FILE);
            }
        }


        String dirPath = f.getParent().toString();
        dirPath =
            dirPath.substring(fileInfo.getRootDir().toString().length());
        dirPath = dirPath.replace("\\", "/");
        //New
        dirPath = dirPath.replaceAll("_", " ");
        List dirToks = (List<String>) StringUtil.split(dirPath, "/", true,
                           true);
        //        System.err.println ("file:" +fileName + " " + dirPath +" " + dirToks);
        Entry  baseGroup = getBaseGroup();

        String dirGroup  =
            getDirNames(fileInfo.getRootDir(), baseGroup, dirToks,
                        !getTestMode()
                        && (groupTemplate.indexOf("${dirgroup}") >= 0));


        dirGroup = SqlUtil.cleanUp(dirGroup);
        dirGroup = dirGroup.replace("\\", "/");


        Hashtable map       = new Hashtable();
        Date      fromDate  = null;
        Date      toDate    = null;
        String    tag       = tagTemplate;
        String    groupName = groupTemplate;
        String    name      = nameTemplate;
        String    desc      = descTemplate;

        //        System.err.println("pattern names:" + patternNames);
        for (int dataIdx = 0; dataIdx < patternNames.size(); dataIdx++) {
            String dataName = patternNames.get(dataIdx);
            if(!Utils.stringDefined(dataName)) {
                continue;
            }
            Object value    = matcher.group(dataIdx + 1);
            if (dataName.equals("fromdate")) {
                value = fromDate = parseDate((String) value);
            } else if (dataName.equals("todate")) {
                value = toDate = parseDate((String) value);
            } else {
                value     = typeHandlerToUse.convert(dataName, (String) value);
                groupName = groupName.replace("${" + dataName + "}",
                        value.toString());
                name = name.replace("${" + dataName + "}", value.toString());
                desc = desc.replace("${" + dataName + "}", value.toString());
                map.put(dataName, value);
            }
        }


        //        System.err.println("values:");
        //        System.err.println("map:" + map);
        Object[] values = typeHandlerToUse.makeValues(map);
        //        Date     createDate = new Date();

        String ext = IOUtil.getFileExtension(fileName);
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        tag = tag.replace("${extension}", ext);
        String filename = f.getName();


        Date createDate = new Date(f.lastModified());
        if (fromDate == null) {
            fromDate = toDate;
        }
        if (toDate == null) {
            toDate = fromDate;
        }

        if(fromDate == null) {
            fromDate =  Utils.extractDate(applyMacros(name, createDate, fromDate, toDate, filename));
        }
        if (toDate == null) {
            toDate = fromDate;
        }

        if (fromDate == null) {
            fromDate = createDate;
        }
        if (toDate == null) {
            toDate = createDate;
        }

        groupName = groupName.replace("${dirgroup}", dirGroup);



        groupName = applyMacros(groupName, createDate, fromDate, toDate,
                                filename);
        name = applyMacros(name, createDate, fromDate, toDate, filename);
        desc = applyMacros(desc, createDate, fromDate, toDate, filename);
        desc = desc.replace("${name}", name);

        if (baseGroup != null) {
            groupName = baseGroup.getFullName() + Entry.PATHDELIMITER
                        + groupName;
        }
        if (getTestMode()) {
            debug("\tname: " + name + "\n\tgroup:" + groupName
                  + "\n\tfromdate:" + getPageHandler().formatDate(fromDate));
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    debug("\tvalue: " + values[i]);
                }
            }

            return null;
        }

        boolean createIfNeeded = !getTestMode();
        Entry   group = getEntryManager().findEntryFromName(getRequest(),
                          groupName, getUser(), createIfNeeded,
                          getLastGroupType(), this);

        if(group == null) {
            logHarvesterInfo ("Could not create group:" + groupName);
            return null;
        } 


        //If its just a placeholder then all we need to do is create the group and return
        if (isPlaceholder) {
            return null;
        }
        String tmpName = f.getName().toLowerCase();
        if (tmpName.equals("readme") || tmpName.equals("readme.txt")) {
            if (group.getDescription().length() > 0) {
                return null;
            }
            group.setDescription(IOUtil.readContents(f.toString(), ""));
            getEntryManager().updateEntry(getRequest(), group);

            return null;
        }

        if (tmpName.equals("thumbs.sb")) {
            return null;
        }
        if (tmpName.endsWith(".thm")) {
            return null;
        }


        Entry entry = typeHandlerToUse.createEntry(getRepository().getGUID());
        Resource resource;
        if (moveToStorage) {
            File fromFile = new File(fileName);
            File newFile  = getStorageManager().moveToStorage(
                               null, fromFile,
                               getStorageManager().getStorageFileName(
                                   fromFile.getName()));
            resource = new Resource(newFile.toString(),
                                    Resource.TYPE_STOREDFILE);
        } else {
            resource = new Resource(fileName, Resource.TYPE_FILE);
        }



        if(getGenerateMd5()) {
            resource.setMd5(IOUtil.getMd5(resource.getPath()));
        }
        entry.initEntry(name, desc, group, getUser(), resource, "",
                        createDate.getTime(), createDate.getTime(),
                        fromDate.getTime(), toDate.getTime(), values);

        Date date = Utils.extractDate(name);
        if(date!=null &&!entry.hasDate()) {
            entry.setStartDate(date.getTime());
            entry.setEndDate(date.getTime());
        }

        //If it is an image then we create a thumbnail for it in the JpegMetadataHandler
        //else we check if there is a .thm file
        if ( !ImageUtils.isImage(resource.getPath())) {
            File thumbnail = null;
            File tmp;
            tmp = new File(IOUtil.stripExtension(resource.getPath())
                           + ".thm");
            if (tmp.exists()) {
                thumbnail = tmp;
            }
            if (thumbnail == null) {
                tmp = new File(IOUtil.stripExtension(resource.getPath())
                               + ".THM");
                if (tmp.exists()) {
                    thumbnail = tmp;
                }
            }

            if (thumbnail != null) {
                String jpegFile = IOUtil.stripExtension(thumbnail.getName())
                                  + ".jpg";
                String newThumbFile =
                    getStorageManager().copyToEntryDir(entry, thumbnail,
                        jpegFile).getName();
                entry.addMetadata(new Metadata(getRepository().getGUID(),
                        entry.getId(), ContentMetadataHandler.TYPE_THUMBNAIL,
                        false, newThumbFile, null, null, null, null));

            }
        }

        /*
        if (tag.length() > 0) {
            List tags = StringUtil.split(tag, ",", true, true);
            for (int i = 0; i < tags.size(); i++) {
                entry.addMetadata(new Metadata(getRepository().getGUID(),
                        entry.getId(), EnumeratedMetadataHandler.TYPE_TAG,
                        DFLT_INHERITED, (String) tags.get(i), "", "", ""));
            }
            }*/
        //        logHarvesterInfo("Created entry:" + f);
        return initializeNewEntry(fileInfo, f, entry);
        //        return entry;

    }

    /**
     * _more_
     *
     * @param fileInfo _more_
     * @param originalFile _more_
     * @param entry _more_
     *
     * @return _more_
     */
    public Entry initializeNewEntry(FileInfo fileInfo, File originalFile,
                                    Entry entry) {
        return entry;
    }

    /**
     * _more_
     */
    public void clearCache() {
        lastRunTime = 0;
        super.clearCache();
    }


    /**
     * _more_
     *
     * @param type _more_
     * @param filepath _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Entry processFile(TypeHandler type, String filepath)
            throws Exception {
        if ( !this.getTypeHandler().equals(type)) {
            return null;
        }
        File f = new File(filepath);

        return processFile(new FileInfo(f.getParentFile()), f);
    }


    /**
     * _more_
     *
     * @return _more_
     */
    public String getLastGroupType() {
        return null;
    }

}
