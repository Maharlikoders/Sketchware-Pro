package mod.elfilibustero.sketch.constants;

import com.sketchware.pro.R;

public class XmlResourceConstant {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_STYLE = 2;
    public static final int TYPE_STYLE_ITEM = 3;

    public static int getIcon(int resType) {
        return switch (resType) {
            case TYPE_STRING ->
                R.drawable.widget_text_view;
            case TYPE_COLOR ->
                R.drawable.color_palette_48;
            case TYPE_STYLE, TYPE_STYLE_ITEM ->
                R.drawable.collage_48;
            default ->
                0;
        };
    }

    public static int getDescription(int resType) {
        return switch (resType) {
            case TYPE_STRING ->
                R.string.design_xml_description_string;
            case TYPE_COLOR ->
                R.string.design_xml_description_color;
            case TYPE_STYLE, TYPE_STYLE_ITEM ->
                R.string.design_xml_description_style;
            default ->
                0;
        };
    }

    public static int getName(int resType) {
        return switch (resType) {
            case TYPE_STRING ->
                R.string.design_xml_title_string;
            case TYPE_COLOR ->
                R.string.design_xml_title_color;
            case TYPE_STYLE, TYPE_STYLE_ITEM ->
                R.string.design_xml_title_style;
            default ->
                0;
        };
    }

    public static String getFileName(int resType) {
        return switch (resType) {
            case TYPE_STRING ->
                "strings";
            case TYPE_COLOR ->
                "colors";
            case TYPE_STYLE, TYPE_STYLE_ITEM ->
                "styles";
            default ->
                "";
        };
    }

    public static int getActivityTitle(int resType) {
        return switch (resType) {
            case TYPE_STRING ->
                R.string.design_xml_title_string_manager;
            case TYPE_COLOR ->
                R.string.design_xml_title_color_manager;
            case TYPE_STYLE, TYPE_STYLE_ITEM ->
                R.string.design_xml_title_style_manager;
            default ->
                0;
        };
    }
}
