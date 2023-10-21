package mod.elfilibustero.sketch.editor.manage.github;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.jgit.api.errors.GitAPIException;

import a.a.a.aB;
import a.a.a.lC;
import a.a.a.wB;

import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageGithubBinding;
import com.sketchware.pro.databinding.PropertyPopupSelectorSingleBinding;

import mod.SketchwareUtil;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.git.Push;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class ManageGitHubActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageGithubBinding binding;
    private GitHubUtil util;
    private GitHubBean bean;
    private String sc_id;

    private final ActivityResultLauncher<Intent> openGitHubActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            initializeGitHub(result.getData().getParcelableExtra("github"));
        }
        binding.pushToGithub.setEnabled(hasCommit());
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageGithubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }
        util = new GitHubUtil(sc_id);
        init();
    }

    @Override
    public void onBackPressed() {
        util.setBean(bean);
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.push_to_github || id == R.id.commit_changes) {
            if (isBeanEmpty()) {
                SketchwareUtil.toast("Configure GitHub settings first, "
                    + "by manually entering the GitHub's details.", Toast.LENGTH_LONG);
            } else if (!binding.gitSwitch.isChecked()) {
                SketchwareUtil.toast("Please enabled switch first", Toast.LENGTH_LONG);
            } else {
                if (id == R.id.push_to_github) {
                    if (hasCommit()) {
                        showPushDialog();
                    }
                } else if (id == R.id.commit_changes) {
                    toGitHubChangesActivity();
                }

            }
        } else if (id == R.id.layout_switch) {
            Switch gitSwitch = binding.gitSwitch;
            if (!gitSwitch.isChecked() && isBeanEmpty()) {
                SketchwareUtil.toast("Configure GitHub settings first, "
                    + "by manually entering the GitHub's details.", Toast.LENGTH_LONG);
            } else {
                gitSwitch.setChecked(!gitSwitch.isChecked());
                if ("Y".equals(bean.useYn) && !gitSwitch.isChecked()) {
                    bean.useYn = "N";
                    gitSwitch.setChecked(false);
                } else {
                    bean.useYn = "Y";
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_github_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_github_settings) {
            toSetupGitHubActivity();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void init() {
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GitHub Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        binding.layoutSwitch.setOnClickListener(this);
        binding.pushToGithub.setOnClickListener(this);
        binding.commitChanges.setOnClickListener(this);
        binding.pushToGithub.setText("Push");
        binding.commitChanges.setText("Changes");
        initializeGitHub(util.getBean());
    }

    private void initializeGitHub(GitHubBean bean) {
        this.bean = bean;
        configure();
    }

    private void configure() {
        boolean useYn = bean.useYn.equals("Y");
        binding.gitSwitch.setChecked(useYn);
        binding.userName.setText(bean.username);
        binding.repoUrl.setText(bean.repoUrl);
        binding.branch.setText(bean.branch);
    }

    private boolean isBeanEmpty() {
        return bean.username.isEmpty()
            && bean.token.isEmpty()
            && bean.email.isEmpty()
            && bean.repoUrl.isEmpty()
            && bean.branch.isEmpty();
    }

    private void toSetupGitHubActivity() {
        var intent = new Intent(getApplicationContext(), SetupGitHubActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("github", bean);
        openGitHubActivity.launch(intent);
    }

    private void toGitHubChangesActivity() {
        var intent = new Intent(getApplicationContext(), ManageGitHubChangesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("github", bean);
        openGitHubActivity.launch(intent);
    }

    private boolean hasCommit() {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                return util.hasCommit();
            } catch (GitAPIException e) {
                SketchwareUtil.toastError(e.getMessage());
            } catch (IOException e) {
                SketchwareUtil.toastError(e.getMessage());
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    private void pushToGitHub(boolean force) {
        new Push(this, sc_id).execute(force);
    }

    private void showPushDialog() {
        var dialog = new aB(this);
        dialog.b("Push");
        dialog.a(R.drawable.ic_repo_push_16);
        dialog.a("Are you sure you want to push all commits?");
        dialog.b("Push", v -> {
            pushToGitHub(false);
            dialog.dismiss();
        });
        dialog.configureDefaultButton("Force", v -> {
            pushToGitHub(true);
            dialog.dismiss();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }
}
