package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sketchware.pro.R;
import com.sketchware.pro.databinding.ManageExternalLibraryBinding;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import a.a.a.wq;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;
import mod.elfilibustero.sketch.lib.utils.NewFileUtil;
import mod.hey.studios.util.Helper;

public class ManageExternalLibraryActivity extends AppCompatActivity {

    private ManageExternalLibraryBinding binding;

    private String sc_id;
    private String dataPath;
    private String initialPath;

    private LibraryAdapter adapter;
    private List<ExternalLibraryBean> beans = new ArrayList<>();
    private List<ExternalLibraryBean> temps = new ArrayList<>();

    private final ActivityResultLauncher<Intent> openLibraryManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadLibraries();
        }
    });

    private final ActivityResultLauncher<Intent> downloadLibrary = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadLibraries();
        }
    });

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
        getSupportActionBar().setTitle("External Library Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));

        dataPath = wq.b(sc_id) + "/external_library";
        initialPath = wq.getExternalLibrary(sc_id);
        FileUtil.makeDir(initialPath);

        adapter = new LibraryAdapter(beans);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(bean -> {
            Intent intent = new Intent(getApplicationContext(), ManageExternalLibraryItemActivity.class);
            intent.putExtra("sc_id", sc_id);
            intent.putExtra("library", bean);
            openLibraryManager.launch(intent);
        });
        loadLibraries();
        binding.fab.setOnClickListener(v -> downloadLibrary());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private void loadLibraries() {
        beans.clear();
        if (!FileUtil.isExistFile(dataPath)) {
            FileUtil.writeFile(dataPath, "[]");
        }
        try {
            String content = FileUtil.readFile(dataPath);
            if (content != null && !content.isEmpty()) {
                temps = new Gson().fromJson(FileUtil.readFile(dataPath), new TypeToken<List<ExternalLibraryBean>>() {
                }.getType());
            } else {
                FileUtil.writeFile(dataPath, "[]");
            }
        } catch (Exception e) {
        }

        try {
            beans.addAll(getExternalLibraries());
        } catch (IOException e) {
        }
        if (beans == null || beans.isEmpty()) {
            binding.guide.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.guide.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private List<ExternalLibraryBean> getExternalLibraries() throws IOException {
        List<ExternalLibraryBean> beans = new ArrayList<>();
        List<String> names = NewFileUtil.listDir(initialPath, NewFileUtil.TYPE_DIRECTORY);
        for (String name : names) {
            ExternalLibraryBean bean = getExternalLibrary(name);
            if (bean == null) {
                bean = new ExternalLibraryBean(name);
                String dependencies = getDependencies(name);
                if (FileUtil.isExistFile(dependencies)) {
                    bean.dependency = FileUtil.readFile(dependencies);
                }
                if (isContainsLibrary(name)) {
                    bean.useYn = "Y";
                }
            }
            beans.add(bean);
        }
        return beans;
    }

    private String getDependencies(String name) {
        return initialPath + "/" + name + "/" + "dependencies";
    }

    private void downloadLibrary() {
        Intent intent = new Intent(getApplicationContext(), ManageExternalAddLibraryActivity.class);
        intent.putExtra("sc_id", sc_id);
        openLibraryManager.launch(intent);
    }

    private ExternalLibraryBean getExternalLibrary(String name) {
        for (int i = 0; i < temps.size(); i++) {
            ExternalLibraryBean bean = temps.get(i);
            if (bean.name.equals(name)) {
                return bean;
            }
        }
        return null;
    }

    private boolean isContainsLibrary(String name) {
        if (temps != null && !temps.isEmpty()) {
            for (ExternalLibraryBean bean : temps) {
                if (bean.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {
        private List<ExternalLibraryBean> files;
        private OnItemClickListener itemClickListener;

        public LibraryAdapter(List<ExternalLibraryBean> libraries) {
            files = libraries;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public interface OnItemClickListener {

            void onItemClick(ExternalLibraryBean bean);
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_external_library_list_item, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            var bean = files.get(position);
            String name = bean.name;
            holder.name.setText(name);
            boolean isEnabled = isContainsLibrary(name);
            holder.enabled.setText(isEnabled ? "ON" : "OFF");
            holder.enabled.setSelected(isEnabled);
            String dependency = bean.dependency;
            if (dependency.isEmpty()) {
                holder.dep.setText(dependency);
                holder.dep.setTextColor(0xffc6c6c6);
            } else {
                holder.dep.setText("Missing dependencies");
                holder.dep.setTextColor(Color.RED);
            }
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public final TextView name;
            public final TextView dep;
            public final TextView enabled;

            public FileViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                dep = itemView.findViewById(R.id.dependencies);
                enabled = itemView.findViewById(R.id.enabled);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                ExternalLibraryBean bean = files.get(getAdapterPosition());
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(bean);
                }
            }
        }
    }
}
