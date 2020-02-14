package ir.payebash.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.jaredrummler.android.widget.AnimatedSvgView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.NotifItem;
import ir.payebash.models.TkModel;
import ir.payebash.models.parsijoo.ParsijooItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskGeoCoding;
import ir.payebash.asynktask.GetTokenAsynkTask;
import ir.payebash.utils.roundedimageview.GpsUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.jaredrummler.android.widget.AnimatedSvgView.STATE_FINISHED;

public class SplashActivity extends AppCompatActivity {

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private AnimatedSvgView svgView2, svgView3;
    @Inject
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Application.getComponent().Inject(this);
        AnimatedSvgView svgView = findViewById(R.id.animated_svg_view);
        svgView2 = findViewById(R.id.line_svg_view);
        svgView3 = findViewById(R.id.font_svg_view);

        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            if (isGPSEnable)
                ActivityCompat.requestPermissions(this, permissions, 123);
            else
                Toast.makeText(this, "برای نمایش موقعیت روی نقشه نیاز دسترسی به GPS می باشد", Toast.LENGTH_LONG).show();
        });

        svgView.start();
        new Handler().postDelayed(() -> {
            svgView2.start();
            svgView3.start();
           /* new Handler().postDelayed(() -> {
                if (NetworkUtils.getConnectivity(SplashActivity.this) != false) {
                    HSH.onOpenPage(SplashActivity.this, MainActivity.class);
                    finish();
                } else if (NetworkUtils.getConnectivity(SplashActivity.this) == false) {
                    HSH.onOpenPage(SplashActivity.this, NoConnectioonActivity.class);
                    finish();
                }
                //finish();
            }, 1500);*/
        }, 1500);
    }

    public void no_connection_retry_click(View v) {
        try {
            GetToken();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            findViewById(R.id.no_connection_ll_main).setVisibility(View.GONE);
            findViewById(R.id.animatedSvgView).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    private void GetServices() {
        Call<List<NotifItem>> call =
                retrofit.create(ApiInterface.class).GetServices();
        call.enqueue(new Callback<List<NotifItem>>() {
            @Override
            public void onResponse(Call<List<NotifItem>> call, retrofit2.Response<List<NotifItem>> response) {
                if (response.code() == 200)
                    try {
                        List<NotifItem> result = response.body();

                        String query;
                        for (int i = 0; i < result.size(); i++) {
                            query = "replace INTO categories ";
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

            @Override
            public void onFailure(Call<List<NotifItem>> call, Throwable t) {

            }
        });
    }

    private void Transaction() {

        try {
            Cursor cr = Application.database.rawQuery("SELECT * from RecentVisit WHERE IsPaid='true' and IsSuccessed = 'false'", null);
            cr.close();
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
                retrofit.create(ApiInterface.class).InsertPayment(params);
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

    private void GetToken() {
        try {
            /*int min = 1000000;
            int max = 9999999;
            int random = new Random().nextInt((max - min) + 1) + min;*/

            Map<String, String> params = new HashMap<>();
            params.put(getString(R.string.client_id), Application.preferences.getString(getString(R.string.UserId), ""));
            params.put(getString(R.string.client_secret), Application.preferences.getString(getString(R.string.UserName), ""));
            params.put(getString(R.string.grant_type), getString(R.string.password).toLowerCase());
            IWebservice.ITkModel del = new IWebservice.ITkModel() {
                @Override
                public void getResult(TkModel token) throws Exception {
                    try {
                        HSH.editor(getString(R.string.Token), token.getAccessToken());
                        GetServices();
                        Transaction();
                        if (svgView3.getState() == STATE_FINISHED) {
                            Transaction();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                                Transaction();
                            }, 1500);
                    } catch (Exception e) {
                        Transaction();
                    }
                }

                @Override
                public void getError(boolean error) throws Exception {
                    if (!error) {
                        try {
                            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                            findViewById(R.id.no_connection_ll_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.animatedSvgView).setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(myIntent);
                        finish();
                    }
                }
            };
            new GetTokenAsynkTask(SplashActivity.this, del, params).GetData();
        } catch (Exception e) {
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                ActivityCompat.requestPermissions(this, permissions, 123);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MyLocation();
        } else {
            Toast.makeText(this, "مجوز دسترسی به مکان از جانب شما رد شد", Toast.LENGTH_SHORT).show();
        }
    }

    private void MyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(command -> {
                            try {
                                if (command != null) {
                                    double latitude = command.getLatitude();
                                    double longitude = command.getLongitude();
                                    GetProvince(String.valueOf(longitude), String.valueOf(latitude));

                                } else
                                    MyLocation();
                            } catch (Exception e) {
                            }
                        })
                        .addOnFailureListener(this, e -> {

                        });
            }
        }
    }

    private void GetProvince(String x, String y) {
        new AsynctaskGeoCoding(new IWebservice.IAddress() {
            @Override
            public void getResult(ParsijooItem s) throws Exception {
                HSH.editor("state", s.getResult().getCounty());
                Cursor cr = Application.database.rawQuery("SELECT * from cities WHERE Name like '%" + s.getResult().getState() + "%' and ParentCode is null", null);
                if (cr.moveToFirst())
                    HSH.editor("stateCode", cr.getString(cr.getColumnIndex("Id")));
                GetToken();
            }

            @Override
            public void getError(String error) throws Exception {

            }
        }, x, y).getData();
    }
}