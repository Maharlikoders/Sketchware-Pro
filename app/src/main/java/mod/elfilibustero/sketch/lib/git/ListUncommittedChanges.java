package mod.elfilibustero.sketch.lib.git;

import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import mod.SketchwareUtil;
import mod.elfilibustero.sketch.beans.GitCommitBean;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;

public class ListUncommittedChanges {

    private String sc_id;

    public ListUncommittedChanges(String sc_id) {
        this.sc_id = sc_id;
    }

    public List<GitCommitBean> get() {
        List<GitCommitBean> list = new ArrayList<>();
        try (Repository repository = new GitHubUtil(sc_id).openRepository()) {
            Git git = new Git(repository);
            Status status = git.status().call();

            Set<String> allChanges = new HashSet<>();
            allChanges.addAll(status.getAdded());
            allChanges.addAll(status.getChanged());
            allChanges.addAll(status.getRemoved());
            allChanges.addAll(status.getModified());
            allChanges.addAll(status.getUncommittedChanges());
            allChanges.addAll(status.getMissing());
            allChanges.addAll(status.getUntracked());

            for (String change : allChanges) {
                GitCommitBean bean = new GitCommitBean(getCommitType(status, change), change);
                if (!isNameAlreadyExists(list, bean)) {
                    list.add(bean);
                }
            }
        } catch (IOException | GitAPIException e) {
            ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError("Error listing uncommitted changes: " + e.getMessage()));
        }
        return list;
    }

    private boolean isNameAlreadyExists(List<GitCommitBean> list, GitCommitBean bean) {
        return list.stream().anyMatch(commitBean -> commitBean.name.equals(bean.name));
    }

    private int getCommitType(Status status, String change) {
        if (status.getAdded().contains(change)) {
            return GitCommitBean.COMMIT_ADDED;
        } else if (status.getChanged().contains(change)) {
            return GitCommitBean.COMMIT_CHANGED;
        } else if (status.getRemoved().contains(change)) {
            return GitCommitBean.COMMIT_REMOVED;
        } else if (status.getModified().contains(change)) {
            return GitCommitBean.COMMIT_MODIFICATON;
        } else if (status.getUncommittedChanges().contains(change)) {
            return GitCommitBean.COMMIT_UNCOMMITTED;
        } else if (status.getMissing().contains(change)) {
            return GitCommitBean.COMMIT_MISSING;
        } else {
            return GitCommitBean.COMMIT_UNTRACKED;
        }
    }
}
