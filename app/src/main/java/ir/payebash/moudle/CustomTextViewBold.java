package ir.payebash.moudle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import ir.payebash.classes.FormatHelper;

public class CustomTextViewBold extends TextView {
    public static Typeface FONT_NAME;


    public CustomTextViewBold(Context context) {
        super(context);
        if (FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "yekanbold.ttf");
        this.setTypeface(FONT_NAME);
    }

    public CustomTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "yekanbold.ttf");
        this.setTypeface(FONT_NAME);
    }

    public CustomTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "yekanbold.ttf");
        this.setTypeface(FONT_NAME);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null)
            text = FormatHelper.toPersianNumber(text.toString());
        super.setText(text, type);
    }
}