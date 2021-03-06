/**Generated by running: java org.unavco.projects.gsac.repository.UnavcoGsacDatabaseManager**/

package org.ramadda.repository.database;

import ucar.unidata.sql.SqlUtil;

//J-
public abstract class Tables {
    public abstract String getName();
    public abstract String getColumns();


    public static class ANCESTORS extends Tables {
        public static final String NAME = "ANCESTORS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_ANCESTOR_ID =  NAME + ".ANCESTOR_ID";
        public static final String COL_NODOT_ANCESTOR_ID =   "ANCESTOR_ID";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_ANCESTOR_ID
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final ANCESTORS table  = new  ANCESTORS();
    }



    public static class ASSOCIATIONS extends Tables {
        public static final String NAME = "ASSOCIATIONS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";
        public static final String COL_TYPE =  NAME + ".TYPE";
        public static final String COL_NODOT_TYPE =   "TYPE";
        public static final String COL_FROM_ENTRY_ID =  NAME + ".FROM_ENTRY_ID";
        public static final String COL_NODOT_FROM_ENTRY_ID =   "FROM_ENTRY_ID";
        public static final String COL_TO_ENTRY_ID =  NAME + ".TO_ENTRY_ID";
        public static final String COL_NODOT_TO_ENTRY_ID =   "TO_ENTRY_ID";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_NAME,COL_TYPE,COL_FROM_ENTRY_ID,COL_TO_ENTRY_ID
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final ASSOCIATIONS table  = new  ASSOCIATIONS();
    }



    public static class COMMENTS extends Tables {
        public static final String NAME = "COMMENTS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_ENTRY_ID =  NAME + ".ENTRY_ID";
        public static final String COL_NODOT_ENTRY_ID =   "ENTRY_ID";
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_DATE =  NAME + ".DATE";
        public static final String COL_NODOT_DATE =   "DATE";
        public static final String COL_SUBJECT =  NAME + ".SUBJECT";
        public static final String COL_NODOT_SUBJECT =   "SUBJECT";
        public static final String COL_COMMENT =  NAME + ".COMMENT";
        public static final String COL_NODOT_COMMENT =   "COMMENT";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_ENTRY_ID,COL_USER_ID,COL_DATE,COL_SUBJECT,COL_COMMENT
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final COMMENTS table  = new  COMMENTS();
    }



    public static class DUMMY extends Tables {
        public static final String NAME = "DUMMY";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";

        public static final String[] ARRAY = new String[] {
            COL_NAME
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final DUMMY table  = new  DUMMY();
    }



