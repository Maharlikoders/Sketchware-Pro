package mod.elfilibustero.sketch.lib.utils;

import com.google.gson.Gson;

import java.io.File;

import a.a.a.wq;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.ProjectConfigurationBean;

public class ProjectConfigurationUtil {
	private final String sc_id;
	private final String path;

	public ProjectConfigurationUtil(String sc_id) {
		this.sc_id = sc_id;
		path = wq.b(sc_id) + File.separator + "project_config";
		if (!FileUtil.isExistFile(path)) {
			FileUtil.writeFile(path, new Gson().toJson(new ProjectConfigurationBean()));
		}
	}

	public ProjectConfigurationBean getConfiguration() {
		return new Gson().fromJson(FileUtil.readFile(path), ProjectConfigurationBean.class);
	}

	public void updateConfiguration(ProjectConfigurationBean bean) {
		FileUtil.writeFile(path, new Gson().toJson(bean));
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
}