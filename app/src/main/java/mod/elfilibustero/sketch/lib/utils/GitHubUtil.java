package mod.elfilibustero.sketch.lib.utils;

import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.RemoteRefUpdate.Status;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import a.a.a.GB;
import a.a.a.KB;
import a.a.a.MA;
import a.a.a.ProjectBuilder;
import a.a.a.aB;
import a.a.a.eC;
import a.a.a.hC;
import a.a.a.iC;
import a.a.a.kC;
import a.a.a.lC;
import a.a.a.nB;
import a.a.a.oB;
import a.a.a.wq;
import a.a.a.xq;
import a.a.a.yB;
import a.a.a.yq;

import com.besome.sketch.SketchApplication;
import com.besome.sketch.beans.BlockBean;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FilePathUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.GitCommitBean;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.beans.ProjectBean;
import mod.elfilibustero.sketch.beans.TempProjectBean;
import mod.hey.studios.editor.manage.block.ExtraBlockInfo;
import mod.hey.studios.editor.manage.block.v2.BlockLoader;
import mod.hey.studios.project.custom_blocks.CustomBlocksManager;
import mod.hey.studios.util.Helper;

public class GitHubUtil {

    private final oB file_utility = new oB();

    private final String sc_id;
    private final String path;
    private HashMap<String, Object> project;
    private yq project_metadata = null;

    public static final List<String> PROJECT_DATA_FILE = Arrays.asList("file", "library", "logic", "resource", "view", "github");
    public static final List<String> PROJECT_RESOURCES_FOLDER = Arrays.asList("fonts", "icons", "images", "sounds");

    public GitHubUtil(String sc_id) {
        this.sc_id = sc_id;
        path = wq.b(sc_id) + File.separator + "github";
        project = lC.b(sc_id);
        project_metadata = new yq(SketchApplication.getContext(), wq.d(sc_id), project);
    }

    public GitHubBean getBean() {
        GitHubBean bean = null;

        if (FileUtil.isExistFile(path)) {
            try {
                bean = new Gson().fromJson(FileUtil.readFile(path), GitHubBean.class);
            } catch (JsonParseException ignored) {
            }
        }

        return bean == null ? new GitHubBean() : bean;
    }

    public void setBean(GitHubBean bean) {
        FileUtil.writeFile(path, new Gson().toJson(bean != null ? bean : new GitHubBean()));
    }

