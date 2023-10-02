package mod.agus.jcoderz.editor.view.palette;

import android.content.Context;

import com.besome.sketch.beans.LayoutBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.editor.view.palette.IconBase;
import com.sketchware.pro.R;

import mod.agus.jcoderz.beans.ViewBeans;

public class IconMultiAutoCompleteTextView extends IconBase {

    public IconMultiAutoCompleteTextView(Context context) {
        super(context);
        setWidgetImage(R.drawable.widget_edit_text);
        setWidgetName("MultiAutoCompleteTextView");
    }

    @Override
    public ViewBean getBean() {
        ViewBean viewBean = new ViewBean();
        viewBean.type = ViewBeans.VIEW_TYPE_WIDGET_MULTIAUTOCOMPLETETEXTVIEW;
        LayoutBean layoutBean = viewBean.layout;
        layoutBean.paddingLeft = 8;
        layoutBean.paddingTop = 8;
        layoutBean.paddingRight = 8;
        layoutBean.paddingBottom = 8;
        viewBean.text.hint = getName();
        viewBean.convert = getName();
        return viewBean;
    }
}
