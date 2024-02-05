package mod.elfilibustero.sketch.lib.git;

import android.widget.TextView;
import org.eclipse.jgit.lib.ProgressMonitor;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class GitProgressMonitor implements ProgressMonitor {

    private TextView message;
    private LinearProgressIndicator progress;
    private boolean canceled = false;

    public GitProgressMonitor(TextView message, LinearProgressIndicator progress) {
        this.message = message;
        this.progress = progress;
    }

    public void cancel() {
        canceled = true;
    }

    @Override
    public void start(int totalTasks) {
        ThreadUtils.runOnUiThread(() -> progress.setMax(totalTasks));
    }

    @Override
    public void beginTask(String title, int totalWork) {
        ThreadUtils.runOnUiThread(() -> message.setText(title));
    }

    @Override
    public void update(int completed) {
        ThreadUtils.runOnUiThread(() -> progress.setProgress(completed));
    }

    @Override
    public void showDuration(boolean show) {
    }

    @Override
    public void endTask() {
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }
}
