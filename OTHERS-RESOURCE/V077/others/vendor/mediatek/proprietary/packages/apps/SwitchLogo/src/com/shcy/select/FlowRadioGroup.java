package com.shcy.select;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

/* loaded from: classes.dex */
public class FlowRadioGroup extends RadioGroup {
    public FlowRadioGroup(Context context) {
        super(context);
    }

    public FlowRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int row = 0;
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (child.getVisibility() != 8) {
                child.measure(0, 0);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width;
                y = (row * height) + height;
                if (x > maxWidth) {
                    x = width;
                    row++;
                    y = (row * height) + height;
                }
            }
        }
        setMeasuredDimension(maxWidth, y);
    }

    @Override // android.widget.LinearLayout, android.view.View, android.view.ViewGroup
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int maxWidth = r - l;
        int x = 0;
        int row = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width;
                int y = (row * height) + height;
                if (x > maxWidth) {
                    x = width;
                    row++;
                    y = (row * height) + height;
                }
                child.layout(x - width, y - height, x, y);
            }
        }
    }
}
