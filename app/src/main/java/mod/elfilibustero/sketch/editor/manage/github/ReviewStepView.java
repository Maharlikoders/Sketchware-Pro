package mod.elfilibustero.sketch.editor.manage.github;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import a.a.a.bB;
import a.a.a.gB;

import com.sketchware.remod.databinding.ManageGithubPreviewBinding;

import mod.elfilibustero.sketch.beans.GitHubBean;

public class ReviewStepView extends LinearLayout implements SetupGitHubActivity.Callback {

    private ManageGithubPreviewBinding binding;

    public ReviewStepView(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        binding = ManageGithubPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        gB.b(this, 600, 200, null);
        binding.layoutSwitch.setOnClickListener(view -> binding.gitSwitch.setChecked(!binding.gitSwitch.isChecked()));
    }

    @Override
    public void getData(GitHubBean gitHubBean) {
        if (binding.gitSwitch.isChecked()) {
            gitHubBean.useYn = "Y";
        } else {
            gitHubBean.useYn = "N";
        }
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setData(GitHubBean gitHubBean) {
        binding.branch.setText(gitHubBean.branch);
        binding.repoUrl.setText(gitHubBean.repoUrl);
        binding.userName.setText(gitHubBean.username);
        binding.gitSwitch.setChecked(gitHubBean.useYn.equals("Y"));
    }
}
