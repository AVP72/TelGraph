package com.pikarevsoft.telgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.pikarevsoft.telgraph.Public.checkGraph;
import static com.pikarevsoft.telgraph.Public.colorY0;
import static com.pikarevsoft.telgraph.Public.colorY1;
import static com.pikarevsoft.telgraph.Public.colorsFile;
import static com.pikarevsoft.telgraph.Public.columnsFile;
import static com.pikarevsoft.telgraph.Public.namesFile;
import static com.pikarevsoft.telgraph.Public.nameY0;
import static com.pikarevsoft.telgraph.Public.nameY1;
import static com.pikarevsoft.telgraph.Public.showY0;
import static com.pikarevsoft.telgraph.Public.showY1;
import static com.pikarevsoft.telgraph.Public.startX;
import static com.pikarevsoft.telgraph.Public.valueX;
import static com.pikarevsoft.telgraph.Public.valueXEnd;
import static com.pikarevsoft.telgraph.Public.valueXStart;
import static com.pikarevsoft.telgraph.Public.valueY0;
import static com.pikarevsoft.telgraph.Public.valueY1;
import static com.pikarevsoft.telgraph.Public.xEnd;
import static com.pikarevsoft.telgraph.Public.xStart;
import static com.pikarevsoft.telgraph.Public.y0File;
import static com.pikarevsoft.telgraph.Public.y1File;

public class StartGraph extends View {

    Paint paintRectGray, paintLineMove, paintY0, paintY1;
    int width, height;
    float widthShowRect = startX;
    Context context;
    final float widthLine = 25;
    float widthStart, widthEnd, widthMin;

    public StartGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        paintRectGray = new Paint();
        paintLineMove = new Paint();
        paintY0 = new Paint();
        paintY1 = new Paint();

        readFile();

        setPaint();

    }

    public void setPaint(){
        paintRectGray.setColor(Color.parseColor("#e9e9e9"));
        paintRectGray.setStrokeWidth(1);
        paintLineMove.setColor(Color.GRAY);
        paintLineMove.setStrokeWidth(2);
        paintY0.setColor(Color.parseColor(colorY0));
        paintY0.setStrokeWidth(3);
        paintY1.setColor(Color.parseColor(colorY1));
        paintY1.setStrokeWidth(3);
    }

    long minus;
    float kfX;

    @Override
    protected void onDraw(Canvas canvas) {

        width = getWidth();
        height = getHeight();

        widthStart = width * 0.05f;
        widthEnd = width * 0.05f;
        widthMin = width * 0.25f;
        if (widthShowRect == startX) widthShowRect = widthMin;
        if (xStart == startX) xStart = width - widthShowRect;
        if (xEnd == startX) xEnd = width;

        canvas.drawRect(0, 0, xStart, height, paintRectGray);
        canvas.drawRect(xEnd, 0, width, height, paintRectGray);

        paintLineMove.setStrokeWidth(2);
        canvas.drawLine(xStart, 0, xEnd, 0, paintLineMove);
        canvas.drawLine(xStart, height, xEnd, height, paintLineMove);

        paintLineMove.setStrokeWidth(widthLine);
        canvas.drawLine(xStart+widthLine/2, 0, xStart+widthLine/2, height, paintLineMove);
        canvas.drawLine(xEnd-widthLine/2, 0, xEnd-widthLine/2, height, paintLineMove);

        int n = valueX.length;
        minus = valueX[0];
        kfX = (float) ((valueX[n-1] - minus)/width);
        calcValueX();

        int maxY=Integer.MIN_VALUE;

        if (showY0) {
            for (int y : valueY0) {
                if (y > maxY) maxY = y;
            }
        }
        if (showY1) {
            for (int y : valueY1) {
                if (y > maxY) maxY = y;
            }
        }
        if (maxY == Integer.MIN_VALUE) return;

        float kfY = (float)maxY/(float)height;

        float beforeX = (valueX[0] - minus)/kfX;
        float beforeY0 = height - (float) valueY0[0]/kfY;
        float beforeY1 = height - (float) valueY1[0]/kfY;

        for (int i=1; i<n; i++){

            float lineX = (valueX[i-1] - minus)/kfX;
            float lineY0 = height - (float) valueY0[i-1]/kfY;
            float lineY1 = height - (float) valueY1[i-1]/kfY;
            if (showY0) canvas.drawLine(beforeX, beforeY0, lineX, lineY0, paintY0);
            if (showY1) canvas.drawLine(beforeX, beforeY1, lineX, lineY1, paintY1);

            beforeX = lineX;
            beforeY0 = lineY0;
            beforeY1 = lineY1;
        }

    }

    float offsetStart = 0f;
    float offsetEnd = 0f;
    boolean moveStart = false, moveEnd = false, moveHalf = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float x = event.getX();
