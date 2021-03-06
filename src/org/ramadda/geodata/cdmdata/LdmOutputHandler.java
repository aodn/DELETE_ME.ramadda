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

package org.ramadda.geodata.cdmdata;


import org.ramadda.repository.*;
import org.ramadda.repository.auth.*;
import org.ramadda.repository.monitor.LdmAction;

import org.ramadda.repository.output.*;
import org.ramadda.util.HtmlUtils;


import org.w3c.dom.*;


import ucar.unidata.sql.SqlUtil;
import ucar.unidata.ui.ImageUtils;
import ucar.unidata.util.DateUtil;
import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;


import ucar.unidata.util.StringUtil;
import ucar.unidata.xml.XmlUtil;


import java.io.*;

import java.io.File;
import java.io.InputStream;



import java.net.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;



import java.util.regex.*;

import java.util.zip.*;


/**
 *
 *
 * @author IDV Development Team
 * @version $Revision: 1.3 $
 */
public class LdmOutputHandler extends OutputHandler {



    /** _more_ */
    public static final OutputType OUTPUT_LDM = new OutputType("LDM Insert",
                                                    "ldm",
                                                    OutputType.TYPE_FILE, "",
                                                    ICON_DATA);


    /**
     * _more_
     *
     *
     * @param repository _more_
     * @param element _more_
     * @throws Exception _more_
     */
    public LdmOutputHandler(Repository repository, Element element)
            throws Exception {
        super(repository, element);
        addType(OUTPUT_LDM);
    }



    /**
     * _more_
     *
     * @param request _more_
     * @param state _more_
     * @param links _more_
     *
     *
     * @throws Exception _more_
     */
    public void getEntryLinks(Request request, State state, List<Link> links)
            throws Exception {

        //Are we configured to do the LDM
        if (getRepository().getProperty(LdmAction.PROP_LDM_PQINSERT,
                                        "").length() == 0) {
            return;
        }
        if (getRepository().getProperty(LdmAction.PROP_LDM_QUEUE,
                                        "").length() == 0) {
            return;
        }

        if ( !request.getUser().getAdmin()) {
            return;
        }
        if (state.entry != null) {
            if ( !state.entry.isFile()) {
                return;
            }
        } else {

            boolean anyFiles = false;
            for (Entry entry : state.getAllEntries()) {
                if (entry.getResource().isFile()) {
                    anyFiles = true;

                    break;
                }
            }
            if ( !anyFiles) {
                return;
            }
        }
        links.add(makeLink(request, state.getEntry(), OUTPUT_LDM));

    }



    /**
     * _more_
     *
     * @param request _more_
     * @param outputType _more_
     * @param entry _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Result outputEntry(Request request, OutputType outputType,
                              Entry entry)
            throws Exception {
        return handleEntries(request, entry,
                             (List<Entry>) Misc.newList(entry));
    }

    /**
     * _more_
     *
     * @param request _more_
     * @param outputType _more_
     * @param group _more_
     * @param subGroups _more_
     * @param entries _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Result outputGroup(Request request, OutputType outputType,
                              Entry group, List<Entry> subGroups,
                              List<Entry> entries)
            throws Exception {
        return handleEntries(request, group, entries);
    }

    /** _more_ */
    private String lastFeed = "SPARE";

    /** _more_ */
    private String lastProductId = "${filename}";

    /**
     * _more_
     *
     * @param request _more_
     * @param parent _more_
     * @param entries _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    private Result handleEntries(Request request, Entry parent,
                                 List<Entry> entries)
            throws Exception {
        StringBuffer sb          = new StringBuffer();
        List<Entry>  fileEntries = new ArrayList<Entry>();
        List<String> ids         = new ArrayList<String>();
        for (Entry entry : entries) {
            if (entry.isFile()) {
                fileEntries.add(entry);
                ids.add(entry.getId());
            }
        }

        String feed = request.getString(LdmAction.PROP_LDM_FEED, lastFeed);
        String productId = request.getString(LdmAction.PROP_LDM_PRODUCTID,
                                             lastProductId);
        if ( !request.defined(LdmAction.PROP_LDM_FEED)) {
            String formUrl;
            if (parent.isGroup() && parent.isDummy()) {
                formUrl = request.url(getRepository().URL_ENTRY_GETENTRIES);
                sb.append(HtmlUtils.form(formUrl));
                sb.append(HtmlUtils.hidden(ARG_ENTRYIDS,
                                           StringUtil.join(",", ids)));
            } else {
                formUrl = request.url(getRepository().URL_ENTRY_SHOW);
                sb.append(HtmlUtils.form(formUrl));
                sb.append(HtmlUtils.hidden(ARG_ENTRYID, parent.getId()));
            }
            sb.append(HtmlUtils.hidden(ARG_OUTPUT, OUTPUT_LDM.getId()));
            sb.append(HtmlUtils.formTable());

            if (fileEntries.size() == 1) {
                File   f        = fileEntries.get(0).getFile();
                String fileTail =
                    getStorageManager().getFileTail(fileEntries.get(0));
                String size = " (" + f.length() + " bytes)";
                sb.append(HtmlUtils.formEntry("File:", fileTail + size));
            } else {
                int size = 0;
                for (Entry entry : fileEntries) {
                    size += entry.getFile().length();
                }
                sb.append(HtmlUtils.formEntry("Files:",
                        fileEntries.size() + " files. Total size:" + size));
            }


            sb.append(
                HtmlUtils.formEntry(
                    "Feed:",
                    HtmlUtils.select(
                        LdmAction.PROP_LDM_FEED,
                        Misc.toList(LdmAction.LDM_FEED_TYPES), feed)));
            String tooltip =
                "macros: ${fromday}  ${frommonth} ${fromyear} ${frommonthname}  <br>"
                + "${today}  ${tomonth} ${toyear} ${tomonthname} <br> "
                + "${filename}  ${fileextension}";
            sb.append(
                HtmlUtils.formEntry(
                    "Product ID:",
                    HtmlUtils.input(
                        LdmAction.PROP_LDM_PRODUCTID, productId,
                        HtmlUtils.SIZE_60 + HtmlUtils.title(tooltip))));

            sb.append(HtmlUtils.formTableClose());
            if (fileEntries.size() > 1) {
                sb.append(HtmlUtils.submit(msg("Insert files into LDM")));
            } else {
                sb.append(HtmlUtils.submit(msg("Insert file into LDM")));
            }
        } else {
            String queue =
                getRepository().getProperty(LdmAction.PROP_LDM_QUEUE, "");
            String pqinsert =
                getRepository().getProperty(LdmAction.PROP_LDM_PQINSERT, "");
            for (Entry entry : fileEntries) {
                String id =
                    getRepository().getEntryManager().replaceMacros(entry,
                        productId);
                LdmAction.insertIntoQueue(getRepository(), pqinsert, queue,
                                          feed, id,
                                          entry.getResource().getPath());
                sb.append("Inserted: "
                          + getStorageManager().getFileTail(entry));
                sb.append(HtmlUtils.br());
            }
            lastFeed      = feed;
            lastProductId = productId;
        }

        return makeLinksResult(request, msg("LDM Insert"), sb,
                               new State(parent));
    }



}
