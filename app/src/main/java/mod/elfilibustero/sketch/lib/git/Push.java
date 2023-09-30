package mod.elfilibustero.sketch.lib.git;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.RemoteRefUpdate.Status;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import a.a.a.aB;

import com.sketchware.remod.R;
import com.sketchware.remod.databinding.ManageGithubPopupPushBinding;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class Push {

    private Executor executor = Executors.newSingleThreadExecutor();

    private Context context;
    private String sc_id;

    public Push(Context context, String sc_id) {
        this.context = context;
        this.sc_id = sc_id;
    }

    public void execute(boolean isForce) {
        GitHubUtil util = new GitHubUtil(sc_id);
        GitHubBean bean = util.getBean();

        if (bean.username.isEmpty() && bean.token.isEmpty()) {
            SketchwareUtil.toastError("Please fillup the github details first");
            return;
        }

        String localRepository = util.getGitHubSrc();
        if (!FileUtil.isDirectory(localRepository)) {
            FileUtil.makeDir(localRepository);
        }

        if (!FileUtil.isExistFile(localRepository)) {
            SketchwareUtil.toastError(localRepository + " is not exists!");
            return;
        }

        var dialog = new aB((Activity) context);
        dialog.a(R.drawable.ic_repo_push_16);
        var binding = ManageGithubPopupPushBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.a(binding.getRoot());
        dialog.setCancelable(false);
        GitProgressMonitor monitor = new GitProgressMonitor(binding.message, binding.indicator);
        dialog.b(Helper.getResString(R.string.common_word_cancel), v -> {
            monitor.cancel();
            dialog.dismiss();
        });
        executor.execute(() -> {
            final File localPath;
            try (Repository repository = util.getRepository()) {
                localPath = repository.getWorkTree();
                Git git = Git.open(localPath);
                PushCommand pushCommand = git.push();
                pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(bean.username, bean.token));
                pushCommand.setRemote(bean.repoUrl);
                pushCommand.setRefSpecs(new RefSpec(bean.branch));
                pushCommand.setForce(isForce);

                Iterable<PushResult> results = pushCommand.setProgressMonitor(monitor).call();
                handlePushResults(results);
                ThreadUtils.runOnUiThread(() -> {
                    SketchwareUtil.toast("Push Successfully");
                    dialog.dismiss();
                });
            } catch (JGitInternalException | GitAPIException | IOException e) {
                ThreadUtils.runOnUiThread(() -> {
                    SketchwareUtil.toastError("Push failed " + e.getMessage());
                    dialog.dismiss();
                });
            }
        });
        dialog.show();
    }

    private void handlePushResults(Iterable<PushResult> results) {
        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                if (update.getStatus() != Status.OK && update.getStatus() != Status.UP_TO_DATE) {
                    String errorMessage = "Push failed: " + update.getStatus();
                    throw new RuntimeException(errorMessage);
                }
            }
        }
    }
}
