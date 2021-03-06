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

package org.ramadda.repository.metadata;


import org.ramadda.repository.*;
import org.ramadda.repository.type.*;
import org.ramadda.util.HtmlUtils;


import org.w3c.dom.*;


import ucar.unidata.ui.ImageUtils;
import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;
import ucar.unidata.util.StringUtil;
import ucar.unidata.util.TwoFacedObject;

import ucar.unidata.xml.XmlUtil;

import java.awt.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;

import java.net.URL;

import java.sql.Statement;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 *
 *
 * @author RAMADDA Development Team
 * @version $Revision: 1.3 $
 */
public class MetadataType extends MetadataTypeBase {

    /** _more_ */
    public static final String TAG_TYPE = "type";

    /** _more_ */
    public static final String TAG_TEMPLATE = "template";


    /** _more_ */
    public static final String TAG_HANDLER = "handler";


    /** _more_ */
    public static final String ATTR_METADATATYPE = "metadatatype";

    /** _more_ */
    public static final String ATTR_CLASS = "class";

    /** _more_ */
    public static final String ATTR_MAKEDATABASE = "makedatabase";


    /** _more_ */
    public static final String ATTR_HANDLER = "handler";

    /** _more_ */
    public static final String ATTR_ID = "id";

    /** _more_ */
    public static final String ATTR_ADMINONLY = "adminonly";

    /** _more_ */
    public static final String ATTR_FORUSER = "foruser";

    /** _more_          */
    public static final String ATTR_ENTRYTYPE = "entrytype";



    /** _more_ */
    public static final String ATTR_DISPLAYCATEGORY = "displaycategory";

    /** _more_ */
    public static final String ATTR_CATEGORY = "category";


    /** _more_ */
    public static final String ATTR_BROWSABLE = "browsable";



    /** _more_ */
    public static final String ATTR_ = "";


    /** _more_          */
    public static final String PROP_METADATA_LABEL = "metadata.label";

    /** _more_ */
    public static String ARG_METADATAID = "metadataid";

    /** _more_ */
    private String id;


    /** _more_ */
    private boolean makeDatabaseTable = false;

    /** _more_ */
    private List<Column> databaseColumns;

    /** _more_ */
    private String displayCategory = "Properties";

    /** _more_ */
    private String category = "Properties";


    /** _more_ */
    private boolean adminOnly = false;


    /** _more_ */
    private boolean browsable = false;

    /** _more_ */
    private boolean forUser = true;

    /** _more_          */
    private String entryType = null;

    /**
     * _more_
     *
     *
     * @param id _more_
     * @param handler _more_
     */
    public MetadataType(String id, MetadataHandler handler) {
        super(handler);
        this.id = id;
    }

    /**
     * _more_
     *
     * @param entry _more_
     *
     * @return _more_
     */
    public boolean isForEntry(Entry entry) {
        if (entryType != null) {
            return entry.isType(entryType);
        }

        return getHandler().isForEntry(entry);
    }



    /**
     * _more_
     *
     * @return _more_
     */
    public String toString() {
        return id;
    }


