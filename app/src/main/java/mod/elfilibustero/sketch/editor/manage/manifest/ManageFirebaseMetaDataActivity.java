package mod.elfilibustero.sketch.editor.manage.manifest;

import a.a.a.aB;
import a.a.a.wB;
import a.a.a.Zx;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.besome.sketch.lib.base.CollapsibleViewHolder;
import com.besome.sketch.lib.ui.CollapsibleButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sketchware.pro.databinding.ColorPickerBinding;
import com.sketchware.pro.databinding.ManageXmlResourceBinding;
import com.sketchware.pro.databinding.ManageXmlResourceAddBinding;
import com.sketchware.pro.databinding.ManageXmlResourceListItemBinding;
import com.sketchware.pro.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.lib.utils.SketchFileUtil;
import mod.hey.studios.util.Helper;

public class ManageFirebaseMetaDataActivity extends AppCompatActivity {

    private String sc_id;
    private String resourcePath = "";

    private int itemPosition = 0;

    private List<Map<String, Object>> resources = new ArrayList<>();

    private ManageXmlResourceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageXmlResourceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        sc_id = getIntent().getStringExtra("sc_id");
        resourcePath = FileUtil.getExternalStorageDir() + "/" + SketchFileUtil.SKETCHWARE_WORKSPACE_DIRECTORY + "/data/" + sc_id + "/injection/firebase/meta-data";
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Firebase Meta-data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        binding.toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));

        binding.fab.setOnClickListener(_view -> {
            showDialog("", "", null, false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        readSettings();
    }

    private void save(String content, String path) {
        FileUtil.writeFile(path, content);
        readSettings();
    }

    private void delete(Map<String, Object> _item) {
        resources.remove(_item);
        save(new Gson().toJson(resources), resourcePath);
    }

    private void showDialog(String _name, String _value, Integer _position, boolean edit) {
        aB dialog = new aB(this);
        dialog.a(R.drawable.delete_96);
        dialog.b(edit ? "Edit" : "Add");
        var binding = ManageXmlResourceAddBinding.inflate(getLayoutInflater());
        var name = binding.name;
        var value = binding.value;
        var picker = binding.picker;
        var check = binding.check;
        name.setHint("Enter name");
        value.setHint("Enter value");
        name.setText(_name);
        value.setText(_value);
        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            String inputName = name.getText().toString();
            String inputValue = value.getText().toString();
            if (edit) {
                resources.get(_position).put("name", inputName);
                resources.get(_position).put("value", inputValue);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("name", inputName);
                map.put("value", inputValue);
                resources.add(map);
            }
            save(new Gson().toJson(resources), resourcePath);
            dialog.dismiss();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void readSettings() {
        if (FileUtil.isExistFile(resourcePath)) {
            resources = new Gson().fromJson(FileUtil.readFile(resourcePath), new TypeToken<List<Map<String, Object>>>(){});
        }

        var recyclerView = binding.recyclerView;
        var adapter = new Adapter(resources);
        var state = recyclerView.getLayoutManager().onSaveInstanceState();
        recyclerView.setAdapter(adapter);
        recyclerView.getLayoutManager().onRestoreInstanceState(state);
        adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private List<Map<String, Object>> data;
        private List<Boolean> collapse;
        private List<Boolean> confirmation;

        public Adapter(List<Map<String, Object>> itemList) {
            data = itemList;
            collapse = new ArrayList<>(Collections.nCopies(itemList.size(), true));
            confirmation = new ArrayList<>(Collections.nCopies(itemList.size(), false));
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ManageXmlResourceListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            var binding = holder.binding;
            if (data.get(position).containsKey("name")) {
                binding.title.setText((String) data.get(position).get("name"));
            }
            if (data.get(position).containsKey("value")) {
                binding.desc.setText((String) data.get(position).get("value"));
            }

            if (holder.isCollapsed()) {
                binding.optionLayout.setVisibility(View.GONE);
                binding.menu.setRotation(0.0f);
            } else {
                binding.optionLayout.setVisibility(View.VISIBLE);
                binding.menu.setRotation(-180.0f);
                if (confirmation.get(position)) {
                    if (holder.shouldAnimateNextTransformation()) {
                        binding.collapsible.showConfirmation();
                        holder.setAnimateNextTransformation(false);
                    } else {
                        binding.collapsible.showConfirmationWithoutAnimation();
                    }
                } else {
                    if (holder.shouldAnimateNextTransformation()) {
                        binding.collapsible.hideConfirmation();
                        holder.setAnimateNextTransformation(false);
                    } else {
                        binding.collapsible.hideConfirmationWithoutAnimation();
                    }
                }
            }
            binding.optionLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends CollapsibleViewHolder {

            private final ManageXmlResourceListItemBinding binding;
            private final LinearLayout root;

            public ViewHolder(@NonNull ManageXmlResourceListItemBinding binding) {
                super(binding.getRoot(), 200);
                this.binding = binding;
                this.root = binding.getRoot();
                binding.collapsible.getInsertButton().setVisibility(View.GONE);
                binding.collapsible.getEditButton().setVisibility(View.GONE);

                binding.collapsible.setButtonOnClickListener(v -> {
                    int lastSelectedItem = getLayoutPosition();
                    if (v instanceof CollapsibleButton) {
                        switch (((CollapsibleButton) v).getButtonId()) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                confirmation.set(lastSelectedItem, true);
                                setAnimateNextTransformation(true);
                                notifyItemChanged(lastSelectedItem);
                                break;
                        }
                        return;
                    }
                    int id = v.getId();
                    if (id == R.id.confirm_yes) {
                        delete(data.get(lastSelectedItem));
                        confirmation.set(lastSelectedItem, false);
                        notifyItemRemoved(lastSelectedItem);
                        notifyItemRangeChanged(lastSelectedItem, getItemCount());
                    } else if (id == R.id.confirm_no) {
                        confirmation.set(lastSelectedItem, false);
                        setAnimateNextTransformation(true);
                        notifyItemChanged(lastSelectedItem);
                    }
                });
                onDoneInitializingViews();
                root.setOnClickListener(_v -> {
                    int position = getLayoutPosition();
                    showDialog((String) data.get(position).get("name"), (String) data.get(position).get("value"), position, true);
                });
                setOnClickCollapseConfig(v -> v != root);
                binding.icon.setImageResource(R.drawable.icons8_app_attrs);
            }

            @Override
            protected boolean isCollapsed() {
                return collapse.get(getLayoutPosition());
            }

            @Override
            protected void setIsCollapsed(boolean isCollapsed) {
                collapse.set(getLayoutPosition(), isCollapsed);
            }

            @NonNull
            @Override
            protected ViewGroup getOptionsLayout() {
                return binding.optionLayout;
            }

            @NonNull
            @Override
            protected Set<? extends View> getOnClickCollapseTriggerViews() {
                return Set.of(binding.menu, root);
            }

            @NonNull
            @Override
            protected Set<? extends View> getOnLongClickCollapseTriggerViews() {
                return Set.of(root);
            }
        }
    }
}
