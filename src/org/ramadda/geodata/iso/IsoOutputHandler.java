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

package org.ramadda.geodata.iso;


import org.ramadda.repository.*;
import org.ramadda.repository.auth.*;
import org.ramadda.repository.metadata.*;
import org.ramadda.repository.output.*;
import org.ramadda.util.HtmlUtils;

import org.ramadda.util.IsoUtil;


import org.w3c.dom.*;


import ucar.unidata.util.DateUtil;
import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;


import ucar.unidata.util.StringUtil;
import ucar.unidata.xml.XmlUtil;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;






/**
 *
 *
 * @author IDV Development Team
 * @version $Revision: 1.3 $
 */
public class IsoOutputHandler extends OutputHandler {



    /** _more_ */
    public static final OutputType OUTPUT_ISO_XML =
        new OutputType("Iso-XML", "iso.xml",
                       OutputType.TYPE_FEEDS | OutputType.TYPE_FORSEARCH, "",
                       ICON_DIF);


    /**
     * _more_
     *
     * @param repository _more_
     * @param element _more_
     * @throws Exception _more_
     */
    public IsoOutputHandler(Repository repository, Element element)
            throws Exception {
        super(repository, element);
        addType(OUTPUT_ISO_XML);
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
        if (state.isDummyGroup()) {
            return;
        }
        if (state.getEntry() != null) {
            links.add(makeLink(request, state.getEntry(), OUTPUT_ISO_XML));
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
        return outputEntry(request, outputType, group);
    }


    /**
     * _more_
     *
     * @param tag _more_
     * @param parent _more_
     * @param text _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    private Element tag(String tag, Element parent, String text)
            throws Exception {
        return XmlUtil.create(parent.getOwnerDocument(), tag, parent, text,
                              null);
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
        Document doc  = XmlUtil.makeDocument();
        Element  root = IsoUtil.makeRoot();

        String   url  =
            request.getAbsoluteUrl(request.url(repository.URL_ENTRY_SHOW,
                ARG_ENTRYID, entry.getId()));

        IsoUtil.addMetadataStandardTag(root);
        IsoUtil.addTextTag(root, IsoUtil.TAG_GMD_DATASETURI, url);

        IsoUtil.addDateStamp(root, new Date(entry.getStartDate()));
        //        tag(DifUtil.TAG_Entry_ID, root, entry.getId());
        /*

        tag(DifUtil.TAG_Summary, root, entry.getDescription());
        parent = tag(DifUtil.TAG_Temporal_Coverage, root, null);
        tag(DifUtil.TAG_Start_Date, parent,
            getRepository().formatYYYYMMDD(new Date(entry.getStartDate())));
        tag(DifUtil.TAG_Stop_Date, parent,
            getRepository().formatYYYYMMDD(new Date(entry.getEndDate())));
        if (entry.hasAreaDefined()) {
            parent = tag(DifUtil.TAG_Spatial_Coverage, root, null);
            tag(DifUtil.TAG_Northernmost_Latitude, parent,
                "" + entry.getNorth());
            tag(DifUtil.TAG_Southernmost_Latitude, parent,
                "" + entry.getSouth());
            tag(DifUtil.TAG_Westernmost_Longitude, parent,
                "" + entry.getWest());
            tag(DifUtil.TAG_Easternmost_Longitude, parent,
                "" + entry.getEast());
        }

        */

        List<Metadata> metadataList = getMetadataManager().getMetadata(entry);
        List<MetadataHandler> metadataHandlers =
            repository.getMetadataManager().getMetadataHandlers();
        for (Metadata metadata : metadataList) {
            for (MetadataHandler metadataHandler : metadataHandlers) {
                if (metadataHandler.canHandle(metadata)) {
                    metadataHandler.addMetadataToXml(request,
                            MetadataTypeBase.TEMPLATETYPE_ISO, entry,
                            metadata, doc, root);

                    break;
                }
            }
        }


        StringBuffer sb = new StringBuffer();
        sb.append(XmlUtil.XML_HEADER);
        sb.append(XmlUtil.toString(root, true));

        return new Result("iso", sb, "text/xml");
    }

}
