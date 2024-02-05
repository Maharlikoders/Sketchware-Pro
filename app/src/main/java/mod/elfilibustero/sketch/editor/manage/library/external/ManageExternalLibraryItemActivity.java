package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageExternalLibraryItemBinding;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import a.a.a.aB;
import a.a.a.wq;
import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;
import mod.elfilibustero.sketch.beans.LibrariesBean;
import mod.elfilibustero.sketch.lib.handler.ExternalLibraryHandler;
import mod.elfilibustero.sketch.lib.utils.NewFileUtil;
import mod.hey.studios.util.Helper;

public class ManageExternalLibraryItemActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageExternalLibraryItemBinding binding;

    private String sc_id;

    private LibraryAdapter adapter;
    private String initialPath;
    private List<String> files = new ArrayList<>();

    private ExternalLibraryHandler handler;
    private ExternalLibraryBean externalLibrary;
    private List<LibrariesBean> list = new ArrayList<>();
    private LibrariesBean bean;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageExternalLibraryItemBinding.inflate(getLayoutInflater());
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_switch) {
            Switch libSwitch = binding.libSwitch;
            libSwitch.setChecked(!libSwitch.isChecked());
            if ("Y".equals(bean.useYn) && !libSwitch.isChecked()) {
                bean.useYn = "N";
                libSwitch.setChecked(false);
            } else {
                bean.useYn = "Y";
            }
        } else if (id == R.id.open_file) {
            openFolder();
        }
    }

    private void init() {
        position = getIntent().getIntExtra("position", 0);
        handler = new ExternalLibraryHandler(sc_id);
        externalLibrary = handler.getBean();
        list = externalLibrary.getLibraries();
        bean = list.get(position);

        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(bean.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        binding.layoutSwitch.setOnClickListener(this);
        binding.openFile.setOnClickListener(this);
        binding.openFile.setText("Open");
        binding.libSwitch.setChecked(bean.useYn.equals("Y"));

        initialPath = wq.getExternalLibrary(sc_id) + "/" + bean.name;
        adapter = new LibraryAdapter(files);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
        });
        loadFiles();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        list.set(position, bean);
        externalLibrary.setLibraries(list);
        handler.setBean(externalLibrary);
        setResult(RESULT_OK, new Intent());
        finish();
    }

    private void loadFiles() {
        files.clear();
        try {
            files.addAll(NewFileUtil.listDir(initialPath, -1));
        } catch (IOException e) {
        }
        if (files == null || files.isEmpty()) {
            binding.guide.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.guide.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void openFolder() {
        if (!FileUtil.isExistFile(initialPath)) {
            SketchwareUtil.toastError("Directory not exists: " + initialPath);
        }
        Intent intent = new Intent();
        intent.setDataAndType(Uri.fromFile(new File(initialPath)), "application/*");
        startActivity(intent);
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {

        private OnItemClickListener itemClickListener;
        private final List<String> files;

        public LibraryAdapter(List<String> files) {
            this.files = files;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public interface OnItemClickListener {

            void onItemClick(String name);
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_external_library_item_list_item, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            var name = files.get(position);
            holder.name.setText(name);
        }

        @Override
        public int getItemCount() {
            return files.size();
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
                    itemClickListener.onItemClick(files.get(getAdapterPosition()));
                }
            }
        }
    }
}
