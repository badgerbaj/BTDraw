package com.jordanbray.btdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.LinkedList;
import java.util.Queue;

import static com.jordanbray.btdraw.R.id.bottom;
import static com.jordanbray.btdraw.R.id.left;
import static com.jordanbray.btdraw.R.id.right;
import static com.jordanbray.btdraw.R.id.top;

public class ArtistView extends View {
    private int mode = 0;
    private Path path;
    private Paint paint, canvasPaint;
    private int paintColor = 0xFF000000;
    private int canvasColor = 0xFFFFFFFF;
    private Canvas canvas;
    private Bitmap canvasBitmap, currentCanvas;
    PointF upperLeft = new PointF(0,0);
    PointF lowerRight = new PointF(0,0);
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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btdraw_intro);
        canvas.drawBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true), getMatrix(), canvasPaint);

        //canvas.drawColor(canvasColor);
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
        // 2 - Rectangle Fill
        // 3 - Oval
        // 4 - Oval Fill
        // 5 - Line
        // 6 - Color Picker
        // 7 - Bucket Fill


        float x = event.getX();
        float y = event.getY();

        // action when the screen is pressed depending on drawing mode
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            saveToBitmap();
            switch (mode) {
                case 0:
                    canvas.drawPoint(x, y, paint);
                    path.moveTo(x, y);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    upperLeft.x = x;
                    upperLeft.y = y;
                    lowerRight.x = x;
                    lowerRight.y = y;
                    break;
                case 6:
                case 7:
                    upperLeft.x = x;
                    upperLeft.y = y;
                default:
                    break;
            }

        }
        // action when the user moves across screen depending on drawing mode
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            switch (mode) {
                case 0:
                    path.lineTo(x, y);
                    break;

                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    lowerRight.x = x;
                    lowerRight.y = y;
                    break;
                default:
                    break;
            }

        }
        // action when the removes touch from screen depending on drawing mode
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (mode) {
                case 0:
                    canvas.drawPath(path, paint);
                    path.reset();
                    break;

                case 1:
                case 2:
                    canvas.drawRect(upperLeft.x,  upperLeft.y, lowerRight.x, lowerRight.y, paint);
                    invalidate();
                    break;
                case 3:
                case 4:
                    RectF rec = new RectF(upperLeft.x,  upperLeft.y, lowerRight.x, lowerRight.y);
                    canvas.drawOval(rec, paint);
                    invalidate();
                    break;
                case 5:
                    canvas.drawLine(upperLeft.x,  upperLeft.y, lowerRight.x, lowerRight.y, paint);
                    invalidate();
                    break;
                case 6:
                    ColorPicker(upperLeft.x,  upperLeft.y);
                    invalidate();
                    break;
                case 7:
                    int pixel = canvasBitmap.getPixel(Math.round(x), Math.round(y));
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);
                    int basecolor = Color.rgb(red, green, blue);
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

    // sets drawing mode and stroke/fill
    public void setMode (int i) {
        mode = i;
        paint.setColor(paintColor);
        switch (i) {
            case 0:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 1:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 2:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
            case 3:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 4:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
            case 5:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 6:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 7:
                paint.setStyle(Paint.Style.STROKE);
                break;
            default:
                break;
        }
    }

    // creates parameters and starts paint bucket if not already running
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

    // sets paint color
    public void setPaintColor (int i) {
        invalidate();
        paintColor = i;
        paint.setColor(paintColor);
    }

    // gets color touched on the screen and sets as paint color
    public void ColorPicker (float x, float y) {
        invalidate();
        int pixel = canvasBitmap.getPixel(Math.round(x), Math.round(y));
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        paintColor = Color.argb(1023, red, green, blue);
        setPaintColor(paintColor);
    }

    // parameters for paint bucket class to be sent to AsyncTask
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

    // runs paint bucket in background
    class UpdateTask extends AsyncTask<PaintParams, Void, Boolean> {
        @Override
        protected Boolean doInBackground(PaintParams... ps) {
                Queue<Point> pointList = new LinkedList<Point>();
                pointList.add(ps[0].point);
                while (pointList.size() > 0) {
                    Point listPt = pointList.poll();
                    if (canvasBitmap.getPixel(listPt.x, listPt.y) != ps[0].basecolor)
                        continue;

                    Point pt = listPt;
                    Point ptAdj = new Point(listPt.x + 1, listPt.y);

                    while ((pt.x > 0) && (canvasBitmap.getPixel(pt.x, pt.y) == ps[0].basecolor)) {
                        canvasBitmap.setPixel(pt.x, pt.y, paintColor);
                        if ((pt.y > 0) && (canvasBitmap.getPixel(pt.x, pt.y - 1) == ps[0].basecolor))
                            pointList.add(new Point(pt.x, pt.y - 1));
                        if ((pt.y < ps[0].height - 1) && (canvasBitmap.getPixel(pt.x, pt.y + 1) == ps[0].basecolor))
                            pointList.add(new Point(pt.x, pt.y + 1));
                        pt.x--;
                    }
                    while (( ptAdj.x < ps[0].width - 1) && (canvasBitmap.getPixel( ptAdj.x,  ptAdj.y) == ps[0].basecolor)) {
                        canvasBitmap.setPixel( ptAdj.x,  ptAdj.y, paintColor);
                        if (( ptAdj.y > 0) && (canvasBitmap.getPixel( ptAdj.x,  ptAdj.y - 1) == ps[0].basecolor))
                            pointList.add(new Point( ptAdj.x,  ptAdj.y - 1));
                        if (( ptAdj.y < ps[0].height - 1) && (canvasBitmap.getPixel( ptAdj.x,  ptAdj.y + 1) == ps[0].basecolor))
                            pointList.add(new Point( ptAdj.x,  ptAdj.y + 1));
                        ptAdj.x++;
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


    // clears canvas for new drawing
    public void newCanvas () {
        canvas.drawColor(Color.WHITE);
        invalidate();
    }

    // sets brush size
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

    // sets erase
    public void Erase () {
        invalidate();
        paint.setColor(canvasColor);
    }

    public void saveToBitmap() {
        setDrawingCacheEnabled(true);

        currentCanvas = getDrawingCache();

        //destroyDrawingCache();
    }
    public void sendBitmapToCanvas () {
        canvas.drawBitmap(currentCanvas, getMatrix(), canvasPaint);
    }
}
