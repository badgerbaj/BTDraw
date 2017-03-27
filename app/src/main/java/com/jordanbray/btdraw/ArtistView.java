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
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    UpdateTask ut;

    public int getPaintColor() {
        return paintColor;
    }

    public int getMode() {
        return mode;
    }

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

                    int pixel = canvasBitmap.getPixel(Math.round(x), Math.round(y));
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);
                    int basecolor = Color.argb(1023, red, green, blue);
                    int height = canvasBitmap.getHeight();
                    int width = canvasBitmap.getWidth();
                    Point pt = new Point(Math.round(x),Math.round(y));
                    PaintParams pp = new PaintParams(pt, basecolor, height, width);
                    if (basecolor != paintColor) {
                        startPaint(pp);
                        Toast.makeText(getContext(), "Filling... ", Toast.LENGTH_LONG).show();
                    }
                    break;


                default:
                    break;
             }
        }
        else {
            return false;
        }
        if (mode == 0) {
            invalidate();
        }
        return true;
    }

    public void setMode (int i) {
        mode = i;
    }

    public void startPaint (PaintParams pp) {
        if (ut != null && ut.getStatus() == AsyncTask.Status.FINISHED){
            ut = null;
        }
        if (ut == null) {
            ut = new UpdateTask();
            ut.execute(pp);
        }
        else {
            Log.i("Paint", "Bucket is already running");
        }
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

    private static class PaintParams {
        Point point;
        int basecolor;
        int height;
        int width;

        PaintParams(Point pt, int base, int h, int w) {
            this.point = pt;
            this.basecolor = base;
            this.height = h;
            this.width = w;
        }
    }

    class UpdateTask extends AsyncTask<PaintParams, Void, Boolean> {

        @Override
        protected Boolean doInBackground(PaintParams... ps) {
                Queue<Point> pointList = new LinkedList<Point>();
                pointList.add(ps[0].point);
                while (pointList.size() > 0) {
                    Point n = pointList.poll();
                    if (canvasBitmap.getPixel(n.x, n.y) != ps[0].basecolor)
                        continue;

                    Point w = n, e = new Point(n.x + 1, n.y);

                    while ((w.x > 0) && (canvasBitmap.getPixel(w.x, w.y) == ps[0].basecolor)) {
                        canvasBitmap.setPixel(w.x, w.y, paintColor);
                        if ((w.y > 0) && (canvasBitmap.getPixel(w.x, w.y - 1) == ps[0].basecolor))
                            pointList.add(new Point(w.x, w.y - 1));
                        if ((w.y < ps[0].height - 1) && (canvasBitmap.getPixel(w.x, w.y + 1) == ps[0].basecolor))
                            pointList.add(new Point(w.x, w.y + 1));

                        w.x--;
                    }

                    while ((e.x < ps[0].width - 1) && (canvasBitmap.getPixel(e.x, e.y) == ps[0].basecolor)) {
                        canvasBitmap.setPixel(e.x, e.y, paintColor);
                        if ((e.y > 0) && (canvasBitmap.getPixel(e.x, e.y - 1) == ps[0].basecolor))
                            pointList.add(new Point(e.x, e.y - 1));
                        if ((e.y < ps[0].height - 1) && (canvasBitmap.getPixel(e.x, e.y + 1) == ps[0].basecolor))
                            pointList.add(new Point(e.x, e.y + 1));

                        e.x++;
                    }

            }
            pointList.clear();
            return true;
        }
        protected void onPostExecute(Boolean fin) {
            invalidate();
            ut = null;
        }

    }







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