    public static class ENTRIES extends Tables {
        public static final String NAME = "ENTRIES";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_TYPE =  NAME + ".TYPE";
        public static final String COL_NODOT_TYPE =   "TYPE";
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";
        public static final String COL_DESCRIPTION =  NAME + ".DESCRIPTION";
        public static final String COL_NODOT_DESCRIPTION =   "DESCRIPTION";
        public static final String COL_PARENT_GROUP_ID =  NAME + ".PARENT_GROUP_ID";
        public static final String COL_NODOT_PARENT_GROUP_ID =   "PARENT_GROUP_ID";
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_RESOURCE =  NAME + ".RESOURCE";
        public static final String COL_NODOT_RESOURCE =   "RESOURCE";
        public static final String COL_RESOURCE_TYPE =  NAME + ".RESOURCE_TYPE";
        public static final String COL_NODOT_RESOURCE_TYPE =   "RESOURCE_TYPE";
        public static final String COL_MD5 =  NAME + ".MD5";
        public static final String COL_NODOT_MD5 =   "MD5";
        public static final String COL_FILESIZE =  NAME + ".FILESIZE";
        public static final String COL_NODOT_FILESIZE =   "FILESIZE";
        public static final String COL_DATATYPE =  NAME + ".DATATYPE";
        public static final String COL_NODOT_DATATYPE =   "DATATYPE";
        public static final String COL_CREATEDATE =  NAME + ".CREATEDATE";
        public static final String COL_NODOT_CREATEDATE =   "CREATEDATE";
        public static final String COL_CHANGEDATE =  NAME + ".CHANGEDATE";
        public static final String COL_NODOT_CHANGEDATE =   "CHANGEDATE";
        public static final String COL_FROMDATE =  NAME + ".FROMDATE";
        public static final String COL_NODOT_FROMDATE =   "FROMDATE";
        public static final String COL_TODATE =  NAME + ".TODATE";
        public static final String COL_NODOT_TODATE =   "TODATE";
        public static final String COL_SOUTH =  NAME + ".SOUTH";
        public static final String COL_NODOT_SOUTH =   "SOUTH";
        public static final String COL_NORTH =  NAME + ".NORTH";
        public static final String COL_NODOT_NORTH =   "NORTH";
        public static final String COL_EAST =  NAME + ".EAST";
        public static final String COL_NODOT_EAST =   "EAST";
        public static final String COL_WEST =  NAME + ".WEST";
        public static final String COL_NODOT_WEST =   "WEST";
        public static final String COL_ALTITUDETOP =  NAME + ".ALTITUDETOP";
        public static final String COL_NODOT_ALTITUDETOP =   "ALTITUDETOP";
        public static final String COL_ALTITUDEBOTTOM =  NAME + ".ALTITUDEBOTTOM";
        public static final String COL_NODOT_ALTITUDEBOTTOM =   "ALTITUDEBOTTOM";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_TYPE,COL_NAME,COL_DESCRIPTION,COL_PARENT_GROUP_ID,COL_USER_ID,COL_RESOURCE,COL_RESOURCE_TYPE,COL_MD5,COL_FILESIZE,COL_DATATYPE,COL_CREATEDATE,COL_CHANGEDATE,COL_FROMDATE,COL_TODATE,COL_SOUTH,COL_NORTH,COL_EAST,COL_WEST,COL_ALTITUDETOP,COL_ALTITUDEBOTTOM
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final ENTRIES table  = new  ENTRIES();
    }



    public static class FAVORITES extends Tables {
        public static final String NAME = "FAVORITES";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_ENTRY_ID =  NAME + ".ENTRY_ID";
        public static final String COL_NODOT_ENTRY_ID =   "ENTRY_ID";
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";
        public static final String COL_CATEGORY =  NAME + ".CATEGORY";
        public static final String COL_NODOT_CATEGORY =   "CATEGORY";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_USER_ID,COL_ENTRY_ID,COL_NAME,COL_CATEGORY
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final FAVORITES table  = new  FAVORITES();
    }



    public static class FTP extends Tables {
        public static final String NAME = "FTP";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_SERVER =  NAME + ".SERVER";
        public static final String COL_NODOT_SERVER =   "SERVER";
        public static final String COL_BASEDIR =  NAME + ".BASEDIR";
        public static final String COL_NODOT_BASEDIR =   "BASEDIR";
        public static final String COL_FTPUSER =  NAME + ".FTPUSER";
        public static final String COL_NODOT_FTPUSER =   "FTPUSER";
        public static final String COL_FTPPASSWORD =  NAME + ".FTPPASSWORD";
        public static final String COL_NODOT_FTPPASSWORD =   "FTPPASSWORD";
        public static final String COL_MAXSIZE =  NAME + ".MAXSIZE";
        public static final String COL_NODOT_MAXSIZE =   "MAXSIZE";
        public static final String COL_PATTERN =  NAME + ".PATTERN";
        public static final String COL_NODOT_PATTERN =   "PATTERN";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_SERVER,COL_BASEDIR,COL_FTPUSER,COL_FTPPASSWORD,COL_MAXSIZE,COL_PATTERN
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final FTP table  = new  FTP();
    }



    public static class GLOBALS extends Tables {
        public static final String NAME = "GLOBALS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";
        public static final String COL_VALUE =  NAME + ".VALUE";
        public static final String COL_NODOT_VALUE =   "VALUE";

        public static final String[] ARRAY = new String[] {
            COL_NAME,COL_VALUE
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final GLOBALS table  = new  GLOBALS();
    }



