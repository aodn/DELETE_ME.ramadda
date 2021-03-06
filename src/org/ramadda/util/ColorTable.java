/*
 * Copyright 2010 UNAVCO, 6350 Nautilus Drive, Boulder, CO 80301
 * http://www.unavco.org
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

package org.ramadda.util;


import ucar.unidata.util.TwoFacedObject;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * @author Jeff McWhirter
 * @version $Revision: 1.3 $
 */
public class ColorTable {

    /** _more_ */
    public static final String COLORTABLE_TOPOGRAPHIC = "TOPOGRAPHIC";

    /** _more_ */
    public static final String COLORTABLE_GRAYSCALE = "GRAYSCALE";

    /** _more_ */
    public static final String COLORTABLE_LASCLASSIFICATION =
        "LASCLASSIFICATION";


    /**
     * get the Color by finding the index of the percent along the table
     *
     * @param table color table
     * @param percent percent into color table (0-1)
     *
     * @return the color
     */
    public static Color getColor(int[][] table, double percent) {
        int index = (int) (percent * table.length) - 1;
        if (index < 0) {
            index = 0;
        }
        if (index >= table.length) {
            index = table.length - 1;
        }
        int[] c = table[index];
        return new Color(c[0], c[1], c[2]);
    }


    /**
     * get the pixel value in the table from the percent
     *
     * @param table the table
     * @param percent the percent
     *
     * @return the pixel value
     */
    public static int getPixelValue(int[][] table, double percent) {
        Color c = getColor(table, percent);
        return ((0xff << 24) | (c.getRed() << 16) | (c.getGreen() << 8)
                | c.getBlue());
    }

    /**
     * get the fixed set of color table names"
     *
     * @return color table names
     */
    public static List getColorTableNames() {
        List l = new ArrayList();
        l.add(new TwoFacedObject("Gray Scale", COLORTABLE_GRAYSCALE));
        l.add(new TwoFacedObject("Topographic", COLORTABLE_TOPOGRAPHIC));
        l.add(new TwoFacedObject("LAS Classification",
                                 COLORTABLE_LASCLASSIFICATION));
        return l;
    }

    /**
     * look up the color table from its name
     *
     * @param name table name
     *
     * @return table
     */
    public static int[][] getColorTable(String name) {
        if (name == null) {
            return GRAYSCALE;
        }
        if (name.equals(COLORTABLE_GRAYSCALE)) {
            return GRAYSCALE;
        }
        if (name.equals(COLORTABLE_TOPOGRAPHIC)) {
            return TOPOGRAPHIC;
        }
        if (name.equals(COLORTABLE_LASCLASSIFICATION)) {
            return LASCLASSIFICATION;
        }
        return GRAYSCALE;
    }

    /**
     * _more_
     *
     * @param name _more_
     * @param min _more_
     * @param max _more_
     *
     * @return _more_
     */
    public static double[] getRange(String name, double min, double max) {
        if (name.equals(COLORTABLE_LASCLASSIFICATION)) {
            return new double[] { 0, 31 };
        }
        return new double[] { min, max };
    }

