package ir.payebash.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.islamkhsh.CardSliderViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.moslem_deris.apps.zarinpal.PaymentBuilder;
import ir.moslem_deris.apps.zarinpal.enums.ZarinPalError;
import ir.moslem_deris.apps.zarinpal.listeners.OnPaymentListener;
import ir.moslem_deris.apps.zarinpal.models.Payment;
import ir.payebash.Adapters.BannerAdapter;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.NotifyData;
import ir.payebash.models.NotifyData.Message;
import ir.payebash.models.PayeItem;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.R;
import ir.payebash.utils.RecyclerSnapHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class MyEventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout pB;
    int checkid = 0;
    boolean isPush = false;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    @Inject
    Retrofit retrofit;
    private ConstraintLayout eventOrganizer, clImmadiate;
    private CollapsingToolbarLayout i;
    private ImageView imgProfile;
    private TextView txtFullname, txtEventTitle, txtEventDate, txtLocation ,
    txtCategory , txtTimeToJoin , txtCost, txtStartDate, txtFollowes, txtDescription,
            txtUpgradeEvent, tvWoman ;
    private EventModel fFeed;
    private EventDetailsModel result;

    private void DeclareElements() {
        pB = findViewById(R.id.progressBar);
        txtEventTitle = findViewById(R.id.txt_event_title);
        txtEventDate = findViewById(R.id.txt_event_date);
        txtCategory = findViewById(R.id.txt_category);
        txtLocation = findViewById(R.id.txt_location);
        txtTimeToJoin = findViewById(R.id.txt_time_to_join);
        txtUpgradeEvent = findViewById(R.id.txt_upgrade_event);
        txtCost = findViewById(R.id.txt_cost);
        txtStartDate = findViewById(R.id.txt_startdate);
        txtFollowes = findViewById(R.id.txt_followers);
        txtDescription = findViewById(R.id.txt_description);
        tvWoman = findViewById(R.id.tv_woman);
        txtUpgradeEvent.setOnClickListener(this::onClick);
        eventOrganizer = findViewById(R.id.cl_event_organizer);
        eventOrganizer.setOnClickListener(this);
        clImmadiate = findViewById(R.id.cl_immadiate);

        txtFullname = findViewById(R.id.txt_fullname);
        imgProfile = findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(this);

        ImageView img = findViewById(R.id.imageView8);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(img, "alpha", 1f, .1f);
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(img, "alpha", .1f, 1f);
        fadeIn.setDuration(1000);
        AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn).after(fadeOut);
        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationSet.start();
            }
        });
        mAnimationSet.start();

        /*rvComment = findViewById(R.id.rv_comment);
        rvComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetailsActivity.this);
        rvComment.setLayoutManager(layoutManager);
        adapter = new CommentsAdapter(PostDetailsActivity.this, Commentfeed, imageLoader);
        rvComment.setAdapter(adapter);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_my_event_details);
            Application.getComponent().Inject(this);
            try {
                fFeed = (EventModel) getIntent().getExtras().getSerializable("feedItem");
                if (fFeed == null) {
                    Intent intent = getIntent();
                    Uri data = intent.getData();
                    fFeed = new EventModel();
                    fFeed.setPostId(data.getEncodedPath().split("/")[3]);
                }
            } catch (Exception e) {
                fFeed = new EventModel();
                fFeed.setPostId(getIntent().getExtras().getString(getString(R.string.PostId)));
                isPush = true;
            }

            DeclareElements();

            /*mMapView = findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);*/

            /*float heightDp = (float) (getResources().getDisplayMetrics().heightPixels / 2);
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            lp.height = (int) heightDp;*/
            AdvertisementDetails();
            try {
                Cursor cr = Application.database.rawQuery("SELECT * from RecentVisit WHERE Id='" + fFeed.getPostId() + "'", null);
                cr.moveToFirst();
                cr.close();
                /*if (cr.getString(cr.getColumnIndex("IsFavorite")).equals("true"))
                    btn_fav.setImageResource(R.drawable.ic_bookmark);
                else
                    btn_fav.setImageResource(R.drawable.ic_bookmark_white);*/
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
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MyEventDetailsActivity.this)) {
            case ConnectionResult.SUCCESS:

                try {
                    if (!result.getLatitude().trim().equals("00")) {
                        /*mMapView.setVisibility(View.VISIBLE);
                        latitude = Double.parseDouble(result.getLatitude());
                        longitude = Double.parseDouble(result.getLongitude());
                        mMapView.onResume();*/
                        try {
                            MapsInitializer.initialize(MyEventDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                       /* mMapView.getMapAsync(googleMap -> {
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
                        });*/
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
        //mMapView.onLowMemory();
    }

   /* @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }*/

    @Override
    public void onPause() {
        super.onPause();
        /*if (null != mMapView)
            mMapView.onPause();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (null != mMapView)
            mMapView.onDestroy();*/
    }

    public void AdvertisementDetails() {

        IWebservice.IEventDetails del = new IWebservice.IEventDetails() {
            @Override
            public void getResult(EventDetailsModel event) throws Exception {
                try {
                    pB.setVisibility(View.GONE);
                    //ll_advertisdetails.setVisibility(View.VISIBLE);
                    result = event;
                    //findViewById(R.id.lbl_is_woman).setVisibility(fFeed.IsWoman() == true ? View.VISIBLE : View.GONE);
                    getMap();
                    try {
                        imageLoader.displayImage(result.getEventOwner().getProfileImage(), imgProfile, options);
                        txtFullname.setText(result.getEventOwner().getUserName());
                        tvWoman.setVisibility(result.isIsWoman()? View.VISIBLE : View.GONE);
                        //clImmadiate.setVisibility(fFeed.IsImmediate() ? View.VISIBLE : View.GONE);
                        txtEventTitle.setText(fFeed.getTitle());
                        txtEventDate.setText(result.getCreateDate());
                        txtTimeToJoin.setText(result.getTimeToJoin());
                        txtCost.setText(result.getCost());
                        txtStartDate.setText(result.getStartDate());
                        txtFollowes.setText(result.getNumberFollowers());
                        txtDescription.setText(result.getDescription());
                        //List<PostDetailsModel> feed = new ArrayList<>();
                        //List<String> result2 = result.getBaseProperty();

                        Cursor cr = null;
                        //PostDetailsModel item = null;
                        try {
                            cr = Application.database.rawQuery("SELECT name from categories where id = '" + result.getSubject() + "' ", null);
                            cr.moveToFirst();
                            txtCategory.setText(cr.getString(cr.getColumnIndex("name")));
                        } catch (Exception e) {
                        }

                    } catch (Exception e) {
                    }
                    //cpv.setVisibility(View.GONE);
                    /////////////////////////////////////////////////////////////////////////
                    try {
                        ArrayList<String> banners = new ArrayList<>();
                        String[] temp = result.getImages().split(",");

                        RecyclerView recyclerView = findViewById(R.id.recycler_banner);
                        ScrollingPagerIndicator indicator = findViewById(R.id.indicator);
                        for (int i = 0; i < temp.length; i++) {

                            temp[i] = (getString(R.string.image) + temp[i] + ".jpg");
                            banners.add(getString(R.string.image) + temp[i] + ".jpg");
                            CardSliderViewPager cardSliderViewPager = findViewById(R.id.pager);
                            //cardSliderViewPager.setAdapter(new BannerAdapter(banners, imageLoader));
                        }

                        BannerAdapter bannerAdapter = new BannerAdapter(Arrays.asList(temp), MyEventDetailsActivity.this);
                        recyclerView.setAdapter(bannerAdapter);

                        RecyclerSnapHelper snapHelper = new RecyclerSnapHelper();
                        snapHelper.attachToRecyclerView(recyclerView);

                        indicator.attachToRecyclerView(recyclerView);
                        indicator.setSelectedDotColor(getResources().getColor(R.color.white));
                        indicator.setVisibleDotCount(3);

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
                                cr.close();
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        DeletePost();
                    }

                } catch (Exception e) {
                    DeletePost();
                    HSH.showtoast(MyEventDetailsActivity.this, "این آگهی حذف شده است.");
                    finish();
                }
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };
       // new AsynctaskEventDetails(this, fFeed.getPostId(), del).getData();
    }

    private void registerDialog() {
        final SweetAlertDialog dialog = new SweetAlertDialog(MyEventDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("عضویت")
                .setContentText("برای نمایش اطلاعات لازم است ابتدا عضو شوید")
                .setConfirmText("بله")
                .setCancelText("فعلا نه")
                .setConfirmClickListener(sDialog -> {
                    HSH.onOpenPage(MyEventDetailsActivity.this, IntroLoginActivity.class);
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
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog_wait.dismiss();
                HSH.showtoast(MyEventDetailsActivity.this, "گزارش شما با موفقیت ثبت گردید");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(MyEventDetailsActivity.this, "مجدد تلاش کنید /مشکل در دریافت اطلاعات");
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
                    (Call<ResponseBody> call, Response<ResponseBody> response) {
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


    private NotifyData notifyData() {
        final PersianCalendar now = new PersianCalendar();
        NotifyData notifydata = new NotifyData(
                fFeed.getPostId(),
                String.format(getString(R.string.join), "\"" + fFeed.getTitle() + "\""),
                String.valueOf(now.getPersianLongDate()),
                result.getSubject() + "",
                fFeed.getCity(),
                fFeed.getCost(),
                //result.getTag(),
                fFeed.getImages().split(",")[0],
                "");
        return notifydata;
    }


    private void CancelPostAsynkTask(final SweetAlertDialog loading) {
        loading.show();
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).CancelPost(fFeed.getPostId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, Response<ResponseBody> response) {

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
        ir.moslem_deris.apps.zarinpal.ZarinPal.pay(MyEventDetailsActivity.this, payment, new OnPaymentListener() {
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
                HSH.showtoast(MyEventDetailsActivity.this, "خطا!!!" + "\n" + errorMessage);
            }
        });
    }

    private void InsertPaymentAsynkTask(final String refID) {
        final SweetAlertDialog dialog = HSH.onProgress_Dialog(MyEventDetailsActivity.this, "لطفا منتظر بمانید ...");
        dialog.show();
        Map<String, String> params = new HashMap<>();
        if ((result.getState()).contains("فوری"))
            params.put(getString(R.string.TypeOfPay), getString(R.string.IsImmediate));
        else if ((result.getState()).contains("دسته بندی"))
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
                    (Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String query = "Update RecentVisit set IsSuccessed = 'true' " +
                                "WHERE data LIKE '%" + fFeed.getPostId() + "%' ";
                        Application.database.execSQL(query);
                        dialog.setTitleText("تراکنش موفق")
                                .setContentText("تراکنش شما با موفقیت انجام شد و رویداد شما بعد از بررسی منتشر خواهد شد.")
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
                } else {
                    InsertPaymentAsynkTask(refID);
                    String query = "Update RecentVisit set IsSuccessed = 'false' " +
                            "WHERE Id LIKE '%" + fFeed.getPostId() + "%' ";
                    Application.database.execSQL(query);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                InsertPaymentAsynkTask(refID);
                String query = "Update RecentVisit set IsSuccessed = 'false' " +
                        "WHERE Id LIKE '%" + fFeed.getPostId() + "%' ";
                Application.database.execSQL(query);
            }
        });
    }


    @Override
    public void onClick(View v) {
        final Dialog dialog2 = new Dialog(MyEventDetailsActivity.this);
        switch (v.getId()) {
            case R.id.btn_mobile:
                Intent in = new Intent(MyEventDetailsActivity.this, LoginActivity.class);
                in.putExtra("Type", "Update");
                startActivityForResult(in, 123);
                break;

            case R.id.btn_pay:
                pay(String.format(getString(R.string.payNote), result.getState().split("-")[0]));
                break;

            case R.id.btn_edit:
                Intent intent;
                intent = new Intent(MyEventDetailsActivity.this, UpdatePostActivity.class);
                intent.putExtra("feedItem", getIntent().getExtras().getSerializable("feedItem"));
                startActivityForResult(intent, 321);
                break;
            case R.id.btn_delete:
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_delete_post);
                TextView txt_delete = dialog2.findViewById(R.id.txt_delete);
                TextView txt_reject = dialog2.findViewById(R.id.txt_reject);
                final RadioGroup radioGroup01 = dialog2.findViewById(R.id.radioGroup);

                if (result.getState().contains(getString(R.string.release1)) ||
                        result.getState().contains(getString(R.string.release2))) {
                    dialog2.findViewById(R.id.radioButton4).setVisibility(View.VISIBLE);
                }
                radioGroup01.setOnCheckedChangeListener((group, checkedId) -> checkid = Integer.parseInt(dialog2.findViewById(checkedId).getTag().toString()));

                txt_delete.setOnClickListener(v1 -> {
                    if (radioGroup01.getCheckedRadioButtonId() != -1) {
                        if (NetworkUtils.getConnectivity(MyEventDetailsActivity.this) != false) {
                            SweetAlertDialog dialog_wait = HSH.onProgress_Dialog(MyEventDetailsActivity.this, "شکیبا باشید ...");
                            if (checkid == 1 || checkid == 2 || checkid == 3)
                                DeletePostAsynkTask(dialog_wait);
                            else
                                CancelPostAsynkTask(dialog_wait);
                            dialog2.dismiss();
                        } else
                            HSH.showtoast(MyEventDetailsActivity.this, "خطا در اتصال به اینترنت");

                    } else
                        HSH.showtoast(MyEventDetailsActivity.this, "یک گزینه را انتخاب کنید.");
                });

                txt_reject.setOnClickListener(v13 -> dialog2.dismiss());
                HSH.dialog(dialog2);
                dialog2.show();
                break;

            /*case R.id.btn_sendComment:
                *//*if (!etComment.getText().toString().trim().equals(""))
                    if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false)
                        SendCommentAsynkTask(etComment.getText().toString().trim());
                    else
                        HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");*//*
                break;*/

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

            case R.id.img_back:
                finish();
                break;

            case R.id.cl_event_organizer:
                if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true"))
                    try {
                        Intent i = new Intent(MyEventDetailsActivity.this, UserProfileActivity.class);
                        i.putExtra("PostId", fFeed.getPostId());
                        i.putExtra("AdvertiserId", result.getEventOwner().getId());
                        i.putExtra("profileimage", result.getEventOwner().getProfileImage());
                        startActivity(i);
                    } catch (Exception e) {
                    }
                else
                    registerDialog();
                break;

            case R.id.img_profile:
                try {
                    final Bundle bundle = new Bundle();
                    Intent i = new Intent(MyEventDetailsActivity.this, ViewPagerActivity.class);
                    PayeItem item = new PayeItem();
                    item.setImages(result.getEventOwner().getProfileImage());
                    bundle.putSerializable("feed", item);
                    i.putExtras(bundle);
                    startActivity(i);
                } catch (Exception e) {
                }
                break;
            case R.id.txt_upgrade_event:
                showBottomsheetNavigation();
                break;

        }
    }


    private void showBottomsheetNavigation() {

        View view = getLayoutInflater().inflate(R.layout.dialog_upgrade_event, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(view);
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                switch (i) {

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        dialog.show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                //setScrim(v);
                dialog.show();
            }
        });
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();
        //ConstraintLayout c1 = view.findViewById(R.id.constraintLayout1);

    }
}

