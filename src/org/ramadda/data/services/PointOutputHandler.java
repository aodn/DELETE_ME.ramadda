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

package org.ramadda.data.services;


import org.ramadda.data.point.*;

import org.ramadda.data.record.*;


import org.ramadda.data.record.*;
import org.ramadda.data.record.filter.*;

import org.ramadda.repository.*;
import org.ramadda.repository.auth.*;
import org.ramadda.repository.job.*;
import org.ramadda.repository.map.*;
import org.ramadda.repository.metadata.*;
import org.ramadda.repository.output.*;
import org.ramadda.repository.type.TypeHandler;
import org.ramadda.util.ColorTable;
import org.ramadda.util.HtmlUtils;
import org.ramadda.util.SelectionRectangle;

import org.ramadda.util.TempDir;

import org.ramadda.util.grid.*;

import org.w3c.dom.*;

import ucar.unidata.data.gis.KmlUtil;


import ucar.unidata.ui.ImageUtils;
import ucar.unidata.util.IOUtil;
import ucar.unidata.util.Misc;
import ucar.unidata.util.StringUtil;
import ucar.unidata.xml.XmlUtil;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import java.awt.image.*;

import java.io.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


import java.util.zip.*;



/**
 */
public class PointOutputHandler extends RecordOutputHandler {

    /** _more_ */
    public static final String ARG_FILLMISSING = "fillmissing";


    /**
     * constructor. This gets called by the Repository via reflection
     * This class is specified in outputhandlers.xml
     *
     *
     * @param repository the repository
     * @param element the xml from outputhandlers.xml
     * @throws Exception on badness
     */
    public PointOutputHandler(Repository repository, Element element)
            throws Exception {
        super(repository, element);
    }


    /**
     * _more_
     *
     * @param request _more_
     * @param entry _more_
     *
     * @return _more_
     */
    public RecordEntry doMakeEntry(Request request, Entry entry) {
        return new PointEntry(this, request, entry);
    }



    /**
     * Not implemented yet. This will get the point index of a given lat/lon
     *
     * @param request request
     * @param entry the entry
     *
     *
     * @return _more_
     * @throws Exception On badness
     */
    public Result outputEntryGetPointIndex(Request request, Entry entry)
            throws Exception {
        //TODO: find the closest index to the lat/lon in the request                                                              
        int          index = 1;
        StringBuffer sb    = new StringBuffer("<result>");
        sb.append("{\"index\":" + index + "}");
        sb.append("</result>");

        return new Result("", sb, "text/xml");
    }



    /**
     * Get the FormHandler property.
     *
     * @return The FormHandler
     */
    public PointFormHandler getPointFormHandler() {
        return (PointFormHandler) super.getFormHandler();
    }



    /**
     * Gets the approximate point count of the given record files. It figures out
     * the  area  of the of the clipping box intersection with each file.
     *
     * @param request request
     * @param subsetEntries entries
     *
     * @return approximate point count in spatial subset
     *
     * @throws Exception On badness
     */
    public long getApproximatePointCount(
            Request request, List<? extends RecordEntry> subsetEntries)
            throws Exception {
        long pointCount = 0;
        storeSession(request);
        double             north     = request.get(ARG_AREA_NORTH, 90.0);
        double             south     = request.get(ARG_AREA_SOUTH, -90.0);
        double             east      = request.get(ARG_AREA_EAST, 180.0);
        double             west      = request.get(ARG_AREA_WEST, -180.0);

        Rectangle2D.Double queryRect = new Rectangle2D.Double(west, south,
                                           east - west, north - south);
        for (RecordEntry entry : subsetEntries) {
            Rectangle2D.Double entryBounds  = entry.getEntry().getBounds();
            Rectangle2D        intersection =
                entryBounds.createIntersection(queryRect);
            double percent =
                (intersection.getWidth() * intersection.getHeight())
                / (entryBounds.getWidth() * entryBounds.getHeight());
            pointCount += (long) (percent * entry.getNumRecords());
        }

        return pointCount;
    }

