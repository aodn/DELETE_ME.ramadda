/*
 * Copyright 2008-2011 Jeff McWhirter/ramadda.org
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.ramadda.geodata.data;


import ucar.unidata.xml.XmlUtil;


/**
 * Class description
 *
 *
 */
public class NcmlUtil {

    /** _more_ */
    public static final String AGG_JOINEXISTING = "joinExisting";

    /** _more_ */
    public static final String AGG_JOINNEW = "joinNew";

    /** _more_ */
    public static final String AGG_UNION = "union";

    /** _more_          */
    public static final String AGG_ENSEMBLE = "ensemble";


    /** _more_          */
    public String aggType;

    /**
     * _more_
     *
     * @param aggType _more_
     */
    public NcmlUtil(String aggType) {
        this.aggType = aggType;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public String toString() {
        return aggType;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public boolean isJoinExisting() {
        return aggType.equalsIgnoreCase(AGG_JOINEXISTING);
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public boolean isJoinNew() {
        return aggType.equalsIgnoreCase(AGG_JOINNEW);
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public boolean isUnion() {
        return aggType.equalsIgnoreCase(AGG_UNION);
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public boolean isEnsemble() {
        return aggType.equalsIgnoreCase(AGG_ENSEMBLE);
    }


    /**
     * _more_
     *
     * @param sb _more_
     */
    public static void openNcml(StringBuffer sb) {
        sb.append(XmlUtil.openTag(TAG_NETCDF,
                                  XmlUtil.attrs(new String[] { "xmlns",
                XMLNS_XMLNS })));
    }


    /**
     * _more_
     *
     * @param sb _more_
     * @param name _more_
     */
    public static void addEnsembleVariables(StringBuffer sb, String name) {
        /*
 <variable name='ens' type='String' shape='ens'>
   <attribute name='long_name' value='ensemble coordinate' />
   <attribute name='_CoordinateAxisType' value='Ensemble' />
 </variable>
        */

        sb.append(XmlUtil.tag(TAG_VARIABLE, XmlUtil.attrs(new String[] {
            ATTR_NAME, name, ATTR_TYPE, "String", ATTR_SHAPE, "ens"
        }), XmlUtil.tag(TAG_ATTRIBUTE, XmlUtil.attrs(new String[] { ATTR_NAME,
                "long_name", ATTR_VALUE,
                "ensemble coordinate" })) + XmlUtil.tag(TAG_ATTRIBUTE,
                    XmlUtil.attrs(new String[] { ATTR_NAME,
                "_CoordinateAxisType", ATTR_VALUE, "Ensemble" }))));
    }


    /** _more_ */
    public static final String XMLNS_XMLNS =
        "http://www.unidata.ucar.edu/namespaces/netcdf/ncml-2.2";



    /** _more_ */
    public static final String TAG_NETCDF = "netcdf";

    /** _more_ */
    public static final String TAG_VARIABLE = "variable";

    /** _more_ */
    public static final String TAG_ATTRIBUTE = "attribute";

    /** _more_ */
    public static final String TAG_AGGREGATION = "aggregation";

    /** _more_ */
    public static final String TAG_VARIABLEAGG = "variableAgg";

    /** _more_ */
    public static final String ATTR_NAME = "name";

    /** _more_ */
    public static final String ATTR_SHAPE = "shape";

    /** _more_ */
    public static final String ATTR_TYPE = "type";

    /** _more_ */
    public static final String ATTR_VALUE = "value";

    /** _more_ */
    public static final String ATTR_DIMNAME = "dimName";

    /** _more_ */
    public static final String ATTR_COORDVALUE = "coordValue";

    /** _more_ */
    public static final String ATTR_LOCATION = "location";

    /** _more_ */
    public static final String ATTR_ENHANCE = "enhance";

    /** _more_ */
    public static final String ATTR_TIMEUNITSCHANGE = "timeUnitsChange";

}