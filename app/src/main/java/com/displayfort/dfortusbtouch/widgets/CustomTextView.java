package com.displayfort.dfortusbtouch.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by pc on 07/03/2019 15:30.
 * Dfortusbtouch
 */
public class CustomTextView extends View {
    public CustomTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        //paint.setShadowLayer(1, 0, 1, Color.parseColor("#000000"));
        paint.setTextSize(18f);
        canvas.rotate(-90, 120, 90);
        canvas.drawText("Text", 0, 0, paint);
        super.onDraw(canvas);
    }
}