    public static class HARVESTERS extends Tables {
        public static final String NAME = "HARVESTERS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_CLASS =  NAME + ".CLASS";
        public static final String COL_NODOT_CLASS =   "CLASS";
        public static final String COL_CONTENT =  NAME + ".CONTENT";
        public static final String COL_NODOT_CONTENT =   "CONTENT";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_CLASS,COL_CONTENT
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final HARVESTERS table  = new  HARVESTERS();
    }



    public static class JOBINFOS extends Tables {
        public static final String NAME = "JOBINFOS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_ENTRY_ID =  NAME + ".ENTRY_ID";
        public static final String COL_NODOT_ENTRY_ID =   "ENTRY_ID";
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_DATE =  NAME + ".DATE";
        public static final String COL_NODOT_DATE =   "DATE";
        public static final String COL_TYPE =  NAME + ".TYPE";
        public static final String COL_NODOT_TYPE =   "TYPE";
        public static final String COL_JOB_INFO_BLOB =  NAME + ".JOB_INFO_BLOB";
        public static final String COL_NODOT_JOB_INFO_BLOB =   "JOB_INFO_BLOB";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_ENTRY_ID,COL_USER_ID,COL_DATE,COL_TYPE,COL_JOB_INFO_BLOB
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final JOBINFOS table  = new  JOBINFOS();
    }



    public static class LOCALFILES extends Tables {
        public static final String NAME = "LOCALFILES";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_LOCALFILEPATH =  NAME + ".LOCALFILEPATH";
        public static final String COL_NODOT_LOCALFILEPATH =   "LOCALFILEPATH";
        public static final String COL_TIMEDELAY =  NAME + ".TIMEDELAY";
        public static final String COL_NODOT_TIMEDELAY =   "TIMEDELAY";
        public static final String COL_INCLUDEPATTERN =  NAME + ".INCLUDEPATTERN";
        public static final String COL_NODOT_INCLUDEPATTERN =   "INCLUDEPATTERN";
        public static final String COL_EXCLUDEPATTERN =  NAME + ".EXCLUDEPATTERN";
        public static final String COL_NODOT_EXCLUDEPATTERN =   "EXCLUDEPATTERN";
        public static final String COL_NAMING =  NAME + ".NAMING";
        public static final String COL_NODOT_NAMING =   "NAMING";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_LOCALFILEPATH,COL_TIMEDELAY,COL_INCLUDEPATTERN,COL_EXCLUDEPATTERN,COL_NAMING
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final LOCALFILES table  = new  LOCALFILES();
    }



    public static class LOCALREPOSITORIES extends Tables {
        public static final String NAME = "LOCALREPOSITORIES";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_EMAIL =  NAME + ".EMAIL";
        public static final String COL_NODOT_EMAIL =   "EMAIL";
        public static final String COL_STATUS =  NAME + ".STATUS";
        public static final String COL_NODOT_STATUS =   "STATUS";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_EMAIL,COL_STATUS
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final LOCALREPOSITORIES table  = new  LOCALREPOSITORIES();
    }



    public static class METADATA extends Tables {
        public static final String NAME = "METADATA";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_ENTRY_ID =  NAME + ".ENTRY_ID";
        public static final String COL_NODOT_ENTRY_ID =   "ENTRY_ID";
        public static final String COL_TYPE =  NAME + ".TYPE";
        public static final String COL_NODOT_TYPE =   "TYPE";
        public static final String COL_INHERITED =  NAME + ".INHERITED";
        public static final String COL_NODOT_INHERITED =   "INHERITED";
        public static final String COL_ATTR1 =  NAME + ".ATTR1";
        public static final String COL_NODOT_ATTR1 =   "ATTR1";
        public static final String COL_ATTR2 =  NAME + ".ATTR2";
        public static final String COL_NODOT_ATTR2 =   "ATTR2";
        public static final String COL_ATTR3 =  NAME + ".ATTR3";
        public static final String COL_NODOT_ATTR3 =   "ATTR3";
        public static final String COL_ATTR4 =  NAME + ".ATTR4";
        public static final String COL_NODOT_ATTR4 =   "ATTR4";
        public static final String COL_EXTRA =  NAME + ".EXTRA";
        public static final String COL_NODOT_EXTRA =   "EXTRA";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_ENTRY_ID,COL_TYPE,COL_INHERITED,COL_ATTR1,COL_ATTR2,COL_ATTR3,COL_ATTR4,COL_EXTRA
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final METADATA table  = new  METADATA();
    }



