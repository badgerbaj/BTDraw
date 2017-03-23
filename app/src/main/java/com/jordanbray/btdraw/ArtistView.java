package com.jordanbray.btdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bjordan on 3/21/2017.
 */

public class ArtistView extends View {

    private Path path;
    private Paint paint, canvasPaint;
    private int paintColor = 0xFF000000;
    private int canvasColor = 0xFFFFFFFF;
    private Canvas canvas;
    private Bitmap canvasBitmap;


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
            path.moveTo(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            canvas.drawPath(path, paint);
            path.reset();
        }
        else {
            return false;
        }

        invalidate();
        return true;
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

}
