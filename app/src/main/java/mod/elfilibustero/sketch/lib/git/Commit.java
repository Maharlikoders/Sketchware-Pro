package mod.elfilibustero.sketch.lib.git;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

import a.a.a.aB;

import com.sketchware.remod.R;
import com.sketchware.remod.databinding.PropertyPopupInputTextBinding;

import mod.SketchwareUtil;
import mod.elfilibustero.sketch.beans.GitCommitBean;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class Commit {

    private Executor executor = Executors.newSingleThreadExecutor();
    private Context context;
    private String sc_id;
    private GitHubUtil gitUtil;

    public Commit(Context context, String sc_id) {
        this.context = context;
        this.sc_id = sc_id;
        gitUtil = new GitHubUtil(sc_id);
    }

    public void execute(String fileName, Callback success) {
        String lastIndexOfFileName = fileName.substring(fileName.lastIndexOf('/') + 1);

        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(context), null, false);
        binding.tiInput.setHint("Update " + lastIndexOfFileName);

        aB dialog = new aB((Activity) context);
        dialog.a(R.drawable.widget_swipe_refresh);
        dialog.b("Commit");
        dialog.a(binding.getRoot());

        dialog.b("Commit", v -> {
            String message = binding.edInput.getText().toString();
            dialog.dismiss();
            executor.execute(() -> {
                final File localPath;
                try (Repository repository = gitUtil.getRepository()) {
                    localPath = repository.getWorkTree();
                    Git git = Git.open(localPath);
                    git.add().addFilepattern(fileName).call();

                    GitHubBean bean = gitUtil.getBean();
                    git.commit()
                        .setAuthor(new PersonIdent(bean.username, bean.email))
                        .setCommitter(new PersonIdent(bean.username, bean.email))
                        .setMessage(!message.isEmpty() ? message : "Update " + lastIndexOfFileName)
                        .call();
                    ThreadUtils.runOnUiThread(() -> SketchwareUtil.toast("Committed file " + fileName));
                    success.onSuccess(true);
                } catch (IOException | GitAPIException e) {
                    ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError("Commit failed " + e.getMessage()));
                    success.onSuccess(false);
                }
            });
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void execute(List<GitCommitBean> beans, Callback success) {
        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(context), null, false);
        binding.tiInput.setHint("Summary (required)");

        aB dialog = new aB((Activity) context);
        dialog.b("Commit");
        dialog.a(binding.getRoot());
        dialog.b("Commit", v -> {
            String message = binding.edInput.getText().toString();
            if (!message.isEmpty()) {
                dialog.dismiss();
                executor.execute(() -> {
                    final File localPath;
                    try (Repository repository = gitUtil.getRepository()) {
                        localPath = repository.getWorkTree();
                        Git git = Git.open(localPath);
                        //File file = new File(repository.getDirectory().getParent(), fileName);
                        for (GitCommitBean bean : beans) {
                            git.add().addFilepattern(bean.name).call();
                        }
                        GitHubBean bean = gitUtil.getBean();
                        git.commit()
                        .setAuthor(new PersonIdent(bean.username, bean.email))
                        .setCommitter(new PersonIdent(bean.username, bean.email))
                        .setMessage(message)
                        .call();
                        ThreadUtils.runOnUiThread(() -> {
                            SketchwareUtil.toast("Committed all file");
                        });
                        success.onSuccess(true);
                    } catch (IOException | GitAPIException e) {
                        ThreadUtils.runOnUiThread(() -> {
                            SketchwareUtil.toastError("Commit failed " + e.getMessage());
                        });
                        success.onSuccess(false);
                    }
                });
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public interface Callback {

        public void onSuccess(boolean success);
    }
}
