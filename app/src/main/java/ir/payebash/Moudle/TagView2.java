package ir.payebash.Moudle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.payebash.Application;
import ir.payebash.R;

/**
 * Created by Payam on 27/06/2016.
 */
public class TagView2 extends TextView {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //private int mTextColor = 0xFFFFFFFF;
    private int mTextSize = (int) getResources().getInteger(R.integer.normalfont);
    private String mText = "Tag View";
    private Rect maskPathBound;

    private int mHeight;
    private int mWidth;


    public TagView2(Context context) {
        super(context);
        this.setGravity(Gravity.CENTER);
    }

    public TagView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setGravity(Gravity.CENTER);
    }

    public TagView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = (mWidth - maskPathBound.width()) / 2;

        int y = (int) ((mHeight + maskPathBound.height()) / 2.5);


        canvas.drawText(mText, x, y, mPaint);

    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        float scale = getResources().getDisplayMetrics().density;

        // text color - #3D3D3D
        //mPaint.setColor(mTextColor);
        //mPaint.setTypeface(Application.font);
        // text size in pixels
        mPaint.setTextSize((int) (mTextSize * scale));
        // text shadow
        // mPaint.setShadowLayer(1f, -10f, -40f, 0x00000000);

        // mPaint.setTypeface(typeface);

        // draw text to the Canvas center
        maskPathBound = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), maskPathBound);

        mWidth = maskPathBound.width() + 25;
        mHeight = maskPathBound.height() + 20;


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth, mHeight);

        params.setMargins(4, 3, 6, 3);

        setLayoutParams(params);

    }

    public int getTagHeight() {
        return mHeight + ((LinearLayout.LayoutParams) getLayoutParams()).topMargin + ((LinearLayout.LayoutParams) getLayoutParams()).bottomMargin;
    }

    public int getTagWidth() {
        return mWidth + ((LinearLayout.LayoutParams) getLayoutParams()).leftMargin + ((LinearLayout.LayoutParams) getLayoutParams()).rightMargin;
    }
}
