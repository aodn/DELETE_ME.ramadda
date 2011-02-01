/*
 * Copyright 1997-2010 Unidata Program Center/University Corporation for
 * Atmospheric Research, P.O. Box 3000, Boulder, CO 80307,
 * support@unidata.ucar.edu.
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
 */

package org.ramadda.repository;


import org.ramadda.repository.auth.*;
import ucar.unidata.util.Counter;

import java.lang.reflect.Method;

import java.util.ArrayList;

import java.util.Hashtable;
import java.util.List;


/**
 */

public class ApiMethod {

    /** _more_ */
    public static final String TAG_API = "api";

    /** _more_ */
    public static final String TAG_GROUP = "group";

    /** _more_ */
    public static final String TAG_PROPERTY = "property";


    /** _more_ */
    public static final String ATTR_REQUEST = "request";

    /** _more_ */
    public static final String ATTR_NEEDS_SSL = "needs_ssl";

    /** _more_ */
    public static final String ATTR_CHECKAUTHMETHOD = "checkauthmethod";

    /** _more_ */
    public static final String ATTR_AUTHMETHOD = "authmethod";

    /** _more_ */
    public static final String ATTR_HANDLER = "handler";

    /** _more_ */
    public static final String ATTR_ACTIONS = "actions";

    /** _more_ */
    public static final String ATTR_TOPLEVEL = "toplevel";

    /** _more_ */
    public static final String ATTR_NAME = "name";

    /** _more_ */
    public static final String ATTR_VALUE = "value";

    /** _more_ */
    public static final String ATTR_METHOD = "method";

    /** _more_ */
    public static final String ATTR_ADMIN = "admin";

    /** _more_ */
    public static final String ATTR_CANCACHE = "cancache";

    /** _more_ */
    public static final String ATTR_ISHOME = "ishome";

    /** _more_ */
    public static final String ATTR_HANDLESHEAD = "head";

    /** _more_ */
    private String request;

    /** _more_ */
    private String name;

    /** _more_ */
    private boolean isTopLevel = false;

    /** _more_ */
    private boolean mustBeAdmin = true;


    /** _more_ */
    private RequestHandler requestHandler;

    /** _more_ */
    private Method method;

    /** _more_ */
    private boolean canCache = false;

    /** _more_ */
    private List actions;

    /** _more_ */
    private Repository repository;

    /** _more_ */
    private RequestUrl url;

    /** _more_ */
    private boolean needsSsl = false;

    /** _more_ */
    private boolean checkAuthMethod = false;

    /** _more_ */
    private String authMethod;

    /** _more_ */
    private boolean handlesHead = false;

    /** _more_ */
    private Counter numberOfCalls = new Counter();


    /**
     * _more_
     *
     *
     *
     * @param repository _more_
     * @param requestHandler _more_
     * @param request _more_
     * @param name _more_
     * @param method _more_
     * @param mustBeAdmin _more_
     * @param needsSsl _more_
     * @param authMethod _more_
     * @param checkAuthMethod _more_
     * @param canCache _more_
     * @param isTopLevel _more_
     */
    public ApiMethod(Repository repository, RequestHandler requestHandler,
                     String request, String name, Method method,
                     boolean mustBeAdmin, boolean needsSsl,
                     String authMethod, boolean checkAuthMethod,
                     boolean canCache, boolean isTopLevel) {
        this.repository      = repository;
        this.requestHandler  = requestHandler;
        this.request         = request;
        this.name            = name;
        this.mustBeAdmin     = mustBeAdmin;
        this.needsSsl        = needsSsl;
        this.authMethod      = authMethod;
        this.checkAuthMethod = checkAuthMethod;

        this.method          = method;
        this.canCache        = canCache;
        this.isTopLevel      = isTopLevel;
    }


    /**
     * _more_
     *
     * @return _more_
     */
    public boolean isWildcard() {
        return request.endsWith("/*");
    }


