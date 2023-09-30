package mod.elfilibustero.sketch.editor.manage.github;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import a.a.a.bB;
import a.a.a.gB;

import com.sketchware.remod.databinding.ManageGithubSetCredentialBinding;

import mod.elfilibustero.sketch.beans.GitHubBean;

public class SetupCredentialStepView extends LinearLayout implements SetupGitHubActivity.Callback {

    private ManageGithubSetCredentialBinding binding;
    private EditText username;
    private EditText email;
    private EditText repoUrl;
    private EditText token;
    private EditText branch;

    public SetupCredentialStepView(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        binding = ManageGithubSetCredentialBinding.inflate(LayoutInflater.from(context), this, true);
        gB.b(this, 600, 200, null);
        username = binding.username;
        email = binding.email;
        token = binding.token;
        repoUrl = binding.repoUrl;
        branch = binding.branch;
    }

    @Override
    public void getData(GitHubBean gitHubBean) {
        gitHubBean.branch = branch.getText().toString();
        gitHubBean.email = email.getText().toString();
        gitHubBean.repoUrl = repoUrl.getText().toString();
        gitHubBean.token = token.getText().toString();
        gitHubBean.username = username.getText().toString();
    }

    @Override
    public String getUrl() {
        return "https://github.com/settings/tokens/new";
    }

    @Override
    public boolean isValid() {
        if (!TextUtils.isEmpty(username.getText().toString())
            && !TextUtils.isEmpty(email.getText().toString())
            && !TextUtils.isEmpty(token.getText().toString())
            && !TextUtils.isEmpty(repoUrl.getText().toString())
            && !TextUtils.isEmpty(branch.getText().toString())) {
            return true;
        } else {
            bB.a(getContext(), "Please fillup all the fields", 1).show();
            return false;
        }
    }

    @Override
    public void setData(GitHubBean gitHubBean) {
        branch.setText(gitHubBean.branch);
        email.setText(gitHubBean.email);
        repoUrl.setText(gitHubBean.repoUrl);
        token.setText(gitHubBean.token);
        username.setText(gitHubBean.username);
    }
}
