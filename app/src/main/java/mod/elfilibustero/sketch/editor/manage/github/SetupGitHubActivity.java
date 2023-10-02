package mod.elfilibustero.sketch.editor.manage.github;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;

import a.a.a.aB;
import a.a.a.wq;
import a.a.a.xB;

import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageGithubSetupBinding;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.git.Clone;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class SetupGitHubActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageGithubSetupBinding binding;
    private GitHubBean bean;
    private String sc_id;
    private Callback callback;
    private String[] stepTitles;
    private String[] stepDescriptions;
    private int stepPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.ani_fade_in, R.anim.ani_fade_out);
        binding = ManageGithubSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bean = getIntent().getParcelableExtra("github");
        showStep(stepPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (stepPosition > 0) {
            showStep(--stepPosition);
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.create_token) {
            goCreateToken();
        } else if (id == R.id.go_to_github) {
            goToGitHub();
        } else if (id == R.id.next_step) {
            nextStep();
        } else if (id == R.id.previous_step) {
            onBackPressed();
        } else if (id == R.id.import_button) {
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.ani_fade_in, R.anim.ani_fade_out);
    }

    private void init() {
        stepTitles = new String[]{
            Helper.getResString(R.string.design_github_setting_step1_title),
            Helper.getResString(R.string.design_github_setting_step2_title)
        };

        stepDescriptions = new String[]{
            Helper.getResString(R.string.design_github_setting_step1_desc),
            Helper.getResString(R.string.design_github_setting_step2_desc)
        };

        binding.goToGithub.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.previousStep.setOnClickListener(this);
        binding.nextStep.setOnClickListener(this);
        binding.importButton.setOnClickListener(this);
        binding.createToken.setOnClickListener(this);
    }

    private void showStep(int position) {
        if (position == 1) {
            binding.topTitle.setText(Helper.getResString(R.string.common_word_review));
            binding.nextStep.setText(Helper.getResString(R.string.common_word_save));
        } else {
            binding.topTitle.setText(xB.b().a(this, R.string.common_word_step, position + 1));
            binding.nextStep.setText(Helper.getResString(R.string.common_word_next));
        }

        if (position == 0) {
            binding.back.setVisibility(View.VISIBLE);
            binding.previousStep.setVisibility(View.GONE);
        } else {
            binding.back.setVisibility(View.GONE);
            binding.previousStep.setVisibility(View.VISIBLE);
        }

        binding.stepTitle.setText(stepTitles[position]);
        binding.stepDescription.setText(stepDescriptions[position]);
        binding.stepContainer.removeAllViews();
        switch (position) {
            case 0:
                binding.goToGithub.setVisibility(View.VISIBLE);
                SetupCredentialStepView setupCredentialStep = new SetupCredentialStepView(this);
                binding.stepContainer.addView(setupCredentialStep);
                setupCredentialStep.setData(bean);
                callback = setupCredentialStep;
                break;

            case 1:
                binding.goToGithub.setVisibility(View.GONE);
                ReviewStepView reviewStep = new ReviewStepView(this);
                binding.stepContainer.addView(reviewStep);
                reviewStep.setData(bean);
                callback = reviewStep;
                break;

            default:
        }

        if (callback.getUrl().isEmpty()) {
            binding.createToken.setVisibility(View.GONE);
        } else {
            binding.createToken.setVisibility(View.VISIBLE);
        }

        if (position > 0) {
            binding.importButton.setVisibility(View.GONE);
        } else {
            binding.importButton.setVisibility(View.VISIBLE);
        }
    }

    private void nextStep() {
        if (callback.isValid()) {
            callback.getData(bean);
            if (stepPosition < 1) {
                showStep(++stepPosition);
            } else {
                showCloneDialog();
            }
        }
    }

    private void showCloneDialog() {
        String data = wq.b(sc_id) + File.separator + "github";
        GitHubBean currentBean = new GitHubBean();
        if (FileUtil.isExistFile(data)) {
            currentBean = new Gson().fromJson(FileUtil.readFile(data), GitHubBean.class);
        }
        if (bean.repoUrl.equals(currentBean.repoUrl)) {
            goBackToManageGitHub();
            return;
        }
        aB dialog = new aB(this);
        dialog.a(R.drawable.ic_repo_forked_16);
        dialog.b("Clone");
        dialog.a("Do you want to clone the repository?");
        dialog.b("Yes", v -> {
            showGitClone(bean);
            dialog.dismiss();
        });
        dialog.a("No", v -> {
            goBackToManageGitHub();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showGitClone(GitHubBean bean) {
        try {
            Clone clone = new Clone(this, sc_id);
            clone.execute(bean, (boolean success, String _sc_id, GitHubBean _bean) -> {
                if (success) {
                    final String errorMessage;
                    try {
                        runOnUiThread(() -> SketchwareUtil.toast("Generating project sources, please wait"));
                        CompletableFuture<Void> build = new GitHubUtil(sc_id).build();
                        build.thenRun(() -> {
                            goBackToManageGitHub();
                        });
                        build.join();
                        return;
                    } catch (FileNotFoundException e) {
                        errorMessage = e.getMessage();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }
                    runOnUiThread(() -> SketchwareUtil.toastError("Generating failed: " + errorMessage));
                }
            });
        } catch (Exception e) {
            goBackToManageGitHub();
            SketchwareUtil.toastError("Cloning failed: " + e.getMessage());
        }
    }

    private void goBackToManageGitHub() {
        new GitHubUtil(sc_id).setBean(bean);
        Intent intent = new Intent();
        intent.putExtra("github", bean);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void goCreateToken() {
        launchUrl(callback.getUrl());
    }

    private void goToGitHub() {
        launchUrl("https://github.com/settings");
    }

    private void launchUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public interface Callback {

        public void getData(GitHubBean bean);

        public String getUrl();

        public boolean isValid();

        public void setData(GitHubBean bean);
    }
}
