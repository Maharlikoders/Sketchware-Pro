package mod.elfilibustero.sketch.editor.manage.github;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.besome.sketch.editor.property.PropertySwitchItem;
import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.sketchware.pro.R;

import a.a.a.mB;
import mod.hey.studios.util.Helper;

public class ManageGitHubSettingActivity extends AppCompatActivity {

    private LinearLayout contentLayout;
    private SharedPreferences.Editor preferenceEditor;

    public static final String GITHUB_SOURCE = "github_src";
    public static final String GITHUB_FETCH = "github_fetch";

    private String sc_id;

    private void addPreference(int key, String name, String description, boolean value) {
        PropertySwitchItem switchItem = new PropertySwitchItem(this);
        switchItem.setKey(key);
        switchItem.setName(name);
        switchItem.setDesc(description);
        switchItem.setValue(value);
        contentLayout.addView(switchItem);
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.layout_main_logo).setVisibility(View.GONE);
        getSupportActionBar().setTitle("GitHub Settings");
        toolbar.setNavigationOnClickListener(view -> {
            if (!mB.a()) onBackPressed();
        });

        contentLayout = findViewById(R.id.content);
        SharedPreferences preferences = getSharedPreferences("GITHUB_SETTINGS", Context.MODE_PRIVATE);
        preferenceEditor = preferences.edit();

        addPreference(0, "GitHub Source",
                "Enable to use Android Studio source code",
                preferences.getBoolean(sc_id + "_" + GITHUB_SOURCE, false));

        addPreference(1, "Auto Fetch",
                "Automatically fetch when remote repository has a new commit",
                preferences.getBoolean(sc_id + "_" + GITHUB_FETCH, false));
    }

    private boolean saveSettings() {
        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            View childAtView = contentLayout.getChildAt(i);
            if (childAtView instanceof PropertySwitchItem) {
                PropertySwitchItem propertySwitchItem = (PropertySwitchItem) childAtView;
                if (0 == propertySwitchItem.getKey()) {
                    preferenceEditor.putBoolean(sc_id + "_" + GITHUB_SOURCE, propertySwitchItem.getValue());
                } else if (1 == propertySwitchItem.getKey()) {
                    preferenceEditor.putBoolean(sc_id + "_" + GITHUB_FETCH, propertySwitchItem.getValue());
                }
            }
        }

        return preferenceEditor.commit();
    }
}