    public static class MONITORS extends Tables {
        public static final String NAME = "MONITORS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_MONITOR_ID =  NAME + ".MONITOR_ID";
        public static final String COL_NODOT_MONITOR_ID =   "MONITOR_ID";
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_FROM_DATE =  NAME + ".FROM_DATE";
        public static final String COL_NODOT_FROM_DATE =   "FROM_DATE";
        public static final String COL_TO_DATE =  NAME + ".TO_DATE";
        public static final String COL_NODOT_TO_DATE =   "TO_DATE";
        public static final String COL_ENCODED_OBJECT =  NAME + ".ENCODED_OBJECT";
        public static final String COL_NODOT_ENCODED_OBJECT =   "ENCODED_OBJECT";

        public static final String[] ARRAY = new String[] {
            COL_MONITOR_ID,COL_NAME,COL_USER_ID,COL_FROM_DATE,COL_TO_DATE,COL_ENCODED_OBJECT
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final MONITORS table  = new  MONITORS();
    }



    public static class PERMISSIONS extends Tables {
        public static final String NAME = "PERMISSIONS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ENTRY_ID =  NAME + ".ENTRY_ID";
        public static final String COL_NODOT_ENTRY_ID =   "ENTRY_ID";
        public static final String COL_ACTION =  NAME + ".ACTION";
        public static final String COL_NODOT_ACTION =   "ACTION";
        public static final String COL_ROLE =  NAME + ".ROLE";
        public static final String COL_NODOT_ROLE =   "ROLE";

        public static final String[] ARRAY = new String[] {
            COL_ENTRY_ID,COL_ACTION,COL_ROLE
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final PERMISSIONS table  = new  PERMISSIONS();
    }



    public static class POINTDATAMETADATA extends Tables {
        public static final String NAME = "POINTDATAMETADATA";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_TABLENAME =  NAME + ".TABLENAME";
        public static final String COL_NODOT_TABLENAME =   "TABLENAME";
        public static final String COL_COLUMNNAME =  NAME + ".COLUMNNAME";
        public static final String COL_NODOT_COLUMNNAME =   "COLUMNNAME";
        public static final String COL_COLUMNNUMBER =  NAME + ".COLUMNNUMBER";
        public static final String COL_NODOT_COLUMNNUMBER =   "COLUMNNUMBER";
        public static final String COL_SHORTNAME =  NAME + ".SHORTNAME";
        public static final String COL_NODOT_SHORTNAME =   "SHORTNAME";
        public static final String COL_LONGNAME =  NAME + ".LONGNAME";
        public static final String COL_NODOT_LONGNAME =   "LONGNAME";
        public static final String COL_UNIT =  NAME + ".UNIT";
        public static final String COL_NODOT_UNIT =   "UNIT";
        public static final String COL_VARTYPE =  NAME + ".VARTYPE";
        public static final String COL_NODOT_VARTYPE =   "VARTYPE";

        public static final String[] ARRAY = new String[] {
            COL_TABLENAME,COL_COLUMNNAME,COL_COLUMNNUMBER,COL_SHORTNAME,COL_LONGNAME,COL_UNIT,COL_VARTYPE
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final POINTDATAMETADATA table  = new  POINTDATAMETADATA();
    }



