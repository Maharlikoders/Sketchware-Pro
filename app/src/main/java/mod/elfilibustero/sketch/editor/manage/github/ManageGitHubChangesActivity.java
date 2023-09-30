package mod.elfilibustero.sketch.editor.manage.github;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sketchware.remod.R;
import com.sketchware.remod.databinding.ManageGithubChangesBinding;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.beans.GitCommitBean;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.git.Commit;
import mod.elfilibustero.sketch.lib.git.ListUncommittedChanges;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class ManageGitHubChangesActivity extends AppCompatActivity {

    private ManageGithubChangesBinding binding;
    private GitHubUtil util;
    private GitHubBean bean;
    private String sc_id;
    private Adapter adapter;

    private List<GitCommitBean> changes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManageGithubChangesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }
        util = new GitHubUtil(sc_id);
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
    }

    private void init() {
        bean = getIntent().getParcelableExtra("github");
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Changes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        binding.fab.setOnClickListener(v -> showCommitDialog());
        binding.selectAll.setOnClickListener(v -> selectAction());
        setupRecyclerView();
        refreshList();
    }

    private void setupRecyclerView() {
        RecyclerView list = binding.recyclerView;

        TypedArray typedArray = obtainStyledAttributes(null, new int[0]);
        try {
            Method method = View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
            method.setAccessible(true);
            method.invoke(list, typedArray);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
        typedArray.recycle();
        list.setVerticalScrollBarEnabled(true);
        adapter = new Adapter(changes);
        adapter.setHasStableIds(true);
        list.setAdapter(adapter);
    }

    private void refreshList() {
        changes.clear();
        changes.addAll(new ListUncommittedChanges(sc_id).get());
        binding.allChanges.setText(changes.size() + " changed files");
        binding.selectAll.setEnabled(changes.size() > 0);
        adapter.notifyDataSetChanged();
    }

    private void selectAction() {
        boolean hasUncommittedChanges = adapter.getSelectedChanges().size() <= 0;
        binding.selectAll.setChecked(hasUncommittedChanges);
        adapter.setSelection(hasUncommittedChanges);
    }

    private void showCommitDialog() {
        List<GitCommitBean> selected = adapter.getSelectedChanges();
        Commit commit = new Commit(this, sc_id);
        Commit.Callback callback = new Commit.Callback() {
            @Override
            public void onSuccess(boolean success) {
                if (success) {
                    runOnUiThread(() -> refreshList());
                }
            }
        };
        if (selected.size() != 0) {
            if (selected.size() == 1) {
                GitCommitBean bean = selected.get(0);
                commit.execute(bean.name, callback);
            } else {
                commit.execute(selected, callback);
            }
        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private final List<GitCommitBean> changes;
        private SparseBooleanArray selectedItems;

        public Adapter(List<GitCommitBean> changes) {
            this.changes = changes;
            changes.sort(Comparator.comparing((GitCommitBean bean) -> bean.name.toLowerCase()));
            selectedItems = new SparseBooleanArray();
        }

        @Override
        public int getItemCount() {
            return changes.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_github_changes_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GitCommitBean change = changes.get(position);
            holder.name.setText(toTruncatedString(change.name));
            holder.type.setImageResource(GitCommitBean.getIcon(change.type));

            holder.itemView.setActivated(selectedItems.get(position, false));

            holder.selected.setChecked(selectedItems.get(position, false));
            View.OnClickListener selectingListener = v -> {
                toggleSelection(position);
            };
            holder.selected.setOnClickListener(selectingListener);
            holder.selectableItem.setOnClickListener(selectingListener);
        }

        public List<GitCommitBean> getSelectedChanges() {
            List<GitCommitBean> selectedChanges = new ArrayList<>();
            for (int i = 0; i < selectedItems.size(); i++) {
                selectedChanges.add(changes.get(selectedItems.keyAt(i)));
            }
            return selectedChanges;
        }

        public void setSelection(boolean select) {
            for (int i = 0; i < changes.size(); i++) {
                selectedItems.put(i, select);
            }
            notifyDataSetChanged();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {

            public final LinearLayout selectableItem;
            public final CheckBox selected;
            public final TextView name;
            public final ImageView type;

            public ViewHolder(View itemView) {
                super(itemView);
                selectableItem = itemView.findViewById(R.id.view_item);
                selected = itemView.findViewById(R.id.check);
                name = itemView.findViewById(R.id.name);
                type = itemView.findViewById(R.id.type);
            }
        }

        private void toggleSelection(int position) {
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
            } else {
                selectedItems.put(position, true);
            }
            notifyItemChanged(position);
        }

        private String toTruncatedString(String text) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int maxLength = screenWidth - 16;

            if (text.length() <= maxLength) {
                return text;
            } else {
                int middle = maxLength / 2;
                int endLength = middle;

                while (endLength > 0 && text.charAt(middle + endLength) != '\\') {
                    endLength--;
                }

                int startLength = middle - endLength;
                StringBuilder builder = new StringBuilder();
                builder.append(text.substring(0, startLength));
                builder.append("...");
                builder.append(text.substring(text.length() - endLength));

                return builder.toString();
            }
        }
    }
}
