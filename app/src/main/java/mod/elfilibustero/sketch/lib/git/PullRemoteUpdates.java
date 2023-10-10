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
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import a.a.a.aB;

import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageGithubPopupPushBinding;

import mod.SketchwareUtil;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class PullRemoteUpdates {

	private Executor executor = Executors.newSingleThreadExecutor();

	private Context context;
	private String sc_id;

	public PullRemoteUpdates(Context context, String sc_id) {
		this.context = context;
		this.sc_id = sc_id;
	}

	public void execute(Callback success) {
		GitHubUtil gitUtil = new GitHubUtil(sc_id);
		GitHubBean bean = gitUtil.getBean();

		var dialog = new aB((Activity) context);
        dialog.a(R.drawable.widget_swipe_refresh);
        dialog.b("Fetching...");
        var binding = ManageGithubPopupPushBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.a(binding.getRoot());
        dialog.setCancelable(false);
        var monitor = new GitProgressMonitor(binding.message, binding.indicator);
        dialog.b(Helper.getResString(R.string.common_word_cancel), v -> {
            success.onSuccess(false);
            monitor.cancel();
            dialog.dismiss();
        });

        binding.message.setText("Checking for updates from the repository...");
        binding.indicator.setIndeterminate(true);

        var credentials = new UsernamePasswordCredentialsProvider(bean.username, bean.token);

        executor.execute(() -> {
            final File localPath;
            try (Repository repository = gitUtil.getRepository()) {
                localPath = repository.getWorkTree();
                Git git = Git.open(localPath);

                FetchCommand fetchCommand = git.fetch();
                fetchCommand.setRemote(bean.repoUrl);
                fetchCommand.setRefSpecs("+refs/heads/*:refs/remotes/origin/*");
                fetchCommand.setProgressMonitor(monitor);
                fetchCommand.setCredentialsProvider(credentials);
                FetchResult result = fetchCommand.call();

                boolean hasUpdates = false;
                for (TrackingRefUpdate update : result.getTrackingRefUpdates()) {
                	if (update.getLocalName().equals("refs/remotes/origin/" + bean.branch)) {
                		hasUpdates = true;
                		break;
                	}
                }
                if (hasUpdates) {
                    ThreadUtils.runOnUiThread(() -> {
                        dialog.dismiss();

                        var pullDialog = new aB((Activity) context);
                        pullDialog.b("Pull");
                        pullDialog.a("Do you want to pull changes in remote repository?");
                        pullDialog.b("Yes", v -> {
                            try {
                                PullCommand pull = git.pull();
                                pull.setRemote("origin");
                                pull.setRemoteBranchName(bean.branch);
                                pull.setProgressMonitor(monitor);
                                pull.setCredentialsProvider(credentials);
                                pull.call();
                                success.onSuccess(true);
                            } catch (GitAPIException e) {
                                success.onSuccess(false);
                            }
                            pullDialog.dismiss();
                        });
                        pullDialog.a("No", v -> {
                            success.onSuccess(false);
                            pullDialog.dismiss();
                        });
                        pullDialog.show();
                    });
                } else {
                    success.onSuccess(false);
                	ThreadUtils.runOnUiThread(() -> {
	                    SketchwareUtil.toast("The remote branch: " + bean.branch + " is up to date.");
	                    dialog.dismiss();
	                });
                }
                git.close();
                return;
            } catch (JGitInternalException | GitAPIException | IOException e) {
                ThreadUtils.runOnUiThread(() -> {
                    SketchwareUtil.toastError("Fetch failed: " + e.getMessage());
                    dialog.dismiss();
                });
                success.onSuccess(false);
            }
        });
        dialog.show();
	}

    public interface Callback {

        void onSuccess(boolean success);
    }
}