    public static class REMOTESERVERS extends Tables {
        public static final String NAME = "REMOTESERVERS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_URL =  NAME + ".URL";
        public static final String COL_NODOT_URL =   "URL";
        public static final String COL_TITLE =  NAME + ".TITLE";
        public static final String COL_NODOT_TITLE =   "TITLE";
        public static final String COL_DESCRIPTION =  NAME + ".DESCRIPTION";
        public static final String COL_NODOT_DESCRIPTION =   "DESCRIPTION";
        public static final String COL_EMAIL =  NAME + ".EMAIL";
        public static final String COL_NODOT_EMAIL =   "EMAIL";
        public static final String COL_ISREGISTRY =  NAME + ".ISREGISTRY";
        public static final String COL_NODOT_ISREGISTRY =   "ISREGISTRY";
        public static final String COL_SELECTED =  NAME + ".SELECTED";
        public static final String COL_NODOT_SELECTED =   "SELECTED";

        public static final String[] ARRAY = new String[] {
            COL_URL,COL_TITLE,COL_DESCRIPTION,COL_EMAIL,COL_ISREGISTRY,COL_SELECTED
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final REMOTESERVERS table  = new  REMOTESERVERS();
    }



    public static class SERVERREGISTRY extends Tables {
        public static final String NAME = "SERVERREGISTRY";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_URL =  NAME + ".URL";
        public static final String COL_NODOT_URL =   "URL";
        public static final String COL_TITLE =  NAME + ".TITLE";
        public static final String COL_NODOT_TITLE =   "TITLE";
        public static final String COL_DESCRIPTION =  NAME + ".DESCRIPTION";
        public static final String COL_NODOT_DESCRIPTION =   "DESCRIPTION";
        public static final String COL_EMAIL =  NAME + ".EMAIL";
        public static final String COL_NODOT_EMAIL =   "EMAIL";
        public static final String COL_ISREGISTRY =  NAME + ".ISREGISTRY";
        public static final String COL_NODOT_ISREGISTRY =   "ISREGISTRY";

        public static final String[] ARRAY = new String[] {
            COL_URL,COL_TITLE,COL_DESCRIPTION,COL_EMAIL,COL_ISREGISTRY
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final SERVERREGISTRY table  = new  SERVERREGISTRY();
    }



    public static class SESSIONS extends Tables {
        public static final String NAME = "SESSIONS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_SESSION_ID =  NAME + ".SESSION_ID";
        public static final String COL_NODOT_SESSION_ID =   "SESSION_ID";
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_CREATE_DATE =  NAME + ".CREATE_DATE";
        public static final String COL_NODOT_CREATE_DATE =   "CREATE_DATE";
        public static final String COL_LAST_ACTIVE_DATE =  NAME + ".LAST_ACTIVE_DATE";
        public static final String COL_NODOT_LAST_ACTIVE_DATE =   "LAST_ACTIVE_DATE";
        public static final String COL_EXTRA =  NAME + ".EXTRA";
        public static final String COL_NODOT_EXTRA =   "EXTRA";

        public static final String[] ARRAY = new String[] {
            COL_SESSION_ID,COL_USER_ID,COL_CREATE_DATE,COL_LAST_ACTIVE_DATE,COL_EXTRA
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final SESSIONS table  = new  SESSIONS();
    }



    public static class TYPE_COLUMN extends Tables {
        public static final String NAME = "TYPE_COLUMN";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_INDEX =  NAME + ".INDEX";
        public static final String COL_NODOT_INDEX =   "INDEX";
        public static final String COL_COLUMN_NAME =  NAME + ".COLUMN_NAME";
        public static final String COL_NODOT_COLUMN_NAME =   "COLUMN_NAME";
        public static final String COL_DATATYPE =  NAME + ".DATATYPE";
        public static final String COL_NODOT_DATATYPE =   "DATATYPE";
        public static final String COL_ENUMERATION_VALUES =  NAME + ".ENUMERATION_VALUES";
        public static final String COL_NODOT_ENUMERATION_VALUES =   "ENUMERATION_VALUES";
        public static final String COL_PROPERTIES =  NAME + ".PROPERTIES";
        public static final String COL_NODOT_PROPERTIES =   "PROPERTIES";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_INDEX,COL_COLUMN_NAME,COL_DATATYPE,COL_ENUMERATION_VALUES,COL_PROPERTIES
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final TYPE_COLUMN table  = new  TYPE_COLUMN();
    }



