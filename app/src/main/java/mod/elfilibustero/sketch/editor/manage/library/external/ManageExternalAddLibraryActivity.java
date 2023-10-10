package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageExternalAddLibraryBinding;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.lib.handler.ExternalLibraryHandler;
import mod.hey.studios.util.Helper;

public class ManageExternalAddLibraryActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageExternalAddLibraryBinding binding;
    private String sc_id;

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
        if (id == R.id.add) {
            addDependency();
        }
    }

    private void init() {
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New External Library");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        binding.add.setOnClickListener(this);
        binding.add.setText("Add dependency");
    }

    private void addDependency() {
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
        //downloadLibrary(groupId, artifactId, version, switchLib.isChecked());
    }

    private void loadDependencies() {

    }
}
