package ir.payebash.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.moslem_deris.apps.zarinpal.PaymentBuilder;
import ir.moslem_deris.apps.zarinpal.enums.ZarinPalError;
import ir.moslem_deris.apps.zarinpal.listeners.OnPaymentListener;
import ir.moslem_deris.apps.zarinpal.models.Payment;
import ir.payebash.Adapters.CommentsAdapter;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.DI.DaggerMainComponent;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.CommentModel;
import ir.payebash.Models.NotifyData;
import ir.payebash.Models.NotifyData.Message;
import ir.payebash.Models.PayeDetailsModel;
import ir.payebash.Models.PayeItem;
import ir.payebash.Models.PostDetailsModel;
import ir.payebash.Moudle.CircleImageView;
import ir.payebash.Moudle.TagLayoutImageView;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static Double latitude, longitude;
    com.google.android.gms.maps.MapView mMapView;
    int checkid = 0;
    boolean isPush = false;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private CollapsingToolbarLayout i;
    private AppBarLayout appBar;
    private LinearLayout ll_advertisdetails;
    //private SliderLayout pager;
    private Button btn_contactWays, btn_advertiser, btnBeup;
    private EditText etComment;
    private ImageView imgProfile;
    private TextView toolbar_title, txt_title, txt_date, txt_report, btnMobile, btnEdit, btnDelete, btnPay;
    private PayeItem fFeed;
    private ImageButton btn_fav, btn_share, btnSendComment, back;
    private PayeDetailsModel result;
    private ProgressWheel cpv;
    private TagLayoutImageView ti;
    private RecyclerView rv_comment;
    private CommentsAdapter adapter;
    private List<CommentModel> Commentfeed = new ArrayList<>();

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    private void DeclareElements() {
        ll_advertisdetails = findViewById(R.id.ll_advertisdetails);
        txt_title = findViewById(R.id.txt_title);
        txt_date = findViewById(R.id.txt_date);
        txt_report = findViewById(R.id.txt_report);
        cpv = findViewById(R.id.cpv);

        btnPay = findViewById(R.id.btn_pay);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnMobile = findViewById(R.id.btn_mobile);
        btn_fav = findViewById(R.id.btn_fav);
        btn_share = findViewById(R.id.btn_share);
        btn_advertiser = findViewById(R.id.btn_advertiser);
        btn_contactWays = findViewById(R.id.btn_contactWays);
        btnSendComment = findViewById(R.id.btn_sendComment);
        back = findViewById(R.id.img_back);

        btnPay.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnMobile.setOnClickListener(this);
        btn_fav.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_advertiser.setOnClickListener(this);
        btn_contactWays.setOnClickListener(this);
        btnSendComment.setOnClickListener(this);
        back.setOnClickListener(this);
        txt_report.setOnClickListener(this);

        etComment = findViewById(R.id.et_comment);
        appBar = findViewById(R.id.app_bar);
        toolbar_title = findViewById(R.id.toolbar_title);
        imgProfile = findViewById(R.id.profile_img);
        //pager = findViewById(R.id.pager);
        ti = findViewById(R.id.tl);
        btnBeup = findViewById(R.id.btnBeup);
        btnBeup.setOnClickListener(this);
        imgProfile.setOnClickListener(this);

        rv_comment = findViewById(R.id.rv_comment);
        rv_comment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetailsActivity.this);
        rv_comment.setLayoutManager(layoutManager);
        adapter = new CommentsAdapter(PostDetailsActivity.this, Commentfeed, imageLoader);
        rv_comment.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_post_details);
            DaggerMainComponent.builder()
                    .imageLoaderMoudle(new ImageLoaderMoudle(this))
                    .build()
                    .Inject(this);
            try {
                fFeed = (PayeItem) getIntent().getExtras().getSerializable("feedItem");
                if (fFeed == null) {
                    Intent intent = getIntent();
                    Uri data = intent.getData();
                    fFeed = new PayeItem();
                    fFeed.setPostId(data.getEncodedPath().split("/")[3]);
                }
            } catch (Exception e) {
                fFeed = new PayeItem();
                fFeed.setPostId(getIntent().getExtras().getString(getString(R.string.PostId)));
                isPush = true;
            }

            DeclareElements();

            mMapView = findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);

            float heightDp = (float) (getResources().getDisplayMetrics().heightPixels / 2.5);
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            lp.height = (int) heightDp;
            AdvertisementDetails();
            try {
                Cursor cr = Application.database.rawQuery("SELECT * from RecentVisit WHERE Id='" + fFeed.getPostId() + "'", null);
                cr.moveToFirst();
                if (cr.getString(cr.getColumnIndex("IsFavorite")).equals("true"))
                    btn_fav.setImageResource(R.drawable.ic_bookmark);
                else
                    btn_fav.setImageResource(R.drawable.ic_bookmark_white);
            } catch (Exception e) {
            }

            i = findViewById(R.id.toolbar_layout);
            i.setTitle("");
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && null != data) {
            Intent resultData = new Intent();
            resultData.putExtra("data", "data");
            setResult(Activity.RESULT_OK, resultData);
            finish();
        }
        if (requestCode == 123 && null != data)
            AdvertisementDetails();
    }

    private void getMap() {
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PostDetailsActivity.this)) {
            case ConnectionResult.SUCCESS:

                try {
                    if (!result.getLatitude().trim().equals("00")) {
                        mMapView.setVisibility(View.VISIBLE);
                        latitude = Double.parseDouble(result.getLatitude());
                        longitude = Double.parseDouble(result.getLongitude());
                        mMapView.onResume();
                        try {
                            MapsInitializer.initialize(PostDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMapView.getMapAsync(googleMap -> {
                            try {
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(latitude, longitude))
                                        .zoom(13).build();
                                MarkerOptions marker2 = new MarkerOptions().position(
                                        new LatLng(latitude, longitude)).title(result.getTitle()).snippet(fFeed.getTag());
                                marker2.icon(BitmapDescriptorFactory.fromResource(R.mipmap.city));
                                googleMap.addMarker(marker2);
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));

                            } catch (Exception e) {
                            }
                        });
                    }
                } catch (Exception e) {
                }
                break;

            case ConnectionResult.SERVICE_MISSING:
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                break;
            default:
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

   /* @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }*/

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    public void AdvertisementDetails() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.PostId), fFeed.getPostId());
        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0000"));
        Call<PayeDetailsModel> call =
                ApiClient.getClient().create(ApiInterface.class).GetPostDetails(params);
        call.enqueue(new Callback<PayeDetailsModel>() {
            @Override
            public void onResponse
                    (Call<PayeDetailsModel> call, retrofit2.Response<PayeDetailsModel> response) {
                try {
                    ll_advertisdetails.setVisibility(View.VISIBLE);
                    result = response.body();
                    findViewById(R.id.lbl_is_woman).setVisibility(fFeed.IsWoman() == true ? View.VISIBLE : View.GONE);
                    getMap();
                    toolbar_title.setText(result.getUsername());
                    imageLoader.displayImage(result.getProfileimage(), imgProfile, options);
                    try {
                        if (fFeed.IsImmediate()) {
                            Spannable spannable = new SpannableString(result.getTitle() + "(فوری)");
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#C8FF0004")), (result.getTitle() + "(فوری)").length() - 6, (result.getTitle() + "(فوری)").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txt_title.setText(spannable, TextView.BufferType.SPANNABLE);
                        } else
                            txt_title.setText(result.getTitle());
                        txt_date.setText(result.getCreateDate());
                        List<PostDetailsModel> feed = new ArrayList<>();
                        List<String> result2 = result.getBaseProperty();

                        Cursor cr = null;
                        PostDetailsModel item = null;
                        try {
                            cr = Application.database.rawQuery("SELECT name from categories where id = '" + result.getSubject() + "' ", null);
                            cr.moveToFirst();
                            item = new PostDetailsModel();
                            item.setTitle("موضوع : " + cr.getString(cr.getColumnIndex("name")));
                            feed.add(item);
                        } catch (Exception e) {
                        }

                        try {
                            cr = Application.database.rawQuery("SELECT id,StateCity from Citys where id = '" + result.getCity() + "' ", null);
                            cr.moveToFirst();
                            item = new PostDetailsModel();
                            item.setTitle("محل رویداد : " + cr.getString(cr.getColumnIndex("StateCity")));
                            feed.add(item);
                        } catch (Exception e) {
                            if (result.getCity() == 1) {
                                item = new PostDetailsModel();
                                item.setTitle("محل رویداد : سراسر کشور");
                                feed.add(item);
                            }
                        }

                        String s = "";
                        for (int i = 0; i < result2.size(); i++) {
                            item = new PostDetailsModel();
                            try {
                                s = result2.get(i);
                                item.setTitle(s);
                                if (fFeed.getCost().equals("")) {
                                    if (s.contains("هزینه : "))
                                        fFeed.setCost(s.split(":")[1].trim());
                                    if (s.contains("مهلت هم پا شدن : "))
                                        fFeed.setTimeToJoin(s.split(":")[1].trim());
                                    if (s.contains("هشتگ : "))
                                        fFeed.setTag(s.split(":")[1].trim());
                                }
                            } catch (Exception e) {
                            }
                            feed.add(item);
                        }
                        getProperty(PostDetailsActivity.this, findViewById(R.id.ll_baseProperty), feed);
                        List<String> applicants_result = result.getApplicants();
                        for (int i = 0; i < applicants_result.size(); i++) {
                            try {
                                TagLayout(applicants_result.get(i), i);
                                if (applicants_result.get(i).contains(Application.preferences.getString(getString(R.string.UserId), "00000000-0000-0000-0000-00000000000000"))) {
                                    btnBeup.setBackgroundColor(Color.parseColor("#DB5255"));
                                    btnBeup.setTextColor(Color.WHITE);
                                    btnBeup.setText("منصرف شدم");
                                    findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                                    if (Commentfeed.size() == 0)
                                        GetCommentAsynkTask();
                                    btnBeup.setOnClickListener(v -> {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(PostDetailsActivity.this, SweetAlertDialog.WARNING_TYPE);
                                        dialog.setTitleText("لغو همراهی");
                                        dialog.setContentText(getString(R.string.sure));
                                        dialog.setConfirmText("بله");
                                        dialog.setCancelText("فعلا نه");
                                        dialog.setConfirmClickListener(sDialog -> {
                                            UpdateApplicantAsynkTask();
                                            dialog.dismissWithAnimation();
                                        });
                                        dialog.setCancelClickListener(sweetAlertDialog -> dialog.dismissWithAnimation());
                                        dialog.setCancelable(true);
                                        HSH.dialog(dialog);

                                    });
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                    cpv.setVisibility(View.GONE);
                    /////////////////////////////////////////////////////////////////////////
                    try {
                        String[] temp = result.getImages().split(",");
                        if (temp.length > 0 && !temp[0].equals("null") && !temp[0].equals("")) {
                        } else {
                            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
                            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            //pager.setVisibility(View.GONE);
                            appBar.setLayoutParams(lp);
                        }
                        final PayeItem item = new PayeItem();
                        item.setImages(result.getImages());
                        try {
                            /*for (int i = 0; i < item.getImages().split(",").length; i++) {
                                TextSliderView textSliderView = new TextSliderView(PostDetailsActivity.this);
                                // initialize a SliderLayout
                                textSliderView
                                        //.description(name)
                                        .image(getString(R.string.image) + item.getImages().split(",")[i] + ".jpg")
                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                                *//*textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra",name);*//*

                                pager.addSlider(textSliderView);
                                textSliderView.setOnSliderClickListener(slider -> {
                                    final Bundle bundle = new Bundle();
                                    Intent i1 = new Intent(PostDetailsActivity.this, ViewPagerActivity.class);
                                    bundle.putSerializable("feed", item);
                                    i1.putExtras(bundle);
                                    startActivity(i1);
                                });
                            }*/
                        } catch (Exception e) {
                        }

                    } catch (Exception e) {
                    }
                    ///////////////////////////////////////////////////////////////////////////
                    if (!fFeed.getTitle().contains("لغو")) {
                        try {
                            Cursor cr = Application.database.rawQuery("SELECT Id from RecentVisit WHERE Id='" + fFeed.getPostId() + "'", null);
                            if (cr.getCount() == 0) {
                                String query;
                                if (isPush == false)
                                    query = "INSERT INTO RecentVisit(Id,IsFavorite,IsWanted) VALUES " +
                                            "('" + fFeed.getPostId() + "','false','false') ";
                                else
                                    query = "INSERT INTO RecentVisit(Id,IsFavorite,IsWanted) VALUES " +
                                            "('" + fFeed.getPostId() + "','false','true') ";

                                Application.database.execSQL(query);
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        DeletePost();
                    }

                } catch (Exception e) {
                    DeletePost();
                    HSH.showtoast(PostDetailsActivity.this, "این آگهی حذف شده است.");
                    finish();
                }

                try {
                    if (null != getIntent().getExtras())
                        if (result.getUserId().equals(Application.preferences.getString(getString(R.string.UserId), "0000"))) {

                            HSH.vectorRight(PostDetailsActivity.this, btnEdit, R.drawable.ic_edit);
                            HSH.vectorRight(PostDetailsActivity.this, btnDelete, R.drawable.ic_delete);
                            HSH.vectorRight(PostDetailsActivity.this, btnPay, R.drawable.ic_payment);
                            HSH.vectorRight(PostDetailsActivity.this, btnMobile, R.drawable.ic_mobile);
                            try {
                                String query = "update RecentVisit set " +
                                        "IsMine = 'true' where Id = '" + fFeed.getPostId() + "'";
                                Application.database.execSQL(query);
                            } catch (Exception e) {
                            }
                            findViewById(R.id.ll_mng).setVisibility(View.VISIBLE);
                            btnBeup.setVisibility(View.GONE);
                            findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                            if (Commentfeed.size() == 0)
                                GetCommentAsynkTask();
                            String[] temp = result.getState().split("-");
                            if (temp[0].contains(getString(R.string.pay)))
                                btnPay.setVisibility(View.VISIBLE);
                            else if (temp[0].contains("تایید شماره")) {
                                btnMobile.setVisibility(View.VISIBLE);
                            }


                            ((TextView) findViewById(R.id.txt_title_state)).setText(temp[0]);
                            ((TextView) findViewById(R.id.txt_description_state)).setText(temp[1]);
                            findViewById(R.id.txt_title_state).setBackgroundColor(Color.parseColor(temp[2]));
                            ((TextView) findViewById(R.id.txt_description_state)).setTextColor(Color.parseColor(temp[2]));
                        } else {
                            try {
                                if (result.getUserId().equals(Application.preferences.getString(getString(R.string.UserId), "0000"))) {
                                    btnBeup.setVisibility(View.GONE);
                                    findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                                    if (Commentfeed.size() == 0)
                                        GetCommentAsynkTask();
                                } else {
                                    findViewById(R.id.ll_mng).setVisibility(View.GONE);
                                    btnBeup.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                findViewById(R.id.ll_mng).setVisibility(View.GONE);
                                btnBeup.setVisibility(View.VISIBLE);
                            }
                        }
                } catch (Exception e) {
                    findViewById(R.id.ll_mng).setVisibility(View.GONE);
                    btnBeup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PayeDetailsModel> call, Throwable t) {
                cpv.setVisibility(View.VISIBLE);
                AdvertisementDetails();
            }
        });
    }

    private void getProperty(final Context _cn, final LinearLayout layout, final List<PostDetailsModel> feed) {

        try {
            int scrollviewposition = 0;
            for (scrollviewposition = 0; scrollviewposition < feed.size(); scrollviewposition++) {

                @SuppressWarnings("static-access")
                LayoutInflater inflater = (LayoutInflater)
                        _cn.getSystemService(_cn.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.item_post_details, null);

                final TextView txt1 = view1.findViewById(R.id.txt1);
                //txt1.setTypeface(Application.font);
                String[] temp = feed.get(scrollviewposition).getTitle().split(":");
                Spannable spannable = new SpannableString(HSH.toPersianNumber(feed.get(scrollviewposition).getTitle()));
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#026d37")), temp[0].length() + 1, feed.get(scrollviewposition).getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt1.setText(spannable, TextView.BufferType.SPANNABLE);
                layout.addView(view1);

                txt1.setOnClickListener(view -> {
                    try {
                        if (txt1.getText().toString().equals(result.getBaseProperty().get(0))) {
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(result.getBaseProperty().get(0).split(":")[1])));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception e) {
                            }
                        } else {
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getBaseProperty().get(1).split(": ")[1]));
                                startActivity(browserIntent);
                            } catch (Exception e1) {
                            }
                        }
                    } catch (Exception e) {
                    }
                });
            }

        } catch (Exception e) {
        }
    }

    private void registerDialog() {
        final SweetAlertDialog dialog = new SweetAlertDialog(PostDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("عضویت")
                .setContentText("برای نمایش اطلاعات لازم است ابتدا عضو شوید")
                .setConfirmText("بله")
                .setCancelText("فعلا نه")
                .setConfirmClickListener(sDialog -> {
                    HSH.onOpenPage(PostDetailsActivity.this, IntroLoginActivity.class);
                    finish();
                });
        dialog.setCancelClickListener(sweetAlertDialog -> dialog.dismissWithAnimation());
        dialog.setCancelable(true);
        HSH.dialog(dialog);
    }

    private void DeletePost() {
        try {
            String query = "DELETE FROM RecentVisit " +
                    "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
            Application.database.execSQL(query);
        } catch (Exception e1) {
        }
    }

    private void SendingReportService(final int checkid, final Dialog dialog_wait) {

        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.ComplainantId), Application.preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.PostId), fFeed.getPostId());
        params.put("Type", String.valueOf(checkid));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).SendingReportService(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                dialog_wait.dismiss();
                HSH.showtoast(PostDetailsActivity.this, "گزارش شما با موفقیت ثبت گردید");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(PostDetailsActivity.this, "مجدد تلاش کنید /مشکل در دریافت اطلاعات");
            }
        });
    }

    private void DeletePostAsynkTask(final SweetAlertDialog loading) {
        loading.show();
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.PostId), fFeed.getPostId());
        params.put(getString(R.string.Status), String.valueOf(checkid));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).RemovePost(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        DeletePost();
                        loading.setTitleText("حذف رویداد!")
                                .setContentText("رویداد شما حذف گردید")
                                .setConfirmText("باشه")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    Intent resultData = new Intent();
                                    resultData.putExtra("data", "data");
                                    setResult(Activity.RESULT_OK, resultData);
                                    finish();
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    } catch (Exception e) {
                    }
                } else
                    DeletePostAsynkTask(loading);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DeletePostAsynkTask(loading);
            }
        });
    }

    private void CancelPostAsynkTask(final SweetAlertDialog loading) {
        loading.show();
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).CancelPost(fFeed.getPostId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.code() == 200)
                    try {
                        NotifyData notifydata = notifyData();
                        notifydata.title = String.format(getString(R.string.cancel), "\"" + fFeed.getTitle() + "\"");
                        notifydata.type = "Cancel";
                        JSONArray jary = new JSONArray(response.body().string());
                        if (jary.length() > 0)
                            for (int i = 0; i < jary.length(); i++) {
                                Call<Message> call2 = ApiClient.getClient2().create(ApiInterface.class).sendMessage(new Message(jary.getString(i), notifydata));
                                call2.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, Response<Message> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {
                                    }
                                });
                            }
                        DeletePost();
                        loading.setTitleText("لغو رویداد")
                                .setContentText("رویداد شما لغو گردید")
                                .setConfirmText("باشه")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    Intent resultData = new Intent();
                                    resultData.putExtra("data", "data");
                                    setResult(Activity.RESULT_OK, resultData);
                                    finish();
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    } catch (Exception e) {
                    }
                else
                    CancelPostAsynkTask(loading);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CancelPostAsynkTask(loading);
            }
        });
    }

    private void SendCommentAsynkTask(final String comment) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.UserName), Application.preferences.getString(getString(R.string.FullName), "0"));
        params.put(getString(R.string.PostId), fFeed.getPostId());
        params.put(getString(R.string.Comment), comment);
        Call<CommentModel> call =
                ApiClient.getClient().create(ApiInterface.class).InsertComment(params);
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse
                    (Call<CommentModel> call, retrofit2.Response<CommentModel> response) {
                if (response.code() == 200) {
                    NotifyData notifydata = notifyData();
                    notifydata.title = String.format(getString(R.string.comment), "\"" + fFeed.getTitle() + "\"");
                    notifydata.type = "Comment";
                    Call<Message> call2 = ApiClient.getClient2().create(ApiInterface.class).sendMessage(new Message(result.getToken(), notifydata));
                    call2.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                        }
                    });
                    try {
                        etComment.setText("");
                        Commentfeed.add(response.body());
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                } else
                    SendCommentAsynkTask(comment);
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {
                SendCommentAsynkTask(comment);
            }
        });
    }

    private void GetCommentAsynkTask() {
        Call<List<CommentModel>> call =
                ApiClient.getClient().create(ApiInterface.class).GetComments(fFeed.getPostId());
        call.enqueue(new Callback<List<CommentModel>>() {
            @Override
            public void onResponse
                    (Call<List<CommentModel>> call, retrofit2.Response<List<CommentModel>> response) {
                if (response.code() == 200) {
                    try {
                        if (adapter.getItemCount() == 0) {
                            for (CommentModel m : response.body())
                                Commentfeed.add(m);
                            adapter.notifyDataSetChanged();
                            rv_comment.scrollToPosition(adapter.getItemCount());
                        }
                    } catch (Exception e) {
                    }
                } else
                    GetCommentAsynkTask();
            }

            @Override
            public void onFailure(Call<List<CommentModel>> call, Throwable t) {
                GetCommentAsynkTask();
            }
        });
    }

    private void pay(String s) {
        Payment payment = null;
        try {
            payment = new PaymentBuilder()
                    .setMerchantID(getString(R.string.Mrchnt))
                    .setAmount(Application.preferences.getInt("Feepayable", 1000))
                    .setDescription(s)
                    .create();
        } catch (Exception e) {
        }
        ir.moslem_deris.apps.zarinpal.ZarinPal.pay(PostDetailsActivity.this, payment, new OnPaymentListener() {
            @Override
            public void onSuccess(String refID) {
                String query = "Update RecentVisit set IsPaid = 'true' , refID = '" + refID + "' " +
                        "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
                Application.database.execSQL(query);
                InsertPaymentAsynkTask(refID);
            }

            @Override
            public void onFailure(ZarinPalError error) {
                String errorMessage = "";
                switch (error) {
                    case INVALID_PAYMENT:
                        errorMessage = "پرداخت تایید نشد";
                        break;
                    case USER_CANCELED:
                        errorMessage = "پرداخت توسط کاربر متوقف شد";
                        break;
                    case NOT_ENOUGH_DATA:
                        errorMessage = "اطلاعات پرداخت کافی نیست";
                        break;
                    case UNKNOWN:
                        errorMessage = "خطای ناشناخته";
                        break;
                }
                Log.wtf("TAG", "::ZarinPal::  ERROR: " + errorMessage);
                HSH.showtoast(PostDetailsActivity.this, "خطا!!!" + "\n" + errorMessage);
            }
        });
    }

    private void InsertPaymentAsynkTask(final String refID) {
        final SweetAlertDialog dialog = HSH.onProgress_Dialog(PostDetailsActivity.this, "لطفا منتظر بمانید ...");
        dialog.show();
        Map<String, String> params = new HashMap<>();
        if ((result.getState().split("-")[0]).contains("فوری"))
            params.put(getString(R.string.TypeOfPay), getString(R.string.IsImmediate));
        else if ((result.getState().split("-")[0]).contains("دسته بندی"))
            params.put(getString(R.string.TypeOfPay), getString(R.string.Category));
        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.PostId), fFeed.getPostId());
        params.put(getString(R.string.Amount), Application.preferences.getString("Feepayable", "1000"));
        params.put(getString(R.string.refID), refID);
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).InsertPayment(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String query = "Update RecentVisit set IsSuccessed = 'true' " +
                                "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
                        Application.database.execSQL(query);
                        dialog.setTitleText("تراکنش موفق")
                                .setContentText("تراکنش شما با موفقیت انجام شد و رویداد شما بعد از بررسی منتشر خواهد شد.")
                                .setConfirmText("باشه")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent resultData = new Intent();
                                        resultData.putExtra("data", "data");
                                        setResult(Activity.RESULT_OK, resultData);
                                        finish();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    } catch (Exception e) {
                    }
                } else {
                    InsertPaymentAsynkTask(refID);
                    String query = "Update RecentVisit set IsSuccessed = 'false' " +
                            "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
                    Application.database.execSQL(query);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                InsertPaymentAsynkTask(refID);
                String query = "Update RecentVisit set IsSuccessed = 'false' " +
                        "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
                Application.database.execSQL(query);
            }
        });
    }

    private void UpdateApplicantAsynkTask() {
        btnBeup.setBackgroundColor(Color.parseColor("#dbdbdb"));
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.PostId), fFeed.getPostId());
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).UpdateApplicants(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String s = response.body().string();
                        if (s.equals("true") && s.length() > 0) {
                            findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                            if (adapter.getItemCount() == 0)
                                GetCommentAsynkTask();
                            Cursor cr = Application.database.rawQuery("SELECT * from RecentVisit WHERE Id='" + fFeed.getPostId() + "'", null);
                            cr.moveToFirst();
                            if (null == cr.getString(cr.getColumnIndex("IsBeup"))) {
                                NotifyData notifydata = notifyData();
                                notifydata.type = "Beup";
                                Call<Message> call2 = ApiClient.getClient2().create(ApiInterface.class).sendMessage(new Message(result.getToken(), notifydata));
                                call2.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, Response<Message> response) {
                                        String query = "Update RecentVisit set IsBeup = 'true' " +
                                                "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
                                        Application.database.execSQL(query);
                                    }

                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {
                                    }
                                });
                            }
                            /*String query = "UPDATE RecentVisit SET " +
                                    "IsWanted = 'true' " +
                                    "WHERE Id = '" + fFeed.getPostId() + "' ";
                            Application.database.execSQL(query);*/

                            btnBeup.setBackgroundColor(Color.parseColor("#DB5255"));
                            btnBeup.setText("منصرف شدم");
                            final CircleImageView i = new CircleImageView(PostDetailsActivity.this);
                            i.setTag(Application.preferences.getString(getString(R.string.UserId), "0"));
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    getPixelValue(PostDetailsActivity.this, getResources().getInteger(R.integer.image))
                                    , getPixelValue(PostDetailsActivity.this, getResources().getInteger(R.integer.image)));
                            i.setLayoutParams(params);
                            imageLoader.displayImage(Application.preferences.getString("ProfileImage", "0"), i, options);
                            HSH.display(PostDetailsActivity.this, ti);
                            ti.addTagView(i);
                            i.setOnClickListener(v -> {
                                if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true"))
                                    try {
                                        Intent in = new Intent(PostDetailsActivity.this, UserProfileActivity.class);
                                        in.putExtra("AdvertiserId", Application.preferences.getString(getString(R.string.UserId), "0"));
                                        in.putExtra("profileimage", Application.preferences.getString("ProfileImage", "0"));
                                        startActivity(in);
                                    } catch (Exception e) {
                                    }
                                else
                                    registerDialog();
                            });

                        } else if (s.equals("false") && s.length() > 0) {
                            /*String query = "UPDATE RecentVisit SET " +
                                    "IsWanted = 'false' " +
                                    "WHERE Id = '" + fFeed.getPostId() + "' ";
                            Application.database.execSQL(query);*/
                           /* btnBeup.setBackgroundColor(Color.parseColor("#5bb85d"));
                            btnBeup.setText("پایه ام");*/
                            findViewById(R.id.ll_comments).setVisibility(View.GONE);
                            HSH.hide(PostDetailsActivity.this, btnBeup);
                            for (int i = 0; i < ti.getChildCount(); i++) {
                                LinearLayout v = (LinearLayout) ti.getChildAt(i);
                                for (int j = 0; j < v.getChildCount(); j++) {
                                    CircleImageView img = (CircleImageView) v.getChildAt(j);
                                    if (img.getTag().equals(Application.preferences.getString(getString(R.string.UserId), "0")))
                                        img.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                } else
                    UpdateApplicantAsynkTask();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                UpdateApplicantAsynkTask();
            }
        });
    }

    private NotifyData notifyData() {
        final PersianCalendar now = new PersianCalendar();
        NotifyData notifydata = new NotifyData(
                fFeed.getPostId(),
                String.format(getString(R.string.join), "\"" + fFeed.getTitle() + "\""),
                String.valueOf(now.getPersianLongDate()),
                fFeed.getSubject(),
                fFeed.getCity(),
                fFeed.getCost(),
                fFeed.getTag(),
                fFeed.getImages().split(",")[0],
                "");
        return notifydata;
    }

    private void TagLayout(final String src, final int j) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    final CircleImageView i = new CircleImageView(PostDetailsActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            getPixelValue(PostDetailsActivity.this, getResources().getInteger(R.integer.image))
                            , getPixelValue(PostDetailsActivity.this, getResources().getInteger(R.integer.image)));
                    i.setLayoutParams(params);
                    i.setTag(src.split("/")[0]);
                    try {
                        if (!src.split("/")[1].contains("https:"))
                            imageLoader.displayImage(getString(R.string.url) + "Images/Users/" + src.split("/")[1] + ".jpg", i, options);
                        else
                            imageLoader.displayImage(src.substring(37), i, options);

                    } catch (Exception e) {
                        i.setImageDrawable(getResources().getDrawable(R.mipmap.ic_paye));
                    }
                    ti.addTagView(i);
                    i.setOnClickListener(v -> {
                        if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true"))
                            try {
                                Intent in = new Intent(PostDetailsActivity.this, UserProfileActivity.class);
                                in.putExtra("AdvertiserId", src.split("/")[0]);
                                in.putExtra("PostId", fFeed.getPostId());
                                if (src.split("/").length > 1) {
                                    if (!src.split("/")[1].contains("https:"))
                                        in.putExtra("profileimage", getString(R.string.url) + "Images/Users/" + src.split("/")[1] + ".jpg");
                                    else
                                        in.putExtra("profileimage", src.substring(37));
                                }
                                startActivity(in);
                            } catch (Exception e) {
                            }
                        else
                            registerDialog();
                    });

                } catch (Exception e) {
                }
            }
        }, 100 * j);
    }

    @Override
    public void onClick(View v) {
        final SweetAlertDialog dialog;
        final Dialog dialog2 = new Dialog(PostDetailsActivity.this);
        switch (v.getId()) {
            case R.id.btn_mobile:
                Intent in = new Intent(PostDetailsActivity.this, LoginActivity.class);
                in.putExtra("Type", "Update");
                startActivityForResult(in, 123);
                break;

            case R.id.btn_pay:
                pay(String.format(getString(R.string.payNote), result.getState().split("-")[0]));
                break;

            case R.id.btn_edit:
                Intent intent;
                intent = new Intent(PostDetailsActivity.this, UpdatePostActivity.class);
                intent.putExtra("feedItem", getIntent().getExtras().getSerializable("feedItem"));
                startActivityForResult(intent, 321);
                break;
            case R.id.btn_delete:
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_delete_post);
                TextView txt_delete = dialog2.findViewById(R.id.txt_delete);
                TextView txt_reject = dialog2.findViewById(R.id.txt_reject);
                final RadioGroup radioGroup01 = dialog2.findViewById(R.id.radioGroup);

                if (result.getState().split("-")[0].contains(getString(R.string.release1)) ||
                        result.getState().split("-")[0].contains(getString(R.string.release2))) {
                    dialog2.findViewById(R.id.radioButton4).setVisibility(View.VISIBLE);
                }
                radioGroup01.setOnCheckedChangeListener((group, checkedId) -> checkid = Integer.parseInt(dialog2.findViewById(checkedId).getTag().toString()));

                txt_delete.setOnClickListener(v1 -> {
                    if (radioGroup01.getCheckedRadioButtonId() != -1) {
                        if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false) {
                            SweetAlertDialog dialog_wait = HSH.onProgress_Dialog(PostDetailsActivity.this, "شکیبا باشید ...");
                            if (checkid == 1 || checkid == 2 || checkid == 3)
                                DeletePostAsynkTask(dialog_wait);
                            else
                                CancelPostAsynkTask(dialog_wait);
                            dialog2.dismiss();
                        } else
                            HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");

                    } else
                        HSH.showtoast(PostDetailsActivity.this, "یک گزینه را انتخاب کنید.");
                });

                txt_reject.setOnClickListener(v13 -> dialog2.dismiss());
                HSH.dialog(dialog2);
                dialog2.show();
                break;

            case R.id.btn_sendComment:
                if (!etComment.getText().toString().trim().equals(""))
                    if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false)
                        SendCommentAsynkTask(etComment.getText().toString().trim());
                    else
                        HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");
                break;
            case R.id.btn_fav:
                Cursor cr = Application.database.rawQuery("SELECT IsFavorite from RecentVisit WHERE Id = '" + fFeed.getPostId() + "'", null);
                if (cr.getCount() > 0) {
                    try {
                        cr.moveToFirst();
                        if (cr.getString(cr.getColumnIndex("IsFavorite")).equals("true")) {
                            try {
                                String query2 = "UPDATE RecentVisit SET " +
                                        "IsFavorite='false' " +
                                        "WHERE Id='" + fFeed.getPostId() + "' ";
                                Application.database.execSQL(query2);
                                HSH.showtoast(PostDetailsActivity.this, "نشان حذف شد");
                                btn_fav.setImageResource(R.drawable.ic_bookmark_white);
                            } catch (Exception e) {
                            }

                        } else if (cr.getString(cr.getColumnIndex("IsFavorite")).equals("false")) {
                            try {
                                String query2 = "UPDATE RecentVisit SET " +
                                        "IsFavorite='true' " +
                                        "WHERE Id='" + fFeed.getPostId() + "' ";

                                Application.database.execSQL(query2);
                                HSH.showtoast(getApplicationContext(), "نشان شد");
                                btn_fav.setImageResource(R.drawable.ic_bookmark);

                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;

            case R.id.btn_share:
                try {
                    String shareBody = getString(R.string.url) + "getwebpostdetails/index/" + fFeed.getPostId();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, fFeed.getTitle() + "\n" + "را در پایه باش ببین");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, null));
                } catch (Exception e) {
                }
                break;

            case R.id.txt_report:
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_report_post);
                TextView txt_send = dialog2.findViewById(R.id.txt_send);
                TextView txt_reject2 = dialog2.findViewById(R.id.txt_reject);
                final RadioGroup radioGroup001 = dialog2.findViewById(R.id.radioGroup);

                radioGroup001.setOnCheckedChangeListener((group, checkedId) -> checkid = Integer.parseInt(dialog2.findViewById(checkedId).getTag().toString()));

                txt_send.setOnClickListener(v12 -> {
                    if (radioGroup001.getCheckedRadioButtonId() != -1) {
                        if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false) {
                            Dialog dialog_wait = HSH.onProgress_Dialog(PostDetailsActivity.this, "شکیبا باشید ...");
                            SendingReportService(checkid, dialog_wait);
                            dialog2.dismiss();
                        } else
                            HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");

                    } else
                        HSH.showtoast(PostDetailsActivity.this, "یک گزینه را انتخاب کنید.");
                });

                txt_reject2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                HSH.dialog(dialog2);
                dialog2.show();
                break;

            case R.id.img_back:
                finish();
                break;


            case R.id.btn_advertiser:
                if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true"))
                    try {
                        Intent i = new Intent(PostDetailsActivity.this, UserProfileActivity.class);
                        i.putExtra("PostId", fFeed.getPostId());
                        i.putExtra("AdvertiserId", result.getUserId());
                        i.putExtra("profileimage", result.getProfileimage());
                        startActivity(i);
                    } catch (Exception e) {
                    }
                else
                    registerDialog();
                break;

            case R.id.btn_contactWays:
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_contact_ways);
                TextView txt_telegram = dialog2.findViewById(R.id.txt_telegram);
                TextView txt_instagram = dialog2.findViewById(R.id.txt_instagram);
                TextView txt_soroosh = dialog2.findViewById(R.id.txt_soroosh);
                TextView txt_phone = dialog2.findViewById(R.id.txt_phone);
                TextView txt_gmail = dialog2.findViewById(R.id.txt_gmail);

                if (result.getPhoneNumber().length() > 10) {
                    txt_phone.setVisibility(View.VISIBLE);
                    txt_phone.setOnClickListener(view -> {
                        try {
                            Uri number = Uri.parse("tel:" + result.getPhoneNumber());
                            final Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                            startActivity(callIntent);
                        } catch (Exception e) {
                        }
                    });
                }
                if (result.getSoroosh().length() > 4) {
                    txt_soroosh.setVisibility(View.VISIBLE);
                    txt_soroosh.setOnClickListener(view -> {
                        try {
                            Uri uri = Uri.parse("https://sapp.ir/" + result.getSoroosh());
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("mobi.mmdt.ott");
                            try {
                                startActivity(likeIng);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://sapp.ir/" + result.getSoroosh())));
                            }
                        } catch (Exception e) {
                        }
                    });
                }

                if (result.getTelegram().length() > 4) {
                    txt_telegram.setVisibility(View.VISIBLE);
                    txt_telegram.setOnClickListener(view -> {
                        try {
                            Intent intent12 = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + result.getTelegram()));
                            startActivity(intent12);
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://web.telegram.org/#/im?p=@" + result.getTelegram())));
                        }
                    });
                }

                try {
                    if (result.getInstagram().length() > 4) {
                        txt_instagram.setVisibility(View.VISIBLE);
                        txt_instagram.setOnClickListener(view -> {
                            Uri uri = Uri.parse("https://instagram.com/_u/" + result.getInstagram());
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            try {
                                startActivity(likeIng);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://instagram.com/" + result.getInstagram())));
                            }
                        });
                    }

                } catch (Exception e) {
                }

                if (result.getGmail().length() > 4) {
                    txt_gmail.setVisibility(View.VISIBLE);
                    txt_gmail.setOnClickListener(view -> {
                        try {
                            Intent intent1 = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:" + result.getGmail());
                            intent1.setData(data);
                            startActivity(intent1);
                        } catch (Exception e) {
                        }
                    });
                }

                if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true")) {
                    HSH.dialog(dialog2);
                    dialog2.show();
                } else
                    registerDialog();
                break;

            case R.id.btnBeup:
                try {
                    if (Application.preferences.getString(getString(R.string.Telegram), "").length() > 4 ||
                            Application.preferences.getString(getString(R.string.Soroosh), "").length() > 4 ||
                            Application.preferences.getString(getString(R.string.Instagram), "").length() > 4) {
                        if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false)
                            UpdateApplicantAsynkTask();
                        else
                            HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");
                    } else {
                        dialog = new SweetAlertDialog(PostDetailsActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("تکمیل اطلاعات")
                                .setContentText("قبل از شرکت در رویداد اطلاعات خود را ثبت نمایید")
                                .setConfirmText("باشه")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        if (!Application.preferences.getString(getString(R.string.UserId), "").equals(""))
                                            HSH.onOpenPage(PostDetailsActivity.this, EditProfileActivity.class);
                                        else
                                            HSH.onOpenPage(PostDetailsActivity.this, IntroLoginActivity.class);
                                        sDialog.dismissWithAnimation();
                                    }
                                });
                        HSH.dialog(dialog);
                        dialog.show();
                    }
                } catch (Exception e) {
                }
                break;

            case R.id.img_profile:
                try {
                    final Bundle bundle = new Bundle();
                    Intent i = new Intent(PostDetailsActivity.this, ViewPagerActivity.class);
                    PayeItem item = new PayeItem();
                    item.setImages(result.getProfileimage());
                    bundle.putSerializable("feed", item);
                    i.putExtras(bundle);
                    startActivity(i);
                } catch (Exception e) {
                }
                break;

        }
    }
}

