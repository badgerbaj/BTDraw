package com.jordanbray.btdraw;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bjordan on 3/21/2017.
 */

public class ArtistView extends View {

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
    }
}
