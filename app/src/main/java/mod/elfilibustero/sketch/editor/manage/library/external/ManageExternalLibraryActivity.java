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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import a.a.a.wq;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.ExternalLibraryBean;
import mod.hey.studios.util.Helper;

public class ManageExternalLibraryActivity extends AppCompatActivity {

	private ManageExternalLibraryBinding binding;

	private String sc_id;
    private String dataPath;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));

        dataPath = wq.b(sc_id) + File.separator + "external_library";

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
        if (FileUtil.isExistFile(dataPath)) {
            FileUtil.writeFile(dataPath, "[]");
        }
        try {
            libraries = new Gson().fromJson(FileUtil.readFile(dataPath), new TypeToken<List<ExternalLibraryBean>>(){}.getType());
            if (libraries == null) libraries = new ArrayList<>();
        } catch (Exception e) {
        }
        if (libraries != null && libraries.isEmpty()) {
        	binding.guide.setVisibility(View.VISIBLE);
        	binding.recyclerView.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
        	binding.guide.setVisibility(View.GONE);
        	binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void downloadLibrary() {
        Intent intent = new Intent(getApplicationContext(), ManageExternalLibraryItemActivity.class);
        intent.putExtra("sc_id", sc_id);
        openLibraryManager.launch(intent);
    }

    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.FileViewHolder> {
        private List<ExternalLibraryBean> libraries;
        private OnItemClickListener itemClickListener;

        public LibraryAdapter(List<ExternalLibraryBean> libraries) {
            this.libraries = libraries;
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
            var bean = libraries.get(position);
            holder.name.setText(bean.name);
            holder.dep.setText(bean.dependency);
        }

        @Override
        public int getItemCount() {
            return libraries.size();
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
