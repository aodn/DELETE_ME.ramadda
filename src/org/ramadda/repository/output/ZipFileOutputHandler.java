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

package org.ramadda.repository.output;


import org.ramadda.repository.*;
import org.ramadda.repository.auth.*;
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

import java.util.zip.*;

import javax.servlet.http.*;


/**
 *
 *
 * @author RAMADDA Development Team
 * @version $Revision: 1.3 $
 */
public class ZipFileOutputHandler extends OutputHandler {



    /** _more_ */
    public static final OutputType OUTPUT_LIST =
        new OutputType("Zip File Listing", "zipfile.list",
                       OutputType.TYPE_FILE, "", ICON_ZIP);




    /**
     * _more_
     *
     *
     * @param repository _more_
     * @param element _more_
     * @throws Exception _more_
     */
    public ZipFileOutputHandler(Repository repository, Element element)
            throws Exception {
        super(repository, element);
        addType(OUTPUT_LIST);
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

        if (state.entry == null) {
            return;
        }

        if ( !state.entry.isFile()) {
            return;
        }
        if ( !getRepository().getAccessManager().canAccessFile(request,
                state.entry)) {
            return;
        }
        String path = state.entry.getResource().getPath().toLowerCase();
        if (path.endsWith(".zip") || path.endsWith(".jar")
                || path.endsWith(".zidv") || path.endsWith(".kmz")) {
            links.add(makeLink(request, state.entry, OUTPUT_LIST));
        }

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
        if ( !getRepository().getAccessManager().canAccessFile(request,
                entry)) {
            throw new AccessException("Cannot access data", request);
        }
        StringBuffer    sb  = new StringBuffer();

        InputStream fis = getStorageManager().getFileInputStream(
                                  entry.getResource().getPath());
        ZipInputStream zin = new ZipInputStream(fis);
        try {
            ZipEntry ze = null;
            sb.append("<ul>");
            String fileToFetch = request.getString(ARG_FILE, null);
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    continue;
                }
                String path = ze.getName();
                if ((fileToFetch != null) && path.equals(fileToFetch)) {
                    HttpServletResponse response =
                        request.getHttpServletResponse();
                    String type = getRepository().getMimeTypeFromSuffix(
                                      IOUtil.getFileExtension(path));
                    response.setContentType(type);
                    OutputStream output = response.getOutputStream();
                    try {
                        IOUtil.writeTo(zin, output);
                    } finally {
                        IOUtil.close(output);
                        IOUtil.close(zin);
                    }

                    return Result.makeNoOpResult();
                    //                    return new Result("", zin, type);
                }
                //            if(path.endsWith("MANIFEST.MF")) continue;
                sb.append("<li>");
                String name = IOUtil.getFileTail(path);
                String url  = getRepository().URL_ENTRY_SHOW + "/" + name;

                url = HtmlUtils.url(url, ARG_ENTRYID, entry.getId(),
                                    ARG_FILE, path, ARG_OUTPUT,
                                    OUTPUT_LIST.getId());
                sb.append(HtmlUtils.href(url, path));
            }
            sb.append("</ul>");
        } finally {
            IOUtil.close(zin);
            IOUtil.close(fis);
        }

        return makeLinksResult(request, msg("Zip File Listing"), sb,
                               new State(entry));
    }



}
