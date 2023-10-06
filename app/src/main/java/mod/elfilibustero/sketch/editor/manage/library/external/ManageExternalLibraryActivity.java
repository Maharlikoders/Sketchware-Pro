package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
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
import mod.hey.studios.util.Helper;

public class ManageExternalLibraryActivity extends AppCompatActivity {

    private ManageExternalLibraryBinding binding;

    private String sc_id;
    private String dataPath;
    private String initialPath;

    private LibraryAdapter adapter;
    private List<ExternalLibraryBean> libraries = new ArrayList<>();

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

        adapter = new LibraryAdapter(libraries);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getApplicationContext(), ManageExternalLibraryItemActivity.class);
            intent.putExtra("sc_id", sc_id);
            intent.putExtra("postion", position);
            intent.putExtra("library", libraries.get(position));
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
        libraries.clear();
        if (!FileUtil.isExistFile(dataPath)) {
            FileUtil.writeFile(dataPath, "[]");
        } else {
            try {
                String content = FileUtil.readFile(dataPath);
                if (content != null && !content.isEmpty()) {
                    libraries = new Gson().fromJson(FileUtil.readFile(dataPath), new TypeToken<List<ExternalLibraryBean>>() {
                    }.getType());
                } 
            } catch (Exception e) {
            }
        }
        getExternalLibraryList(libraries);
        if (libraries == null || libraries.isEmpty()) {
            binding.guide.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.guide.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void downloadLibrary() {
        Intent intent = new Intent(getApplicationContext(), ManageExternalAddLibraryActivity.class);
        intent.putExtra("sc_id", sc_id);
        openLibraryManager.launch(intent);
    }

    private void getExternalLibraryList(final List<ExternalLibraryBean> libraries) {
        libraries.removeIf(bean -> !Files.exists(Paths.get(initialPath + "/" + bean.getName())));
        
        if (libraries != null || !libraries.isEmpty()) {
            try (Stream<Path> folderStream = Files.walk(Paths.get(initialPath), 1, FileVisitOption.FOLLOW_LINKS)) {
                List<ExternalLibraryBean> beans = folderStream.filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(ExternalLibraryBean::new)
                    .filter(bean -> libraries.contains(bean))
                    .collect(Collectors.toList());
                libraries.addAll(beans);
            } catch (IOException e) {
            }
        }
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {

        private List<ExternalLibraryBean> beans;
        private OnItemClickListener itemClickListener;
        private List<String> currentNames;

        public LibraryAdapter(List<ExternalLibraryBean> libraries) {
            currentNames = libraries.stream().map(ExternalLibraryBean::getName).collect(Collectors.toList());
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public interface OnItemClickListener {

            void onItemClick(int position);
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_external_library_list_item, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            var bean = beans.get(position);
            holder.name.setText(bean.name);
            holder.dep.setText(bean.dependency);
        }

        @Override
        public int getItemCount() {
            return beans.size();
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
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            }
        }
    }
}