    public CompletableFuture<Void> build(boolean updatingExistingProject) throws IOException, Exception {
        try (Repository repository = getRepository()) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    buildCustomBlock(repository);
                } catch (Exception e) {
                    ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError(e.getMessage()));
                }
                return null;
            })
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                try {
                    buildProjectResources();
                } catch (Exception e) {
                    ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError(e.getMessage()));
                }
                return null;
            }))
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                try {
                    buildDataFile(repository);
                } catch (Exception e) {
                    ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError(e.getMessage()));
                }
                return null;
            }))
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                try {
                    buildProjectFile(repository, updatingExistingProject);
                } catch (Exception e) {
                    ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError(e.getMessage()));
                }
                return null;
            }));
        }
    }

    private boolean isFileExists(Repository repository, String path) throws IOException {
        try (Git git = new Git(repository)) {
            RevCommit commit = git.log().setMaxCount(1).call().iterator().next();

            try (RevWalk walk = new RevWalk(repository)) {
                RevTree tree = commit.getTree();
                try (TreeWalk treeWalk = TreeWalk.forPath(repository, path, tree)) {
                    return treeWalk != null && treeWalk.getFileMode(0).equals(FileMode.REGULAR_FILE);
                }
            }
        } catch (NoFilepatternException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void buildProjectFile(Repository repository, boolean updatingExistingProject) throws Exception {
        String projectPath = getGitHubProject("project.json");
        String toProjectPath = wq.c(sc_id) + File.separator + "project";
        FileUtil.makeDir(wq.c(sc_id));

        if (!isFileExists(repository, "project.json")) {
            throw new Exception("Project file does not exist");
        }

        String content = FileUtil.readFile(projectPath);

        TempProjectBean temp = new Gson().fromJson(content, TempProjectBean.class);
        if (temp == null) {
            throw new Exception("Failed to parse project file");
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("sc_id", sc_id);
        data.put("my_sc_pkg_name", temp.getPackageName());
        data.put("my_ws_name", temp.getWorkspaceName());
        data.put("my_app_name", temp.getAppName());
        data.put("color_primary", temp.getColorPrimary());
        data.put("color_primary_dark", temp.getColorPrimaryDark());
        data.put("color_accent", temp.getColorAccent());
        data.put("color_control_normal", temp.getColorControlNormal());
        data.put("color_control_highlight", temp.getColorControlHighlight());
        data.put("custom_icon", temp.isCustomIcon());
        data.put("sc_ver_code", temp.getVersionCode());
        data.put("sc_ver_name", temp.getVersionName());
        data.put("sketchware_ver", GB.d(SketchApplication.getContext()));
        if (updatingExistingProject) {
            lC.b(sc_id, data);
        } else {
            data.put("my_sc_reg_dt", new nB().a("yyyyMMddHHmmss"));
            lC.a(sc_id, data);
            wq.a(SketchApplication.getContext(), sc_id);
            new oB().b(wq.b(sc_id));
        }
    }

    private ProjectBean getDefaultProjectFile() {
        String newProject = lC.c();
        ProjectBean bean = new ProjectBean();
        bean.setCustomIcon(false);
        bean.setWorkspaceName(newProject);
        bean.setAppName(newProject);
        bean.setPackageName("com.my." + newProject.toLowerCase());
        bean.setVersionCode("1");
        bean.setVersionName("1.0");
        bean.setColorPrimary(0xff008dcd);
        bean.setColorPrimaryDark(0xff0084c2);
        bean.setColorAccent(0xff008dcd);
        bean.setColorControlNormal(0xff57beee);
        bean.setColorControlHighlight(0x20008dcd);
        bean.setId(sc_id);
        bean.setSketchwareVer(GB.d(SketchApplication.getContext()));
        bean.setRegisteredDate(new nB().a("yyyyMMddHHmmss"));
        return bean;
    }

    private Map<String, Object> getOldDefaultProjectFile() {
        String newProject = lC.c();
        Map<String, Object> map = new HashMap<>();
        map.put("custom_icon", false);
        map.put("sc_ver_code", "1");
        map.put("sc_ver_name", "1.0");
        map.put("my_ws_name", newProject);
        map.put("my_app_name", newProject);
        map.put("my_sc_pkg_name", "com.my." + newProject.toLowerCase());
        map.put("color_primary", 0xFF008DCD);
        map.put("color_primary_dark", 0xFF0084C2);
        map.put("color_accent", 0xFF008DCD);
        map.put("color_control_normal", 0xFF57BEEE);
        map.put("color_control_highlight", 0x20008DCD);
        map.put("sc_id", sc_id);
        map.put("my_sc_pkg_name", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
        map.put("sketchware_ver", GB.d(SketchApplication.getContext()));
        return map;
    }

    private String getDefaultFileData() {
        StringBuilder builder = new StringBuilder();
        builder.append("@activity").append("\n");
        Map<String, Object> item = new HashMap<>();
        item.put("fileName", "main");
        item.put("fileType", 0);
        item.put("keyboardSetting", 0);
        item.put("options", 1);
        item.put("orientation", 0);
        item.put("theme", -1);
        builder.append(new Gson().toJson(item)).append("\n");
        builder.append("@customview").append("\n");
        return builder.toString();
    }

    private String getDefaultLibraryData() {
        StringBuilder builder = new StringBuilder();
        builder.append("@firebaseDB").append("\n");
        builder.append(getDefaultLibraryItem(0)).append("\n");
        builder.append("@compat").append("\n");
        builder.append(getDefaultLibraryItem(1)).append("\n");
        builder.append("@admob").append("\n");
        builder.append(getDefaultLibraryItem(2)).append("\n");
        builder.append("@googleMap").append("\n");
        builder.append(getDefaultLibraryItem(3)).append("\n");
        return builder.toString();
    }

    private String getDefaultLibraryItem(int type) {
        Map<String, Object> item = new HashMap<>();
        item.put("addUnits", new ArrayList<>());
        item.put("data", "");
        item.put("libType", type);
        item.put("reserved1", "");
        item.put("reserved2", "");
        item.put("reserved3", "");
        item.put("testDevices", new ArrayList<>());
        item.put("useYn", "N");
        return new Gson().toJson(item);
    }

    private String getDefaultResourceData() {
        StringBuilder builder = new StringBuilder();
        builder.append("@images").append("\n");
        builder.append("@sounds").append("\n");
        builder.append("@fonts").append("\n");
        return builder.toString();
    }

    private String getDefaultViewData() {
        StringBuilder builder = new StringBuilder();
        builder.append("@main.xml_fab").append("\n");
        builder.append("{\"adSize\":\"\",\"adUnitId\":\"\",\"alpha\":1.0,\"checked\":0,\"choiceMode\":0,\"clickable\":1,\"convert\":\"\",\"customView\":\"\",\"dividerHeight\":1,\"enabled\":1,\"firstDayOfWeek\":1,\"id\":\"_fab\",\"image\":{\"rotate\":0,\"scaleType\":\"CENTER\"},\"indeterminate\":\"false\",\"index\":0,\"inject\":\"\",\"layout\":{\"backgroundColor\":16777215,\"borderColor\":-16740915,\"gravity\":0,\"height\":-2,\"layoutGravity\":85,\"marginBottom\":16,\"marginLeft\":16,\"marginRight\":16,\"marginTop\":16,\"orientation\":-1,\"paddingBottom\":0,\"paddingLeft\":0,\"paddingRight\":0,\"paddingTop\":0,\"weight\":0,\"weightSum\":0,\"width\":-2},\"max\":100,\"parentType\":-1,\"preIndex\":0,\"preParentType\":0,\"progress\":0,\"progressStyle\":\"?android:progressBarStyle\",\"scaleX\":1.0,\"scaleY\":1.0,\"spinnerMode\":1,\"text\":{\"hint\":\"\",\"hintColor\":-10453621,\"imeOption\":0,\"inputType\":1,\"line\":0,\"singleLine\":0,\"text\":\"\",\"textColor\":-16777216,\"textFont\":\"default_font\",\"textSize\":12,\"textType\":0},\"translationX\":0.0,\"translationY\":0.0,\"type\":16}");
        return builder.toString();
    }

    private void buildDataFile(Repository repository) throws Exception {
        String data = wq.b(sc_id);
        String errorMessage = "%s data not found";
        FileUtil.makeDir(data + "/");
        String file = FileUtil.readFile(getFile(getGitHubProject("src")));
        //if (!isFileExists(repository, getFile("src"))) {
        //    throw new FileNotFoundException(String.format(errorMessage, "File"));
        //}
        if (isFileExists(repository, getFile("src"))) {
            if (!SketchFileUtil.encrypt(file, getFile(data))) {
                throw new RuntimeException("Failed to build file data");
            }
        } else if (!SketchFileUtil.encrypt(getDefaultFileData(), getFile(data))) {
            throw new RuntimeException("Failed to build file data");
        }

        String library = FileUtil.readFile(getLibrary(getGitHubProject("src")));
        //if (!isFileExists(repository, getLibrary("src"))) {
        //   throw new FileNotFoundException(String.format(errorMessage, "Library"));
        //}
        if (isFileExists(repository, getLibrary("src"))) {
            if (!SketchFileUtil.encrypt(library, getLibrary(data))) {
                throw new RuntimeException("Failed to build library data");
            }
        } else if (!SketchFileUtil.encrypt(getDefaultLibraryData(), getLibrary(data))) {
            throw new RuntimeException("Failed to build library data");
        }

        String logic = FileUtil.readFile(getLogic(getGitHubProject("src")));
        //if (!isFileExists(repository, getLogic("src"))) {
        //    throw new FileNotFoundException(String.format(errorMessage, "Logic"));
        //}
        if (isFileExists(repository, getLogic("src"))) {
            if (!SketchFileUtil.encrypt(logic, getLogic(data))) {
                throw new RuntimeException("Failed to build logic data");
            }
        } else {
            FileUtil.writeFile(getLogic(data), "");
        }

        String res = FileUtil.readFile(getResource(getGitHubProject("src")));
        //if (!isFileExists(repository, getResource("src"))) {
        //    throw new FileNotFoundException(String.format(errorMessage, "Resource"));
        //}
        if (isFileExists(repository, getResource("src"))) {
            if (!SketchFileUtil.encrypt(res, getResource(data))) {
                throw new RuntimeException("Failed to build resource data");
            }
        } else if (!SketchFileUtil.encrypt(getDefaultResourceData(), getResource(data))) {
            throw new RuntimeException("Failed to build resource data");
        }

        String view = FileUtil.readFile(getView(getGitHubProject("src")));
        //if (!isFileExists(repository, getView("src"))) {
        //    throw new FileNotFoundException(String.format(errorMessage, "View"));
        //}
        if (isFileExists(repository, getView("src"))) {
            if (!SketchFileUtil.encrypt(view, getView(data))) {
                throw new RuntimeException("Failed to build view data");
            }
        } else if (!SketchFileUtil.encrypt(getDefaultViewData(), getView(data))) {
            throw new RuntimeException("Failed to build view data");
        }

        String srcDataDir = getGitHubProject("src/data");
        if (!FileUtil.isDirectory(srcDataDir)) {
            return;
        }
        NewFileUtil.copyDir(srcDataDir, data);
    }

    private void buildProjectResources() {
        File resourcePath = new File(FileUtil.getExternalStorageDir(), wq.l);
        String srcResDir = getGitHubProject("src/resources");
        if (FileUtil.isDirectory(srcResDir)) {
            try {
                for (String subFolder : PROJECT_RESOURCES_FOLDER) {
                    String resFolder = srcResDir + File.separator + subFolder;
                    if (FileUtil.isDirectory(resFolder)) {
                        File resSubFolderPath = new File(resourcePath, subFolder + File.separator + sc_id);
                        String resSubFolder = resSubFolderPath.getAbsolutePath();
                        FileUtil.makeDir(resSubFolder);
                        FileUtil.copyDirectory(new File(resFolder), new File(resSubFolder));
                    }
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private void buildCustomBlock(Repository repository) throws Exception {
        String srcBlockInfo = getGitHubProject("src/block/custom_blocks");
        if (isFileExists(repository, "src/block/custom_blocks")) {
            FileUtil.copyFile(srcBlockInfo, wq.b(sc_id));
        }
        String srcBlocks = getGitHubProject("src/block/blocks.json");
        String customBlockDataPath = FileUtil.getExternalStorageDir() + File.separator + wq.l + File.separator + "block" + File.separator + sc_id + File.separator + "data.json";
        if (isFileExists(repository, "src/block/blocks.json")) {
            ArrayList<HashMap<String, Object>> custom_blocks = new ArrayList<>();
            ArrayList<HashMap<String, Object>> blocks = new Gson().fromJson(FileUtil.readFile(srcBlocks), Helper.TYPE_MAP_LIST);
            if (FileUtil.isExistFile(customBlockDataPath)) {
                custom_blocks = new Gson().fromJson(FileUtil.readFile(customBlockDataPath), Helper.TYPE_MAP_LIST);
            }
            custom_blocks.addAll(blocks);
            FileUtil.writeFile(customBlockDataPath, new Gson().toJson(custom_blocks));
        }
    }

    public CompletableFuture<Void> generate() {
        Executor executor = Executors.newFixedThreadPool(3);
        try (Repository repository = openRepository()) {
            return CompletableFuture.supplyAsync(() -> {
                //Project File (.sketchware/mysc/list/sc_id/project)
                generateProjectFile();
                return null;
            }, executor)
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                //Project File (.sketchware/mysc/list/sc_id/project)
                generateProjectFile();
                return null;
            }, executor))
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                //Project Data (.sketchware/data/sc_id/)
                generateProjectData();
                return null;
            }, executor))
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                //Project Resources
                generateProjectResources();
                return null;
            }, executor))
                .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                //Custom Block
                generateCustomBlock();
                return null;
            }, executor));
        } catch (Exception e) {
            SketchwareUtil.toastError("Updating repository failed: " + e.getMessage());
            return null;
        }
    }

    private void writeFile(String from, String to) throws Exception {
        if (FileUtil.isExistFile(from)) {
            String content = SketchFileUtil.decrypt(from);
            if (content != null) {
                FileUtil.writeFile(to, content);
            }
        }
    }

    private void generateProjectFile() {
        String projectFile = wq.c(sc_id) + File.separator + "project";
        try {
            String content = SketchFileUtil.decrypt(projectFile);
            ProjectBean bean = new Gson().fromJson(content, ProjectBean.class);
            TempProjectBean temp = new TempProjectBean();
            temp.setCustomIcon(bean.isCustomIcon());
            temp.setWorkspaceName(bean.getWorkspaceName());
            temp.setAppName(bean.getAppName());
            temp.setPackageName(bean.getPackageName());
            temp.setVersionCode(bean.getVersionCode());
            temp.setVersionName(bean.getVersionName());
            temp.setColorPrimary(bean.getColorPrimary());
            temp.setColorPrimaryDark(bean.getColorPrimaryDark());
            temp.setColorAccent(bean.getColorAccent());
            temp.setColorControlNormal(bean.getColorControlNormal());
            temp.setColorControlHighlight(bean.getColorControlHighlight());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileUtil.writeFile(getGitHubProject("project.json"), gson.toJson(temp));
        } catch (Exception ignored) {
        }
    }

    private void generateProjectData() {
        String data = wq.b(sc_id);
        try {
            writeFile(getFile(data), getFile(getGitHubProject("src")));
            writeFile(getLibrary(data), getLibrary(getGitHubProject("src")));
            writeFile(getLogic(data), getLogic(getGitHubProject("src")));
            writeFile(getResource(data), getResource(getGitHubProject("src")));
            writeFile(getView(data), getView(getGitHubProject("src")));

            String toData = getGitHubProject("src/data");
            if (new File(data).exists()) {
                if (isFileSizeChanged(data, toData)) {
                    FileUtil.deleteFile(toData);
                    NewFileUtil.copyDir(data, toData);
                }

                for (String exclusion : PROJECT_DATA_FILE) {
                    String dataFile = toData + File.separator + exclusion;
                    if (FileUtil.isExistFile(dataFile)) {
                        FileUtil.deleteFile(dataFile);
                    }
                }
            } else {
                FileUtil.deleteFile(toData);
            }
        } catch (Exception ignored) {
        }
    }

    private void generateProjectResources() {
        File gitResourcePath = new File(getGitHubProject("src/resources"));
        try {
            for (String subFolder : PROJECT_RESOURCES_FOLDER) {
                File resFolder = new File(getResources(subFolder));
                File resSubFolder = new File(gitResourcePath, subFolder);
                if (resFolder.exists()) {
                    if (isFileSizeChanged(resFolder.getAbsolutePath(), resSubFolder.getAbsolutePath())) {
                        FileUtil.deleteFile(resSubFolder.getAbsolutePath());
                        FileUtil.copyDirectory(resFolder, resSubFolder);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void generateCustomBlock() {
        //Custom Block Info
        CustomBlocksManager customBlock = new CustomBlocksManager(sc_id);
        List<ExtraBlockInfo> blockInfos = new ArrayList<>();
        List<String> opCodes = new ArrayList<>();
        for (BlockBean bean : customBlock.getUsedBlocks()) {
            blockInfos.add(BlockLoader.getBlockInfo(bean.opCode));
            opCodes.add(bean.opCode);
        }
        if (!blockInfos.isEmpty()) {
            String blockInfoStr = new Gson().toJson(blockInfos);
            FileUtil.writeFile(getGitHubProject("src/block/custom_blocks"), blockInfoStr);
        }

        //Used Custom Block
        String customBlockPath = FileUtil.getExternalStorageDir() + File.separator + wq.l + File.separator + "block/My Block/block.json";
        if (FileUtil.isExistFile(customBlockPath)) {
            ArrayList<HashMap<String, Object>> custom_blocks = new Gson().fromJson(FileUtil.readFile(customBlockPath), Helper.TYPE_MAP_LIST);
            List<Map<String, Object>> blocks = new ArrayList<>();
            int blocksCount = 0;
            for (HashMap<String, Object> block : custom_blocks) {
                String name = (String) block.get("name");
                if (opCodes.contains(name)) {
                    blocks.add(custom_blocks.get(blocksCount));
                }
                blocksCount++;
            }
            if (!blocks.isEmpty()) {
                String blockStr = new Gson().toJson(blocks);
                FileUtil.writeFile(getGitHubProject("src/block/blocks.json"), blockStr);
            }
        }

    }

    public void generateSrc() throws Exception {
        hC fileManager = new hC(sc_id);
        kC resourceManager = new kC(sc_id);
        eC logicManager = new eC(sc_id);
        iC libraryManager = new iC(sc_id);
        fileManager.i();
        resourceManager.s();
        logicManager.g();
        logicManager.e();
        libraryManager.i();

        /* Extract project type template */
        project_metadata.a(SketchApplication.getContext(), wq.e(xq.a(sc_id) ? "600" : sc_id));

        /* Start generating project files */
        ProjectBuilder builder = new ProjectBuilder(SketchApplication.getContext(), project_metadata);
        project_metadata.a(libraryManager, fileManager, logicManager, true);
        builder.buildBuiltInLibraryInformation();
        project_metadata.b(fileManager, logicManager, libraryManager, builder.getBuiltInLibraryManager());
        if (yB.a(lC.b(sc_id), "custom_icon")) {
            project_metadata.a(wq.e() + File.separator + sc_id + File.separator + "icon.png");
        }
        project_metadata.a();
        resourceManager.b(project_metadata.resDirectoryPath + File.separator + "drawable-xhdpi");
        resourceManager.c(project_metadata.resDirectoryPath + File.separator + "raw");
        resourceManager.a(project_metadata.assetsPath + File.separator + "fonts");
        project_metadata.f();

        /* It makes no sense that those methods aren't static */
        FilePathUtil util = new FilePathUtil();
        File pathJava = new File(util.getPathJava(sc_id));
        File pathResources = new File(util.getPathResource(sc_id));
        File pathAssets = new File(util.getPathAssets(sc_id));
        File pathNativeLibraries = new File(util.getPathNativelibs(sc_id));

        if (pathJava.exists()) {
            FileUtil.copyDirectory(pathJava, new File(project_metadata.javaFilesPath + File.separator + project_metadata.packageNameAsFolders));
        }
        if (pathResources.exists()) {
            FileUtil.copyDirectory(pathResources, new File(project_metadata.resDirectoryPath));
        }
        if (pathAssets.exists()) {
            FileUtil.copyDirectory(pathAssets, new File(project_metadata.assetsPath));
        }
        if (pathNativeLibraries.exists()) {
            FileUtil.copyDirectory(pathNativeLibraries, new File(project_metadata.generatedFilesPath, "jniLibs"));
        }

        ArrayList<String> toCompress = new ArrayList<>();
        toCompress.add(project_metadata.projectMyscPath);
        String exportedFilename = yB.c(project, "my_ws_name") + ".zip";

        String exportedSourcesZipPath = wq.s() + File.separator + "export_src" + File.separator + exportedFilename;
        if (file_utility.e(exportedSourcesZipPath)) {
            file_utility.c(exportedSourcesZipPath);
        }

        ArrayList<String> toExclude = new ArrayList<>();
        if (!new File(new FilePathUtil().getPathJava(sc_id) + File.separator + "SketchApplication.java").exists()) {
            toExclude.add("SketchApplication.java");
        }
        toExclude.add("DebugActivity.java");

        new KB().a(exportedSourcesZipPath, toCompress, toExclude);
        project_metadata.e();
    }

    public String getGitHubSrc() {
        return wq.getGitHubSrc(sc_id);
    }

    private String getGitHubProject(String path) {
        return getGitHubSrc() + File.separator + path;
    }

    private String getResources(String subFolder) {
        return FileUtil.getExternalStorageDir() + File.separator + wq.l + File.separator + subFolder + File.separator + sc_id;
    }

    private String getFile(String path) {
        return path + File.separator + "file";
    }

    private String getLibrary(String path) {
        return path + File.separator + "library";
    }

    private String getLogic(String path) {
        return path + File.separator + "logic";
    }

    private String getResource(String path) {
        return path + File.separator + "resource";
    }

    private String getView(String path) {
        return path + File.separator + "view";
    }

    private boolean isFileSizeChanged(String source, String destination) throws IOException {
        long sourceSize = NewFileUtil.getFolderSize(source);
        long destSize = NewFileUtil.getFolderSize(destination);
        return sourceSize != destSize;
    }

    public List<String> getAllBranches() {
        List<String> branches = new ArrayList<>();
        try (Repository repository = getRepository()) {
            Git git = new Git(repository);
            List<Ref> allBranches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            for (Ref branch : allBranches) {
                String branchName = branch.getName();
                if (branchName.startsWith("refs/heads/")) {
                    branchName = branchName.substring("refs/heads/".length());
                } else if (branchName.startsWith("refs/remotes/")) {
                    branchName = branchName.substring("refs/remotes/".length());
                }
                branches.add(branchName);
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public Repository openRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        String localRepository = getGitHubSrc() + "/.git";
        if (FileUtil.isExistFile(localRepository) && FileUtil.isDirectory(localRepository)) {
            return builder.setGitDir(new File(localRepository))
                .readEnvironment()
                .findGitDir()
                .build();
        } else {
            return null;
        }
    }

    public Repository getRepository() throws IOException, GitAPIException {
        String localRepository = getGitHubSrc() + "/.git";
        if (!FileUtil.isExistFile(localRepository) && !FileUtil.isDirectory(localRepository)) {
            Git.init().setDirectory(new File(getGitHubSrc())).call();
            ThreadUtils.runOnUiThread(() -> SketchwareUtil.toast("Created a new repository at " + getGitHubSrc()));
        }
        Repository repository = new RepositoryBuilder().setGitDir(new File(localRepository)).build();
        return repository;
    }

    public static void printDiff(DiffEntry diff) {
        //System.out.printf("Diff: %-6s: %s%6s -> %6s: %s%n");
    }
}
