package ir.payebash.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import ir.payebash.R;


public class CodePicker extends LinearLayout {
    final int DEFAULT_COUNT = 4;
    private int count;

    public CodePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ActionBar);
            this.count = a.getInteger(0, 4);
            setOrientation(HORIZONTAL);
            setGravity(17);
            for (int i = 0; i < this.count; i++) {
                addView(generateDigitContainer(i));
            }
            a.recycle();
        }
    }

    private EditText generateDigitContainer(final int id) {
        final EditText et = new EditText(getContext());
        LayoutParams params = new LayoutParams(-2, -2);
        params.setMargins(4, 0, 4, 0);
        Typeface tp = Typeface.createFromAsset(getContext().getAssets(), "yekanmedium.ttf");
        et.setId(id);
        et.setBackgroundResource(R.drawable.shadow_code_picker);
        et.setTextSize(20.0f);
        et.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        et.setGravity(17);
        et.setRawInputType(8194);
        et.setFilters(new InputFilter[]{new LengthFilter(1)});
        et.setTypeface(tp);
        et.setLayoutParams(params);
        et.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et.getText().length() > 0) {
                    if (CodePicker.this.findViewById(id + 1) != null) {
                        CodePicker.this.findViewById(id + 1).requestFocus();
                    }
                } else if (CodePicker.this.findViewById(id - 1) != null) {
                    CodePicker.this.findViewById(id - 1).requestFocus();
                }
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        return et;
    }

    public String getCode() {
        String code = "";
        for (int i = 0; i < this.count; i++) {
            code = code + ((EditText) findViewById(i)).getText().toString();
        }
        return code;
    }
}
