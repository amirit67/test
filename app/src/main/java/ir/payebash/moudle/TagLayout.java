package ir.payebash.moudle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Payam on 27/06/2016.
 */
public class TagLayout extends LinearLayout {

    private LinearLayout mRow;

    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void addTagView(TagView tag) {

        this.setOrientation(VERTICAL);

        if (mRow == null) {
            mRow = new LinearLayout(getContext());
            mRow.setOrientation(HORIZONTAL);

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.gravity = Gravity.RIGHT;
            mRow.setLayoutParams(params);

            addView(mRow);
            mRow.addView(tag);
        } else if (!allowAddToThisRow(tag)) {

            mRow = new LinearLayout(getContext());
            mRow.setOrientation(HORIZONTAL);

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.gravity = Gravity.RIGHT;
            mRow.setLayoutParams(params);


            addView(mRow);

            mRow.addView(tag);
        }

    }

    private boolean allowAddToThisRow(TagView v) {

        int sumWith = v.getTagWidth();

        for (int i = 0; i < mRow.getChildCount(); i++) {
            sumWith += ((TagView) mRow.getChildAt(i)).getTagWidth();
        }

        if (sumWith < mRow.getWidth()) {
            mRow.addView(v);
            return true;

        }

        return false;
    }
}
