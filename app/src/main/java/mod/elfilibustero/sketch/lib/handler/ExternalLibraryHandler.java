package mod.elfilibustero.sketch.lib.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import a.a.a.wq;

import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;
import mod.elfilibustero.sketch.beans.LibrariesBean;

public class ExternalLibraryHandler {

	private final String sc_id;
	public final String dataPath;
	public final String initialPath;
	public static final int DEPENDENCIES = 0;
	public static final int LIBRARIES = 1;
	public ExternalLibraryBean externalLibrary;
	public List<LibrariesBean> beans;

	public ExternalLibraryHandler(String sc_id) {
		this.sc_id = sc_id;
		dataPath = wq.b(sc_id) + File.separator +"external_library";
		initialPath = wq.getExternalLibrary(sc_id);
		if (FileUtil.isExistFile(dataPath)) {
			externalLibrary = getBean();
			return;
		}
		externalLibrary = new ExternalLibraryBean();
		FileUtil.writeFile(dataPath, "{}");
	}

	public ExternalLibraryBean getBean() {
        ExternalLibraryBean externalLibrary = null;
        if (FileUtil.isExistFile(dataPath)) {
        	try {
        		externalLibrary = new Gson().fromJson(FileUtil.readFile(dataPath), ExternalLibraryBean.class);
	        } catch (Exception e) {
	        }
        }
        return externalLibrary == null || externalLibrary.isEmpty() ? new ExternalLibraryBean() : externalLibrary;
    }

    public void setBean(ExternalLibraryBean externalLibrary) {
        FileUtil.writeFile(dataPath, new Gson().toJson(externalLibrary != null ? externalLibrary : new ExternalLibraryBean()));
    }

	public List<String> get(ResourceType type) {
		return get(type.getType());
	}

	public List<String> get(String file) {
		List<String> jars = new ArrayList<>();
		for (LibrariesBean bean : externalLibrary.getLibraries()) {
			String path = initialPath + File.separator + bean.name + File.separator + file;
			if (bean.useYn.equals("Y") && FileUtil.isExistFile(path)) {
				jars.add(path);
			}
		}
		return jars;
	}

	public String getJar() {
		return ":" + String.join(":", get(ResourceType.JAR));
	}

	public String getPackageName() {
		List<String> packages = new ArrayList<>();
		for (String path : get("config")) {
			packages.add(FileUtil.readFile(path));
		}
		return String.join(":", packages);
	}

	public enum ResourceType {
		RES("res"),
		DEX("classes.dex"),
		JAR("classes.jar"),
		ANDROID_MANIFEST("AndroidManifest.xml"),
		JNI("jni"),
		ASSETS("assets"),
		PROGUARD("proguard.txt");

		private final String type;

		ResourceType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
}
