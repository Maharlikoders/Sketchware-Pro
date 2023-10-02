package mod.agus.jcoderz.editor.view.palette;

import android.content.Context;
import android.view.ViewGroup;

import com.besome.sketch.beans.LayoutBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.editor.view.palette.IconBase;
import com.sketchware.pro.R;

import mod.agus.jcoderz.beans.ViewBeans;

public class IconDatePicker extends IconBase {

    public IconDatePicker(Context context) {
        super(context);
        setWidgetImage(R.drawable.date_span_96);
        setWidgetName("DatePicker");
    }

    @Override
    public ViewBean getBean() {
        ViewBean viewBean = new ViewBean();
        viewBean.type = ViewBeans.VIEW_TYPE_WIDGET_DATEPICKER;
        LayoutBean layoutBean = viewBean.layout;
        layoutBean.paddingLeft = 8;
        layoutBean.paddingTop = 8;
        layoutBean.paddingRight = 8;
        layoutBean.paddingBottom = 8;
        layoutBean.width = ViewGroup.LayoutParams.MATCH_PARENT;
        viewBean.convert = getName();
        return viewBean;
    }
}