//        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (x > xStart + widthStart && x < xEnd - widthEnd){
                    offsetStart = xStart - x;
                    offsetEnd = xEnd - x;
                    moveHalf = true;
                }
                if (x >= xStart - widthStart && x <= xStart + widthStart) {
                    offsetStart = xStart - x;
                    moveStart = true;
                }
                if (x >= xEnd - widthEnd && x <= xEnd + widthEnd) {
                    offsetEnd = xEnd - x;
                    moveEnd = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                // перемещение
                if (x > xStart + widthStart && x < xEnd - widthEnd && !moveStart && !moveEnd && moveHalf) {
                    xStart = x + offsetStart;
                    xEnd = x + offsetEnd;
                    if (xStart < 0) {
                        xStart = 0;
                        xEnd = xStart + widthShowRect;
                    }
                    if (xEnd > width) {
                        xEnd = width;
                        xStart = xEnd - widthShowRect;
                    }
                }

                // левый край
                if (x >= xStart - widthStart && x <= xStart + widthStart && moveStart) {
                    xStart = x + offsetStart;
                    if (xStart < 0) xStart = 0;
                    if (xStart > xEnd - widthMin) xStart = xEnd - widthMin;
                }

                // правый край
                if (x >= xEnd - widthEnd && x <= xEnd + widthEnd && moveEnd) {
                    xEnd = x + offsetEnd;
                    if (xEnd > width)  xEnd = width;
                    if (xEnd < xStart + widthMin) xEnd = xStart + widthMin;

                }

                widthShowRect = xEnd - xStart;

                invalidate();

                calcValueX();

                break;
            case MotionEvent.ACTION_UP:
                moveStart = moveEnd = moveHalf = false;
                break;
        }
        return true;
    }

    void calcValueX(){
        valueXStart = (long) (kfX * xStart + minus);
        valueXEnd = (long) (kfX * xEnd + minus);
    }

    public void readFile(){

        String dateStr = "";
        try {
            dateStr = readText(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray array;
        try {
            array = new JSONArray(dateStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        int n = array.length();
        JSONObject[] gr = new JSONObject[n];
        JSONArray x;
        JSONArray y0;
        JSONArray y1;
        JSONObject names;
        JSONObject colors;
        try {
            for (int i=0; i<array.length();i++) {
                gr[i] = array.getJSONObject(i);
                if (i==checkGraph){
                    x = gr[i].getJSONArray(columnsFile).getJSONArray(0);
                    y0 = gr[i].getJSONArray(columnsFile).getJSONArray(1);
                    y1 = gr[i].getJSONArray(columnsFile).getJSONArray(2);
                    names = gr[i].getJSONObject(namesFile);
                    nameY0 = names.getString(y0File);
                    nameY1 = names.getString(y1File);
                    colors = gr[i].getJSONObject(colorsFile);
                    colorY0 = colors.getString(y0File);
                    colorY1 = colors.getString(y1File);


                    int n0 = x.length()-1;
                    valueX = new long[n0];
                    valueY0 = new int[n0];
                    valueY1 = new int[n0];

                    for (int k=0; k<n0; k++){
                        valueX[k] = x.getLong(k+1);
                    }
                    for (int k=0; k<n0; k++){
                        valueY0[k] = y0.getInt(k+1);
                    }
                    for (int k=0; k<n0; k++){
                        valueY1[k] = y1.getInt(k+1);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String readText(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.data);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        StringBuilder sb= new StringBuilder();
        String s;
        while((  s = br.readLine())!=null)  sb.append(s);
        return sb.toString();
    }

}
