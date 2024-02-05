package mod.elfilibustero.sketch.lib.utils;

import com.google.gson.Gson;

import java.io.File;

import a.a.a.wq;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.BuildConfigurationBean;
import mod.elfilibustero.sketch.beans.ProjectConfigurationBean;

public class ProjectConfigurationUtil {
	private final String sc_id;
	private final String path;
	private final String buildPath;

	public ProjectConfigurationUtil(String sc_id) {
		this.sc_id = sc_id;
		path = wq.b(sc_id) + File.separator + "project_config";
		buildPath = wq.b(sc_id) + File.separator + "build_config";
		if (!FileUtil.isExistFile(path)) {
			FileUtil.writeFile(path, new Gson().toJson(new ProjectConfigurationBean()));
		}

		if (!FileUtil.isExistFile(buildPath)) {
			FileUtil.writeFile(buildPath, new Gson().toJson(new BuildConfigurationBean()));
		}
	}

	public ProjectConfigurationBean getConfiguration() {
		return new Gson().fromJson(FileUtil.readFile(path), ProjectConfigurationBean.class);
	}

	public BuildConfigurationBean getBuildConfiguration() {
		return new Gson().fromJson(FileUtil.readFile(buildPath), BuildConfigurationBean.class);
	}

	public void updateConfiguration(ProjectConfigurationBean bean) {
		FileUtil.writeFile(path, new Gson().toJson(bean));
	}

	public void updateBuildConfiguration(BuildConfigurationBean bean) {
		FileUtil.writeFile(buildPath, new Gson().toJson(bean));
	}

	public String getMinSdk() {
		return getConfiguration().getMinSdk();
	}

	public void setMinSdk(String min) {
		ProjectConfigurationBean bean = getConfiguration();
		bean.setMinSdk(min);
		updateConfiguration(bean);
	}

	public String getTargetSdk() {
		return getConfiguration().getTargetSdk();
	}

	public void setTargetSdk(String target) {
		ProjectConfigurationBean bean = getConfiguration();
		bean.setTargetSdk(target);
		updateConfiguration(bean);
	}

	public String getApplicationClass() {
		return getConfiguration().getApplicationClass();
	}

	public void setApplicationClass(String appClass) {
		ProjectConfigurationBean bean = getConfiguration();
		bean.setApplicationClass(appClass);
		updateConfiguration(bean);
	}

	public String getUtilClass() {
		return getConfiguration().getUtilClass();
	}

	public void setUtilClass(String utilClass) {
		ProjectConfigurationBean bean = getConfiguration();
		bean.setUtilClass(utilClass);
		updateConfiguration(bean);
	}

	public String getOldMethods() {
		return getConfiguration().getOldMethods();
	}

	public void setOldMethods(String disable) {
		ProjectConfigurationBean bean = getConfiguration();
		bean.setOldMethods(disable);
		updateConfiguration(bean);
	}

	public String getBridgelessThemes() {
		return getConfiguration().getBridgelessThemes();
	}

	public void setBridgelessThemes(String enable) {
		ProjectConfigurationBean bean = getConfiguration();
		bean.setBridgelessThemes(enable);
		updateConfiguration(bean);
	}

	public String getDexer() {
		return getBuildConfiguration().getDexer();
	}

	public void setDexer(String dexer) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setDexer(dexer);
		updateBuildConfiguration(bean);
	}

	public String getClassPath() {
		return getBuildConfiguration().getClassPath();
	}

	public void setClassPath(String classPath) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setClassPath(classPath);
		updateBuildConfiguration(bean);
	}

	public String getLogcat() {
		return getBuildConfiguration().getLogcat();
	}

	public void setLogcat(String logcat) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setLogcat(logcat);
		updateBuildConfiguration(bean);
	}

	public String getHttpLegacy() {
		return getBuildConfiguration().getHttpLegacy();
	}

	public void setHttpLegacy(String httpLegacy) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setHttpLegacy(httpLegacy);
		updateBuildConfiguration(bean);
	}

	public String getAndroidJar() {
		return getBuildConfiguration().getAndroidJar();
	}

	public void setAndroidJar(String androidJar) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setAndroidJar(androidJar);
		updateBuildConfiguration(bean);
	}

	public String getWarning() {
		return getBuildConfiguration().getWarning();
	}

	public void setWarning(String warning) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setWarning(warning);
		updateBuildConfiguration(bean);
	}

	public String getJavaVersion() {
		return getBuildConfiguration().getJavaVersion();
	}

	public void setJavaVersion(String javaVersion) {
		BuildConfigurationBean bean = getBuildConfiguration();
		bean.setJavaVersion(javaVersion);
		updateBuildConfiguration(bean);
	}
}