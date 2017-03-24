package com.jordanbray.btdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bjordan on 3/21/2017.
 */

public class ArtistView extends View {

    private int mode = 0;
    private Path path;
    private Paint paint, canvasPaint;
    private int paintColor = 0xFF000000;
    private int canvasColor = 0xFFFFFFFF;
    private Canvas canvas;
    private Bitmap canvasBitmap;
    PointF upperleft = new PointF(0,0);
    PointF lowerright = new PointF(0,0);


    public ArtistView(Context context) {
        super(context);
        setupDrawing();

    }

    public ArtistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        //creates new paint and path objects and sets default color
        path = new Path();
        paint = new Paint();
        paint.setColor(paintColor);

        //creates initial properties
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //creates canvas paint object
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBitmap);
        canvas.drawColor(canvasColor);

    }

    @Override
    protected void onDraw(Canvas c) {
            c.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            c.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mode == 0) {
                path.moveTo(x, y);
            }
            if (mode == 1) {
                upperleft.x = x;
                upperleft.y = y;
                lowerright.x = x;
                lowerright.y = y;
            }
            if (mode == 2) {
                upperleft.x = x;
                upperleft.y = y;
                lowerright.x = x;
                lowerright.y = y;
            }
            if (mode == 3) {
                upperleft.x = x;
                upperleft.y = y;
                lowerright.x = x;
                lowerright.y = y;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mode == 0) {
                path.lineTo(x, y);
            }
            if (mode == 1) {
                lowerright.x = x;
                lowerright.y = y;
            }
            if (mode == 2) {
                lowerright.x = x;
                lowerright.y = y;
            }
            if (mode == 3) {
                lowerright.x = x;
                lowerright.y = y;
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mode == 0) {
                canvas.drawPath(path, paint);
                path.reset();
            }
            if (mode == 1) {
                canvas.drawRect(upperleft.x,  upperleft.y, lowerright.x, lowerright.y, paint);
                invalidate();
            }
            if (mode == 2) {
                RectF rec = new RectF(upperleft.x,  upperleft.y, lowerright.x, lowerright.y);
                canvas.drawOval(rec, paint);
                invalidate();
            }
            if (mode == 3) {
                canvas.drawLine(upperleft.x,  upperleft.y, lowerright.x, lowerright.y, paint);
                invalidate();
            }
        }
        else {
            return false;
        }

        invalidate();
        return true;
    }

    public void setMode (int i) {
        mode = i;
    }

    public void setPaintColor (int i) {
        invalidate();
        paintColor = i;
        paint.setColor(paintColor);
    }

    public void Erase () {
        invalidate();
        paintColor = canvasColor;
        paint.setColor(paintColor);
    }

    public void setCanvasColor () {
        canvas.drawColor(canvasColor);
    }



}
