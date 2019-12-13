package ir.payebash.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OverlapRecyclerViewDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public OverlapRecyclerViewDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);

        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }

    }


    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        // outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        if (itemPosition != parent.getAdapter().getItemCount() - 1)
            outRect.set(-15, 10, 0, 0);
        else
            outRect.set(0, 10, 0, 0);
    }
}