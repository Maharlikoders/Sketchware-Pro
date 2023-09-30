package mod.elfilibustero.sketch.beans;

import com.google.gson.annotations.SerializedName;

public class ProjectBean {
    @SerializedName("custom_icon")
    private boolean customIcon;
    
    @SerializedName("sc_ver_code")
    private String versionCode;
    
    @SerializedName("my_ws_name")
    private String projectName;
    
    @SerializedName("color_accent")
    private int colorAccent;
    
    @SerializedName("my_app_name")
    private String appName;
    
    @SerializedName("sc_ver_name")
    private String versionName;
    
    @SerializedName("sc_id")
    private String id;
    
    @SerializedName("color_primary")
    private int colorPrimary;
    
    @SerializedName("color_control_highlight")
    private int colorControlHighlight;
    
    @SerializedName("color_control_normal")
    private int colorControlNormal;
    
    @SerializedName("my_sc_reg_dt")
    private String registeredDate;
    
    @SerializedName("sketchware_ver")
    private int sketchwareVer;
    
    @SerializedName("my_sc_pkg_name")
    private String packageName;
    
    @SerializedName("color_primary_dark")
    private int colorPrimaryDark;
    
    public ProjectBean() {
    }
    
    public boolean isCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(boolean customIcon) {
        this.customIcon = customIcon;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
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

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getColorPrimary() {
        return colorPrimary;
    }

    public void setColorPrimary(int colorPrimary) {
        this.colorPrimary = colorPrimary;
    }
    
    public int getColorPrimaryDark() {
        return colorPrimaryDark;
    }

    public void setColorPrimaryDark(int colorPrimaryDark) {
        this.colorPrimaryDark = colorPrimaryDark;
    }
    
    public int getColorAccent() {
        return colorAccent;
    }

    public void setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
    }

    public int getColorControlHighlight() {
        return colorControlHighlight;
    }

    public void setColorControlHighlight(int colorControlHighlight) {
        this.colorControlHighlight = colorControlHighlight;
    }

    public int getColorControlNormal() {
        return colorControlNormal;
    }

    public void setColorControlNormal(int colorControlNormal) {
        this.colorControlNormal = colorControlNormal;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public int getSketchwareVer() {
        return sketchwareVer;
    }

    public void setSketchwareVer(int sketchwareVer) {
        this.sketchwareVer = sketchwareVer;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
