package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import a.a.a.aB;
import a.a.a.wB;

import com.besome.sketch.design.BuildingDialog;
import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageExternalAddLibraryBinding;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.DependencyBean;
import mod.hey.studios.util.Helper;
import mod.jbk.build.BuiltInLibraries;
import mod.pranav.dependency.resolver.DependencyResolver;

public class ManageExternalAddLibraryActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageExternalAddLibraryBinding binding;
    private String sc_id;
    private Switch switchLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageExternalAddLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }
        init();
    }

    @Override
    public void onBackPressed() {
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
        if (id == R.id.layout_switch) {
            switchLib.setChecked(!switchLib.isChecked());
        } else if (id == R.id.download) {
            setup();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    private void init() {
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New External Library");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        switchLib = binding.switchLib;
        binding.layoutSwitch.setOnClickListener(this);
        binding.download.setOnClickListener(this);
        binding.download.setText("Download");
    }

    private void setup() {
        String groupId = binding.group.getText().toString();
        String artifactId = binding.artifact.getText().toString();
        String version = binding.version.getText().toString();

        if (TextUtils.isEmpty(groupId)) {
            SketchwareUtil.toastError("Please enter Group ID");
            return;
        }

        if (TextUtils.isEmpty(artifactId)) {
            SketchwareUtil.toastError("Please enter Artifact ID");
            return;
        }

        if (TextUtils.isEmpty(version)) {
            SketchwareUtil.toastError("Please enter Version Name");
            return;
        }
        downloadLibrary(groupId, artifactId, version, switchLib.isChecked());
    }

    private void downloadLibrary(String group, String artifact, String version, boolean skip) {
        var dialog = new BuildingDialog(this);
        dialog.setCancelable(false);
        dialog.setIsCancelableOnBackPressed(false);
        var resolver = new DependencyResolver(DependencyBean.Companion.from(group + ":" + artifact + ":" + version));
        resolver.setScId(sc_id);
        resolver.skipDependencies(skip);
        var handler = new Handler(Looper.getMainLooper());

        class SetTextRunnable implements Runnable {

            private final String message;

            SetTextRunnable(String message) {
                this.message = message;
            }

            @Override
            public void run() {
                dialog.setProgress(message);
            }
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            BuiltInLibraries.maybeExtractAndroidJar(progress -> handler.post(new SetTextRunnable(progress)));
            BuiltInLibraries.maybeExtractCoreLambdaStubsJar();

            resolver.resolveDependency(new DependencyResolver.DependencyResolverCallback() {
                @Override
                public void invalidPackaging(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Invalid packaging for dependency " + dep));
                }

                @Override
                public void dexing(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Dexing dependency " + dep));
                }

                @Override
                public void dexingFailed(@NonNull String dependency, @NonNull Exception e) {
                    handler.post(() -> {
                        SketchwareUtil.showAnErrorOccurredDialog(ManageExternalAddLibraryActivity.this,
                            "Dexing dependency '" + dependency + "' failed: " + Log.getStackTraceString(e));
                    });
                }

                @Override
                public void log(@NonNull String msg) {
                    handler.post(new SetTextRunnable(msg));
                }

                @Override
                public void downloading(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Downloading dependency " + dep));
                }

                @Override
                public void startResolving(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Resolving dependency " + dep));
                }

                @Override
                public void onTaskCompleted(@NonNull List<String> dependencies) {
                    handler.post(() -> {
                        dialog.dismiss();
                        setResult(RESULT_OK, new Intent());
                        finish();
                    });
                }

                @Override
                public void onDependencyNotFound(@NonNull String dep) {
                    handler.post(() -> {
                        SketchwareUtil.toastError("Dependency " + dep + " not found");
                        dialog.dismiss();
                    });
                }

                @Override
                public void onDependencyResolveFailed(@NonNull Exception e) {
                    handler.post(() -> {
                        SketchwareUtil.toastError(e.getMessage());
                        dialog.dismiss();
                    });
                }

                @Override
                public void onDependencyResolved(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Dependency " + dep + " resolved"));
                }
            });
        });
        dialog.show();
    }
}