    /**
     * _more_
     *
     * @param root _more_
     * @param manager _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public static List<MetadataType> parse(Element root,
                                           MetadataManager manager)
            throws Exception {
        List<MetadataType> types = new ArrayList<MetadataType>();
        parse(root, manager, types);

        return types;
    }

    /**
     * _more_
     *
     * @param root _more_
     * @param manager _more_
     * @param types _more_
     *
     * @throws Exception _more_
     */
    private static void parse(Element root, MetadataManager manager,
                              List<MetadataType> types)
            throws Exception {

        NodeList children = XmlUtil.getElements(root);
        if ((children.getLength() == 0)
                && root.getTagName().equals(TAG_HANDLER)) {
            Class c = Misc.findClass(XmlUtil.getAttribute(root, ATTR_CLASS));
            MetadataHandler handler = manager.getHandler(c);
        }

        for (int i = 0; i < children.getLength(); i++) {
            Element node = (Element) children.item(i);
            if (node.getTagName().equals(TAG_HANDLER)) {
                parse(node, manager, types);

                continue;
            }

            if (node.getTagName().equals(TAG_TEMPLATE)) {
                String templateType = XmlUtil.getAttribute(node, ATTR_TYPE);
                String metadataType = XmlUtil.getAttribute(node,
                                          ATTR_METADATATYPE);

                if (XmlUtil.hasAttribute(node, ATTR_FILE)) {
                    manager.addTemplate(
                        metadataType, templateType,
                        manager.getStorageManager().readSystemResource(
                            XmlUtil.getAttribute(node, ATTR_FILE)));

                } else {
                    manager.addTemplate(metadataType, templateType,
                                        XmlUtil.getChildText(node));
                }

                continue;
            }
            if ( !node.getTagName().equals(TAG_TYPE)) {
                manager.logError("Unknown metadata xml tag:"
                                 + XmlUtil.toString(node), null);
            }

            Class c = Misc.findClass(XmlUtil.getAttributeFromTree(node,
                          ATTR_CLASS,
                          "org.ramadda.repository.metadata.MetadataHandler"));

            String          id           = XmlUtil.getAttribute(node, ATTR_ID);
            MetadataHandler handler      = manager.getHandler(c);
            MetadataType    metadataType = new MetadataType(id, handler);
            metadataType.init(node);
            handler.addMetadataType(metadataType);
            types.add(metadataType);
        }
    }


    /**
     * _more_
     *
     * @param node _more_
     *
     * @throws Exception _more_
     */
    public void init(Element node) throws Exception {
        super.init(node);
        setAdminOnly(XmlUtil.getAttributeFromTree(node, ATTR_ADMINONLY,
                false));

        setForUser(XmlUtil.getAttributeFromTree(node, ATTR_FORUSER, true));
        entryType = XmlUtil.getAttributeFromTree(node, ATTR_ENTRYTYPE,
                (String) null);


        setBrowsable(XmlUtil.getAttributeFromTree(node, ATTR_BROWSABLE,
                false));

        setDisplayCategory(XmlUtil.getAttributeFromTree(node,
                ATTR_DISPLAYCATEGORY, "Properties"));

        setCategory(XmlUtil.getAttributeFromTree(node, ATTR_CATEGORY,
                handler.getHandlerGroupName()));

        makeDatabaseTable = XmlUtil.getAttributeFromTree(node,
                ATTR_MAKEDATABASE, false);
        if (makeDatabaseTable) {
            initDatabase();
        }
    }



    /**
     * _more_
     *
     * @return _more_
     */
    public String getTableName() {
        return "md_" + id;
    }


    /**
     * _more_
     *
     * @throws Exception _more_
     */
    private void initDatabase() throws Exception {
        Statement statement = getDatabaseManager().createStatement();
        databaseColumns = new ArrayList<Column>();
        int          cnt       = 0;
        final String tableName = "md_" + id;
        System.err.println("Making db:" + tableName);
        TypeHandler typeHandler = new TypeHandler(getRepository()) {
            public String getTableName() {
                return tableName;
            }
        };
        StringBuffer tableDef = new StringBuffer("CREATE TABLE " + tableName
                                    + " (\n");

        tableDef.append(
            "id varchar(200), entry_id varchar(200), type varchar(200), inherited int)");
        try {
            getDatabaseManager().executeAndClose(tableDef.toString());
        } catch (Throwable exc) {
            System.err.println("EXC:" + exc);
            if (exc.toString().indexOf("already exists") < 0) {
                //TODO:
                //                throw new WrapperException(exc);
            }
        }

        StringBuffer indexDef = new StringBuffer();
        indexDef.append("CREATE INDEX " + getTableName() + "_INDEX_" + "id"
                        + "  ON " + getTableName() + " (" + "id" + ");\n");
        indexDef.append("CREATE INDEX " + getTableName() + "_INDEX_"
                        + "entry_id" + "  ON " + getTableName() + " ("
                        + "entry_id" + ");\n");
        indexDef.append("CREATE INDEX " + getTableName() + "_INDEX_" + "type"
                        + "  ON " + getTableName() + " (" + "type" + ");\n");

        try {
            getDatabaseManager().loadSql(indexDef.toString(), true, false);
        } catch (Throwable exc) {
            //TODO:
            //            throw new WrapperException(exc);
        }




        for (MetadataElement element : getChildren()) {
            Column column = new Column(typeHandler, element.getId(),
                                       element.getDataType(), cnt);
            column.setSize(XmlUtil.getAttribute(element.getXmlNode(),
                    column.ATTR_SIZE, 200));
            column.createTable(statement);
            databaseColumns.add(column);
            cnt++;
        }
        getDatabaseManager().closeAndReleaseConnection(statement);
    }

