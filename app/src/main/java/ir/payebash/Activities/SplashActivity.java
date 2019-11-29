package ir.payebash.Activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;

import com.jaredrummler.android.widget.AnimatedSvgView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.NotifItem;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        GetServices();
        Transaction();
        AnimatedSvgView svgView = findViewById(R.id.animated_svg_view);
        svgView.start();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                AnimatedSvgView svgView2 = findViewById(R.id.line_svg_view);
                svgView2.start();
                AnimatedSvgView svgView3 = findViewById(R.id.font_svg_view);
                svgView3.start();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (NetworkUtils.getConnectivity(SplashActivity.this) != false) {
                            HSH.onOpenPage(SplashActivity.this, MainActivity.class);
                            finish();
                        } else if (NetworkUtils.getConnectivity(SplashActivity.this) == false) {
                            HSH.onOpenPage(SplashActivity.this, NoConnectioonActivity.class);
                            finish();
                        }
                        //finish();
                    }
                }, 1500);
            }
        }, 1500);
    }

    private void GetServices() {
        Call<List<NotifItem>> call =
                ApiClient.getClient().create(ApiInterface.class).GetServices();
        call.enqueue(new Callback<List<NotifItem>>() {
            @Override
            public void onResponse(Call<List<NotifItem>> call, retrofit2.Response<List<NotifItem>> response) {
                if (response.code() == 200)
                    try {
                        List<NotifItem> result = response.body();
                        String query;
                        Cursor cr = null;
                        for (int i = 0; i < result.size(); i++) {
                            cr = Application.database.rawQuery("SELECT id FROM categories WHERE id = '" + result.get(i).getId() + "'", null);
                            try {
                                if (cr.getCount() > 0) {
                                    query = "UPDATE categories SET " +
                                            "parentId='" + result.get(i).getParentId() + "' , " +
                                            "hasChild='" + result.get(i).getHasChild() + "' , " +
                                            "name='" + result.get(i).getName() + "' , " +
                                            "description='" + result.get(i).getDescription() + "' , " +
                                            "imageUrl ='" + result.get(i).getImageUrl() + "' , " +
                                            "addedFeild ='" + result.get(i).getAddedFeild() + "' , " +
                                            "isCommercial ='" + result.get(i).getIsCommercial() + "' , " +
                                            "sort ='" + result.get(i).getSort() + "' , " +
                                            "state='" + result.get(i).getState() + "'" +
                                            " WHERE id = '" + result.get(i).getId() + "'";
                                    Application.database.execSQL(query);
                                } else {
                                    query = "INSERT INTO categories ";
                                    query += "(id,parentId,hasChild,name,description,imageUrl,addedFeild,isCommercial,sort,state)" +
                                            "VALUES('" + result.get(i).getId() + "'," +
                                            "'" + result.get(i).getParentId() + "'," +
                                            "'" + result.get(i).getHasChild() + "'," +
                                            "'" + result.get(i).getName() + "'," +
                                            "'" + result.get(i).getDescription() + "'," +
                                            "'" + result.get(i).getImageUrl() + "'," +
                                            "'" + result.get(i).getAddedFeild() + "'," +
                                            "'" + result.get(i).getIsCommercial() + "'," +
                                            "'" + result.get(i).getSort() + "'," +
                                            "'" + result.get(i).getState() + "')";
                                    Application.database.execSQL(query);
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                else
                    GetServices();
            }

            @Override
            public void onFailure(Call<List<NotifItem>> call, Throwable t) {
                GetServices();
            }
        });
    }

    private void Transaction() {

        try {
            Cursor cr = Application.database.rawQuery("SELECT * from RecentVisit WHERE IsPaid='true' and IsSuccessed = 'false'", null);
            for (int i = 0; i < cr.getCount(); i++) {
                cr.moveToPosition(i);
                Map<String, String> params = new HashMap<>();
                params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                params.put(getString(R.string.PostId), cr.getString(cr.getColumnIndex("Id")));
                params.put(getString(R.string.Amount), "1000");
                params.put(getString(R.string.refID), cr.getString(cr.getColumnIndex("refID")));
                InsertPaymentAsynkTask(params);
            }
        } catch (Exception e) {
        }
    }

    private void InsertPaymentAsynkTask(final Map<String, String> params) {
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).InsertPayment(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String query = "Update RecentVisit set IsSuccessed = 'true' " +
                                "WHERE data LIKE '%" + params.get(getString(R.string.PostId)) + "%' ";
                        Application.database.execSQL(query);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

}
