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

package org.ramadda.repository;


import org.ramadda.repository.output.*;

import org.ramadda.util.HtmlUtils;


import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;

import java.io.File;

import java.util.ArrayList;
import java.util.List;


/**
 * Class FileInfo _more_
 *
 *
 * @author RAMADDA Development Team
 * @version $Revision: 1.30 $
 */
public class Link {

    /** _more_ */
    String url;

    /** _more_ */
    String label;

    /** _more_ */
    String icon;

    /** _more_ */
    protected boolean hr = false;

    /** _more_ */
    int type = OutputType.TYPE_ACTION;

    /** _more_ */
    OutputType outputType;


    /** _more_ */
    String category;

    /**
     * _more_
     *
     * @param hr _more_
     */
    public Link(boolean hr) {
        this.hr = hr;
    }

    /**
     * _more_
     *
     * @param url _more_
     * @param icon _more_
     * @param label _more_
     * @param type _more_
     */
    public Link(String url, String icon, String label, int type) {
        this(url, icon, label, null, type);
    }


    /**
     * _more_
     *
     * @param url _more_
     * @param icon _more_
     * @param label _more_
     */
    public Link(String url, String icon, String label) {
        this(url, icon, label, null);
    }


    /**
     * _more_
     *
     * @param url _more_
     * @param icon _more_
     * @param label _more_
     * @param outputType _more_
     */
    public Link(String url, String icon, String label,
                OutputType outputType) {
        this(url, icon, label, outputType, getLinkType(outputType));
    }



    /**
     * _more_
     *
     * @param url _more_
     * @param icon _more_
     * @param label _more_
     * @param outputType _more_
     * @param linkType _more_
     */
    public Link(String url, String icon, String label, OutputType outputType,
                int linkType) {
        this.url        = url;
        this.label      = label;
        this.icon       = icon;
        this.outputType = outputType;
        this.type       = linkType;
    }

    /**
     * _more_
     *
     * @param typeMask _more_
     *
     * @return _more_
     */
    public boolean isType(int typeMask) {
        return (getType() & typeMask) != 0;
    }

    /**
     * _more_
     *
     * @param type _more_
     */
    public void setLinkType(int type) {
        this.type = type;
    }

    /**
     * _more_
     *
     * @param outputType _more_
     *
     * @return _more_
     */
    public static int getLinkType(OutputType outputType) {
        if (outputType == null) {
            return OutputType.TYPE_ACTION;
        }

        return outputType.getType();
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public int getType() {
        return type;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public OutputType getOutputType() {
        return outputType;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public boolean getHr() {
        return hr;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public String toString() {
        if (hr) {
            return "<hr>";
        }
        if (icon == null) {
            return HtmlUtils.href(url, label);
        }

        return HtmlUtils.href(url, HtmlUtils.img(icon, label));
    }


    /**
     * Set the Url property.
     *
     * @param value The new value for Url
     */
    public void setUrl(String value) {
        url = value;
    }

    /**
     * Get the Url property.
     *
     * @return The Url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the Label property.
     *
     * @param value The new value for Label
     */
    public void setLabel(String value) {
        label = value;
    }

    /**
     * Get the Label property.
     *
     * @return The Label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the Icon property.
     *
     * @param value The new value for Icon
     */
    public void setIcon(String value) {
        icon = value;
    }

    /**
     * Get the Icon property.
     *
     * @return The Icon
     */
    public String getIcon() {
        return icon;
    }



}
