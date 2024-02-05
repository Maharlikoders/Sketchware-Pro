package mod.elfilibustero.sketch.beans;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

public class TempProjectBean {
    @SerializedName("custom_icon")
    private boolean customIcon;
    
    @SerializedName("my_ws_name")
    private String projectName;
    
    @SerializedName("app_name")
    private String appName;
    
    @SerializedName("my_sc_pkg_name")
    private String packageName;
    
    @SerializedName("version_code")
    private String versionCode;
    
    @SerializedName("version_name")
    private String versionName;
    
    @SerializedName("color_primary")
    private String colorPrimary;
    
    @SerializedName("color_primary_dark")
    private String colorPrimaryDark;
    
    @SerializedName("color_accent")
    private String colorAccent;
    
    @SerializedName("color_control_highlight")
    private String colorControlHighlight;
    
    @SerializedName("color_control_normal")
    private String colorControlNormal;
    
    public TempProjectBean() {
    }
    
    public boolean isCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(boolean customIcon) {
        this.customIcon = customIcon;
    }

    public String getWorkspaceName() {
        return projectName;
    }

    public void setWorkspaceName(String projectName) {
        this.projectName = projectName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getColorPrimary() {
        return Color.parseColor(colorPrimary);
    }

    public void setColorPrimary(int colorPrimary) {
        this.colorPrimary = formatColor(colorPrimary);
    }
    
    public int getColorPrimaryDark() {
        return Color.parseColor(colorPrimaryDark);
    }

    public void setColorPrimaryDark(int colorPrimaryDark) {
        this.colorPrimaryDark = formatColor(colorPrimaryDark);
    }
    
    public int getColorAccent() {
        return Color.parseColor(colorAccent);
    }

    public void setColorAccent(int colorAccent) {
        this.colorAccent = formatColor(colorAccent);
    }

    public int getColorControlHighlight() {
        return Color.parseColor(colorControlHighlight);
    }

    public void setColorControlHighlight(int colorControlHighlight) {
        this.colorControlHighlight = formatColor(colorControlHighlight);
    }

    public int getColorControlNormal() {
        return Color.parseColor(colorControlNormal);
    }

    public void setColorControlNormal(int colorControlNormal) {
        this.colorControlNormal = formatColor(colorControlNormal);
    }

    private String formatColor(int color) {
        return String.format("#%08X", color & 0xFFFFFFFF);
    }
}
