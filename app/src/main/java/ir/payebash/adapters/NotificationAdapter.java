package ir.payebash.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.classes.HSH;
import ir.payebash.fragments.NotificationFragment;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.models.CategoryItem;
import ir.payebash.models.NotifyData;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static ir.payebash.Application.preferences;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private String ServicesIds;
    private List<CategoryItem> feedItemList;
    private Context context;
    private Cursor cr;
    private String subscribeType = "";
    private SweetAlertDialog dialog;
    private List<CategoryItem> feed = new ArrayList<>();

    public NotificationAdapter(Context context) {

        try {
            this.context = context;
            ServicesIds = preferences.getString(context.getString(R.string.ServicesIds), "").trim();
            cr = Application.database.rawQuery("SELECT * from categories where state = 'true' and parentId = 'null' order by sort", null);
            while (cr.moveToNext()) {
                CategoryItem item = new CategoryItem();
                item.setId(cr.getString(cr.getColumnIndex("id")));
                item.setName(cr.getString(cr.getColumnIndex("name")));
                try {
                    item.setDescription(cr.getString(cr.getColumnIndex("description")));
                } catch (Exception e) {
                }
                try {
                    item.setImg(cr.getString(cr.getColumnIndex("img")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                feed.add(item);
            }
            this.feedItemList = feed;
            cr.close();
        } catch (Exception e) {
        }

    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, null);
        return new NotificationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationHolder Holder, final int pos) {

        if (Holder instanceof NotificationHolder) {
            Holder.tvTitle.setText(feedItemList.get(pos).getName());
            Holder.tvdescription.setText(feedItemList.get(pos).getDescription());
            if (ServicesIds.trim().contains("," + NotificationFragment.btn_location.getTag() + "-" + feedItemList.get(pos).getId()))
                Holder.img_tick.setVisibility(View.VISIBLE);
            else
                Holder.img_tick.setVisibility(View.GONE);

            Holder.itemView.setOnClickListener(v -> {
                ServicesIds = preferences.getString(context.getString(R.string.ServicesIds), "").replace(" ", "").trim();
                String[] temp = ServicesIds.split(",");
                ServicesIds = "";
                for (int i = 0; i < temp.length; i++)
                    if (!temp[i].equals("") && !temp[i].contains("null") && temp[i].contains("-")) {
                        if (i == temp.length - 1)
                            ServicesIds += "," + temp[i] + ",";
                        else
                            ServicesIds += "," + temp[i];
                    }

                if (!NotificationFragment.btn_location.getTag().toString().equals("0")) {
                    Holder.pb.setVisibility(View.VISIBLE);
                    int img_tick_isVisible = Holder.img_tick.getVisibility();
                    Holder.img_tick.setVisibility(View.GONE);
                    String ServiceId = NotificationFragment.btn_location.getTag() + "-" + feedItemList.get(pos).getId();
                    if ((ServicesIds.trim() + ",").contains("," + ServiceId + ",")) {
                        ServicesIds = ServicesIds.trim().replace("," + ServiceId + ",", ",");
                        subscribeType = "unsubscribe";
                    } else {
                        ServicesIds += ServiceId.replace(",,", ",") + ",";
                        subscribeType = "subscribe";
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put(context.getString(R.string.UserId), preferences.getString(context.getString(R.string.UserId), "0").trim());
                    params.put(context.getString(R.string.ServicesIds), ServicesIds.trim());
                    //params.put(context.getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
                    ServicesIds(params, Holder.img_tick, img_tick_isVisible, Holder.pb, ServiceId.trim());
                } else {
                    dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("انتخاب شهر")
                            .setContentText("برای دریافت اعلانیه های هر دسته لازم است ابتدا شهر و سپس دسته مورد نظر خود را انتخاب نمایید")
                            .setConfirmText("باشه")
                            .setConfirmClickListener(sDialog -> {
                                dialog.dismiss();
                               // HSH.selectLocation(context, 1, NotificationFragment.btn_location);
                            });
                    HSH.dialog(dialog);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        try {
            return position;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void ServicesIds(final Map<String, String> params, final View img_tick, final int img_tick_isVisible, final ProgressBar pb, final String ServiceId) {
        Call<ResponseBody> call = ApiClient.getClient().create(ApiInterface.class).UpdateMyServices(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    HSH.editor(context.getString(R.string.ServicesIds), ServicesIds.trim());
                    if (subscribeType.equals("unsubscribe")) {
                        img_tick.setVisibility(View.GONE);
                        unSubscribeTopic(ServiceId);
                    } else {
                        img_tick.setVisibility(View.VISIBLE);
                        SubscribeTopic(ServiceId);
                    }
                    notifyDataSetChanged();
                } else {
                    HSH.showtoast(context, "خطا در ارسال");
                    img_tick.setVisibility(img_tick_isVisible);
                }
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(context, "خطا در ارسال");
                pb.setVisibility(View.GONE);
                img_tick.setVisibility(img_tick_isVisible);
            }
        });
    }

    public void SubscribeTopic(String ServiceId) {
        Call<ResponseBody> call = ApiClient.getClient3().create(ApiInterface.class).Subscribe(FirebaseInstanceId.getInstance().getToken(), ServiceId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(context, "خطا در ارسال");
            }
        });
    }

    public void unSubscribeTopic(final String ServiceId) {
        try {
            final String[] s = new String[1];
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            s[0] = task.getResult().getToken();
                            NotifyData.Data d = new NotifyData.Data(context.getString(R.string.topics) + ServiceId, s);
                            Call<ResponseBody> call = ApiClient.getClient3().create(ApiInterface.class).unSubscribe(d);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    HSH.showtoast(context, "خطا در ارسال");
                                }
                            });
                            return;
                        }
                    });

        } catch (Exception e) {
        }
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvdescription;
        public ImageView img_tick;
        public ProgressBar pb;

        public NotificationHolder(View view) {
            super(view);

            this.tvTitle = view.findViewById(R.id.lbl_name);
            this.tvdescription = view.findViewById(R.id.lbl_description);
            this.pb = view.findViewById(R.id.pb);
            this.img_tick = view.findViewById(R.id.img_tick);

        }

    }
}