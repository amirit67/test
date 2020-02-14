package ir.payebash.moudle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TagLayoutImageView extends LinearLayout {

    private LinearLayout mRow;

    public TagLayoutImageView(Context context) {
        super(context);
    }

    public TagLayoutImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagLayoutImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void addTagView(CircleImageView tag) {

        this.setOrientation(VERTICAL);
        if (mRow == null) {
            mRow = new LinearLayout(getContext());
            mRow.setOrientation(HORIZONTAL);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            mRow.setLayoutParams(params);
            addView(mRow);
            mRow.addView(tag);
        } else if (!allowAddToThisRow(tag)) {

            mRow = new LinearLayout(getContext());
            mRow.setOrientation(HORIZONTAL);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            mRow.setLayoutParams(params);

            addView(mRow);

            mRow.addView(tag);
        }
    }

    private boolean allowAddToThisRow(CircleImageView v) {

        int sumWith = 200;

        for (int i = 0; i < mRow.getChildCount(); i++) {
            sumWith += (mRow.getChildAt(i)).getWidth();
        }
        if (sumWith < mRow.getWidth()) {
            mRow.addView(v);
            return true;
        }
        return false;
    }
}
