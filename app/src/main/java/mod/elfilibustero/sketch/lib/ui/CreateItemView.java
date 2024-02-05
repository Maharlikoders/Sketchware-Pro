package mod.elfilibustero.sketch.lib.ui;

import a.a.a.wB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.sketchware.pro.databinding.CreateItemViewBinding;
import com.sketchware.pro.R;

public class CreateItemView extends CardView {
    private CreateItemViewBinding binding;

    public static final int PROJECT = 0;
    public static final int RESTORE = 1;
    public static final int CLONE = 2;

    public CreateItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        binding = CreateItemViewBinding.inflate(LayoutInflater.from(context), this, true);
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) wB.a(context, 8f);
        layoutParams.topMargin = (int) wB.a(context, 4f);
        layoutParams.bottomMargin = (int) wB.a(context, 4f);
        layoutParams.rightMargin = (int) wB.a(context, 8f);
        setLayoutParams(layoutParams);
    }

    public void setData(int type) {
        binding.icon.setImageResource(getIcon(type));
        binding.title.setText(getName(type));
        binding.desc.setText(getDescription(type));
    }

    private int getIcon(int type) {
        return switch (type) {
            case PROJECT -> R.drawable.app_icon00;
            case RESTORE -> R.drawable.data_backup_96;
            case CLONE -> R.drawable.ic_repo_forked_16;
            default -> 0;
        };
    }

    private String getName(int type) {
        return switch (type) {
            case PROJECT -> "Create";
            case RESTORE -> "Restore";
            case CLONE -> "Clone";
            default -> "";
        };
    }

    private String getDescription(int type) {
        return switch (type) {
            case PROJECT -> "Create a new project";
            case RESTORE -> "Restore your project";
            case CLONE -> "Clone a github project";
            default -> "";
        };
    }
}
