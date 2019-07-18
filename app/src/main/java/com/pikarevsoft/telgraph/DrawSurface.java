package com.pikarevsoft.telgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.pikarevsoft.telgraph.Public.colorY0;
import static com.pikarevsoft.telgraph.Public.colorY1;
import static com.pikarevsoft.telgraph.Public.nameY0;
import static com.pikarevsoft.telgraph.Public.nameY1;
import static com.pikarevsoft.telgraph.Public.showLine;
import static com.pikarevsoft.telgraph.Public.showY0;
import static com.pikarevsoft.telgraph.Public.showY1;
import static com.pikarevsoft.telgraph.Public.valueX;
import static com.pikarevsoft.telgraph.Public.valueXEnd;
import static com.pikarevsoft.telgraph.Public.valueXStart;
import static com.pikarevsoft.telgraph.Public.valueY0;
import static com.pikarevsoft.telgraph.Public.valueY1;
import static com.pikarevsoft.telgraph.Public.xSurf;

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread drawThread;

    public DrawSurface(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        drawThread = new DrawThread(getHolder(), width, height);
        drawThread.setRunning(true);
        drawThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                toLog(""+e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        xSurf = event.getX();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                showLine = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                showLine = false;
                break;
        }
        return true;
    }

    void toLog(String s){
        if (s == null) s = "null";
        Log.i("avp", ""+s);
    }

}

class DrawThread extends Thread{

    private boolean running = false;
    private SurfaceHolder surfaceHolder;
    int width, height;
    private Paint paintY0, paintY1, paintLineGrid, paintLine, paintRadiusInto;