    /**
     * _more_
     *
     * @param request _more_
     * @param repository _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public boolean isRequestOk(Request request, Repository repository)
            throws Exception {
        User user = request.getUser();
        if (mustBeAdmin && !user.getAdmin()) {
            return false;
        }
        if (actions.size() > 0) {
            for (int i = 0; i < actions.size(); i++) {
                if ( !repository.getAccessManager().canDoAction(request,
                        (String) actions.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }




    /**
     * _more_
     *
     * @return _more_
     */
    public String toString() {
        return request;
    }

    /**
     * _more_
     *
     * @param request _more_
     *
     * @return _more_
     *
     * @throws Exception _more_
     */
    public Result invoke(Request request) throws Exception {
        return (Result) getMethod().invoke(requestHandler,
                                           new Object[] { request });
    }


    /**
     * Set the Request property.
     *
     * @param value The new value for Request
     */
    public void setRequest(String value) {
        request = value;
    }

    /**
     * Get the Request property.
     *
     * @return The Request
     */
    public String getRequest() {
        return request;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public RequestUrl getUrl() {
        if (url == null) {
            url = new RequestUrl(repository, request);
        }
        return url;
    }

    /**
     * Set the Method property.
     *
     * @param value The new value for Method
     */
    public void setMethod(Method value) {
        method = value;
    }

    /**
     * Get the Method property.
     *
     * @return The Method
     */
    public Method getMethod() {
        return method;
    }




    /**
     * Set the CanCache property.
     *
     * @param value The new value for CanCache
     */
    public void setCanCache(boolean value) {
        canCache = value;
    }

    /**
     * Get the CanCache property.
     *
     * @return The CanCache
     */
    public boolean getCanCache() {
        return canCache;
    }


    /**
     * Set the Name property.
     *
     * @param value The new value for Name
     */
    public void setName(String value) {
        name = value;
    }

    /**
     * Get the Name property.
     *
     * @return The Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the IsTopLevel property.
     *
     * @param value The new value for IsTopLevel
     */
    public void setIsTopLevel(boolean value) {
        isTopLevel = value;
    }

    /**
     * Get the IsTopLevel property.
     *
     * @return The IsTopLevel
     */
    public boolean getIsTopLevel() {
        return isTopLevel;
    }

    /**
     * Set the MustBeAdmin property.
     *
     * @param value The new value for MustBeAdmin
     */
    public void setMustBeAdmin(boolean value) {
        mustBeAdmin = value;
    }

    /**
     * Get the MustBeAdmin property.
     *
     * @return The MustBeAdmin
     */
    public boolean getMustBeAdmin() {
        return mustBeAdmin;
    }

    /**
     * Set the Actions property.
     *
     * @param value The new value for Actions
     */
    public void setActions(List value) {
        actions = value;
    }

    /**
     * Get the Actions property.
     *
     * @return The Actions
     */
    public List getActions() {
        return actions;
    }


    /**
     * Set the NeedsSsl property.
     *
     * @param value The new value for NeedsSsl
     */
    public void setNeedsSsl(boolean value) {
        this.needsSsl = value;
    }

    /**
     * Get the NeedsSsl property.
     *
     * @return The NeedsSsl
     */
    public boolean getNeedsSsl() {
        return this.needsSsl;
    }

    /**
     * Set the CheckAuthMethod property.
     *
     * @param value The new value for CheckAuthMethod
     */
    public void setCheckAuthMethod(boolean value) {
        this.checkAuthMethod = value;
    }

    /**
     * Get the CheckAuthMethod property.
     *
     * @return The CheckAuthMethod
     */
    public boolean getCheckAuthMethod() {
        return this.checkAuthMethod;
    }

    /**
     * Set the AuthMethod property.
     *
     * @param value The new value for AuthMethod
     */
    public void setAuthMethod(String value) {
        this.authMethod = value;
    }

    /**
     * Get the AuthMethod property.
     *
     * @return The AuthMethod
     */
    public String getAuthMethod() {
        return this.authMethod;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public int getNumberOfCalls() {
        return numberOfCalls.getCount();
    }

    /**
     * _more_
     */
    public void incrNumberOfCalls() {
        numberOfCalls.incr();
    }


}