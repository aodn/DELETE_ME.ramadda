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
import org.ramadda.repository.metadata.*;
import org.ramadda.repository.type.*;

import org.ramadda.util.AtomUtil;
import org.ramadda.util.HtmlUtils;


import org.w3c.dom.*;

import ucar.unidata.sql.SqlUtil;

import ucar.unidata.ui.ImageUtils;
import ucar.unidata.util.DateUtil;
import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;


import ucar.unidata.util.StringBufferCollection;


import ucar.unidata.util.StringUtil;
import ucar.unidata.xml.XmlUtil;


import java.io.*;

import java.io.File;



import java.net.*;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;



import java.util.regex.*;

import java.util.zip.*;


/**
 * Provides ATOM xml representation
 *
 *
 * @author RAMADDA Development Team
 */
public class AtomOutputHandler extends OutputHandler {

    /** mime type */
    public static final String MIME_ATOM = "application/atom+xml";


    /** _more_ */
    SimpleDateFormat sdf =
        new SimpleDateFormat("EEE dd, MMM yyyy HH:mm:ss Z");

    /** _more_ */
    public static final OutputType OUTPUT_ATOM =
        new OutputType("ATOM Feed", "atom",
                       OutputType.TYPE_FEEDS | OutputType.TYPE_FORSEARCH, "",
                       ICON_ATOM);



    /**
     * _more_
     *
     * @param repository _more_
     * @param element _more_
     * @throws Exception _more_
     */
    public AtomOutputHandler(Repository repository, Element element)
            throws Exception {
        super(repository, element);
        addType(OUTPUT_ATOM);
    }




    /**
     * _more_
     *
     * @param request _more_
     * @param state _more_
     * @param links _more_
     *
     * @throws Exception _more_
     */
    public void getEntryLinks(Request request, State state, List<Link> links)
            throws Exception {
        if (state.getEntry() != null) {
            links.add(
                makeLink(
                    request, state.getEntry(), OUTPUT_ATOM,
                    "/" + IOUtil.stripExtension(state.getEntry().getName())
                    + ".xml"));
        }
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
        entries.addAll(subGroups);

        return outputEntries(request, group, entries);
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
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(entry);

        return outputEntries(request, entry, entries);
    }


