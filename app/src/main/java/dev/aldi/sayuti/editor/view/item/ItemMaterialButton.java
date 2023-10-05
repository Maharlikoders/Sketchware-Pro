package dev.aldi.sayuti.editor.view.item;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.appcompat.widget.AppCompatButton;

import com.besome.sketch.beans.ViewBean;
import com.sketchware.pro.R;

import a.a.a.sy;
import a.a.a.wB;

public class ItemMaterialButton extends AppCompatButton implements sy {

    private final Paint paint;
    private final float paddingFactor;
    private final Rect rect;
    private ViewBean viewBean;
    private boolean hasSelection;
    private boolean hasFixed;
    private int mainColor = 0;

    public ItemMaterialButton(Context context) {
        super(context);
        paddingFactor = wB.a(context, 1.0f);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x9599d5d0);
        rect = new Rect();

        setDrawingCacheEnabled(true);
        setFocusable(false);
    }

    @Override
    public ViewBean getBean() {
        return viewBean;
    }

    @Override
    public void setBean(ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    @Override
    public boolean getFixed() {
        return hasFixed;
    }

    public void setFixed(boolean z) {
        hasFixed = z;
    }

    public boolean getSelection() {
        return hasSelection;
    }

    @Override
    public void setSelection(boolean z) {
        hasSelection = z;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (hasSelection) {
            rect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRect(rect, paint);
        }/* else {
            ColorStateList colorState = ColorStateList.valueOf(getResources().getColor(R.color.color_primary));
            if (mainColor != 0) {
                colorState = ColorStateList.valueOf(mainColor);
            }
            super.setBackgroundTintList(colorState);
        }*/
        super.onDraw(canvas);
    }

    public void setBackgroundTint(int color) {
        if (color == 0xffffff || color == Color.TRANSPARENT) {
            if (mainColor != 0) {
                super.setBackgroundTintList(ColorStateList.valueOf(mainColor));
            }
        } else {
            super.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    public void setMainColor(int color) {
        mainColor = color;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding((int) (left * paddingFactor), (int) (top * paddingFactor), (int) (right * paddingFactor), (int) (bottom * paddingFactor));
    }
}
