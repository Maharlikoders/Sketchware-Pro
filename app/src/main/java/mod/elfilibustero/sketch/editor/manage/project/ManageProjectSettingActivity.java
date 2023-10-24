package mod.elfilibustero.sketch.editor.manage.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.sketchware.pro.R;

import a.a.a.mB;

import mod.elfilibustero.sketch.lib.ui.SketchInputItem;
import mod.elfilibustero.sketch.lib.utils.ProjectConfigurationUtil;

public class ManageProjectSettingActivity extends AppCompatActivity {

    private LinearLayout contentLayout;
    private String sc_id;
    private ProjectConfigurationUtil util;

    private void addPreferenceInput(String key, String value) {
        SketchInputItem inputItem = new SketchInputItem(this);
        inputItem.setKey(key);
        inputItem.setValue(value);
        contentLayout.addView(inputItem);
    }

    @Override
    public void onBackPressed() {
        if (saveSettings()) {
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_settings);

        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }

        util = new ProjectConfigurationUtil(sc_id);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.layout_main_logo).setVisibility(View.GONE);
        getSupportActionBar().setTitle("Project Manager");
        toolbar.setNavigationOnClickListener(view -> {
            if (!mB.a()) onBackPressed();
        });

        contentLayout = findViewById(R.id.content);

        addPreferenceInput("min_sdk", util.getMinSdk());
    }
}
