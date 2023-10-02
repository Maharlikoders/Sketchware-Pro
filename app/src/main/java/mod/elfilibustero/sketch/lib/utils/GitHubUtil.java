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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import a.a.a.lC;
import a.a.a.wq;
import a.a.a.yB;

import com.besome.sketch.SketchApplication;
import com.besome.sketch.beans.BlockBean;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.GitCommitBean;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.beans.ProjectBean;
import mod.elfilibustero.sketch.beans.TempProjectBean;
import mod.elfilibustero.sketch.lib.viewmodel.GitHubViewModel;
import mod.hey.studios.editor.manage.block.ExtraBlockInfo;
import mod.hey.studios.editor.manage.block.v2.BlockLoader;
import mod.hey.studios.project.custom_blocks.CustomBlocksManager;
import mod.hey.studios.util.Helper;

public class GitHubUtil {

    private final String sc_id;
    private final String path;
    private Map<String, Object> project;

    public static final List<String> PROJECT_DATA_FILE = Arrays.asList("file", "library", "logic", "resource", "view", "github");
    public static final List<String> PROJECT_RESOURCES_FOLDER = Arrays.asList("fonts", "icons", "images", "sounds");

    public GitHubUtil(String sc_id) {
        this.sc_id = sc_id;
        path = wq.b(sc_id) + File.separator + "github";
        project = lC.b(sc_id);
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

    public CompletableFuture<Void> build() throws IOException, Exception {
        Executor executor = Executors.newFixedThreadPool(3);
        try (Repository repository = Git.open(new File(getGitHubSrc())).getRepository()) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    buildProjectFile();
                } catch (Exception e) {
                    throw new Exception(e.toString());
                }
                return null;
            }, executor)
            .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                try {
                    buildDataFile(repository);
                } catch (IOException e) {
                    throw new IOException(e.toString());
                }
                return null;
            }, executor))
            .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                buildProjectResources();
                return null;
            }, executor))
            .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                buildLocalLibrary();
                return null;
            }, executor))
            .thenComposeAsync(result -> CompletableFuture.supplyAsync(() -> {
                try {
                    buildCustomBlock(repository);
                } catch (IOException e) {
                    throw new IOException(e.toString());
                }
                return null;
            }, executor));
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

    private void buildProjectFile() throws Exception {
        String projectPath = getGitHubProject("project.json");
        String toProjectPath = wq.c(sc_id) + File.separator + "project";
        String content = FileUtil.readFile(projectPath);
        try {
            TempProjectBean temp = new Gson().fromJson(content, TempProjectBean.class);
            ProjectBean bean;
            if (temp != null) {
                bean = new ProjectBean();
                bean.setCustomIcon(temp.isCustomIcon());
                bean.setWorkspaceName(temp.getWorkspaceName());
                bean.setAppName(temp.getAppName());
                bean.setPackageName(temp.getPackageName());
                bean.setVersionCode(temp.getVersionCode());
                bean.setVersionName(temp.getVersionName());
                bean.setColorPrimary(temp.getColorPrimary());
                bean.setColorPrimaryDark(temp.getColorPrimaryDark());
                bean.setColorAccent(temp.getColorAccent());
                bean.setColorControlNormal(temp.getColorControlNormal());
                bean.setColorControlHighlight(temp.getColorControlHighlight());
                bean.setId(sc_id);
                bean.setSketchwareVer(GB.d(SketchApplication.getContext()));
                bean.setRegisteredDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
            } else {
                bean = getDefaultProjectFile();
            }
            FileUtil.makeDir(wq.c(sc_id) + "/");
            SketchwareUtil.toast(new Gson().toJson(bean));
            if (!SketchFileUtil.encrypt(new Gson().toJson(bean), toProjectPath)) {
                throw new RuntimeException("Failed to build project file");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
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
        bean.setRegisteredDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
        return bean;
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

        try {
            String srcDataDir = getGitHubProject("src/data");
            if (!FileUtil.isDirectory(srcDataDir)) {
                return;
            }
            NewFileUtil.copyDir(srcDataDir, data);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
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
            }
        }
    }

    private void buildLocalLibrary() {
        File localLibraryDest = new File(getGitHubProject("libs"));

        if (localLibraryDest.exists() && localLibraryDest.isDirectory()) {
            File[] localLibsContent = localLibraryDest.listFiles();
            if (localLibsContent != null) {
                for (File localLib : localLibsContent) {
                    File localLibRealPath = new File(FileUtil.getExternalStorageDir() + "/.sketchware/libs/local_libs", localLib.getName());
                    if (!localLibRealPath.exists()) {
                        localLibRealPath.mkdirs();
                        try {
                            FileUtil.copyDirectory(localLib, localLibRealPath);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }

    private void buildCustomBlock(Repository repository) throws IOException {
        String srcBlockInfo = getGitHubProject("src/block/custom_blocks");
        if (isFileExists(repository, "src/block/custom_blocks")) {
            FileUtil.copyFile(srcBlockInfo, wq.b(sc_id));
        }
        String srcBlocks = getGitHubProject("src/block/blocks.json");
        String customBlockTempPath = FileUtil.getExternalStorageDir() + File.separator + wq.l + File.separator + "block/My Block/temp.json";
        if (isFileExists(repository, "src/block/blocks.json")) {
            ArrayList<HashMap<String, Object>> custom_blocks = new ArrayList<>();
            try {
                ArrayList<HashMap<String, Object>> blocks = new Gson().fromJson(FileUtil.readFile(srcBlocks), Helper.TYPE_MAP_LIST);
                if (FileUtil.isExistFile(customBlockTempPath)) {
                    custom_blocks = new Gson().fromJson(FileUtil.readFile(customBlockTempPath), Helper.TYPE_MAP_LIST);
                }
                custom_blocks.addAll(blocks);
                FileUtil.writeFile(customBlockTempPath, new Gson().toJson(custom_blocks));
            } catch (Exception e) {
            }
        }
    }

    public void generate() {
        try (Repository repository = openRepository()) {
            //Project File (.sketchware/mysc/list/sc_id/project)
            generateProjectFile();

            //Project Data (.sketchware/data/sc_id/project)
            generateProjectData();

            //Project Resources
            generateProjectResources();

            //Local Library
            generateLocalLibrary();

            //Custom Block
            generateCustomBlock();
        } catch (Exception e) {
            SketchwareUtil.toastError(e.getMessage());
        }
        
    }

    private void writeFile(String from, String to) {
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
        writeFile(getFile(data), getFile(getGitHubProject("src")));
        writeFile(getLibrary(data), getLibrary(getGitHubProject("src")));
        writeFile(getLogic(data), getLogic(getGitHubProject("src")));
        writeFile(getResource(data), getResource(getGitHubProject("src")));
        writeFile(getView(data), getView(getGitHubProject("src")));

        try {
            Set<String> exclusions = new HashSet<>();
            for (String exclusion : PROJECT_RESOURCES_FOLDER) {
                exclusions.add(exclusion);
            }
            NewFileUtil.copyFiles(data, getGitHubProject("src/data"), exclusions);
            String files = data + File.separator + "files";
            String injections = data + File.separator + "injection";
            NewFileUtil.copyDir(files, getGitHubProject("src/data/files"));
            NewFileUtil.copyDir(injections, getGitHubProject("src/data/injection"));
        } catch (Exception ignored) {
        }
    }

    private void generateProjectResources() {
        File gitResourcePath = new File(getGitHubProject("src/resources"));
        FileUtil.makeDir(gitResourcePath.getAbsolutePath());

        try {
            for (String subFolder : PROJECT_RESOURCES_FOLDER) {
                File resSubFolder = new File(gitResourcePath, subFolder);
                FileUtil.makeDir(resSubFolder.getAbsolutePath());
                FileUtil.copyDirectory(new File(getResources(subFolder)), resSubFolder);
            }
        } catch (Exception ignored) {
        }
    }

    private void generateLocalLibrary() {
        String data = wq.b(sc_id);
        File localLibrary = new File(data, "local_library");

        if (localLibrary.exists()) {
            try {
                JSONArray array = new JSONArray(FileUtil.readFile(localLibrary.getAbsolutePath()));
                File localLibraryDest = new File(getGitHubProject("libs"));
                localLibraryDest.mkdirs();

                for (int i = 0; i < array.length(); i++) {
                    String jarPath = array.getJSONObject(i).optString("jarPath");
                    if (jarPath != null && !jarPath.isEmpty()) {
                        File jarFile = new File(jarPath).getParentFile();
                        if (jarFile != null) {
                            FileUtil.copyDirectory(jarFile, new File(localLibraryDest, jarFile.getName()));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
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