    /**
     * Checks for  any spatial bounds URL arguments. If defined then only returns
     * the RecordEntry objects that intersect the bounds
     *
     * @param request The request
     * @param recordEntries The entries to process
     *
     * @return spatially subsetting ReordEntry-s
     *
     * @throws Exception On badness
     */
    public List<RecordEntry> doSubsetEntries(
            Request request, List<? extends RecordEntry> recordEntries)
            throws Exception {
        List<RecordEntry>  result  = new ArrayList<RecordEntry>();

        SelectionRectangle theBbox = TypeHandler.getSelectionBounds(request);
        if ( !theBbox.anyDefined()) {
            result.addAll(recordEntries);

            return result;
        }


        storeSession(request);

        theBbox.normalizeLongitude();
        SelectionRectangle[] bboxes = theBbox.splitOnDateLine();

        for (RecordEntry recordEntry : recordEntries) {
            Entry entry = recordEntry.getEntry();
            if ( !entry.hasAreaDefined()) {
                continue;
            }
            for (SelectionRectangle bbox : bboxes) {
                Rectangle2D.Double queryRect = new Rectangle2D.Double(
                                                   bbox.getWest(-180),
                                                   bbox.getSouth(-90),
                                                   bbox.getEast(180)
                                                   - bbox.getWest(
                                                       -180), bbox.getNorth(
                                                       90) - bbox.getSouth(
                                                       -90));
                Rectangle2D.Double entryRect =
                    new Rectangle2D.Double(entry.getWest(), entry.getSouth(),
                                           entry.getEast() - entry.getWest(),
                                           entry.getNorth()
                                           - entry.getSouth());
                if (entryRect.intersects(queryRect)
                        || entryRect.contains(queryRect)
                        || queryRect.contains(entryRect)) {
                    result.add(recordEntry);

                    break;
                }
            }
        }

        return result;
    }



    /**
     * _more_
     *
     * @param request The request
     * @param recordEntries _more_
     * @param bounds _more_
     *
     * @return _more_
     *
     * @throws Exception On badness
     */
    public GridVisitor makeGridVisitor(
            Request request, List<? extends PointEntry> recordEntries,
            Rectangle2D.Double bounds)
            throws Exception {
        int imageWidth  = request.get(ARG_WIDTH, DFLT_WIDTH);
        int imageHeight = request.get(ARG_HEIGHT, DFLT_HEIGHT);

        if ((imageWidth > 2500) || (imageHeight > 2500)) {
            throw new IllegalArgumentException("Too large image dimension: "
                    + imageWidth + " X " + imageHeight);
        }
        //        System.err.println("Grid BOUNDS: " + bounds);

        IdwGrid llg = new IdwGrid(imageWidth, imageHeight, bounds.y,
                                  bounds.x, bounds.y + bounds.height,
                                  bounds.x + bounds.width);

        //        System.err.println("NLAS Request:" + request.getFullUrl());
        //llg.fillValue(Double.NaN);
        //If nothing specified then default to 2 grid cells radius
        if ( !request.defined(ARG_GRID_RADIUS_DEGREES)
                && !request.defined(ARG_GRID_RADIUS_CELLS)) {
            llg.setRadius(0.0);
            llg.setNumCells(2);
        } else {
            //If the user did not change the degrees radius then get the default radius from the bounds
            if (request.getString(ARG_GRID_RADIUS_DEGREES, "").equals(
                    request.getString(ARG_GRID_RADIUS_DEGREES_ORIG, ""))) {
                //                System.err.println("getting default:" +
                //                                   getFormHandler().getDefaultRadiusDegrees(request, bounds));
                llg.setRadius(
                    getFormHandler().getDefaultRadiusDegrees(
                        request, bounds));
            } else {
                //                System.err.println("using arg:" +
                //                                   request.get(ARG_GRID_RADIUS_DEGREES, 0.0));
                llg.setRadius(request.get(ARG_GRID_RADIUS_DEGREES, 0.0));
            }
            llg.setNumCells(request.get(ARG_GRID_RADIUS_CELLS, 0));
        }
        if (llg.getCellIndexDelta() > 100) {
            System.err.println("NLAS bad grid neighborhood size: "
                               + llg.getCellIndexDelta());
            System.err.println("NLAS llg: " + llg);
            System.err.println("NLAS request:" + request.getFullUrl());

            throw new IllegalArgumentException("bad grid neighborhood size: "
                    + llg.getCellIndexDelta());
        }

        GridVisitor visitor = new GridVisitor(this, request, llg);

        return visitor;
    }



