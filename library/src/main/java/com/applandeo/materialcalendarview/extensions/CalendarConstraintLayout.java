package com.applandeo.materialcalendarview.extensions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.GridView;

public class CalendarConstraintLayout extends ConstraintLayout {

    public CalendarConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarConstraintLayout(Context context) {
        super(context);
    }

    public CalendarConstraintLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //This method is needed to get wrap_content height for GridView
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawLine(10, 10, 100, 100, paint);

    }
}
