package ir.payebash.holders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import ir.payebash.moudle.CircleImageView;
import ir.payebash.moudle.TriangleLabelView;
import ir.payebash.R;

public class PayeHolder extends RecyclerView.ViewHolder {
    public TextView txt_loc;
    public TextView txt_title;
    public TextView txt_deadline;
    public TextView txt_cost;
    public CircleImageView img_post;
    public LinearLayout mTagLayout;
    public TriangleLabelView lbl_state;
    public RelativeLayout rl_tag;

    public PayeHolder(View view) {
        super(view);
        this.txt_loc = view.findViewById(R.id.txt_loc);
        this.txt_title = view.findViewById(R.id.title);
        this.txt_deadline = view.findViewById(R.id.txt_deadline);
        this.txt_cost = view.findViewById(R.id.txt_cost);
        this.img_post = view.findViewById(R.id.img_post);
        this.mTagLayout = view.findViewById(R.id.mTagLayout);
        this.lbl_state = view.findViewById(R.id.lbl_state);
        this.rl_tag = view.findViewById(R.id.rl_tag);
    }

}