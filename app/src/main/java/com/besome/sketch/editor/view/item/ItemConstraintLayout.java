package com.besome.sketch.editor.view.item;

import a.a.a.sy;
import a.a.a.ty;
import a.a.a.wB;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.besome.sketch.beans.ViewBean;

public class ItemConstraintLayout extends LinearLayout implements sy, ty {

    private ViewBean viewBean = null;
    private boolean isSelected = false;
    private boolean isFixed = false;
    private Paint paint;

    private Rect rect;

    public static final int PARENT_ID = ConstraintSet.PARENT_ID;
    public static final int LEFT = ConstraintSet.LEFT;
    public static final int RIGHT = ConstraintSet.RIGHT;
    public static final int START = ConstraintSet.START;
    public static final int END = ConstraintSet.END;
    public static final int TOP = ConstraintSet.TOP;
    public static final int BOTTOM = ConstraintSet.BOTTOM;
    public static final int BASELINE = ConstraintSet.BASELINE;
    
    public ItemConstraintLayout(Context context) {
        super(context);
        initialize(context);
    }

    @Override
    public void a() {
        int index = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof sy syChild) {
                syChild.getBean().index = index;
                index++;
            }
        }
    }

    private void initialize(Context context) {
        setDrawingCacheEnabled(true);
        setMinimumWidth((int) wB.a(context, 32.0F));
        setMinimumHeight((int) wB.a(context, 32.0F));
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(wB.a(getContext(), 2.0F));
        rect = new Rect();
    }

    @Override
    public void addView(View child, int index) {
        int childCount = getChildCount();

        if (index > childCount) {
            super.addView(child);
        } else {
            int firstGoneIndex = -1;

            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i).getVisibility() == View.GONE) {
                    firstGoneIndex = i;
                    break;
                }
            }

            if (firstGoneIndex >= 0 && index >= firstGoneIndex) {
                super.addView(child, index + 1);
            } else {
                super.addView(child, index);
            }
        }
    }

    @Override
    public ViewBean getBean() {
        return viewBean;
    }

    @Override
    public boolean getFixed() {
        return isFixed;
    }

    public boolean getSelection() {
        return isSelected;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isFixed) {
            if (isSelected) {
                paint.setColor(0x9599d5d0);
                rect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawRect(rect, paint);
            }
            paint.setColor(0x60000000);

            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            canvas.drawLine(0.0F, 0.0F, (float) measuredWidth, 0.0F, paint);
            canvas.drawLine(0.0F, 0.0F, 0.0F, (float) measuredHeight, paint);
            canvas.drawLine((float) measuredWidth, 0.0F, (float) measuredWidth, (float) measuredHeight, paint);
            canvas.drawLine(0.0F, (float) measuredHeight, (float) measuredWidth, (float) measuredHeight, paint);
        }

        super.onDraw(canvas);
    }

    @Override
    public void setBean(ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    @Override
    public void setChildScrollEnabled(boolean childScrollEnabled) {
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            if (child instanceof ty) {
                ((ty) child).setChildScrollEnabled(childScrollEnabled);
            }

            if (child instanceof ItemHorizontalScrollView) {
                ((ItemHorizontalScrollView) child).setScrollEnabled(childScrollEnabled);
            }

            if (child instanceof ItemVerticalScrollView) {
                ((ItemVerticalScrollView) child).setScrollEnabled(childScrollEnabled);
            }
        }
    }

    public void setFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding((int) wB.a(getContext(), (float) left), (int) wB.a(getContext(), (float) top), (int) wB.a(getContext(), (float) right), (int) wB.a(getContext(), (float) bottom));
    }

    @Override
    public void setSelection(boolean selected) {
        isSelected = selected;
        invalidate();
    }

    private void setConstraint(View target, int value, int startSide, int endSide) {
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(target.getId(), startSide, value, endSide);
        set.applyTo(layout);
    }

    public void setLeftToLeft(View target, int value) {
        setConstraint(target, value, LEFT, LEFT);
    }

    public void setRightToRight(View target, int value) {
        setConstraint(target, value, RIGHT, RIGHT);
    }

    public void setLeftToRight(View target, int value) {
        setConstraint(target, value, LEFT, RIGHT);
    }

    public void setRightToLeft(View target, int value) {
        setConstraint(target, value, RIGHT, LEFT);
    }

    public void setTopToTop(View target, int value) {
        setConstraint(target, value, TOP, TOP);
    }

    public void setBottomToBottom(View target, int value) {
        setConstraint(target, value, BOTTOM, BOTTOM);
    }

    public void setTopToBottom(View target, int value) {
        setConstraint(target, value, TOP, BOTTOM);
    }

    public void setBottomToTop(View target, int value) {
        setConstraint(target, value, BOTTOM, TOP);
    }

    public void setBaselineToBaseline(View target, int value) {
        setConstraint(target, value, BASELINE, BASELINE);
    }

    public void setStartToStart(View target, int value) {
        setConstraint(target, value, START, START);
    }

    public void setEndToEnd(View target, int value) {
        setConstraint(target, value, END, END);
    }

    public void setStartToEnd(View target, int value) {
        setConstraint(target, value, START, END);
    }

    public void setEndToStart(View target, int value) {
        setConstraint(target, value, END, START);
    }
}