    /** fixed gray scale color table */
    public static final int[][] LASCLASSIFICATION = {
        { 255, 255, 255 }, { 255, 255, 255 }, { 252, 153, 4 }, { 32, 55, 1 },
        { 81, 138, 3 }, { 147, 251, 4 }, { 84, 60, 3 }, { 134, 133, 132 },
        { 134, 133, 132 }, { 2, 127, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
        { 246, 247, 249 }, { 246, 247, 249 }, { 246, 247, 249 },
    };


    /** fixed gray scale color table */
    public static final int[][] GRAYSCALE = {
        { 0, 0, 0 }, { 1, 1, 1 }, { 2, 2, 2 }, { 3, 3, 3 }, { 4, 4, 4 },
        { 5, 5, 5 }, { 6, 6, 6 }, { 7, 7, 7 }, { 8, 8, 8 }, { 9, 9, 9 },
        { 10, 10, 10 }, { 11, 11, 11 }, { 12, 12, 12 }, { 13, 13, 13 },
        { 14, 14, 14 }, { 15, 15, 15 }, { 16, 16, 16 }, { 17, 17, 17 },
        { 18, 18, 18 }, { 19, 19, 19 }, { 20, 20, 20 }, { 21, 21, 21 },
        { 22, 22, 22 }, { 23, 23, 23 }, { 24, 24, 24 }, { 25, 25, 25 },
        { 26, 26, 26 }, { 27, 27, 27 }, { 28, 28, 28 }, { 29, 29, 29 },
        { 30, 30, 30 }, { 31, 31, 31 }, { 32, 32, 32 }, { 33, 33, 33 },
        { 34, 34, 34 }, { 35, 35, 35 }, { 36, 36, 36 }, { 37, 37, 37 },
        { 38, 38, 38 }, { 39, 39, 39 }, { 40, 40, 40 }, { 41, 41, 41 },
        { 42, 42, 42 }, { 43, 43, 43 }, { 44, 44, 44 }, { 45, 45, 45 },
        { 46, 46, 46 }, { 47, 47, 47 }, { 48, 48, 48 }, { 49, 49, 49 },
        { 50, 50, 50 }, { 51, 51, 51 }, { 52, 52, 52 }, { 53, 53, 53 },
        { 54, 54, 54 }, { 55, 55, 55 }, { 56, 56, 56 }, { 57, 57, 57 },
        { 58, 58, 58 }, { 59, 59, 59 }, { 60, 60, 60 }, { 61, 61, 61 },
        { 62, 62, 62 }, { 63, 63, 63 }, { 64, 64, 64 }, { 65, 65, 65 },
        { 66, 66, 66 }, { 67, 67, 67 }, { 68, 68, 68 }, { 69, 69, 69 },
        { 70, 70, 70 }, { 71, 71, 71 }, { 72, 72, 72 }, { 73, 73, 73 },
        { 74, 74, 74 }, { 75, 75, 75 }, { 76, 76, 76 }, { 77, 77, 77 },
        { 78, 78, 78 }, { 79, 79, 79 }, { 80, 80, 80 }, { 81, 81, 81 },
        { 82, 82, 82 }, { 83, 83, 83 }, { 84, 84, 84 }, { 85, 85, 85 },
        { 86, 86, 86 }, { 87, 87, 87 }, { 88, 88, 88 }, { 89, 89, 89 },
        { 90, 90, 90 }, { 91, 91, 91 }, { 92, 92, 92 }, { 93, 93, 93 },
        { 94, 94, 94 }, { 95, 95, 95 }, { 96, 96, 96 }, { 97, 97, 97 },
        { 98, 98, 98 }, { 99, 99, 99 }, { 100, 100, 100 }, { 101, 101, 101 },
        { 102, 102, 102 }, { 103, 103, 103 }, { 104, 104, 104 },
        { 105, 105, 105 }, { 106, 106, 106 }, { 107, 107, 107 },
        { 108, 108, 108 }, { 109, 109, 109 }, { 110, 110, 110 },
        { 111, 111, 111 }, { 112, 112, 112 }, { 113, 113, 113 },
        { 114, 114, 114 }, { 115, 115, 115 }, { 116, 116, 116 },
        { 117, 117, 117 }, { 118, 118, 118 }, { 119, 119, 119 },
        { 120, 120, 120 }, { 121, 121, 121 }, { 122, 122, 122 },
        { 123, 123, 123 }, { 124, 124, 124 }, { 125, 125, 125 },
        { 126, 126, 126 }, { 127, 127, 127 }, { 128, 128, 128 },
        { 129, 129, 129 }, { 130, 130, 130 }, { 131, 131, 131 },
        { 132, 132, 132 }, { 133, 133, 133 }, { 134, 134, 134 },
        { 135, 135, 135 }, { 136, 136, 136 }, { 137, 137, 137 },
        { 138, 138, 138 }, { 139, 139, 139 }, { 140, 140, 140 },
        { 141, 141, 141 }, { 142, 142, 142 }, { 143, 143, 143 },
        { 144, 144, 144 }, { 145, 145, 145 }, { 146, 146, 146 },
        { 147, 147, 147 }, { 148, 148, 148 }, { 149, 149, 149 },
        { 150, 150, 150 }, { 151, 151, 151 }, { 152, 152, 152 },
        { 153, 153, 153 }, { 154, 154, 154 }, { 155, 155, 155 },
        { 156, 156, 156 }, { 157, 157, 157 }, { 158, 158, 158 },
        { 159, 159, 159 }, { 160, 160, 160 }, { 161, 161, 161 },
        { 162, 162, 162 }, { 163, 163, 163 }, { 164, 164, 164 },
        { 165, 165, 165 }, { 166, 166, 166 }, { 167, 167, 167 },
        { 168, 168, 168 }, { 169, 169, 169 }, { 170, 170, 170 },
        { 171, 171, 171 }, { 172, 172, 172 }, { 173, 173, 173 },
        { 174, 174, 174 }, { 175, 175, 175 }, { 176, 176, 176 },
        { 177, 177, 177 }, { 178, 178, 178 }, { 179, 179, 179 },
        { 180, 180, 180 }, { 181, 181, 181 }, { 182, 182, 182 },
        { 183, 183, 183 }, { 184, 184, 184 }, { 185, 185, 185 },
        { 186, 186, 186 }, { 187, 187, 187 }, { 188, 188, 188 },
        { 189, 189, 189 }, { 190, 190, 190 }, { 191, 191, 191 },
        { 192, 192, 192 }, { 193, 193, 193 }, { 194, 194, 194 },
        { 195, 195, 195 }, { 196, 196, 196 }, { 197, 197, 197 },
        { 198, 198, 198 }, { 199, 199, 199 }, { 200, 200, 200 },
        { 201, 201, 201 }, { 202, 202, 202 }, { 203, 203, 203 },
        { 204, 204, 204 }, { 205, 205, 205 }, { 206, 206, 206 },
        { 207, 207, 207 }, { 208, 208, 208 }, { 209, 209, 209 },
        { 210, 210, 210 }, { 211, 211, 211 }, { 212, 212, 212 },
        { 213, 213, 213 }, { 214, 214, 214 }, { 215, 215, 215 },
        { 216, 216, 216 }, { 217, 217, 217 }, { 218, 218, 218 },
        { 219, 219, 219 }, { 220, 220, 220 }, { 221, 221, 221 },
        { 222, 222, 222 }, { 223, 223, 223 }, { 224, 224, 224 },
        { 225, 225, 225 }, { 226, 226, 226 }, { 227, 227, 227 },
        { 228, 228, 228 }, { 229, 229, 229 }, { 230, 230, 230 },
        { 231, 231, 231 }, { 232, 232, 232 }, { 233, 233, 233 },
        { 234, 234, 234 }, { 235, 235, 235 }, { 236, 236, 236 },
        { 237, 237, 237 }, { 238, 238, 238 }, { 239, 239, 239 },
        { 240, 240, 240 }, { 241, 241, 241 }, { 242, 242, 242 },
        { 243, 243, 243 }, { 244, 244, 244 }, { 245, 245, 245 },
        { 246, 246, 246 }, { 247, 247, 247 }, { 248, 248, 248 },
        { 249, 249, 249 }, { 250, 250, 250 }, { 251, 251, 251 },
        { 252, 252, 252 }, { 253, 253, 253 }, { 254, 254, 254 },
        { 255, 255, 255 },
    };


    /** fixed topographic color table */
    public static final int[][] TOPOGRAPHIC = {
        { 25, 25, 255 }, { 20, 170, 42 }, { 20, 170, 42 }, { 27, 174, 35 },
        { 35, 179, 28 }, { 43, 184, 22 }, { 51, 188, 15 }, { 59, 193, 9 },
        { 67, 198, 2 }, { 70, 200, 0 }, { 71, 199, 0 }, { 72, 199, 1 },
        { 73, 198, 1 }, { 74, 198, 2 }, { 75, 197, 2 }, { 76, 197, 3 },
        { 78, 197, 3 }, { 79, 196, 4 }, { 80, 196, 4 }, { 81, 195, 5 },
        { 82, 195, 5 }, { 83, 194, 6 }, { 85, 194, 6 }, { 86, 194, 7 },
        { 87, 193, 7 }, { 88, 193, 8 }, { 89, 192, 8 }, { 90, 192, 9 },
        { 92, 191, 9 }, { 93, 191, 10 }, { 94, 191, 10 }, { 95, 190, 11 },
        { 96, 190, 11 }, { 97, 189, 12 }, { 98, 189, 12 }, { 100, 188, 13 },
        { 101, 188, 13 }, { 102, 188, 14 }, { 103, 187, 14 },
        { 104, 187, 15 }, { 105, 186, 15 }, { 107, 186, 16 },
        { 108, 185, 16 }, { 109, 185, 17 }, { 110, 185, 17 },
        { 111, 184, 18 }, { 112, 184, 18 }, { 114, 183, 19 },
        { 115, 183, 19 }, { 116, 182, 20 }, { 117, 182, 21 },
        { 118, 182, 21 }, { 119, 181, 22 }, { 120, 181, 22 },
        { 122, 180, 23 }, { 123, 180, 23 }, { 124, 179, 24 },
        { 125, 179, 24 }, { 126, 179, 25 }, { 127, 178, 25 },
        { 129, 178, 26 }, { 130, 177, 26 }, { 131, 177, 27 },
        { 132, 176, 27 }, { 133, 176, 28 }, { 134, 176, 28 },
        { 136, 175, 29 }, { 137, 175, 29 }, { 138, 174, 30 },
        { 139, 174, 30 }, { 140, 173, 31 }, { 141, 173, 31 },
        { 143, 173, 32 }, { 144, 172, 32 }, { 145, 172, 33 },
        { 146, 171, 33 }, { 147, 171, 34 }, { 148, 170, 34 },
        { 149, 170, 35 }, { 151, 170, 35 }, { 152, 169, 36 },
        { 153, 169, 36 }, { 154, 168, 37 }, { 155, 168, 37 },
        { 156, 167, 38 }, { 158, 167, 38 }, { 159, 167, 39 },
        { 160, 166, 39 }, { 161, 166, 40 }, { 162, 165, 40 },
        { 163, 165, 41 }, { 165, 165, 42 }, { 165, 165, 42 },
        { 165, 165, 43 }, { 165, 165, 44 }, { 165, 165, 45 },
        { 166, 166, 46 }, { 166, 166, 47 }, { 166, 166, 48 },
        { 166, 166, 49 }, { 166, 166, 50 }, { 167, 167, 51 },
        { 167, 167, 52 }, { 167, 167, 53 }, { 167, 167, 54 },
        { 167, 167, 55 }, { 168, 168, 56 }, { 168, 168, 57 },
        { 168, 168, 58 }, { 168, 168, 59 }, { 169, 169, 60 },
        { 169, 169, 61 }, { 169, 169, 62 }, { 169, 169, 63 },
        { 169, 169, 64 }, { 170, 170, 65 }, { 170, 170, 66 },
        { 170, 170, 67 }, { 170, 170, 68 }, { 170, 170, 68 },
        { 171, 171, 69 }, { 171, 171, 70 }, { 171, 171, 71 },
        { 171, 171, 72 }, { 172, 172, 73 }, { 172, 172, 74 },
        { 172, 172, 75 }, { 172, 172, 76 }, { 172, 172, 77 },
        { 173, 173, 78 }, { 173, 173, 79 }, { 173, 173, 80 },
        { 173, 173, 81 }, { 173, 173, 82 }, { 174, 174, 83 },
        { 174, 174, 84 }, { 174, 174, 85 }, { 174, 174, 86 },
        { 175, 175, 87 }, { 175, 175, 88 }, { 175, 175, 89 },
        { 175, 175, 90 }, { 175, 175, 91 }, { 176, 176, 92 },
        { 176, 176, 93 }, { 176, 176, 94 }, { 176, 176, 95 },
        { 176, 176, 95 }, { 177, 177, 96 }, { 177, 177, 97 },
        { 177, 177, 98 }, { 177, 177, 99 }, { 178, 178, 100 },
        { 178, 178, 101 }, { 178, 178, 102 }, { 178, 178, 103 },
        { 178, 178, 104 }, { 179, 179, 105 }, { 179, 179, 106 },
        { 179, 179, 107 }, { 179, 179, 108 }, { 179, 179, 109 },
        { 180, 180, 110 }, { 180, 180, 111 }, { 180, 180, 112 },
        { 180, 180, 113 }, { 181, 181, 114 }, { 181, 181, 115 },
        { 181, 181, 116 }, { 181, 181, 117 }, { 181, 181, 118 },
        { 182, 182, 119 }, { 182, 182, 120 }, { 182, 182, 121 },
        { 182, 182, 121 }, { 182, 182, 122 }, { 183, 183, 123 },
        { 183, 183, 124 }, { 183, 183, 125 }, { 183, 183, 126 },
        { 184, 184, 127 }, { 184, 184, 128 }, { 184, 184, 129 },
        { 184, 184, 130 }, { 184, 184, 131 }, { 185, 185, 132 },
        { 185, 185, 133 }, { 185, 185, 134 }, { 185, 185, 135 },
        { 185, 185, 136 }, { 186, 186, 137 }, { 186, 186, 138 },
        { 186, 186, 139 }, { 186, 186, 140 }, { 186, 186, 141 },
        { 187, 187, 142 }, { 187, 187, 143 }, { 187, 187, 144 },
        { 187, 187, 145 }, { 188, 188, 146 }, { 188, 188, 147 },
        { 188, 188, 148 }, { 188, 188, 148 }, { 188, 188, 149 },
        { 189, 189, 150 }, { 189, 189, 151 }, { 189, 189, 152 },
        { 189, 189, 153 }, { 189, 189, 154 }, { 190, 190, 155 },
        { 190, 190, 156 }, { 190, 190, 157 }, { 190, 190, 158 },
        { 191, 191, 159 }, { 191, 191, 160 }, { 191, 191, 161 },
        { 191, 191, 162 }, { 191, 191, 163 }, { 192, 192, 164 },
        { 192, 192, 165 }, { 192, 192, 166 }, { 192, 192, 167 },
        { 192, 192, 168 }, { 193, 193, 169 }, { 193, 193, 170 },
        { 193, 193, 171 }, { 193, 193, 172 }, { 194, 194, 173 },
        { 194, 194, 174 }, { 194, 194, 175 }, { 194, 194, 175 },
        { 194, 194, 176 }, { 195, 195, 177 }, { 195, 195, 178 },
        { 195, 195, 179 }, { 195, 195, 180 }, { 195, 195, 181 },
        { 196, 196, 182 }, { 196, 196, 183 }, { 196, 196, 184 },
        { 196, 196, 185 }, { 197, 197, 186 }, { 197, 197, 187 },
        { 197, 197, 188 }, { 197, 197, 189 }, { 197, 197, 190 },
        { 198, 198, 191 }, { 198, 198, 192 }, { 198, 198, 193 },
        { 198, 198, 194 }, { 198, 198, 195 }, { 199, 199, 196 },
        { 199, 199, 197 }, { 199, 199, 198 }, { 199, 199, 199 },
        { 255, 255, 255 },
    };

}
