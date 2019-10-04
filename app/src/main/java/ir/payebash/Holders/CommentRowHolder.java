package ir.payebash.Holders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ir.payebash.Application;
import ir.payebash.Moudle.CircleImageView;
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