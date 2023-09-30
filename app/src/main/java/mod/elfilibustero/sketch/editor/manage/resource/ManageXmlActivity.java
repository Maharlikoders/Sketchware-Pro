package mod.elfilibustero.sketch.editor.manage.resource;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.sketchware.remod.databinding.ManageXmlBinding;
import com.sketchware.remod.R;

import mod.elfilibustero.sketch.constants.XmlResourceConstant;
import mod.elfilibustero.sketch.editor.manage.resource.XmlItemView;
import mod.hey.studios.util.Helper;

public class ManageXmlActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageXmlBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageXmlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addItem(ResourceType.STRING);
        addItem(ResourceType.COLOR);
        addItem(ResourceType.STYLE);
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(Helper.getResString(R.string.design_actionbar_title_manager_xml));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        binding.toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
    }

    private void addItem(ResourceType type) {
        XmlItemView item = new XmlItemView(this);
        item.setTag(type);
        item.setData(getResourceType(type));
        item.setOnClickListener(this);
        binding.itemLayout.addView(item);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof ResourceType resourceType) {
            switch (resourceType) {
                case STRING:
                    toXmlResourceActivity(ResourceType.STRING);
                    break;
                case COLOR:
                    toXmlResourceActivity(ResourceType.COLOR);
                    break;
                case STYLE:
                    toXmlResourceActivity(ResourceType.STYLE);
                    break;
            }
        }
    }

    private int getResourceType(ResourceType resourceType) {
        return switch (resourceType) {
            case STRING -> XmlResourceConstant.TYPE_STRING;
            case COLOR -> XmlResourceConstant.TYPE_COLOR;
            case STYLE -> XmlResourceConstant.TYPE_STYLE;
            default -> 0;
        };
    }

    private void toXmlResourceActivity(ResourceType resourceType) {
        Intent intent = new Intent(getApplicationContext(), ManageXmlResourceActivity.class);
        intent.putExtra("sc_id", getIntent().getStringExtra("sc_id"));
        intent.putExtra("type", getResourceType(resourceType));
        startActivity(intent);
    }

    public enum ResourceType {
        STRING,
        COLOR,
        STYLE
    }
}
