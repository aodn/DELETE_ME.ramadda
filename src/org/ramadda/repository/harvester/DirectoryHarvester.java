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
import org.ramadda.repository.type.*;
import org.ramadda.util.HtmlUtils;


import org.w3c.dom.*;

import ucar.unidata.sql.SqlUtil;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;



import java.util.regex.*;


/**
 *
 *
 *
 * @author RAMADDA Development Team
 * @version $Revision: 1.3 $
 */
public class DirectoryHarvester extends Harvester {


    /**
     * _more_
     *
     * @param repository _more_
     */
    public DirectoryHarvester(Repository repository) {
        super(repository);
    }

    /**
     * _more_
     *
     * @param repository _more_
     * @param id _more_
     *
     * @throws Exception _more_
     */
    public DirectoryHarvester(Repository repository, String id)
            throws Exception {
        super(repository, id);
    }


    /**
     * _more_
     *
     * @param repository _more_
     * @param element _more_
     *
     * @throws Exception _more_
     */
    public DirectoryHarvester(Repository repository, Element element)
            throws Exception {
        super(repository, element);
    }


    /**
     * _more_
     *
     * @return _more_
     */
    public String getDescription() {
        return "Make folders from directory tree";
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
        sb.append(HtmlUtils.formEntry(msgLabel("Harvester name"),
                                      HtmlUtils.input(ARG_NAME, getName(),
                                          HtmlUtils.SIZE_40)));
        sb.append(HtmlUtils
            .formEntry(msgLabel("Run"), HtmlUtils
                .checkbox(ATTR_ACTIVEONSTART, "true", getActiveOnStart()) + HtmlUtils
                .space(1) + msg("Active on startup") + HtmlUtils.space(3)
                    + HtmlUtils.checkbox(ATTR_MONITOR, "true", getMonitor())
                        + HtmlUtils.space(1) + msg("Monitor")
                            + HtmlUtils.space(3) + msgLabel("Sleep")
                                + HtmlUtils.space(1)
                                    + HtmlUtils
                                        .input(ATTR_SLEEP, ""
                                            + getSleepMinutes(), HtmlUtils
                                                .SIZE_5) + HtmlUtils.space(1)
                                                    + "(" + msg("minutes")
                                                        + ")"));



        List<File>   rootDirs   = getRootDirs();

        String       extraLabel = "";
        StringBuffer inputText  = new StringBuffer();
        for (File rootDir : rootDirs) {
            String path = rootDir.toString();
            path = path.replace("\\", "/");
            inputText.append(path);
            inputText.append("\n");
            if ( !rootDir.exists()) {
                extraLabel = HtmlUtils.space(2)
                             + HtmlUtils.bold("Directory does not exist");
            }
        }


        sb.append(
            RepositoryManager.tableSubHeader("Walk the directory tree"));
        sb.append(HtmlUtils.formEntry(msgLabel("Under directory"),
                                      HtmlUtils.input(ATTR_ROOTDIR,
                                          inputText,
                                          HtmlUtils.SIZE_60) + extraLabel));
        sb.append(
            RepositoryManager.tableSubHeader("Create new folders under"));

        addBaseGroupSelect(ATTR_BASEGROUP, sb);
    }


    /**
     * _more_
     *
     *
     * @param timestamp _more_
     * @throws Exception _more_
     */
    protected void runInner(int timestamp) throws Exception {
        if ( !canContinueRunning(timestamp)) {
            return;
        }
        Entry baseGroup = getBaseGroup();
        if (baseGroup == null) {
            baseGroup = getEntryManager().getTopGroup();
        }
        for (File rootDir : getRootDirs()) {
            walkTree(rootDir, baseGroup);
        }
    }


    /**
     * _more_
     *
     * @param dir _more_
     * @param parentGroup _more_
     *
     * @throws Exception _more_
     */
    protected void walkTree(File dir, Entry parentGroup) throws Exception {
        String name    = dir.getName();
        File   xmlFile = new File(IOUtil.joinDir(dir.getParentFile(),
                           "." + name + ".ramadda"));
        Entry fileInfoEntry = getEntryManager().getTemplateEntry(dir);
        Entry group         = getEntryManager().findGroupFromName(getRequest(),
                          parentGroup.getFullName() + "/" + name, getUser(),
                          false);
        if (group == null) {
            group = getEntryManager().makeNewGroup(parentGroup, name,
                    getUser(), fileInfoEntry);
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                walkTree(files[i], group);
            }
        }
    }

}
