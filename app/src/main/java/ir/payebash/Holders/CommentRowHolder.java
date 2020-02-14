package ir.payebash.Holders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import ir.payebash.moudle.CircleImageView;
import ir.payebash.R;


public class CommentRowHolder extends RecyclerView.ViewHolder {
    public TextView txt_title;
    public CircleImageView img;

    public CommentRowHolder(View view) {
        super(view);
        img = view.findViewById(R.id.img_profile);
        this.txt_title = view.findViewById(R.id.txt1);
    }

}