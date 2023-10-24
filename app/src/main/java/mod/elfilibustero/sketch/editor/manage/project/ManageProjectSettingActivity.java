package mod.elfilibustero.sketch.editor.manage.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.besome.sketch.lib.ui.MiddleLineHeader;
import com.sketchware.pro.R;

import a.a.a.mB;
import a.a.a.Kw;
import a.a.a.wB;

import mod.elfilibustero.sketch.lib.ui.SketchInputItem;
import mod.elfilibustero.sketch.lib.utils.ProjectConfigurationUtil;

public class ManageProjectSettingActivity extends AppCompatActivity implements Kw {

    private LinearLayout contentLayout;
    private String sc_id;
    private ProjectConfigurationUtil util;

    private void addPreferenceInput(String key, String value) {
        SketchInputItem inputItem = new SketchInputItem(this);
        inputItem.setKey(key);
        inputItem.setValue(value);
        inputItem.setOnValueChangedListener(this);
        contentLayout.addView(inputItem);
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
        addPreferenceInput("target_sdk", util.getTargetSdk());
        MiddleLineHeader projectHeader = new MiddleLineHeader(this);
        projectHeader.b.setText("More Settings");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) wB.a(this, 8f);
        layoutParams.topMargin = (int) wB.a(this, 4f);
        layoutParams.bottomMargin = (int) wB.a(this, 4f);
        layoutParams.rightMargin = (int) wB.a(this, 0f);
        projectHeader.setLayoutParams(layoutParams);
        contentLayout.addView(projectHeader);
        addPreferenceInput("app_class", util.getApplicationClass());
        addPreferenceInput("util_class", util.getUtilClass());
        addPreferenceInput("disable_old_methods", util.getOldMethods());
        addPreferenceInput("enable_bridgeless_themes", util.getBridgelessThemes());
    }

    @Override
    public void a(String key, Object value) {
        switch (key) {
            case "min_sdk":
                util.setMinSdk((String)value);
                break;
            case "target_sdk":
                util.setTargetSdk((String)value);
                break;
            case "app_class":
                util.setApplicationClass((String)value);
                break;
            case "util_class":
                util.setUtilClass((String)value);
                break;
            case "disable_old_methods":
                util.setOldMethods((String)value);
                break;
            case "enable_bridgeless_themes":
                util.setBridgelessThemes((String)value);
                break;
        }
    }
}