    /**
     * _more_
     *
     * @param metadata _more_
     * @param entry _more_
     *
     * @throws Exception _more_
     */
    public void newEntry(Metadata metadata, Entry entry) throws Exception {
        for (MetadataElement element : getChildren()) {
            if (element.getDataType().equals(element.DATATYPE_FILE)) {
                String fileArg = metadata.getAttr(element.getIndex());
                if ((fileArg == null) || (fileArg.length() == 0)) {
                    continue;
                }
                if ( !entry.getIsLocalFile()) {
                    fileArg = getStorageManager().copyToEntryDir(entry,
                            new File(fileArg)).getName();
                }
                metadata.setAttr(element.getIndex(), fileArg);
            }
        }
    }


    /**
     * _more_
     *
     * @param entry _more_
     * @param node _more_
     * @param metadata _more_
     * @param fileMap _more_
     * @param internal _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public boolean processMetadataXml(Entry entry, Element node,
                                      Metadata metadata, Hashtable fileMap,
                                      boolean internal)
            throws Exception {
        NodeList elements = XmlUtil.getElements(node);

        for (MetadataElement element : getChildren()) {
            if ( !element.getDataType().equals(element.DATATYPE_FILE)) {
                continue;
            }
            String  fileArg  = null;
            Element attrNode = XmlUtil.findElement(elements,
                                   Metadata.ATTR_INDEX,
                                   "" + element.getIndex());
            if (attrNode == null) {
                fileArg = XmlUtil.getAttribute(node,
                        "attr" + element.getIndex(), (String) null);
                if (fileArg == null) {
                    System.err.println("Could not find attr node:"
                                       + XmlUtil.toString(node));

                    continue;
                }
            } else {
                fileArg = XmlUtil.getAttribute(attrNode, "fileid",
                        (String) null);
            }
            if (fileArg == null) {
                System.err.println("Could not find fileid:"
                                   + XmlUtil.toString(attrNode));

                continue;
            }

            String fileName = null;
            if (internal) {
                fileName = fileArg;
            } else {
                String tmpFile = (String) fileMap.get(fileArg);
                if (tmpFile == null) {
                    try {
                        //See if its a URL
                        URL testUrl = new URL(fileArg);

                        continue;
                    } catch (Exception ignore) {
                        handler.getRepository().getLogManager().logError(
                            "No attachment uploaded file:" + fileArg);
                        handler.getRepository().getLogManager().logError(
                            "available files: " + fileMap);

                        return false;
                    }
                }
                File file = new File(tmpFile);
                fileName = getStorageManager().copyToEntryDir(entry, file,
                        metadata.getAttr(element.getIndex())).getName();
            }
            metadata.setAttr(element.getIndex(), fileName);
        }

        return true;

    }



    /**
     *  _more_
     *
     *  @param request _more_
     *  @param entry _more_
     *  @param id _more_
     *  @param suffix _more_
     * @param oldMetadata _more_
     *  @param newMetadata _more_
     *
     *
     * @return _more_
     *  @throws Exception _more_
     */
    public Metadata handleForm(Request request, Entry entry, String id,
                               String suffix, Metadata oldMetadata,
                               boolean newMetadata)
            throws Exception {
        boolean inherited = request.get(ARG_METADATA_INHERITED + suffix,
                                        false);
        Metadata metadata = new Metadata(id, entry.getId(), getId(),
                                         inherited);
        for (MetadataElement element : getChildren()) {
            String value = element.handleForm(request, entry, metadata,
                               oldMetadata, suffix);
            metadata.setAttr(element.getIndex(), value);
        }

        return metadata;
    }


