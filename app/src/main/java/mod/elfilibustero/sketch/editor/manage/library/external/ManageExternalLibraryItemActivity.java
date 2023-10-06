package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import mod.hey.studios.util.Helper;

public class ManageExternalLibraryItemActivity extends AppCompatActivity implements View.OnClickListener {

	private ManageExternalLibraryItemBinding binding;

	private String sc_id;

	private LibraryAdapter adapter;
    private String initialPath;
    private String currentPath;
    private File filePath;
    private List<File> fileList = new ArrayList<>();

    private ExternalLibraryBean bean;

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
        bean = getIntent().getParcelableExtra("library");
        setup();
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
        }
    }

    private void init() {
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(bean.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
    }

    private void setup() {
        initialPath = wq.getExternalLibrary(sc_id) + bean.name;
        currentPath = initialPath;
        adapter = new LibraryAdapter(fileList);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            
        });
        var currentDirectory = new File(currentPath);
        if (!currentDirectory.exists() || !currentDirectory.isDirectory()) {
            currentDirectory = new File(initialPath);
            currentPath = currentDirectory.getAbsolutePath();
        }
        loadFiles(currentDirectory, fileList, adapter);
        adapter.setOnItemClickListener(file -> {
            filePath = file;
            if (file.isDirectory()) {
                currentPath = file.getAbsolutePath();
                loadFiles(file, fileList, adapter);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (currentPath.equals(initialPath)) {
            //setResult(RESULT_OK, new Intent());
            finish();
        } else if (adapter != null) {
            var lastPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
            var currentDirectory = new File(lastPath);
            if (!currentDirectory.exists() || !currentDirectory.isDirectory()) {
                currentDirectory = new File(initialPath);
                currentPath = currentDirectory.getAbsolutePath();
            } else {
                currentPath = lastPath;
            }
            loadFiles(currentDirectory, fileList, adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

     private void loadFiles(File directory, List<File> fileList, LibraryAdapter adapter) {
        fileList.clear();
        File[] files = directory.listFiles();
        if (files != null) {
            Arrays.sort(files, (file1, file2) -> {
                if (file1.isDirectory() && !file2.isDirectory()) {
                    return -1;
                } else if (!file1.isDirectory() && file2.isDirectory()) {
                    return 1;
                } else {
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            });
            for (File file : files) {
                fileList.add(file);
            }
        }
        if (fileList.isEmpty()) {
            binding.guide.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.guide.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {
        private OnItemClickListener itemClickListener;
        private final List<File> fileList;

        public LibraryAdapter(List<File> fileList) {
            this.fileList = fileList;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(File file);
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_external_library_item_list_item, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            var file = fileList.get(position);
            holder.name.setText(file.getName());
        }

        @Override
        public int getItemCount() {
            return fileList.size();
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
                var file = fileList.get(getAdapterPosition());
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(file);
                }
            }
        }
    }
}
