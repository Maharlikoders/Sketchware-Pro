package mod.elfilibustero.sketch.beans;

import com.google.gson.annotations.SerializedName;

public class ProjectConfigurationBean {

	@SerializedName("min_sdk")
	private String minSdk;
	@SerializedName("target_sdk")
	private String targetSdk;
	@SerializedName("app_class")
	private String appClass;
	@SerializedName("util_class")
	private String utilClass;
	@SerializedName("disable_old_methods")
	private String oldMethods;
	@SerializedName("enable_bridgeless_themes")
	private String bridgelessThemes;

	public ProjectConfigurationBean() {
		minSdk = "21";
		targetSdk = "33";
		appClass = ".SketchApplication";
		utilClass = "SketchwareUtil";
		oldMethods = "false";
		bridgelessThemes = "false";
	}

	public String getMinSdk() {
		return minSdk;
	}

	public void setMinSdk(String min) {
		minSdk = min;
	}

	public String getTargetSdk() {
		return targetSdk;
	}

	public void setTargetSdk(String target) {
		targetSdk = target;
	}

	public String getApplicationClass() {
		return appClass;
	}

	public void setApplicationClass(String appClass) {
		this.appClass = appClass;
	}

	public String getUtilClass() {
		return utilClass;
	}

	public void setUtilClass(String utilClass) {
		this.utilClass = utilClass;
	}

	public String getOldMethods() {
		return oldMethods;
	}

	public void setOldMethods(String disable) {
		oldMethods = disable;
	}

	public String getBridgelessThemes() {
		return bridgelessThemes;
	}

	public void setBridgelessThemes(String enable) {
		bridgelessThemes = enable;
	}
}
