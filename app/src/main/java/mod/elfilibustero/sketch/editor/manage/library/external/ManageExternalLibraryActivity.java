package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
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
import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageExternalLibraryBinding;

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
import mod.hey.studios.util.Helper;

public class ManageExternalLibraryActivity extends AppCompatActivity {

	private ManageExternalLibraryBinding binding;

	private String sc_id;
	private String initialPath;
	private String currentPath;

	private LibraryAdapter adapter;
    private List<File> fileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageExternalLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }
        init();
    }

    private void init() {
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));

        initialPath = wq.getExternalLibrary(sc_id);
        currentPath = initialPath;

        adapter = new LibraryAdapter(fileList);
        binding.recyclerView.setAdapter(adapter);

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
            dismiss();
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

    private void loadFiles(File directory, List<File> fileList, FileAdapter adapter) {
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
        private final List<File> fileList;
        private OnItemClickListener itemClickListener;
        private int lastCheckedPosition = -1;

        public LibraryAdapter(List<File> fileList) {
            this.fileList = fileList;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_external_library_list_item, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            var file = fileList.get(position);
            holder.name.setText(file.getName());
            var dependency = new File(file, "dependencies");
            if (dependency.exists() && dependency.isFile()) {
            	holder.dep.setText(FileUtil.readFile(dependency.getAbsolutePath()));
            	holder.dep.setVisibility(View.VISIBLE);
            } else {
            	holder.dep.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public final TextView name;
            public final TextView dep;

            public FileViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                dep = itemView.findViewById(R.id.dependencies);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                var file = fileList.get(getAdapterPosition());
                if (!file.isDirectory()) {
                    int copyOfLastCheckedPosition = lastCheckedPosition;
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemChanged(copyOfLastCheckedPosition);
                    notifyItemChanged(lastCheckedPosition);
                } else {
                    lastCheckedPosition = -1;
                }
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(file);
                }
            }
        }
    }
}
