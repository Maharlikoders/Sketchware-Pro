package mod.elfilibustero.sketch.lib.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import a.a.a.wq;

import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;

public class ExternalLibraryHandler {

	private final String sc_id;
	private final String dataPath;
	private final String initialPath;
	public final List<ExternalLibraryBean> beans;

	public ExternalLibraryHandler(String sc_id) {
		this.sc_id = sc_id;
		dataPath = wq.b(sc_id) + File.separator +"external_library";
		initialPath = wq.getExternalLibrary(sc_id);
		if (FileUtil.isExistFile(dataPath)) {
			try {
	            String content = FileUtil.readFile(dataPath);
	            if (content != null && !content.isEmpty()) {
	                beans = new Gson().fromJson(FileUtil.readFile(dataPath), new TypeToken<List<ExternalLibraryBean>>() {
	                }.getType());
	            }
	            if (beans != null) {
	            	return;
	            }
	        } catch (Exception e) {
	        }
		}
		beans = new ArrayList<>();
		FileUtil.writeFile(dataPath, "[]");
	}

	public List<String> get(ResourceType type) {
		return get(type.getType());
	}

	public List<String> get(String file) {
		List<String> jars = new ArrayList<>();
		for (ExternalLibraryBean bean : beans) {
			String path = initialPath + File.separator + bean.name + File.separator + file;
			if (bean.useYn.equals("Y") && FileUtil.isExistFile(path)) {
				jars.add(path);
			}
		}
		return jars;
	}

	public String getJar() {
		return String.join(":", get(ResourceType.JAR));
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