package com.jordanbray.btdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

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
        // modes:
        // 0 - Brush
        // 1 - Rectangle
        // 2 - Oval
        // 3 - Line
        // 4 - Color Picker
        // 5 - Bucket Fill

        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (mode) {
                case 0:
                    canvas.drawPoint(x, y, paint);
                    path.moveTo(x, y);
                    break;

                case 1:
                case 2:
                case 3:
                    upperleft.x = x;
                    upperleft.y = y;
                    lowerright.x = x;
                    lowerright.y = y;
                    break;
                case 4:
                case 5:
                    upperleft.x = x;
                    upperleft.y = y;
                default:
                    break;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            switch (mode) {
                case 0:
                    path.lineTo(x, y);
                    break;

                case 1:
                case 2:
                case 3:
                    lowerright.x = x;
                    lowerright.y = y;
                    break;
                default:
                    break;
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (mode) {
                case 0:
                    canvas.drawPath(path, paint);
                    path.reset();
                    break;

                case 1:
                    canvas.drawRect(upperleft.x,  upperleft.y, lowerright.x, lowerright.y, paint);
                    invalidate();
                    break;

                case 2:
                    RectF rec = new RectF(upperleft.x,  upperleft.y, lowerright.x, lowerright.y);
                    canvas.drawOval(rec, paint);
                    invalidate();
                    break;
                case 3:
                    canvas.drawLine(upperleft.x,  upperleft.y, lowerright.x, lowerright.y, paint);
                    invalidate();
                    break;
                case 4:
                    ColorPicker(upperleft.x,  upperleft.y);
                    invalidate();
                    break;
                case 5:
                    /*
                    int pixel = canvasBitmap.getPixel(Math.round(x), Math.round(y));
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);
                    int basecolor = Color.argb(1023, red, green, blue);
                    Point pt = new Point(Math.round(x),Math.round(y));
                    PaintBucket(canvasBitmap, pt, basecolor, paintColor);
                    invalidate();
                    break;
                    */

                default:
                    break;
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

    public void ColorPicker (float x, float y) {
        invalidate();
        int pixel = canvasBitmap.getPixel(Math.round(x), Math.round(y));
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        paintColor = Color.argb(1023, red, green, blue);
        setPaintColor(paintColor);
    }

    /* public void PaintBucket (Bitmap bmp, Point pt, int targetColor, int replacementColor) {
            Queue<Point> q = new LinkedList<Point>();
            q.add(pt);
            while (q.size() > 0) {
                Point n = q.poll();
                if (bmp.getPixel(n.x, n.y) != targetColor)
                    continue;

                Point w = n, e = new Point(n.x + 1, n.y);

                while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                    bmp.setPixel(w.x, w.y, replacementColor);
                    if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                        q.add(new Point(w.x, w.y - 1));
                    if ((w.y < bmp.getHeight() - 1) && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                        q.add(new Point(w.x, w.y + 1));

                    w.x--;
                }

                while ((e.x < bmp.getWidth() - 1) && (bmp.getPixel(e.x, e.y) == targetColor)) {
                    bmp.setPixel(e.x, e.y, replacementColor);
                    if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                        q.add(new Point(e.x, e.y - 1));
                    if ((e.y < bmp.getHeight() - 1) && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                        q.add(new Point(e.x, e.y + 1));

                    e.x++;
                }
            }
            q.clear();
        }
*/



    public void setBrushSize (int i) {
        invalidate();
        if (i == 1) {
            paint.setStrokeWidth(10);
        }
        if (i == 2) {
            paint.setStrokeWidth(20);
        }
        if (i == 3) {
            paint.setStrokeWidth(30);
        }
    }

    public void Erase () {
        invalidate();
        paintColor = canvasColor;
        paint.setColor(paintColor);
    }






}
