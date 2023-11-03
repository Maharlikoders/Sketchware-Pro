package com.besome.sketch.editor.view.palette;

import android.content.Context;
import android.view.ViewGroup;

import com.besome.sketch.beans.ViewBean;
import com.sketchware.pro.R;

public class IconConstraintLayout extends IconBase {

    public IconConstraintLayout(Context context) {
        super(context);
        setWidgetImage(R.drawable.widget_relative_layout);
        setWidgetName("ConstraintLayout");
    }

    @Override
    public ViewBean getBean() {
        ViewBean viewBean = new ViewBean();
        viewBean.type = ViewBean.VIEW_TYPE_LAYOUT_CONSTRAINT;
        viewBean.layout.orientation = VERTICAL;
        viewBean.layout.width = ViewGroup.LayoutParams.MATCH_PARENT;
        viewBean.layout.height = ViewGroup.LayoutParams.MATCH_PARENT;
        viewBean.layout.paddingLeft = 8;
        viewBean.layout.paddingTop = 8;
        viewBean.layout.paddingRight = 8;
        viewBean.layout.paddingBottom = 8;
        viewBean.convert = "androidx.constraintlayout.widget.ConstraintLayout";
        return viewBean;
    }
}
