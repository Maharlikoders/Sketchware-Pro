package mod.elfilibustero.sketch.lib.git;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import a.a.a.aB;

import com.sketchware.pro.R;

import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hey.studios.util.Helper;

public class BranchSelection {

    private Context context;
    private String sc_id;

    public BranchSelection(Context context, String sc_id) {
        this.context = context;
        this.sc_id = sc_id;
    }

    public void execute() {
        GitHubUtil util = new GitHubUtil(sc_id);
        List<String> branches = util.getAllBranches();

        aB dialog = new aB((Activity) context);
        dialog.b("Branches");
        RecyclerView list = new RecyclerView(context);

        TypedArray typedArray = context.obtainStyledAttributes(null, new int[0]);
        try {
            Method method = View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
            method.setAccessible(true);
            method.invoke(list, typedArray);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //LogUtil.e(TAG, "Couldn't add scrollbars to RecyclerView", e);
        }
        typedArray.recycle();
        list.setVerticalScrollBarEnabled(true);

        list.setLayoutManager(new LinearLayoutManager(null));
        Adapter adapter = new Adapter(branches);
        adapter.setHasStableIds(true);
        list.setAdapter(adapter);
        dialog.a(list);
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            String branch = adapter.getSelectedBranch();
            selectBranch(util, branch);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void selectBranch(GitHubUtil util, String branch) {
        try (Repository repository = util.getRepository()) {
            Git git = new Git(repository);

            CheckoutCommand checkout = git.checkout()
                .setName(branch)
                .setCreateBranch(false);

            checkout.call();
        } catch (GitAPIException e) {

        } catch (Exception e) {

        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private final List<String> branches;
        private int lastCheckedPosition = -1;

        public Adapter(List<String> branches) {
            this.branches = branches;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_file_picker_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.name.setText((String) branches.get(position));
            if (position == lastCheckedPosition) {
                holder.imageSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imageSelected.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return branches.size();
        }

        public String getSelectedBranch() {
            return branches.get(lastCheckedPosition);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public final TextView name;
            public final FrameLayout fileArea;
            public final ImageView imageSelected;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_file_name);
                fileArea = itemView.findViewById(R.id.file_area);
                imageSelected = itemView.findViewById(R.id.img_selected);
                ImageView branchImg = new ImageView(itemView.getContext());
                fileArea.addView(branchImg);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                var file = branches.get(getAdapterPosition());
                int copyOfLastCheckedPosition = lastCheckedPosition;
                lastCheckedPosition = getAdapterPosition();
                notifyItemChanged(copyOfLastCheckedPosition);
                notifyItemChanged(lastCheckedPosition);
            }
        }
    }
}
