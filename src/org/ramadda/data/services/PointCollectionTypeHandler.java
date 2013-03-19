
package org.ramadda.data.services;


import org.ramadda.data.record.RecordField;
import org.ramadda.data.services.*;


import org.ramadda.repository.*;
import org.ramadda.repository.map.MapInfo;
import org.ramadda.repository.metadata.*;
import org.ramadda.repository.output.*;
import org.ramadda.repository.type.*;
import org.ramadda.util.AtomUtil;

import org.ramadda.util.HtmlUtils;


import org.w3c.dom.*;

import ucar.unidata.xml.XmlUtil;


import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 *
 *
 * @author Jeff McWhirter
 * @version $Revision: 1.3 $
 */
public  class PointCollectionTypeHandler extends RecordCollectionTypeHandler {


    /**
     * ctor
     *
     * @param repository the repository
     * @param node the xml node from the types.xml file
     * @throws Exception On badness
     */
    public PointCollectionTypeHandler(Repository repository, Element node)
            throws Exception {
        super(repository, node);
    }


    public  RecordOutputHandler getRecordOutputHandler() {
        return (RecordOutputHandler)getRepository().getOutputHandler(org.ramadda.data.services.PointOutputHandler.class);
    }


}