    /**
     * _more_
     *
     * @param request _more_
     * @param parentEntry _more_
     * @param entries _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    private Result outputEntries(Request request, Entry parentEntry,
                                 List<Entry> entries)
            throws Exception {

        String       feedId = request.getAbsoluteUrl(request.getRequestPath());
        StringBuffer sb     = new StringBuffer();
        sb.append(AtomUtil.openFeed(feedId));
        sb.append("\n");
        sb.append(AtomUtil.makeTitle(parentEntry.getName()
                                     + " ATOM Site Feed"));
        sb.append("\n");
        sb.append(
            AtomUtil.makeLink(
                AtomUtil.REL_SELF, request.getAbsoluteUrl(request.getUrl())));
        sb.append("\n");
        for (Entry entry : entries) {
            List<AtomUtil.Link> links   = new ArrayList<AtomUtil.Link>();
            String              selfUrl =
                request.getAbsoluteUrl(
                    HtmlUtils.url(
                                  getRepository().getUrlPath(request,
                            getRepository().URL_ENTRY_SHOW), ARG_ENTRYID,
                                entry.getId()));
            links.add(new AtomUtil.Link(AtomUtil.REL_ALTERNATE, selfUrl,
                                        "Web page", "text/html"));
            String resource = entry.getResource().getPath();
            if (ImageUtils.isImage(resource)) {
                String imageUrl = request.getAbsoluteUrl(
                                      HtmlUtils.url(
                                          getRepository().URL_ENTRY_GET
                                          + entry.getId()
                                          + IOUtil.getFileExtension(
                                              resource), ARG_ENTRYID,
                                                  entry.getId()));
                links.add(new AtomUtil.Link(AtomUtil.REL_IMAGE, imageUrl,
                                            "Image"));
            }

            TypeHandler   typeHandler = entry.getTypeHandler();
            List<Service> services    = new ArrayList<Service>();
            typeHandler.getServices(request, entry, services);
            for (Service service : services) {
                String url      = service.getUrl();
                String relType  = service.getType();
                String name     = service.getName();
                String mimeType = service.getMimeType();
                links.add(new AtomUtil.Link(relType, url, name, mimeType));
            }

            List<String> urls = new ArrayList<String>();
            getMetadataManager().getThumbnailUrls(request, entry, urls);

            for (String url : urls) {
                links.add(new AtomUtil.Link("thumbnail",
                                            request.getAbsoluteUrl(url),
                                            "Thumbnail"));
            }

            StringBuffer extra = new StringBuffer();
            Document     doc   =
                XmlUtil.getDocument("<content type=\"text/xml\"></content>");
            Element root = doc.getDocumentElement();
            typeHandler.addMetadataToXml(entry, root, extra, "atom");

            List<Metadata> inheritedMetadata =
                getMetadataManager().getInheritedMetadata(request, entry);
            List<Metadata> metadataList =
                getMetadataManager().getMetadata(entry);
            List<MetadataHandler> metadataHandlers =
                repository.getMetadataManager().getMetadataHandlers();
            for (Metadata metadata : metadataList) {
                if (metadata.getType().equals(
                        MetadataHandler.TYPE_SPATIAL_POLYGON)) {
                    //                    <georss:polygon>45.256 -110.45 46.46 -109.48 43.84 -109.86 45.256 -110.45</georss:polygon>
                    extra.append("<georss:polygon>");
                    List<double[]> points   = new ArrayList<double[]>();
                    String         s        = metadata.getAttr1();

                    double         firstLat = Double.NaN;
                    double         firstLon = Double.NaN;
                    for (String pair : StringUtil.split(s, ";", true, true)) {
                        List<String> toks = StringUtil.splitUpTo(pair, ",",
                                                2);
                        if (toks.size() != 2) {
                            continue;
                        }
                        double lat = Misc.decodeLatLon(toks.get(0));
                        double lon = Misc.decodeLatLon(toks.get(1));
                        extra.append(lat);
                        extra.append(" ");
                        extra.append(lon);
                        extra.append(" ");
                        if (Double.isNaN(firstLat)) {
                            firstLat = lat;
                            firstLon = lon;
                        }
                    }
                    //Close the circle
                    extra.append(firstLat);
                    extra.append(" ");
                    extra.append(firstLon);
                    extra.append(" ");
                    extra.append("</georss:polygon>\n");

                    continue;
                }

                addMetadata(request, entry, doc, root, metadata,
                            metadataHandlers);
            }

            for (Metadata metadata : inheritedMetadata) {
                logInfo("addMetadata with inherited metadata:" + metadata);
                addMetadata(request, entry, doc, root, metadata,
                            metadataHandlers);
            }


            if (entry.hasAreaDefined()) {
                extra.append("<georss:box>" + entry.getSouth() + " "
                             + entry.getWest() + " " + entry.getNorth() + " "
                             + entry.getEast() + "</georss:box>\n");
            }

            extra.append(XmlUtil.toString(root));

            String desc = entry.getDescription();
            if (TypeHandler.isWikiText(desc)) {
                desc = "";
            }

            String authorUrl = request.getAbsoluteUrl(
                                   getRepository().URL_ENTRY_SHOW.toString());
            sb.append(AtomUtil.makeEntry(entry.getName(), selfUrl,
                                         new Date(entry.getCreateDate()),
                                         new Date(entry.getChangeDate()),
                                         new Date(entry.getStartDate()),
                                         new Date(entry.getEndDate()), desc,
                                         null, entry.getUser().getName(),
                                         authorUrl, links, extra.toString()));
        }
        sb.append(AtomUtil.closeFeed());

        return new Result("", sb, MIME_ATOM);
    }

    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param doc _more_
     * @param root _more_
     * @param metadata _more_
     * @param metadataHandlers _more_
     *
     * @throws Exception _more_
     */
    private void addMetadata(Request request, Entry entry, Document doc,
                             Element root, Metadata metadata,
                             List<MetadataHandler> metadataHandlers)
            throws Exception {
        MetadataHandler metadataHandler = getMetadataManager().findHandler(metadata.getType());
        if(metadataHandler == null) {
            logError("Could not find metadata handler for:" + metadata, null);
            return;
        }


        if (!metadataHandler.addMetadataToXml(request, "atom",
                                              entry, metadata, doc, root)) {
            logInfo ("addMetadata:" + metadata +" no mapping to atom");
            if(!metadataHandler.addMetadataToXml(request, "dif", entry,
                                                 metadata, doc, root)) {
                System.err.println ("could not add metadata to ATOM");
            }
            
        } 
    }



}