    DrawThread(SurfaceHolder surfaceHolder, int width, int height) {
        this.surfaceHolder = surfaceHolder;
        this.width = width;
        this.height = height;
        paintY0 = new Paint();
        paintY1 = new Paint();
        paintLineGrid = new Paint();
        paintLine = new Paint();
        paintRadiusInto = new Paint();

    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    @Override
    public void run() {
        Canvas canvas;
        paintY0.setColor(Color.parseColor(colorY0));
        paintY0.setTextSize(35);
        paintY0.setStrokeWidth(4);
        paintY1.setColor(Color.parseColor(colorY1));
        paintY1.setTextSize(35);
        paintY1.setStrokeWidth(4);
        paintLineGrid.setColor(Color.GRAY);
        paintLineGrid.setStrokeWidth(2);
        paintLineGrid.setTextSize(35);
        paintLine.setColor(Color.parseColor("#e9e9e9"));
        paintLine.setStrokeWidth(2);
        paintRadiusInto.setColor(Color.WHITE);

        while (running){
            canvas = null;
            float kfSurfX;
            float kfSurfY;
            int maxY, heightDraph;

            int iStart=0, iEnd=valueX.length-1;
            for (int i=0; i<valueX.length; i++){
                if (valueX[i] <= valueXStart) iStart = i;
                if (valueX[i] <= valueXEnd) iEnd = i;
            }

            maxY=Integer.MIN_VALUE;
            for (int i=iStart;i<iEnd; i++) {
                if (showY0) if (valueY0[i] > maxY) maxY = valueY0[i];
                if (showY1) if (valueY1[i] > maxY) maxY = valueY1[i];
            }

            float nMax=1;
            for (int i=1;i<Integer.MAX_VALUE;i=i*10){
                if ((maxY / i) < 1){
                    nMax = i/10;
                    break;
                }
            }
            float maxCeil = (float) Math.ceil((float)maxY / nMax);
            maxCeil = maxCeil * nMax;


            heightDraph = (int)(height * 0.9);
            kfSurfY = maxCeil/(float)heightDraph;

            long minus0 = valueX[iStart];
            kfSurfX = (valueX[iEnd] - minus0)/width;

            try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas != null){
                        canvas.drawColor(Color.WHITE);
                        canvas.drawLine(0, heightDraph, width, heightDraph, paintLineGrid);
                        int nLineGrid = 5;//6;
                        float shiftTextY = height * 0.02f;
                        float shiftTextX = width * 0.02f;
                        canvas.drawText(""+0, shiftTextX, heightDraph-shiftTextY, paintLineGrid);

                        for (int i=1; i<nLineGrid; i++){
                            float yLineGrid = heightDraph - heightDraph/nLineGrid*i;
                            canvas.drawLine(0, yLineGrid, width, yLineGrid, paintLineGrid);
                            String s = Integer.toString((int)((i * maxCeil / nLineGrid)));
                            canvas.drawText(s, shiftTextX, yLineGrid-shiftTextY, paintLineGrid);
                        }

                        long ll = (valueX[iEnd] - valueX[iStart]) / nLineGrid;
                        for (int i=0; i<nLineGrid;i++){
                            float positionX = shiftTextX + width/nLineGrid * i;
                            String date = getTxtDate(valueX[iStart] + ll*i);
                            canvas.drawText(date, positionX, height - shiftTextY, paintLineGrid);
                        }

                        if (kfSurfX != 0){

                            float beforeX = (valueX[iStart] - minus0) / kfSurfX;
                            float beforeY0 = heightDraph - (float)valueY0[iStart] / kfSurfY;
                            float beforeY1 = heightDraph - (float)valueY1[iStart] / kfSurfY;

                            for (int i=iStart+1; i<iEnd; i++) {
                                float lineX = (valueX[i] - minus0) / kfSurfX;
                                float lineY0 = heightDraph - (float)valueY0[i] / kfSurfY;
                                float lineY1 = heightDraph - (float)valueY1[i] / kfSurfY;
                                if (showY0) canvas.drawLine(beforeX, beforeY0, lineX, lineY0, paintY0);
                                if (showY1) canvas.drawLine(beforeX, beforeY1, lineX, lineY1, paintY1);
                                beforeX = lineX;
                                beforeY0 = lineY0;
                                beforeY1 = lineY1;
                            }

                            int selectPoint = Integer.MIN_VALUE;
                            if (showLine){
                                for (int i=iStart; i<iEnd; i++){
                                    float x = (valueX[i] - minus0) / kfSurfX;
                                    if (x > xSurf){
                                        float r = x - xSurf;
                                        float l = xSurf - (valueX[i-1] - minus0) / kfSurfX;
                                        if (r < l) selectPoint = i;
                                            else selectPoint = i-1;
                                        break;
                                    }
                                }
                                if (selectPoint != Integer.MIN_VALUE) {
                                    float xSelectPoint = (valueX[selectPoint] - minus0) / kfSurfX;
                                    canvas.drawLine(xSelectPoint, 0f, xSelectPoint, heightDraph, paintLineGrid);

                                    float radius = 13f;
                                    float radiusInto = 8f;
                                    float y0SelectPoint = heightDraph - (float) valueY0[selectPoint] / kfSurfY;
                                    if (showY0) canvas.drawCircle(xSelectPoint, y0SelectPoint, radius, paintY0);
                                    if (showY0) canvas.drawCircle(xSelectPoint, y0SelectPoint, radiusInto, paintRadiusInto);
                                    float y1SelectPoint = heightDraph - (float) valueY1[selectPoint] / kfSurfY;
                                    if (showY1) canvas.drawCircle(xSelectPoint, y1SelectPoint, radius, paintY1);
                                    if (showY1) canvas.drawCircle(xSelectPoint, y1SelectPoint, radiusInto, paintRadiusInto);

                                    float fShift = 4f;//width*0.01f;
                                    float fLeft = xSelectPoint;// + width*0.01f;
                                    float fTop = height * 0.05f;
                                    float fRigth = fLeft + width*0.25f;
                                    float fBottom = height * 0.2f;
                                    float fCycle = width * 0.03f;
                                    float fMinus;
                                    if (fRigth > width) fMinus = fRigth - fLeft;
                                        else fMinus = 0f;

                                    float fTextLeft = fLeft+fShift+fCycle;
                                    float fTextBottom = fBottom-fCycle*1.5f;
                                    float fTextTop = fTop*2;

                                    canvas.drawRoundRect(fLeft-fMinus, fTop, fRigth-fMinus, fBottom, fCycle, fCycle, paintLine);
                                    canvas.drawRoundRect(fLeft+fShift-fMinus, fTop+fShift, fRigth-fShift-fMinus, fBottom-fShift, fCycle, fCycle, paintRadiusInto);
                                    canvas.drawText(getTxtDateWeek(valueX[selectPoint]), fTextLeft-fMinus, fTextTop, paintLineGrid);
                                    if (showY0) canvas.drawText(Integer.toString(valueY0[selectPoint]), fTextLeft-fMinus, fTextBottom, paintY0);
                                    if (showY0) canvas.drawText(nameY0, fTextLeft-fMinus, fBottom-fShift*3, paintY0);
                                    if (showY1) canvas.drawText(Integer.toString(valueY1[selectPoint]), fTextLeft+fCycle*4-fMinus, fTextBottom, paintY1);
                                    if (showY1) canvas.drawText(nameY1, fTextLeft+fCycle*4-fMinus, fBottom-fShift*3, paintY1);



                                }
                            }
                        }
                    }

                } finally {
                    if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
                }
        }
    }

    private String getTxtDate(long time){
        Calendar d = Calendar.getInstance();
        d.setTimeInMillis(time);
        final SimpleDateFormat dataFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        return dataFormat.format(d.getTime());
    }

    private String getTxtDateWeek(long time){
        Calendar d = Calendar.getInstance();
        d.setTimeInMillis(time);
        final SimpleDateFormat dataFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        return dataFormat.format(d.getTime());
    }

}
