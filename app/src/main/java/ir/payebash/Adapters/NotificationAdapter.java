package ir.payebash.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Fragments.NotificationFragment;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.CategoryItem;
import ir.payebash.Models.NotifyData;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class NotificationAdapter extends BaseAdapter {

    private String ServicesIds;
    private List<CategoryItem> feedItemList;
    private Context context;
    private Cursor cr;
    private String subscribeType = "";
    private SweetAlertDialog dialog;
    private List<CategoryItem> feed = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public NotificationAdapter(Context context) {

        try {
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            ServicesIds = Application.preferences.getString(context.getString(R.string.ServicesIds), "").trim();
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
        } catch (Exception e) {
        }

    }

    @Override
    public int getCount() {
        try {
            return feedItemList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        try {
            return position;
        } catch (Exception e) {
            return true;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {


        // Inflate the item layout and set the views
        try {
            View listItem = convertView;
            final int pos = position;
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.item_category, null);
            }
            final TextView tvTitle = listItem.findViewById(R.id.lbl_name);
            final TextView tvdescription = listItem.findViewById(R.id.lbl_description);
            final ProgressBar pb = listItem.findViewById(R.id.pb);
            final ImageView img_tick = listItem.findViewById(R.id.img_tick);

            tvTitle.setText(feedItemList.get(pos).getName());
            tvdescription.setText(feedItemList.get(pos).getDescription());
            if (ServicesIds.trim().contains("," + NotificationFragment.btn_location.getTag() + "-" + feedItemList.get(pos).getId()))
                img_tick.setVisibility(View.VISIBLE);
            else
                img_tick.setVisibility(View.GONE);

            listItem.setOnClickListener(v -> {
                ServicesIds = Application.preferences.getString(context.getString(R.string.ServicesIds), "").replace(" ", "").trim();
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
                    pb.setVisibility(View.VISIBLE);
                    int img_tick_isVisible = img_tick.getVisibility();
                    img_tick.setVisibility(View.GONE);
                    String ServiceId = NotificationFragment.btn_location.getTag() + "-" + feedItemList.get(pos).getId();
                    if ((ServicesIds.trim() + ",").contains("," + ServiceId + ",")) {
                        ServicesIds = ServicesIds.trim().replace("," + ServiceId + ",", ",");
                        subscribeType = "unsubscribe";
                    } else {
                        ServicesIds += ServiceId.replace(",,", ",") + ",";
                        subscribeType = "subscribe";
                    }
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    //Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                Map<String, String> params = new HashMap<>();
                                params.put(context.getString(R.string.UserId), Application.preferences.getString(context.getString(R.string.UserId), "0").trim());
                                params.put(context.getString(R.string.ServicesIds), ServicesIds.trim());
                                params.put(context.getString(R.string.Token), task.getResult().getToken());
                                ServicesIds(params, img_tick, img_tick_isVisible, pb, ServiceId.trim());

                            });

                } else {
                    dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("انتخاب شهر")
                            .setContentText("برای دریافت اعلانیه های هر دسته لازم است ابتدا شهر و سپس دسته مورد نظر خود را انتخاب نمایید")
                            .setConfirmText("باشه")
                            .setConfirmClickListener(sDialog -> {
                                dialog.dismiss();
                                HSH.selectLocation(context, 1, NotificationFragment.btn_location);
                            });
                    HSH.dialog(dialog);
                }
            });

            return listItem;
        } catch (Exception e) {
            return null;
        }
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
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    Call<ResponseBody> call = ApiClient.getClient3().create(ApiInterface.class).Subscribe(task.getResult().getToken(), ServiceId);
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

                });
    }

    public void unSubscribeTopic(String ServiceId) {
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String[] s = new String[1];
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

                    });

        } catch (Exception e) {
        }
    }
}