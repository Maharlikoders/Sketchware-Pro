package mod.elfilibustero.sketch.lib.git;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import a.a.a.aB;

import com.sketchware.remod.R;
import com.sketchware.remod.databinding.ManageGithubPopupPushBinding;
import com.sketchware.remod.databinding.PropertyPopupInputTextBinding;
import com.sketchware.remod.databinding.PropertyPopupCheckboxBinding;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class Clone {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Context context;
    private String sc_id;

    private GitHubUtil gitUtil;

    public Clone(Context context, String sc_id) {
        this.context = context;
        this.sc_id = sc_id;
        gitUtil = new GitHubUtil(sc_id);
    }

    public void execute(Callback callback) {
        String localRepositoryPath = gitUtil.getGitHubSrc();
        FileUtil.deleteFile(localRepositoryPath);

        var dialog = new aB((Activity) context);
        dialog.a(R.drawable.ic_repo_forked_16);
        var binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(context), null, false);
        binding.tiInput.setHint("https://github.com/Sketchware-Pro/Sketchware-Pro.git");
        binding.edInput.setSingleLine(true);

        var userBinding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(context), null, false);
        userBinding.tiInput.setHint("Username");
        userBinding.edInput.setSingleLine(true);
        var userLayout = userBinding.getRoot();

        var tokenBinding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(context), null, false);
        tokenBinding.tiInput.setHint("Token/Password");
        tokenBinding.edInput.setSingleLine(true);
        var tokenLayout = tokenBinding.getRoot();

        userLayout.setVisibility(View.GONE);
        tokenLayout.setVisibility(View.GONE);

        var checkBinding = PropertyPopupCheckboxBinding.inflate(LayoutInflater.from(context), null, false);

        checkBinding.check.setOnCheckedChangeListener((buttomView, isChecked) -> {
            if (isChecked) {
                userLayout.setVisibility(View.VISIBLE);
                tokenLayout.setVisibility(View.VISIBLE);
            } else {
                userLayout.setVisibility(View.GONE);
                tokenLayout.setVisibility(View.GONE);
            }
        });

        var container = new LinearLayout(context);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);

        container.addView(binding.getRoot());
        container.addView(userLayout);
        container.addView(tokenLayout);
        container.addView(checkBinding.getRoot());

        dialog.b("Clone");
        dialog.a(container);

        var progress = new aB((Activity) context);
        progress.a(R.drawable.ic_repo_forked_16);
        var progressBinding = ManageGithubPopupPushBinding.inflate(LayoutInflater.from(context), null, false);
        progress.b("Cloning...");
        progress.a(progressBinding.getRoot());
        progress.setCancelable(false);
        GitProgressMonitor monitor = new GitProgressMonitor(progressBinding.message, progressBinding.indicator);
        progress.b(Helper.getResString(R.string.common_word_cancel), v -> {
            monitor.cancel();
            progress.dismiss();
        });
        dialog.b("Clone", v -> {
            String remoteUrl = binding.edInput.getText().toString();
            String userName = userBinding.edInput.getText().toString();
            String token = tokenBinding.edInput.getText().toString();
            dialog.dismiss();

            boolean isPrivate = checkBinding.check.isChecked();
            if (!remoteUrl.isEmpty()) {
                progress.show();
                progressBinding.message.setText("Cloning to " + localRepositoryPath);
                Future<?> future = executor.submit(() -> {
                    final File localPath;
                    try {
                        CloneCommand clone = Git.cloneRepository();
                        clone.setURI(remoteUrl);
                        clone.setDirectory(new File(localRepositoryPath));
                        if (isPrivate) {
                            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, token));
                        }
                        clone.setProgressMonitor(monitor);
                        clone.call();

                        GitHubBean bean = new GitHubBean();
                        if (isPrivate) {
                            bean.username = userName;
                            bean.token = token;
                        }
                        bean.repoUrl = remoteUrl;
                        bean.branch = "main";
                        progress.dismiss();
                        callback.onSuccess(true, sc_id, bean);
                        return;
                    } catch (GitAPIException e) {
                        ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError("Cloning failed " + e.getMessage()));
                    }
                    progress.dismiss();
                    FileUtil.deleteFile(localRepositoryPath);
                    callback.onSuccess(false, sc_id, null);
                });

                executor.shutdown();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void execute(GitHubBean bean, Callback success) {
        var progress = new aB((Activity) context);
        progress.a(R.drawable.ic_repo_forked_16);
        var progressBinding = ManageGithubPopupPushBinding.inflate(LayoutInflater.from(context), null, false);
        progress.b("Cloning...");
        progress.a(progressBinding.getRoot());
        progress.setCancelable(false);
        GitProgressMonitor monitor = new GitProgressMonitor(progressBinding.message, progressBinding.indicator);
        progress.b(Helper.getResString(R.string.common_word_cancel), v -> {
            monitor.cancel();
            progress.dismiss();
        });
        progress.show();

        String localRepositoryPath = gitUtil.getGitHubSrc();
        FileUtil.deleteFile(localRepositoryPath);
        progressBinding.message.setText("Cloning to " + localRepositoryPath);
        Future<?> future = executor.submit(() -> {
            final File localPath;
            try {
                CloneCommand clone = Git.cloneRepository();
                clone.setURI(bean.repoUrl);
                clone.setDirectory(new File(localRepositoryPath));
                clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(bean.username, bean.token));
                clone.setProgressMonitor(monitor);
                clone.call();
                progress.dismiss();
                success.onSuccess(true, sc_id, bean);
                return;
            } catch (GitAPIException e) {
                ThreadUtils.runOnUiThread(() -> SketchwareUtil.toastError("Cloning failed " + e.getMessage()));
            }
            progress.dismiss();
            FileUtil.deleteFile(localRepositoryPath);
            success.onSuccess(false, sc_id, bean);
        });
        executor.shutdown();
    }

    public interface Callback {

        public void onSuccess(boolean success, String sc_id, GitHubBean bean);
    }
}
