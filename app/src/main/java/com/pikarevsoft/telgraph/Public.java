package com.pikarevsoft.telgraph;

public class Public {

    static final String columnsFile = "columns";
    static final String namesFile = "names";
    static final String colorsFile = "colors";
    static final String y0File = "y0";
    static final String y1File = "y1";

    static long[] valueX;
    static int[] valueY0;
    static int[] valueY1;
    static String nameY0;
    static String nameY1;
    static String colorY0;
    static String colorY1;

    static long valueXStart = 0L;
    static long valueXEnd = 0L;

    public static final float startX = -1000f;
    static float xStart = startX, xEnd = startX, yStart, yEnd;

    static boolean showY0 = true;
    static boolean showY1 = true;

    static boolean showLine = false;
    static float xSurf;

    static int checkGraph = 0;

}
