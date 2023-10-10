package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageExternalAddLibraryBinding;

import mod.SketchwareUtil;
import mod.elfilibustero.sketch.beans.DependencyBean;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.lib.handler.ExternalLibraryHandler;
import mod.hey.studios.util.Helper;

public class ManageExternalAddLibraryActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageExternalAddLibraryBinding binding;
    private String sc_id;
    private ExternalLibraryHandler handler;
    private ExternalLibraryBean externalLibrary;
    private List<DependencyBean> temp = new ArrayList<>();
    private List<DependencyBean> dependencies = new ArrayList<>();
    private LibraryAdapter adapter;

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
        if (!temp.equals(dependencies)) {
            setResult(RESULT_OK, new Intent());
        }
        finish();
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
        handler = new ExternalLibraryHandler(sc_id);
        temp = handler.externalLibrary.getDependencies();
        adapter = new LibraryAdapter(dependencies);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {

        });
        loadDependencies();
    }

    private void addDependency() {
        String groupId = binding.group.getText().toString();
        String artifactId = binding.artifact.getText().toString();
        String version = binding.version.getText().toString();

        if (TextUtils.isEmpty(groupId)) {
            binding.group.requestFocus();
            SketchwareUtil.toastError("Please enter Group ID");
            return;
        }

        if (TextUtils.isEmpty(artifactId)) {
            binding.artifact.requestFocus();
            SketchwareUtil.toastError("Please enter Artifact ID");
            return;
        }

        if (TextUtils.isEmpty(version)) {
            binding.version.requestFocus();
            SketchwareUtil.toastError("Please enter Version Name");
            return;
        }

        DependencyBean dependency = DependencyBean.Companion.from(groupId + ":" + artifactId + ":" + version);
        if (dependencies.contains(dependency)) {
            SketchwareUtil.toastError("Dependency: " + dependency.toString() + " already exists");
            return;
        }

        dependencies.add(dependency);
        externalLibrary.setDependencies(dependencies);
        handler.setBean(externalLibrary);
        binding.group.setText("");
        binding.artifact.setText("");
        binding.version.setText("");
        loadDependencies();
    }

    private void loadDependencies() {
        externalLibrary = handler.getBean();
        dependencies.clear();
        dependencies.addAll(externalLibrary.getDependencies());
        adapter.notifyDataSetChanged();
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {

        private OnItemClickListener itemClickListener;
        private final List<DependencyBean> dependencies;

        public LibraryAdapter(List<DependencyBean> dependencies) {
            this.dependencies = dependencies;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public interface OnItemClickListener {

            void onItemClick(DependencyBean bean);
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_external_library_item_list_item, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            var dependency = dependencies.get(position);
            holder.name.setText(dependency.toString());
        }

        @Override
        public int getItemCount() {
            return dependencies.size();
        }

        public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public final TextView name;

            public FileViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(dependencies.get(getAdapterPosition()));
                }
            }
        }
    }
}