    public static class USERROLES extends Tables {
        public static final String NAME = "USERROLES";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_ROLE =  NAME + ".ROLE";
        public static final String COL_NODOT_ROLE =   "ROLE";

        public static final String[] ARRAY = new String[] {
            COL_USER_ID,COL_ROLE
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final USERROLES table  = new  USERROLES();
    }



    public static class USERS extends Tables {
        public static final String NAME = "USERS";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_ID =  NAME + ".ID";
        public static final String COL_NODOT_ID =   "ID";
        public static final String COL_NAME =  NAME + ".NAME";
        public static final String COL_NODOT_NAME =   "NAME";
        public static final String COL_EMAIL =  NAME + ".EMAIL";
        public static final String COL_NODOT_EMAIL =   "EMAIL";
        public static final String COL_QUESTION =  NAME + ".QUESTION";
        public static final String COL_NODOT_QUESTION =   "QUESTION";
        public static final String COL_ANSWER =  NAME + ".ANSWER";
        public static final String COL_NODOT_ANSWER =   "ANSWER";
        public static final String COL_PASSWORD =  NAME + ".PASSWORD";
        public static final String COL_NODOT_PASSWORD =   "PASSWORD";
        public static final String COL_ADMIN =  NAME + ".ADMIN";
        public static final String COL_NODOT_ADMIN =   "ADMIN";
        public static final String COL_LANGUAGE =  NAME + ".LANGUAGE";
        public static final String COL_NODOT_LANGUAGE =   "LANGUAGE";
        public static final String COL_TEMPLATE =  NAME + ".TEMPLATE";
        public static final String COL_NODOT_TEMPLATE =   "TEMPLATE";
        public static final String COL_ISGUEST =  NAME + ".ISGUEST";
        public static final String COL_NODOT_ISGUEST =   "ISGUEST";
        public static final String COL_PROPERTIES =  NAME + ".PROPERTIES";
        public static final String COL_NODOT_PROPERTIES =   "PROPERTIES";

        public static final String[] ARRAY = new String[] {
            COL_ID,COL_NAME,COL_EMAIL,COL_QUESTION,COL_ANSWER,COL_PASSWORD,COL_ADMIN,COL_LANGUAGE,COL_TEMPLATE,COL_ISGUEST,COL_PROPERTIES
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final USERS table  = new  USERS();
    }



    public static class USER_ACTIVITY extends Tables {
        public static final String NAME = "USER_ACTIVITY";

        public String getName() {return NAME;}
        public String getColumns() {return COLUMNS;}
        public static final String COL_USER_ID =  NAME + ".USER_ID";
        public static final String COL_NODOT_USER_ID =   "USER_ID";
        public static final String COL_DATE =  NAME + ".DATE";
        public static final String COL_NODOT_DATE =   "DATE";
        public static final String COL_WHAT =  NAME + ".WHAT";
        public static final String COL_NODOT_WHAT =   "WHAT";
        public static final String COL_EXTRA =  NAME + ".EXTRA";
        public static final String COL_NODOT_EXTRA =   "EXTRA";
        public static final String COL_IPADDRESS =  NAME + ".IPADDRESS";
        public static final String COL_NODOT_IPADDRESS =   "IPADDRESS";

        public static final String[] ARRAY = new String[] {
            COL_USER_ID,COL_DATE,COL_WHAT,COL_EXTRA,COL_IPADDRESS
        };
        public static final String COLUMNS = SqlUtil.comma(ARRAY);
        public static final String NODOT_COLUMNS = SqlUtil.commaNoDot(ARRAY);
        public static final String INSERT =SqlUtil.makeInsert(NAME, NODOT_COLUMNS,SqlUtil.getQuestionMarks(ARRAY.length));
    public static final USER_ACTIVITY table  = new  USER_ACTIVITY();
    }



}
