package mod.elfilibustero.sketch.beans;

import com.google.gson.annotations.SerializedName;

public class BuildConfigurationBean {

	@SerializedName("dexer")
	private String dexer;
	@SerializedName("classpath")
	private String classPath;
	@SerializedName("enable_logcat")
	private String logcat;
	@SerializedName("no_http_legacy")
	private String httpLegacy;
	@SerializedName("android_jar")
	private String androidJar;
	@SerializedName("no_warn")
	private String warning;
	@SerializedName("java_ver")
	private String javaVersion;

	public BuildConfigurationBean() {
		dexer = "D8";
		classPath = "";
		logcat = "true";
		httpLegacy = "false";
		androidJar = "";
		warning = "false";
		javaVersion = "1.8";
	}

	public String getDexer() {
		return dexer;
	}

	public void setDexer(String dexer) {
		this.dexer = dexer;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getLogcat() {
		return logcat;
	}

	public void setLogcat(String logcat) {
		this.logcat = logcat;
	}

	public String getHttpLegacy() {
		return httpLegacy;
	}

	public void setHttpLegacy(String httpLegacy) {
		this.httpLegacy = httpLegacy;
	}

	public String getAndroidJar() {
		return androidJar;
	}

	public void setAndroidJar(String androidJar) {
		this.androidJar = androidJar;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
}