    /**
     * _more_
     *
     * @param request The request
     * @param jobId The job ID
     * @param mainEntry Either the  Collection or File Entry
     * @param llg latlongrid
     * @param grid _more_
     * @param missingValue _more_
     * @param fileSuffix _more_
     *
     * @throws Exception On badness
     */
    public void writeAsciiArcGrid(Request request, Object jobId,
                                  Entry mainEntry, IdwGrid llg,
                                  double[][] grid, double missingValue,
                                  String fileSuffix)
            throws Exception {
        boolean     haveMissingValue = !Double.isNaN(missingValue);
        final int   imageWidth       = llg.getWidth();
        final int   imageHeight      = llg.getHeight();
        PrintWriter pw = getPrintWriter(request, jobId, mainEntry,
                                        fileSuffix);
        pw.println("ncols " + imageWidth);
        pw.println("nrows " + imageHeight);
        pw.println("xllcorner " + llg.getWest());
        pw.println("yllcorner " + llg.getSouth());
        pw.println("cellsize "
                   + (llg.getEast() - llg.getWest()) / imageWidth);
        pw.println("nodata_value " + LatLonGrid.GRID_MISSING);
        System.err.println("NLAS: writing ARC ASCII grid " + imageWidth
                           + " X " + imageHeight);
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                double value = grid[y][x];
                if ((value != value) || (value == LatLonGrid.GRID_MISSING)
                        || (haveMissingValue && (value == missingValue))) {
                    value = LatLonGrid.GRID_MISSING;
                }
                pw.print(value);
                pw.print(" ");
            }
            pw.print("\n");
        }
        System.err.println("NLAS: done writing ARC ASCII grid ");
        pw.close();
    }


    /**
     * _more_
     *
     * @param request The request
     * @param imageFile _more_
     * @param llg latlongrid
     * @param grid _more_
     * @param missingValue _more_
     *
     * @throws Exception On badness
     */
    public void writeImage(Request request, File imageFile, LatLonGrid llg,
                           double[][] grid, double missingValue)
            throws Exception {
        int     imageWidth       = llg.getWidth();
        int     imageHeight      = llg.getHeight();

        boolean haveMissingValue = !Double.isNaN(missingValue);
        Color   defaultColor     = Color.CYAN;
        int[]   pixels           = new int[imageWidth * imageHeight];
        int     index            = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixels[index++] = ((0xff << 24)
                                   | (defaultColor.getRed() << 16)
                                   | (defaultColor.getRed() << 8)
                                   | defaultColor.getRed());
            }
        }


        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                double value = grid[y][x];
                if (haveMissingValue && (value == missingValue)) {
                    continue;
                }
                if (Double.isNaN(value)) {
                    continue;
                }
                max = Math.max(max, value);
                min = Math.min(min, value);
            }
        }

        int[][] colorTable =
            ColorTable.getColorTable(request.getString(ARG_COLORTABLE, ""));
        double[] range =
            ColorTable.getRange(request.getString(ARG_COLORTABLE, ""), min,
                                max);
        min   = range[0];
        max   = range[1];
        index = 0;
        double colorRange    = max - min;
        double colorRangeMin = min;
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                double value = grid[y][x];
                if (Double.isNaN(value) || (value == LatLonGrid.GRID_MISSING)
                        || (haveMissingValue && (value == missingValue))) {
                    //Set missing to transparent
                    pixels[index] = (0x00 << 24);
                } else {
                    //TODO: Check range for DBZ exception
                    double percent = (value - colorRangeMin) / colorRange;
                    pixels[index] = ColorTable.getPixelValue(colorTable,
                            percent);
                }
                index++;
            }
        }


        Image newImage = Toolkit.getDefaultToolkit().createImage(
                             new MemoryImageSource(
                                 imageWidth, imageHeight, pixels, 0,
                                 imageWidth));

        ImageUtils.writeImageToFile(newImage, imageFile);
    }


    /**
     * _more_
     *
     * @param request the request
     * @param mainEntry Either the  Collection or File Entry
     * @param entries entries to process
     * @param jobId The job ID
     *
     * @return _more_
     *
     * @throws Exception on badness
     */
    public Result outputEntryKmlTrack(Request request, Entry mainEntry,
                                      List<? extends PointEntry> entries,
                                      Object jobId)
            throws Exception {
        Element root      = KmlUtil.kml(mainEntry.getName() + " Tracks");
        Element topFolder = KmlUtil.folder(root,
                                           mainEntry.getName() + " Tracks",
                                           false);

        for (PointEntry pointEntry : entries) {
            Entry             entry    = pointEntry.getEntry();
            final int[]       pointCnt = { 0 };
            final float[][][] coords   = {
                new float[3][1000]
            };
            RecordVisitor     visitor  = new BridgeRecordVisitor(this) {
                public boolean doVisitRecord(RecordFile file,
                                             VisitInfo visitInfo,
                                             Record record) {
                    PointRecord pointRecord = (PointRecord) record;
                    float[][]   kmlCoords   = coords[0];
                    if (pointCnt[0] >= kmlCoords[0].length) {
                        kmlCoords = coords[0] = Misc.expand(kmlCoords);
                    }
                    kmlCoords[0][pointCnt[0]] =
                        (float) pointRecord.getLatitude();
                    kmlCoords[1][pointCnt[0]] =
                        (float) pointRecord.getLongitude();
                    kmlCoords[2][pointCnt[0]] =
                        (float) pointRecord.getAltitude();
                    pointCnt[0]++;

                    return true;
                }
                public void finished(RecordFile file, VisitInfo visitInfo) {
                    super.finished(file, visitInfo);
                }
            };
            long numRecords = pointEntry.getNumRecords();
            int  skip       = (int) (numRecords / 1000);
            getRecordJobManager().visitSequential(request, pointEntry,
                    visitor, new VisitInfo(true, skip));

            coords[0] = Misc.copy(coords[0], pointCnt[0]);
            Element folder = KmlUtil.folder(topFolder, entry.getName(),
                                            false);
            if (entry.getDescription().length() > 0) {
                KmlUtil.description(folder, entry.getDescription());
            }
            KmlUtil.placemark(folder, "Track", "", coords[0], Color.red, 2);
        }

        PrintWriter pw = getPrintWriter(request, jobId, mainEntry, ".kml");
        XmlUtil.toString(root, pw);
        pw.close();

        return getDummyResult();
    }

    /**
     * gets the bounds of the given entries
     *
     * @param request the request
     * @param entries _more_
     *
     * @return the bounds - north, west,south,east
     */
    public Rectangle2D.Double getBounds(Request request,
                                        List<? extends PointEntry> entries) {


        SelectionRectangle theBbox = TypeHandler.getSelectionBounds(request);
        theBbox.normalizeLongitude();

        //TODO: handle date line
        SelectionRectangle[] bboxes = theBbox.splitOnDateLine();
        double               north  = 0;
        double               south  = 0;
        double               east   = 0;
        double               west   = 0;
        SelectionRectangle   bbox   = bboxes[0];


        for (int i = 0; i < entries.size(); i++) {
            PointEntry pointEntry = entries.get(i);
            Entry      entry      = pointEntry.getEntry();
            double     tmpnorth   = bbox.getNorth(entry.getNorth());
            double     tmpsouth   = bbox.getSouth(entry.getSouth());
            double     tmpeast    = bbox.getEast(entry.getEast());
            double     tmpwest    = bbox.getWest(entry.getWest());
            if (i == 0) {
                north = tmpnorth;
                south = tmpsouth;
                east  = tmpeast;
                west  = tmpwest;
            } else {
                north = Math.max(tmpnorth, north);
                south = Math.min(tmpsouth, south);
                east  = Math.max(tmpeast, east);
                west  = Math.min(tmpwest, west);
            }

            /**
             * north = entry.getNorth();
             * south =  entry.getSouth();
             * east = entry.getEast();
             * west = entry.getWest();
             */

        }





        //TODO: is this right?
        if (Math.abs(north - south) > Math.abs(east - west)) {
            //            east = west + Math.abs(north - south);
        } else {
            //            north = south + Math.abs(east - west);
        }

        //        System.err.println("BOUNDS: lat:" + north +" " + south + " lon:" + west +" " + east);
        return new Rectangle2D.Double(west, north, east - west,
                                      south - north);
    }




}