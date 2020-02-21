package ir.payebash.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.moslem_deris.apps.zarinpal.PaymentBuilder;
import ir.moslem_deris.apps.zarinpal.enums.ZarinPalError;
import ir.moslem_deris.apps.zarinpal.listeners.OnPaymentListener;
import ir.moslem_deris.apps.zarinpal.models.Payment;
import ir.payebash.Constants;
import ir.payebash.adapters.BannerAdapter;
import ir.payebash.adapters.FollowersAdapter;
import ir.payebash.adapters.PersonAddedAdapter;
import ir.payebash.Application;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.databinding.ActivityEventDetails2Binding;
import ir.payebash.models.InputPostDetailsParamsModel;
import ir.payebash.models.NotifyData;
import ir.payebash.models.NotifyData.Message;
import ir.payebash.models.PayeItem;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.models.event.detail.RequestStateItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskEventDetails;
import ir.payebash.utils.RecyclerSnapHelper;
import ir.payebash.utils.reviewratings.BarLabels;
import ir.payebash.utils.reviewratings.RatingReviews;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class PostDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static Double latitude, longitude;
    //com.google.android.gms.maps.MapView mMapView;
    private ConstraintLayout pB;
    private ProgressBar pbTrustPercent;
    int checkid = 0;
    boolean isPush = false;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    @Inject
    Retrofit retrofit;
    private ConstraintLayout eventOrganizer;
    private CollapsingToolbarLayout i;
    //private LinearLayout ll_advertisdetails;
    private TextView /*btn_contactWays,*/ btnBeup;
    //private EditText etComment;
    private ImageView imgProfile;
    private TextView txtEventDate, txtTimeToJoin,
            txtUpgradeEvent,
            btnShare, btnReport
            /*, btnMobile, btnEdit, btnDelete, btnPay*/;
    private EventModel fFeed;
    //private ImageButton /*btn_fav,*/ btnSendComment, back;
    private EventDetailsModel result;
    //private ProgressWheel cpv;
    private RecyclerView rvGroupFriends;
    private View followerView = null;
    private BottomSheetDialog dialogFollower = null;
    //private RecyclerView rvComment;
    //private CommentsAdapter adapter;
    // private List<CommentModel> Commentfeed = new ArrayList<>();
    private ActivityEventDetails2Binding binding;

    private void DeclareElements() {
        pB = findViewById(R.id.progressBar);
        txtEventDate = findViewById(R.id.txt_event_date);
        txtTimeToJoin = findViewById(R.id.txt_time_to_join);
        txtUpgradeEvent = findViewById(R.id.txt_upgrade_event);
        btnReport = findViewById(R.id.btn_report);
        findViewById(R.id.cs_rating).setOnClickListener(this::onClick);
        pbTrustPercent = findViewById(R.id.pb_trust_percent);
        txtUpgradeEvent.setOnClickListener(this::onClick);
        /*cpv = findViewById(R.id.cpv);

        btnPay = findViewById(R.id.btn_pay);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnMobile = findViewById(R.id.btn_mobile);
        //btn_fav = findViewById(R.id.btn_fav);*/
        btnShare = findViewById(R.id.btn_share);
        eventOrganizer = findViewById(R.id.cl_event_organizer);
        findViewById(R.id.img_back).setOnClickListener(this::onClick);
        //btn_contactWays = findViewById(R.id.btn_contactWays);
        /*btnSendComment = findViewById(R.id.btn_sendComment);


        btnPay.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnMobile.setOnClickListener(this);
        //btn_fav.setOnClickListener(this);*/
        btnShare.setOnClickListener(this);
        eventOrganizer.setOnClickListener(this);
        //btn_contactWays.setOnClickListener(this);
        /*btnSendComment.setOnClickListener(this);
        back.setOnClickListener(this);*/
        btnReport.setOnClickListener(this);

        //etComment = findViewById(R.id.et_comment);
        imgProfile = findViewById(R.id.img_profile);
        //pager = findViewById(R.id.pager);
        findViewById(R.id.group_friends).setOnClickListener(this::onClick);

        btnBeup = findViewById(R.id.btn_beup);
        btnBeup.setOnClickListener(this);
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

    private void checkUserState() {
        if (null != result.getRequestState()) {

            String state = result.getRequestState().getState();

            btnBeup.setText(result.getRequestState().getTitleState());
            btnBeup.setTextColor(state.equals(Constants.WATING_STATE) ? Color.WHITE
                    : state.equals(Constants.ACCEPT_STATE) ? Color.BLACK : Color.WHITE);

            btnBeup.setBackgroundResource(state.equals(Constants.WATING_STATE) ? R.drawable.rounded_corners_solid_blue
                    : state.equals(Constants.ACCEPT_STATE) ? R.drawable.rounded_corners_strok_black :
                    state.equals(Constants.REJECT_STATE) ? R.drawable.rounded_corners_solid_red :
                            R.drawable.rounded_corners_solid_navyblue);

            if (result.getRequestState().getState().equals(Constants.REJECT_STATE) ||
                    result.getRequestState().getState().equals(Constants.CANCEL_STATE))
                btnBeup.setEnabled(false);
            //findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
            //if (Commentfeed.size() == 0)
            //  GetCommentAsynkTask();
            btnBeup.setOnClickListener(v -> {
                final SweetAlertDialog dialog = new SweetAlertDialog(PostDetailsActivity.this, SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText(getString(R.string.sure));
                //dialog.setContentText(getString(R.string.sure));
                dialog.setConfirmText("بله");
                dialog.setCancelText("فعلا نه");
                dialog.setConfirmClickListener(sDialog -> {
                    UpdateApplicantAsynkTask();
                    dialog.dismissWithAnimation();
                });
                dialog.setCancelClickListener(sweetAlertDialog -> dialog.dismissWithAnimation());
                dialog.setCancelable(true);
                dialog.show();
            });
        }
    }

    private void checkFollowers() {
        rvGroupFriends = findViewById(R.id.rv_group_friends);
        rvGroupFriends.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetailsActivity.this);
        rvGroupFriends.setLayoutManager(layoutManager);
        PersonAddedAdapter adapter = new PersonAddedAdapter(PostDetailsActivity.this, imageLoader);
        rvGroupFriends.setAdapter(adapter);
        adapter.addItems(fFeed.getFollowers());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
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

            binding = DataBindingUtil.setContentView(this, R.layout.activity_event_details2);
            binding.setEvent(fFeed);
            Application.getComponent().Inject(this);

            DeclareElements();
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
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PostDetailsActivity.this)) {
            case ConnectionResult.SUCCESS:

                try {
                    if (!result.getLatitude().trim().equals("00")) {
                        /*mMapView.setVisibility(View.VISIBLE);
                        latitude = Double.parseDouble(result.getLatitude());
                        longitude = Double.parseDouble(result.getLongitude());
                        mMapView.onResume();*/
                        try {
                            MapsInitializer.initialize(PostDetailsActivity.this);
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
                    result = event;
                    binding.setEventDetails(result);
                    checkUserState();
                    checkFollowers();
                    getMap();
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                        txtTimeToJoin.setText(HSH.printDifference(new Date(),
                                simpleDateFormat.parse(result.getTimeToJoin().replace("T", " "))));
                        txtEventDate.setText(HSH.printDifference(simpleDateFormat.parse(result.getCreateDate()
                                .replace("T", " ")), new Date()) + " پیش");

                    } catch (Exception e) {
                    }
                    //cpv.setVisibility(View.GONE);
                    /////////////////////////////////////////////////////////////////////////
                    try {
                        ArrayList<String> banners = new ArrayList<>();
                        String[] temp = result.getImages().split(",");

                        RecyclerView recyclerView = findViewById(R.id.recycler_banner);
                        recyclerView.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetailsActivity.this, LinearLayoutManager.HORIZONTAL, true);
                        recyclerView.setLayoutManager(layoutManager);
                        ScrollingPagerIndicator indicator = findViewById(R.id.indicator);
                        for (int i = 0; i < temp.length; i++) {

                            temp[i] = (getString(R.string.image) + temp[i] + ".jpg");
                            banners.add(getString(R.string.image) + temp[i] + ".jpg");
                            //CardSliderViewPager cardSliderViewPager = findViewById(R.id.pager);
                            //cardSliderViewPager.setAdapter(new BannerAdapter(banners, imageLoader));
                        }

                        BannerAdapter bannerAdapter = new BannerAdapter(Arrays.asList(temp), PostDetailsActivity.this);
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
                    HSH.showtoast(PostDetailsActivity.this, "این آگهی حذف شده است.");
                    finish();
                }

                try {
                    String s = Application.preferences.getString(getString(R.string.UserId), "0000");
                    if (null != getIntent().getExtras())
                        if (result.getEventOwner().getId().toLowerCase().equals(s)) {
                            try {
                                String query = "update RecentVisit set " +
                                        "IsMine = 'true' where Id = '" + fFeed.getPostId() + "'";
                                Application.database.execSQL(query);
                            } catch (Exception e) {
                            }
                            //findViewById(R.id.ll_mng).setVisibility(View.VISIBLE);
                            btnBeup.setVisibility(View.GONE);
                            //findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                            /*if (Commentfeed.size() == 0)
                                GetCommentAsynkTask();
                            String[] temp = result.getState().split("-");
                            if (temp[0].contains(getString(R.string.pay)))
                                btnPay.setVisibility(View.VISIBLE);
                            else if (temp[0].contains("تایید شماره")) {
                                btnMobile.setVisibility(View.VISIBLE);
                            }*/


                            /*((TextView) findViewById(R.id.txt_title_state)).setText(temp[0]);
                            ((TextView) findViewById(R.id.txt_description_state)).setText(temp[1]);
                            findViewById(R.id.txt_title_state).setBackgroundColor(Color.parseColor(temp[2]));
                            ((TextView) findViewById(R.id.txt_description_state)).setTextColor(Color.parseColor(temp[2]));*/
                        } else {
                            try {
                                if (result.getEventOwner().getId().equals(Application.preferences.getString(getString(R.string.UserId), "0000"))) {
                                    btnBeup.setVisibility(View.GONE);
                                    //findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                                    /*if (Commentfeed.size() == 0)
                                        GetCommentAsynkTask();*/
                                } else {
                                    //findViewById(R.id.ll_mng).setVisibility(View.GONE);
                                    btnBeup.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                //findViewById(R.id.ll_mng).setVisibility(View.GONE);
                                btnBeup.setVisibility(View.VISIBLE);
                            }
                        }
                } catch (Exception e) {
                    /*findViewById(R.id.ll_mng).setVisibility(View.GONE);*/
                    btnBeup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };

        InputPostDetailsParamsModel input = new InputPostDetailsParamsModel();
        input.setCurrentUserId(Application.preferences.getString(getString(R.string.UserId), ""));
        input.setEventId(fFeed.getPostId());
        new AsynctaskEventDetails(this, input, del).getData();
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
        params.put(getString(R.string.PostId), fFeed.getPostId());
        params.put("Type", String.valueOf(checkid));
        Call<ResponseBody> call =
                retrofit.create(ApiInterface.class).SendingReportService(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                retrofit.create(ApiInterface.class).RemovePost(params);
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
                } /*else
                    DeletePostAsynkTask(loading);*/
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //DeletePostAsynkTask(loading);
            }
        });
    }

    private void CancelPostAsynkTask(final SweetAlertDialog loading) {
        loading.show();
        Call<ResponseBody> call =
                retrofit.create(ApiInterface.class).CancelPost(fFeed.getPostId());
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
                retrofit.create(ApiInterface.class).InsertPayment(params);
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

    private void UpdateApplicantAsynkTask() {
        Call<RequestStateItem> call =
                retrofit.create(ApiInterface.class).UpdateApplicants(fFeed.getPostId());
        call.enqueue(new Callback<RequestStateItem>() {
            @Override
            public void onResponse
                    (Call<RequestStateItem> call, Response<RequestStateItem> response) {
                findViewById(R.id.pb_update_applicant).setVisibility(View.GONE);
                if (response.code() == 200) {
                    try {
                            /*findViewById(R.id.ll_comments).setVisibility(View.VISIBLE);
                            if (adapter.getItemCount() == 0)
                                GetCommentAsynkTask();*/
                        Cursor cr = Application.database.rawQuery("SELECT * from RecentVisit WHERE Id='" + fFeed.getPostId() + "'", null);
                        cr.moveToFirst();
                        if (null == cr.getString(cr.getColumnIndex("IsBeup"))) {
                            NotifyData notifydata = notifyData();
                            notifydata.type = "Beup";
                            Call<Message> call2 = ApiClient.getClient2().create(ApiInterface.class).sendMessage(new Message(result.getEventOwner().getToken(), notifydata));
                            call2.enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    String query = "Update RecentVisit set IsBeup = 'true' " +
                                            "WHERE Id LIKE '%" + fFeed.getPostId() + "%' ";
                                    Application.database.execSQL(query);
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {
                                }
                            });
                        }

                        String state = response.body().getState();
                        btnBeup.setText(response.body().getTitleState());
                        btnBeup.setTextColor(state.equals("1") ? Color.WHITE
                                : state.equals("2") ? Color.BLACK : Color.WHITE);

                        btnBeup.setBackgroundResource(state.equals("1") ? R.drawable.rounded_corners_solid_blue
                                : state.equals("2") ? R.drawable.rounded_corners_strok_black :
                                state.equals("3") ? R.drawable.rounded_corners_solid_red :
                                        R.drawable.rounded_corners_solid_navyblue);

                        if (response.body().getState().equals("3") ||
                                response.body().getState().equals("4"))
                            btnBeup.setEnabled(false);

                        if (state.equals("1")) {
                            final Dialog dialog = new Dialog(PostDetailsActivity.this, R.style.BottomSheetDialog);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_send_request);
                            dialog.show();
                            dialog.findViewById(R.id.tv_dismiss).setOnClickListener(v -> dialog.dismiss());
                        }

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestStateItem> call, Throwable t) {
            }
        });
    }

    private NotifyData notifyData() {
        final PersianCalendar now = new PersianCalendar();
        NotifyData notifydata = new NotifyData(
                fFeed.getPostId(),
                String.format(getString(R.string.join), "\"" + fFeed.getTitle() + "\""),
                String.valueOf(now.getPersianLongDate()),
                result.getSubject(),
                fFeed.getCity(),
                fFeed.getCost(),
                /*fFeed.getTag(),*/
                fFeed.getImages().split(",")[0],
                "");
        return notifydata;
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

            /*case R.id.btn_sendComment:
             *//*if (!etComment.getText().toString().trim().equals(""))
                    if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false)
                        SendCommentAsynkTask(etComment.getText().toString().trim());
                    else
                        HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");*//*
                break;*/
           /* case R.id.btn_fav:
                Cursor cr = Application.database.rawQuery("SELECT IsFavorite from RecentVisit WHERE Id = '" + fFeed.getPostId() + "'", null);
                cr.close();
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
                                //btn_fav.setImageResource(R.drawable.ic_bookmark_white);
                            } catch (Exception e) {
                            }

                        } else if (cr.getString(cr.getColumnIndex("IsFavorite")).equals("false")) {
                            try {
                                String query2 = "UPDATE RecentVisit SET " +
                                        "IsFavorite='true' " +
                                        "WHERE Id='" + fFeed.getPostId() + "' ";

                                Application.database.execSQL(query2);
                                HSH.showtoast(getApplicationContext(), "نشان شد");
                                //btn_fav.setImageResource(R.drawable.ic_bookmark);

                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
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

            case R.id.btn_report:
                View view = getLayoutInflater().inflate(R.layout.dialog_report_post, null);
                BottomSheetDialog dialogReport = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                dialogReport.setContentView(view);
                TextView txt_send = dialogReport.findViewById(R.id.txt_send);
                TextView txt_reject2 = dialogReport.findViewById(R.id.txt_reject);
                final RadioGroup radioGroup001 = dialogReport.findViewById(R.id.radioGroup);

                radioGroup001.setOnCheckedChangeListener((group, checkedId) -> checkid = Integer.parseInt(dialogReport.findViewById(checkedId).getTag().toString()));

                txt_send.setOnClickListener(v12 -> {
                    if (radioGroup001.getCheckedRadioButtonId() != -1) {
                        if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false) {
                            SendingReportService(checkid, dialogReport);
                            dialog2.dismiss();
                        } else
                            HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");

                    } else
                        HSH.showtoast(PostDetailsActivity.this, "یک گزینه را انتخاب کنید.");
                });

                txt_reject2.setOnClickListener(v14 -> dialogReport.dismiss());
                BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
                mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int i) {

                        switch (i) {

                            case BottomSheetBehavior.STATE_HIDDEN:
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED: {
                                dialogReport.show();
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
                        dialogReport.show();
                    }
                });
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                dialogReport.show();
                break;

            case R.id.img_back:
                finish();
                break;


            case R.id.cl_event_organizer:
                if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true"))
                    try {
                        Intent i = new Intent(PostDetailsActivity.this, UserProfileActivity.class);
                        i.putExtra("PostId", fFeed.getPostId());
                        i.putExtra("AdvertiserId", result.getEventOwner().getId());
                        i.putExtra("profileimage", result.getEventOwner().getProfileImage());
                        startActivity(i);
                    } catch (Exception e) {
                    }
                else
                    registerDialog();
                break;

            //case R.id.btn_contactWays:

            case R.id.btn_beup:
                findViewById(R.id.pb_update_applicant).setVisibility(View.VISIBLE);
                if (NetworkUtils.getConnectivity(PostDetailsActivity.this) != false)
                    UpdateApplicantAsynkTask();
                else
                    HSH.showtoast(PostDetailsActivity.this, "خطا در اتصال به اینترنت");
                break;

            case R.id.img_profile:
                try {
                    final Bundle bundle = new Bundle();
                    Intent i = new Intent(PostDetailsActivity.this, ViewPagerActivity.class);
                    PayeItem item = new PayeItem();
                    item.setImages(result.getEventOwner().getProfileImage());
                    bundle.putSerializable("feed", item);
                    i.putExtras(bundle);
                    startActivity(i);
                } catch (Exception e) {
                }
                break;

            case R.id.group_friends:
                showBottomsheetFollwoers();
                break;
            case R.id.txt_upgrade_event:
                showBottomsheetNavigation();
                break;
            case R.id.cs_rating:
                showBottomsheetRating();
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

    private void showBottomsheetFollwoers() {

        if (followerView == null) {
            followerView = getLayoutInflater().inflate(R.layout.dialog_friend_in_event, null);
            dialogFollower = new BottomSheetDialog(this, R.style.BottomSheetDialog);
            dialogFollower.setContentView(followerView);

            ConstraintLayout csFollowers = followerView.findViewById(R.id.cs_followers);

            if (result.getFollowers().size() > 0) {
                csFollowers.setVisibility(View.VISIBLE);
                RecyclerView rv = followerView.findViewById(R.id.rv_followers);
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
                rv.addItemDecoration(new ItemDecorationAlbumColumns(this, ItemDecorationAlbumColumns.VERTICAL_LIST));
                FollowersAdapter adapter = new FollowersAdapter(PostDetailsActivity.this);
                rv.setAdapter(adapter);

                adapter.addItems(result.getFollowers());

            } else
                followerView.findViewById(R.id.img_no_followers).setVisibility(View.VISIBLE);

            BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) followerView.getParent());
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            dialogFollower.show();
        } else
            dialogFollower.show();
        //ConstraintLayout c1 = view.findViewById(R.id.constraintLayout1);

    }

    private void showBottomsheetRating() {

        View view = getLayoutInflater().inflate(R.layout.dialog_rating, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(view);

        int sum = 0, rate1 = 0, rate2 = 0, rate3 = 0, rate4 = 0, rate5 = 0;
        if (null != result.getRating())
            for (int i = 0; i < result.getRating().size(); i++) {
                sum += result.getRating().get(i).getCount();
                if (result.getRating().get(i).getRate() == 1)
                    rate1 = result.getRating().get(i).getCount();
                else if (result.getRating().get(i).getRate() == 2)
                    rate2 = result.getRating().get(i).getCount();
                else if (result.getRating().get(i).getRate() == 3)
                    rate3 = result.getRating().get(i).getCount();
                else if (result.getRating().get(i).getRate() == 4)
                    rate4 = result.getRating().get(i).getCount();
                else if (result.getRating().get(i).getRate() == 5)
                    rate5 = result.getRating().get(i).getCount();
            }

        RatingReviews ratingReviews = view.findViewById(R.id.rating_reviews);
        int colors[] = new int[]{
                Color.parseColor("#0e9d58"),
                Color.parseColor("#bfd047"),
                Color.parseColor("#ffc105"),
                Color.parseColor("#ef7e14"),
                Color.parseColor("#d36259")};

        int raters[] = new int[]{
                (rate5 * 100 / (sum == 0 ? 1 : sum)),
                (rate4 * 100 / (sum == 0 ? 1 : sum)),
                (rate3 * 100 / (sum == 0 ? 1 : sum)),
                (rate2 * 100 / (sum == 0 ? 1 : sum)),
                (rate1 * 100 / (sum == 0 ? 1 : sum))
        };
        ratingReviews.createRatingBars(100, BarLabels.STYPE1, colors, raters);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating(Float.parseFloat(result.getEventOwner().getRate()));

        TextView tvRate = view.findViewById(R.id.tv_rate);
        tvRate.setText(result.getEventOwner().getRate());

        TextView tvCount = view.findViewById(R.id.tv_count);
        tvCount.setText(sum + "");

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

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        ImageLoader.getInstance().displayImage(url, view);
    }
}

