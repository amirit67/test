package ir.payebash.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.R;
import ir.payebash.modelviewsChat.MessageViewModel;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<MessageViewModel> messages;
    Context context;
    @Inject
    ImageLoader imageLoader;


    public MessageAdapter(Context context, ArrayList<MessageViewModel> messages) {
        this.context = context;
        this.messages = messages;
        Application.getComponent().Inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == 1) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_mine, null);
            return new ViewHolder(v);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_other, null);
            return new ViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).IsMine;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof ViewHolder) {
            /*if (i == 0)
                details.clear();*/
            final ViewHolder Holder;
            Holder = (ViewHolder) holder;
            Holder.setIsRecyclable(false);

            try {
                //byte[] decodedString = Base64.decode(messages.get(i).Avatar.replace("data:image/false;base64,", ""), Base64.DEFAULT);
                //Bitmap avatarBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //Holder.avatar.setImageBitmap(messages.get(i).Timestamp);
                imageLoader.displayImage(messages.get(i).Avatar, Holder.avatar);
                Holder.time.setText(messages.get(i).Timestamp);
                Holder.user.setText(messages.get(i).From);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Holder.content.setText(messages.get(i).Content);


        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView time, content, user;

        ViewHolder(View v) {
            super(v);
            avatar = v.findViewById(R.id.imgMessageAvatar);
            time = v.findViewById(R.id.txt_time);
            content = v.findViewById(R.id.txtMessageContent);
            user = v.findViewById(R.id.txtMessageUser);
        }

    }
}