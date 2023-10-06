package mod.elfilibustero.sketch.editor.manage.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.besome.sketch.editor.manage.library.ManageLibraryActivity;

import com.sketchware.pro.databinding.ManageXmlBinding;
import com.sketchware.pro.R;

import dev.aldi.sayuti.editor.manage.ManageLocalLibraryActivity;

import mod.elfilibustero.sketch.constants.LibrariesConstant;
import mod.elfilibustero.sketch.editor.manage.library.LibraryItemView;
import mod.elfilibustero.sketch.editor.manage.library.external.ManageExternalLibraryActivity;
import mod.hey.studios.activity.managers.nativelib.ManageNativelibsActivity;
import mod.hey.studios.util.Helper;

public class ManageLibrariesActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageXmlBinding binding;
    private String sc_id;
    private boolean isResultOK = false;

    private final ActivityResultLauncher<Intent> openLibraryManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        isResultOK = result.getResultCode() == RESULT_OK;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageXmlBinding.inflate(getLayoutInflater());
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
        addItem(LibraryType.BUILT_IN);
        addItem(LibraryType.EXTERNAL);
        addItem(LibraryType.NATIVE);
        addItem(LibraryType.LIBRARY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (isResultOK) setResult(RESULT_OK, new Intent());
        finish();
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Libraries Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        binding.toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
    }

    private void addItem(LibraryType type) {
        LibraryItemView item = new LibraryItemView(this);
        item.setTag(type);
        item.setData(getResourceType(type));
        item.setOnClickListener(this);
        binding.itemLayout.addView(item);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof LibraryType libraryType) {
            switch (libraryType) {
                case BUILT_IN:
                    toLibraryActivity(LibraryType.BUILT_IN, ManageLibraryActivity.class, openLibraryManager);
                    break;
                case EXTERNAL:
                    toLibraryActivity(LibraryType.EXTERNAL, ManageExternalLibraryActivity.class, null);
                    break;
                case NATIVE:
                    toLibraryActivity(LibraryType.NATIVE, ManageNativelibsActivity.class, null);
                    break;
                case LOCAL:
                    toLibraryActivity(LibraryType.LOCAL, ManageLocalLibraryActivity.class, null);
                    break;
            }
        }
    }

    private int getResourceType(LibraryType libraryType) {
        return switch (libraryType) {
            case BUILT_IN -> LibrariesConstant.TYPE_BUILT_IN;
            case EXTERNAL -> LibrariesConstant.TYPE_EXTERNAL;
            case NATIVE -> LibrariesConstant.TYPE_NATIVE;
            case LOCAL -> LibrariesConstant.TYPE_LOCAL;
            default -> 0;
        };
    }

    private void toLibraryActivity(LibraryType libraryType, Class<? extends Activity> toLaunch, ActivityResultLauncher<Intent> optionalLauncher) {
        Intent intent = new Intent(getApplicationContext(), toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        if (optionalLauncher == null) {
            startActivity(intent);
        } else {
            optionalLauncher.launch(intent);
        }
    }

    public enum LibraryType {
        BUILT_IN,
        EXTERNAL,
        NATIVE,
        LOCAL
    }
}
