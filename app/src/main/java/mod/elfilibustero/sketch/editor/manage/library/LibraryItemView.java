package mod.elfilibustero.sketch.editor.manage.library;

import a.a.a.wB;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.sketchware.pro.databinding.ManageXmlBaseItemBinding;
import com.sketchware.pro.R;

import mod.elfilibustero.sketch.constants.LibrariesConstant;
import mod.hey.studios.util.Helper;

public class LibraryItemView extends CardView {
    private ManageXmlBaseItemBinding binding;

    public LibraryItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        binding = ManageXmlBaseItemBinding.inflate(LayoutInflater.from(context), this, true);
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
        binding.icon.setImageResource(LibrariesConstant.getIcon(type));
        binding.title.setText(LibrariesConstant.getName(type));
        String desc = LibrariesConstant.getDescription(type);
        if (desc.equals("Deprecated")) {
            binding.desc.setTextColor(Color.RED);
        }
        binding.desc.setText(desc);
    }
}
