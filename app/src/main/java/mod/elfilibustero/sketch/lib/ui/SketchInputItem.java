package mod.elfilibustero.sketch.lib.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sketchware.pro.R;

import a.a.a.Kw;
import a.a.a.OB;
import a.a.a.SB;
import a.a.a.TB;
import a.a.a._B;
import a.a.a.aB;
import a.a.a.jC;
import a.a.a.mB;
import a.a.a.uq;
import a.a.a.wB;
import mod.hey.studios.util.Helper;

@SuppressLint("ViewConstructor")
public class SketchInputItem extends RelativeLayout implements View.OnClickListener {

    private Context context;
    private String key = "";
    private String value = "";
    private ImageView imgLeftIcon;
    private int icon;
    private TextView tvName;
    private TextView tvValue;
    private View propertyItem;
    private View propertyMenuItem;
    private Kw valueChangeListener;

    public SketchInputItem(Context context) {
        super(context);
        initialize(context);
    }

    private void setIcon(ImageView imageView) {
        switch (key) {
            case "min_sdk":
            case "target_sdk":
                icon = R.drawable.one_to_many_48;
                break;
        }
        imageView.setImageResource(icon);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        tvName.setText(getName());
        if (propertyMenuItem.getVisibility() == VISIBLE) {
            setIcon(findViewById(R.id.img_icon));
            ((TextView) findViewById(R.id.tv_title)).setText(getName());
            return;
        }
        setIcon(imgLeftIcon);
    }

    private String getName() {
        return switch (key) {
            case "min_sdk" -> "Min SDK";
            case "target_sdk" -> "Target SDK";
            default -> "";
        };
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        tvValue.setText(value);
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            switch (key) {
                case "min_sdk":
                case "target_sdk":
                    showNumberDecimalInputDialog(0, 33);
                    return;
            }
        }
    }

    public void setOnPropertyValueChangeListener(Kw onPropertyValueChangeListener) {
        valueChangeListener = onPropertyValueChangeListener;
    }

    public void setOrientationItem(int orientationItem) {
        if (orientationItem == 0) {
            propertyItem.setVisibility(GONE);
            propertyMenuItem.setVisibility(VISIBLE);
        } else {
            propertyItem.setVisibility(VISIBLE);
            propertyMenuItem.setVisibility(GONE);
        }
    }

    private void initialize(Context context) {
        this.context = context;
        wB.a(context, this, R.layout.property_input_item);
        tvName = findViewById(R.id.tv_name);
        tvValue = findViewById(R.id.tv_value);
        imgLeftIcon = findViewById(R.id.img_left_icon);
        propertyItem = findViewById(R.id.property_item);
        propertyMenuItem = findViewById(R.id.property_menu_item);
        setSoundEffectsEnabled(true);
        setOnClickListener(this);
    }

    private void showNumberInputDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setText(value);
        TB validator = new TB(context, view.findViewById(R.id.ti_input), 0,
                (key.equals("property_max") || key.equals("property_progress")) ? 0x7fffffff : 999);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showTextInputDialog(int minValue, int maxValue) {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        SB lengthValidator = new SB(context, view.findViewById(R.id.ti_input), minValue, maxValue);
        lengthValidator.a(value);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (lengthValidator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showNumberDecimalInputDialog(int minValue, int maxValue) {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        input.setInputType(minValue < 0 ?
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(value);
        OB validator = new OB(context, view.findViewById(R.id.ti_input), minValue, maxValue);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }
}
