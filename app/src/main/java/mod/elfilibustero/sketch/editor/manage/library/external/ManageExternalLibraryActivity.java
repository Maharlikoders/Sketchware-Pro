package mod.elfilibustero.sketch.editor.manage.library.external;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import com.besome.sketch.design.BuildingDialog;
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
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import a.a.a.wq;
import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.DependencyBean;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;
import mod.elfilibustero.sketch.beans.LibrariesBean;
import mod.elfilibustero.sketch.lib.handler.ExternalLibraryHandler;
import mod.elfilibustero.sketch.lib.utils.NewFileUtil;
import mod.hey.studios.util.Helper;
import mod.jbk.build.BuiltInLibraries;
import mod.pranav.dependency.resolver.DependencyResolver;

public class ManageExternalLibraryActivity extends AppCompatActivity {

    private ManageExternalLibraryBinding binding;

    private String sc_id;
    private String initialPath;

    private LibraryAdapter adapter;

    private ExternalLibraryBean externalLibrary;
    private List<LibrariesBean> beans = new ArrayList<>();
    private List<LibrariesBean> temps = new ArrayList<>();
    private List<DependencyBean> dependencies = new ArrayList<>();

    private ExternalLibraryHandler handler;

    private final ActivityResultLauncher<Intent> openLibraryManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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

        handler = new ExternalLibraryHandler(sc_id);
        externalLibrary = handler.externalLibrary;
        initialPath = handler.initialPath;
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
        binding.fab.setOnClickListener(v -> addLibrary());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_elibrary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_refresh) {
            downloadLibrary();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadLibraries() {
        beans.clear();
        try {
            temps = externalLibrary.libraries;
        } catch (Exception e) {
        }
        try {
            beans.addAll(getExternalLibraries());
            externalLibrary.libraries = beans;
            handler.setBean(externalLibrary);
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

    private List<LibrariesBean> getExternalLibraries() throws IOException {
        List<LibrariesBean> beans = new ArrayList<>();
        List<String> names = NewFileUtil.listDir(initialPath, NewFileUtil.TYPE_DIRECTORY);
        for (String name : names) {
            LibrariesBean bean = getExternalLibrary(name);
            if (bean == null) {
                bean = new LibrariesBean(name);
            }
            String dependencies = getDependencies(name);
            if (FileUtil.isExistFile(dependencies)) {
                bean.dependency = FileUtil.readFile(dependencies);
            }
            beans.add(bean);
        }
        return beans;
    }

    private String getDependencies(String name) {
        return initialPath + "/" + name + "/" + "dependencies";
    }

    private void addLibrary() {
        Intent intent = new Intent(getApplicationContext(), ManageExternalAddLibraryActivity.class);
        intent.putExtra("sc_id", sc_id);
        startActivity(intent);
    }

    private LibrariesBean getExternalLibrary(String name) {
        for (int i = 0; i < temps.size(); i++) {
            LibrariesBean bean = temps.get(i);
            if (bean.name.equals(name)) {
                return bean;
            }
        }
        return null;
    }

    private boolean isContainsLibrary(String name) {
        if (temps != null && !temps.isEmpty()) {
            for (LibrariesBean bean : temps) {
                if (bean.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void downloadLibrary() {
        var dialog = new BuildingDialog(this);
        dialog.setCancelable(false);
        dialog.setIsCancelableOnBackPressed(false);
        var resolver = new DependencyResolver(externalLibrary.getDependencies());
        resolver.setScId(sc_id);
        resolver.skipSubDependencies(false);
        var handler = new Handler(Looper.getMainLooper());

        class SetTextRunnable implements Runnable {

            private final String message;

            SetTextRunnable(String message) {
                this.message = message;
            }

            @Override
            public void run() {
                dialog.setProgress(message);
            }
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            BuiltInLibraries.maybeExtractAndroidJar(progress -> handler.post(new SetTextRunnable(progress)));
            BuiltInLibraries.maybeExtractCoreLambdaStubsJar();

            resolver.resolveDependency(new DependencyResolver.DependencyResolverCallback() {
                @Override
                public void invalidPackaging(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Invalid packaging for dependency " + dep));
                }

                @Override
                public void dexing(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Dexing dependency " + dep));
                }

                @Override
                public void dexingFailed(@NonNull String dependency, @NonNull Exception e) {
                    handler.post(() -> {
                        SketchwareUtil.showAnErrorOccurredDialog(ManageExternalLibraryActivity.this,
                            "Dexing dependency '" + dependency + "' failed: " + Log.getStackTraceString(e));
                    });
                }

                @Override
                public void log(@NonNull String msg) {
                    handler.post(new SetTextRunnable(msg));
                }

                @Override
                public void downloading(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Downloading dependency " + dep));
                }

                @Override
                public void startResolving(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Resolving dependency " + dep));
                }

                @Override
                public void onTaskCompleted(@NonNull List<String> dependencies) {
                    handler.post(() -> {
                        dialog.dismiss();
                        loadLibraries();
                    });
                }

                @Override
                public void onDependencyNotFound(@NonNull String dep) {
                    handler.post(() -> {
                        SketchwareUtil.toastError("Dependency " + dep + " not found");
                        dialog.dismiss();
                    });
                }

                @Override
                public void onDependencyResolveFailed(@NonNull Exception e) {
                    handler.post(() -> {
                        SketchwareUtil.toastError(e.getMessage());
                        dialog.dismiss();
                    });
                }

                @Override
                public void onDependencyResolved(@NonNull String dep) {
                    handler.post(new SetTextRunnable("Dependency " + dep + " resolved"));
                }
            });
        });
        dialog.show();
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {
        private List<LibrariesBean> files;
        private OnItemClickListener itemClickListener;

        public LibraryAdapter(List<LibrariesBean> libraries) {
            files = libraries;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.itemClickListener = listener;
        }

        public interface OnItemClickListener {

            void onItemClick(LibrariesBean bean);
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
            boolean isEnabled = bean.useYn.equals("Y");
            holder.enabled.setText(isEnabled ? "ON" : "OFF");
            holder.enabled.setSelected(isEnabled);
            String dependency = bean.dependency;
            if (!dependency.isEmpty()) {
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
                LibrariesBean bean = files.get(getAdapterPosition());
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(bean);
                }
            }
        }
    }
}
