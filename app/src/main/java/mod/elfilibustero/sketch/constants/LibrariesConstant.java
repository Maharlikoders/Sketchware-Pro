package mod.elfilibustero.sketch.constants;

import com.sketchware.pro.R;

public class LibrariesConstant {
    public static final int TYPE_BUILT_IN = 0;
    public static final int TYPE_EXTERNAL = 1;
    public static final int TYPE_NATIVE = 2;
    public static final int TYPE_LOCAL = 3;

    public static int getIcon(int resType) {
        return switch (resType) {
            case TYPE_BUILT_IN ->
                R.drawable.categorize_48;
            case TYPE_NATIVE ->
                R.drawable.cpp;
            case TYPE_EXTERNAL, TYPE_LOCAL ->
                R.drawable.open_box_48;
            default ->
                0;
        };
    }

    public static String getDescription(int resType) {
        return switch (resType) {
            case TYPE_BUILT_IN ->
                "Configure built-in libraries";
            case TYPE_NATIVE ->
                "Add native libraries (.so)";
            case TYPE_EXTERNAL ->
                "Configure external libraries";
            case TYPE_LOCAL ->
                "Deprecated";
            default ->
                "";
        };
    }

    public static String getName(int resType) {
        return switch (resType) {
            case TYPE_BUILT_IN ->
                "Built-in";
            case TYPE_NATIVE ->
                "Native";
            case TYPE_EXTERNAL ->
                "External";
            case TYPE_LOCAL ->
                "Local";
            default ->
                "";
        };
    }
}