    /**
     * _more_
     *
     * @param request _more_
     * @param templateType _more_
     * @param entry _more_
     * @param metadata _more_
     * @param parent _more_
     *
     *
     * @return _more_
     * @throws Exception _more_
     */
    public boolean addMetadataToXml(Request request, String templateType,
                                    Entry entry, Metadata metadata,
                                    Element parent)
            throws Exception {


        String xml = applyTemplate(templateType, entry, metadata, parent);
        if ((xml == null) || (xml.length() == 0)) {
            return false;
        }
        xml = "<tmp>" + xml + "</tmp>";
        Element root = null;
        try {
            root = XmlUtil.getRoot(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception exc) {
            throw new IllegalStateException("XML Error:" + exc
                                            + "\nCould not create xml:"
                                            + xml);
        }
        if (root == null) {
            throw new IllegalStateException("Could not create xml:" + xml);
        }
        NodeList children = XmlUtil.getElements(root);
        for (int i = 0; i < children.getLength(); i++) {
            Element node = (Element) children.item(i);
            node = (Element) parent.getOwnerDocument().importNode(node, true);
            parent.appendChild(node);
        }

        return true;
    }







    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param sb _more_
     * @param metadata _more_
     * @param forLink _more_
     *
     * @throws Exception _more_
     */
    public void decorateEntry(Request request, Entry entry, StringBuffer sb,
                              Metadata metadata, boolean forLink)
            throws Exception {
        decorateEntry(request, entry, sb, metadata, forLink, false);
    }

    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param sb _more_
     * @param metadata _more_
     * @param forLink _more_
     * @param isThumbnail _more_
     *
     * @throws Exception _more_
     */
    public void decorateEntry(Request request, Entry entry, StringBuffer sb,
                              Metadata metadata, boolean forLink,
                              boolean isThumbnail)
            throws Exception {
        for (MetadataElement element : getChildren()) {
            if ( !element.getDataType().equals(element.DATATYPE_FILE)) {
                continue;
            }
            if ( !element.showAsAttachment()) {
                continue;
            }
            if (element.getThumbnail() || isThumbnail) {
                String html = getFileHtml(request, entry, metadata, element,
                                          forLink);
                if (html != null) {
                    sb.append(HtmlUtils.space(1));
                    sb.append(html);
                    sb.append(HtmlUtils.space(1));
                } else {
                    String value = metadata.getAttr(element.getIndex());
                    if ((value != null) && value.startsWith("http")) {
                        sb.append(HtmlUtils.space(1));
                        sb.append(HtmlUtils.img(value));
                        sb.append(HtmlUtils.space(1));
                    }
                }

                continue;
            }
            if ( !forLink) {
                String html = getFileHtml(request, entry, metadata, element,
                                          false);
                if (html != null) {
                    sb.append(HtmlUtils.space(1));
                    sb.append(html);
                    sb.append(HtmlUtils.space(1));
                }
            }
        }
    }


    /**
     * _more_
     *
     * @param oldEntry _more_
     * @param newEntry _more_
     * @param newMetadata _more_
     *
     * @throws Exception _more_
     */
    public void initializeCopiedMetadata(Entry oldEntry, Entry newEntry,
                                         Metadata newMetadata)
            throws Exception {
        for (MetadataElement element : getChildren()) {
            if ( !element.getDataType().equals(element.DATATYPE_FILE)) {
                continue;
            }
            String oldFileName = newMetadata.getAttr(element.getIndex());
            String newFileName = getStorageManager().copyToEntryDir(oldEntry,
                                     newEntry, oldFileName);
            newMetadata.setAttr(element.getIndex(), newFileName);
        }
    }



    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param urls _more_
     * @param metadata _more_
     *
     * @throws Exception _more_
     */
    public void getThumbnailUrls(Request request, Entry entry,
                                 List<String> urls, Metadata metadata)
            throws Exception {
        for (MetadataElement element : getChildren()) {
            if ( !element.getDataType().equals(element.DATATYPE_FILE)) {
                continue;
            }
            if ( !element.showAsAttachment()) {
                continue;
            }
            if (element.getThumbnail()) {
                String url = getImageUrl(request, entry, metadata, null);
                if (url != null) {
                    urls.add(url);
                }
            }
        }
    }



    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param metadata _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Result processView(Request request, Entry entry, Metadata metadata)
            throws Exception {
        int elementIndex = request.get(ARG_ELEMENT, 0) - 1;
        if ((elementIndex < 0) || (elementIndex >= getChildren().size())) {
            return new Result("", "Cannot process view");
        }
        MetadataElement element = getChildren().get(elementIndex);
        if ( !element.getDataType().equals(element.DATATYPE_FILE)) {
            return new Result("", "Cannot process view");
        }
        File f = getFile(entry, metadata, element);
        if (f == null) {
            return new Result("", "File does not exist");
        }
        String mimeType = handler.getRepository().getMimeTypeFromSuffix(
                              IOUtil.getFileExtension(f.toString()));
        if (request.get(ARG_THUMBNAIL, false)) {
            File thumb = getStorageManager().getTmpFile(request,
                             IOUtil.getFileTail(f.toString()));
            if ( !thumb.exists()) {
                Image image = ImageUtils.readImage(f.toString());
                image = ImageUtils.resize(image, 100, -1);
                ImageUtils.waitOnImage(image);
                ImageUtils.writeImageToFile(image, thumb.toString());
            }
            f = thumb;
        }

        
        /*        Result result = new Result(
                            "thumbnail",
                            IOUtil.readBytes(
                                getStorageManager().getFileInputStream(f),
                                null, true), mimeType);*/
        InputStream inputStream = getStorageManager().getFileInputStream(f);
        Result result = new Result(inputStream, mimeType);
        return result;
    }






    /**
     * _more_
     *
     * @param request _more_
     * @param metadata _more_
     *
     * @return _more_
     */
    public String getSearchUrl(Request request, Metadata metadata) {
        if ( !getSearchable()) {
            return null;
        }

        List args = new ArrayList();
        args.add(ARG_METADATA_TYPE + "." + getId());
        args.add(this.toString());


        for (MetadataElement element : getChildren()) {
            if ( !element.getSearchable()) {
                continue;
            }
            args.add(ARG_METADATA_ATTR + element.getIndex() + "." + getId());
            args.add(metadata.getAttr(element.getIndex()));
        }

        //by default search on attr1 if none are set above
        if (args.size() == 2) {
            args.add(ARG_METADATA_ATTR1 + "." + getId());
            args.add(metadata.getAttr1());
        }

        for (Object o : args) {
            if (o == null) {
                System.err.println("NULL: " + args);

                return null;
            }
        }

        try {
            return HtmlUtils
                .url(request
                    .url(handler.getRepository().getSearchManager()
                        .URL_ENTRY_SEARCH), args);
        } catch (Exception exc) {
            System.err.println("ARGS:" + args);

            throw new RuntimeException(exc);
        }
    }


    /**
     * _more_
     *
     * @param entry _more_
     * @param sb _more_
     * @param metadata _more_
     *
     * @throws Exception _more_
     */
    public void getTextCorpus(Entry entry, StringBuffer sb, Metadata metadata)
            throws Exception {
        for (MetadataElement element : getChildren()) {
            String value = metadata.getAttr(element.getIndex());
            element.getTextCorpus(value, sb);
        }
    }

    /**
     * _more_
     *
     * @param type _more_
     *
     * @return _more_
     */
    public String getTemplate(String type) {
        String template = super.getTemplate(type);
        if (template != null) {
            return template;
        }

        return getMetadataManager().getTemplate(id, type);
    }



    /**
     * _more_
     *
     * @param metadata _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public String getTypeLabel(Metadata metadata) throws Exception {
        String nameString = getName();
        for (MetadataElement element : getChildren()) {
            String value = metadata.getAttr(element.getIndex());
            if (value == null) {
                value = "";
            }
            nameString = nameString.replace("${attr" + element.getIndex()
                                            + "}", value);
        }

        return nameString;
    }


    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     * @param metadata _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public String[] getHtml(Request request, Entry entry, Metadata metadata)
            throws Exception {
        if ( !getShowInHtml()) {
            return null;
        }
        StringBuffer content = new StringBuffer();
        if (getSearchable()) {
            content.append(handler.getSearchLink(request, metadata));
        }

        String nameString   = getTypeLabel(metadata);
        String lbl          = msgLabel(nameString);
        String htmlTemplate = getTemplate(TEMPLATETYPE_HTML);
        if (htmlTemplate != null) {
            String html = htmlTemplate;
            for (MetadataElement element : getChildren()) {
                String value = metadata.getAttr(element.getIndex());
                if (value == null) {
                    value = "null";
                }
                html = applyMacros(html, element, value);
            }
            content.append(html);
        } else {
            int                   cnt      = 1;
            boolean               didOne   = false;

            List<MetadataElement> children = getChildren();
            if (children.size() > 1) {
                content.append(HtmlUtils.formTable());
            } else {
                content.append(HtmlUtils.formTable());
                //??                    "<table border=0 cellpadding=0 cellspacing=0>");
            }
            for (MetadataElement element : children) {
                MetadataElement.FormInfo formInfo =
                    element.getHtml(metadata.getAttr(cnt), 0);
                if (formInfo != null) {
                    //xxxx
                    if ( !element.isGroup() && (children.size() == 1)) {
                        content.append(
                            HtmlUtils.row(
                                HtmlUtils.colspan(formInfo.content, 2)));
                    } else {
                        content.append(HtmlUtils.formEntry(formInfo.label,
                                formInfo.content));
                    }
                    didOne = true;
                }
                cnt++;
            }
            content.append("</table>");
            if ( !didOne) {
                return null;
            }
        }

        return new String[] { lbl, content.toString() };
    }


    /**
     * _more_
     *
     * @param template _more_
     * @param element _more_
     * @param value _more_
     *
     * @return _more_
     */
    public String applyMacros(String template, MetadataElement element,
                              String value) {
        template = super.applyMacros(template, element, value);
        if (template != null) {
            while (template.indexOf("${id}") >= 0) {
                template = template.replace("${id}", id);
            }
        }

        return template;
    }


    /**
     * _more_
     *
     * @param handler _more_
     * @param request _more_
     * @param entry _more_
     * @param metadata _more_
     * @param suffix _more_
     * @param forEdit _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public String[] getForm(MetadataHandler handler, Request request,
                            Entry entry, Metadata metadata, String suffix,
                            boolean forEdit)
            throws Exception {

        String lbl = (String) request.getExtraProperty(PROP_METADATA_LABEL);
        if (lbl == null) {
            lbl = msgLabel(getName());
        }
        String submit = HtmlUtils.submit(msg("Add") + HtmlUtils.space(1)
                                         + getName());
        String       cancel = HtmlUtils.submit(msg("Cancel"), ARG_CANCEL);


        StringBuffer sb     = new StringBuffer();

        if ( !forEdit) {
            sb.append(header(msgLabel("Add") + getName()));
        }
        sb.append(HtmlUtils.br());
        String lastGroup = null;
        for (MetadataElement element : getChildren()) {
            if ((element.getGroup() != null)
                    && !Misc.equals(element.getGroup(), lastGroup)) {
                lastGroup = element.getGroup();
                sb.append(HtmlUtils.row(HtmlUtils.colspan(header(lastGroup),
                        2)));
            }
            String elementLbl = msgLabel(element.getLabel());
            String widget     =
                element.getForm(request, entry, metadata, suffix,
                                metadata.getAttr(element.getIndex()),
                                forEdit);
            if ((widget == null) || (widget.length() == 0)) {}
            else {
                String suffixLabel = element.getSuffixLabel();
                if (suffixLabel == null) {
                    suffixLabel = "";
                }

                sb.append(HtmlUtils.formEntry(elementLbl,
                        widget + suffixLabel));
            }
        }

        sb.append(
            HtmlUtils.formEntry(
                msgLabel("Inherited"),
                HtmlUtils.checkbox(
                    ARG_METADATA_INHERITED + suffix, "true",
                    metadata.getInherited())));



        String argtype = ARG_METADATA_TYPE + suffix;
        String argid   = ARG_METADATAID + suffix;
        sb.append(HtmlUtils.hidden(argtype, getId())
                  + HtmlUtils.hidden(argid, metadata.getId()));

        if ( !forEdit && (entry != null)) {
            sb.append(HtmlUtils.formEntry("", submit + cancel));
        }


        return new String[] { lbl, sb.toString() };
    }







    /**
     * _more_
     *
     * @param id _more_
     *
     * @return _more_
     */
    public boolean isType(String id) {
        return Misc.equals(this.id, id);
    }



    /**
     *  Get the ID property.
     *
     *  @return The Id
     */
    public String getId() {
        return id;
    }




    /**
     *  Set the Category property.
     *
     *  @param value The new value for Category
     */
    public void setCategory(String value) {
        category = value;
    }

    /**
     *  Get the Category property.
     *
     *  @return The Category
     */
    public String getCategory() {
        return category;
    }





    /**
     *  Set the DisplayCategory property.
     *
     *  @param value The new value for DisplayCategory
     */
    public void setDisplayCategory(String value) {
        this.displayCategory = value;
    }

    /**
     *  Get the DisplayCategory property.
     *
     *  @return The DisplayCategory
     */
    public String getDisplayCategory() {
        return this.displayCategory;
    }

    /**
     *  Set the AdminOnly property.
     *
     *  @param value The new value for AdminOnly
     */
    public void setAdminOnly(boolean value) {
        this.adminOnly = value;
    }

    /**
     *  Get the AdminOnly property.
     *
     *  @return The AdminOnly
     */
    public boolean getAdminOnly() {
        return this.adminOnly;
    }



    /**
     * Set the Browsable property.
     *
     * @param value The new value for Browsable
     */
    public void setBrowsable(boolean value) {
        this.browsable = value;
    }

    /**
     * Get the Browsable property.
     *
     * @return The Browsable
     */
    public boolean getBrowsable() {
        return this.browsable;
    }

    /**
     * Set the ForUser property.
     *
     * @param value The new value for ForUser
     */
    public void setForUser(boolean value) {
        this.forUser = value;
    }

    /**
     * Get the ForUser property.
     *
     * @return The ForUser
     */
    public boolean getForUser() {
        return this.forUser;
    }





    /**
     * _more_
     *
     * @return _more_
     */
    public boolean getHasDatabaseTable() {
        return makeDatabaseTable;
    }

}
