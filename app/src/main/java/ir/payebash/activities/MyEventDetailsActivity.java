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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
/*import ir.moslem_deris.apps.zarinpal.PaymentBuilder;
import ir.moslem_deris.apps.zarinpal.enums.ZarinPalError;
import ir.moslem_deris.apps.zarinpal.listeners.OnPaymentListener;
import ir.moslem_deris.apps.zarinpal.models.Payment;*/
import ir.payebash.BuildConfig;
import ir.payebash.Constants;
import ir.payebash.adapters.BannerAdapter;
import ir.payebash.Application;
import ir.payebash.adapters.FollowersAdapter;
import ir.payebash.adapters.PersonAddedAdapter;
import ir.payebash.classes.BaseFragment;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.databinding.ActivityEventDetails2Binding;
import ir.payebash.databinding.ActivityMyEventDetailsBinding;
import ir.payebash.databinding.ActivityUpdateEventBinding;
import ir.payebash.models.InputPostDetailsParamsModel;
import ir.payebash.models.NotifyData;
import ir.payebash.models.NotifyData.Message;
import ir.payebash.models.PayeItem;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.R;
import ir.payebash.models.event.detail.RequestStateItem;
import ir.payebash.remote.AsynctaskEventDetails;
import ir.payebash.utils.FragmentStack;
import ir.payebash.utils.RecyclerSnapHelper;
import ir.payebash.utils.hashtaghelper.HashTagHelper;
import ir.payebash.utils.reviewratings.BarLabels;
import ir.payebash.utils.reviewratings.RatingReviews;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class MyEventDetailsActivity extends BaseFragment implements View.OnClickListener {

    private static Double latitude, longitude;
    //com.google.android.gms.maps.MapView mMapView;
    private ConstraintLayout pB;
    int checkid = 0;
    boolean isPush = false;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    @Inject
    Retrofit retrofit;
    private CollapsingToolbarLayout i;
    //private LinearLayout ll_advertisdetails;
    //private EditText etComment;
    private ImageView imgProfile;
    private TextView txtEventDate, txtTimeToJoin,
            txtUpgradeEvent,
            btnShare, btnEdit
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
    private View rootView = null;
    private ActivityMyEventDetailsBinding binding;
    public static MyEventDetailsActivity fragment = null;
    private static final String ARG_PARAM1 = "param1";

    public static MyEventDetailsActivity newInstance(EventModel eventDetailsModel) {
        if (fragment == null)
            fragment = new MyEventDetailsActivity();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, eventDetailsModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //eventDetailsModel = (EventDetailsModel) getArguments().getSerializable(ARG_PARAM1);
            fFeed = (EventModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    private void DeclareElements() {
        pB = rootView.findViewById(R.id.progressBar);
        txtEventDate = rootView.findViewById(R.id.txt_event_date);
        txtTimeToJoin = rootView.findViewById(R.id.txt_time_to_join);
        txtUpgradeEvent = rootView.findViewById(R.id.txt_upgrade_event);
        txtUpgradeEvent.setOnClickListener(this::onClick);
        /*cpv = rootView.findViewById(R.id.cpv);

        btnPay = rootView.findViewById(R.id.btn_pay);
        btnEdit = rootView.findViewById(R.id.btn_edit);
        btnDelete = rootView.findViewById(R.id.btn_delete);
        btnMobile = rootView.findViewById(R.id.btn_mobile);
        //btn_fav = rootView.findViewById(R.id.btn_fav);*/
        btnEdit = rootView.findViewById(R.id.btn_edit);
        btnShare = rootView.findViewById(R.id.btn_share);
        rootView.findViewById(R.id.img_back).setOnClickListener(this::onClick);
        //btn_contactWays = rootView.findViewById(R.id.btn_contactWays);
        /*btnSendComment = rootView.findViewById(R.id.btn_sendComment);


        btnPay.setOnClickListener(getActivity());
        btnDelete.setOnClickListener(getActivity());
        btnMobile.setOnClickListener(getActivity());
        //btn_fav.setOnClickListener(getActivity());*/
        btnEdit.setOnClickListener(this::onClick);
        btnShare.setOnClickListener(this::onClick);
        //btn_contactWays.setOnClickListener(getActivity());
        /*btnSendComment.setOnClickListener(getActivity());
        back.setOnClickListener(getActivity());*/

        //etComment = rootView.findViewById(R.id.et_comment);
        imgProfile = rootView.findViewById(R.id.img_profile);
        //pager = rootView.findViewById(R.id.pager);
        rootView.findViewById(R.id.group_friends).setOnClickListener(this::onClick);

        imgProfile.setOnClickListener(this::onClick);

        ImageView img = rootView.findViewById(R.id.imageView8);
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

        /*rvComment = rootView.findViewById(R.id.rv_comment);
        rvComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvComment.setLayoutManager(layoutManager);
        adapter = new CommentsAdapter(getActivity(), Commentfeed, imageLoader);
        rvComment.setAdapter(adapter);*/
    }

    private void checkFollowers() {
        rvGroupFriends = rootView.findViewById(R.id.rv_group_friends);
        rvGroupFriends.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvGroupFriends.setLayoutManager(layoutManager);
        PersonAddedAdapter adapter = new PersonAddedAdapter(getActivity(), imageLoader);
        rvGroupFriends.setAdapter(adapter);
        adapter.addItems(fFeed.getFollowers());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            try {

                if (fFeed == null) {
                    Intent intent = getActivity().getIntent();
                    Uri data = intent.getData();
                    fFeed = new EventModel();
                    fFeed.setPostId(data.getEncodedPath().split("/")[3]);
                }
            } catch (Exception e) {
                fFeed = new EventModel();
                fFeed.setPostId(getActivity().getIntent().getExtras().getString(getString(R.string.PostId)));
                isPush = true;
            }

            binding = DataBindingUtil.inflate(
                    inflater, R.layout.activity_my_event_details, container, false);
            rootView = binding.getRoot();
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

            i = rootView.findViewById(R.id.toolbar_layout);
            i.setTitle("");
        } catch (Exception e) {
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && null != data) {
            Intent resultData = new Intent();
            resultData.putExtra("data", "data");
            getActivity().setResult(Activity.RESULT_OK, resultData);
            getFragmentManager().popBackStack();
        }
        if (requestCode == 123 && null != data)
            AdvertisementDetails();
    }

    private void getMap() {
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:

                try {
                    if (!result.getLatitude().trim().equals("00")) {
                        /*mMapView.setVisibility(View.VISIBLE);
                        latitude = Double.parseDouble(result.getLatitude());
                        longitude = Double.parseDouble(result.getLongitude());
                        mMapView.onResume();*/
                        try {
                            MapsInitializer.initialize(getActivity());
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
                    checkFollowers();
                    getMap();
                    HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), hashTag -> {

                    });
                    mTextHashTagHelper.handle(rootView.findViewById(R.id.txt_description));
                    //List<String> allHashTags = mTextHashTagHelper.getAllHashTags(true);
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

                        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_banner);
                        recyclerView.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
                        recyclerView.setLayoutManager(layoutManager);
                        ScrollingPagerIndicator indicator = rootView.findViewById(R.id.indicator);
                        for (int i = 0; i < temp.length; i++) {

                            temp[i] = BuildConfig.BaseUrl + (getString(R.string.image) + temp[i] + ".jpg");
                            banners.add(BuildConfig.BaseUrl + getString(R.string.image) + temp[i] + ".jpg");
                            //CardSliderViewPager cardSliderViewPager = rootView.findViewById(R.id.pager);
                            //cardSliderViewPager.setAdapter(new BannerAdapter(banners, imageLoader));
                        }

                        BannerAdapter bannerAdapter = new BannerAdapter(Arrays.asList(temp), getActivity());
                        recyclerView.setAdapter(bannerAdapter);

                        RecyclerSnapHelper snapHelper = new RecyclerSnapHelper();
                        snapHelper.attachToRecyclerView(recyclerView);

                        indicator.attachToRecyclerView(recyclerView);
                        indicator.setSelectedDotColor(getResources().getColor(R.color.white));
                        indicator.setVisibleDotCount(3);

                    } catch (Exception e) {
                    }
                    ///////////////////////////////////////////////////////////////////////////
                    String query;
                    query = "replace INTO RecentVisit ";
                    query += "(Id) VALUES('" + fFeed.getPostId() + "')";
                    Application.database.execSQL(query);

                } catch (Exception e) {
                    DeletePost();
                    HSH.showtoast(getActivity(), "این آگهی حذف شده است.");
                    getFragmentManager().popBackStack();
                }
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };

        InputPostDetailsParamsModel input = new InputPostDetailsParamsModel();
        input.setCurrentUserId(Application.preferences.getString(getString(R.string.UserId), ""));
        input.setEventId(fFeed.getPostId());
        new AsynctaskEventDetails(getActivity(), input, del).getData();
    }

    private void registerDialog() {
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("عضویت")
                .setContentText("برای نمایش اطلاعات لازم است ابتدا عضو شوید")
                .setConfirmText("بله")
                .setCancelText("فعلا نه")
                .setConfirmClickListener(sDialog -> {
                    HSH.onOpenPage(getActivity(), IntroLoginActivity.class);
                    getFragmentManager().popBackStack();
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
                HSH.showtoast(getActivity(), "گزارش شما با موفقیت ثبت گردید");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(getActivity(), "مجدد تلاش کنید /مشکل در دریافت اطلاعات");
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
                                    getActivity().setResult(Activity.RESULT_OK, resultData);
                                    getFragmentManager().popBackStack();
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
                                    getActivity().setResult(Activity.RESULT_OK, resultData);
                                    getFragmentManager().popBackStack();
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
       /* Payment payment = null;
        try {
            payment = new PaymentBuilder()
                    .setMerchantID(getString(R.string.Mrchnt))
                    .setAmount(Application.preferences.getInt("Feepayable", 1000))
                    .setDescription(s)
                    .create();
        } catch (Exception e) {
        }
        ir.moslem_deris.apps.zarinpal.ZarinPal.pay(getActivity(), payment, new OnPaymentListener() {
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
                HSH.showtoast(getActivity(), "خطا!!!" + "\n" + errorMessage);
            }
        });*/
    }

    private void InsertPaymentAsynkTask(final String refID) {
        final SweetAlertDialog dialog = HSH.onProgress_Dialog(getActivity(), "لطفا منتظر بمانید ...");
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
                                    getActivity().setResult(Activity.RESULT_OK, resultData);
                                    getFragmentManager().popBackStack();
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
        final Dialog dialog2 = new Dialog(getActivity());
        switch (v.getId()) {
            case R.id.btn_pay:
                pay(String.format(getString(R.string.payNote), result.getState().split("-")[0]));
                break;

            case R.id.btn_edit:
                FragmentStack fragmentStack = new FragmentStack(getActivity(), getActivity().getSupportFragmentManager(),
                        R.id.main_frame);
                fragmentStack.replace(UpdateEventActivity.newInstance(result));
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
                        if (NetworkUtils.getConnectivity(getActivity()) != false) {
                            SweetAlertDialog dialog_wait = HSH.onProgress_Dialog(getActivity(), "شکیبا باشید ...");
                            if (checkid == 1 || checkid == 2 || checkid == 3)
                                DeletePostAsynkTask(dialog_wait);
                            else
                                CancelPostAsynkTask(dialog_wait);
                            dialog2.dismiss();
                        } else
                            HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");

                    } else
                        HSH.showtoast(getActivity(), "یک گزینه را انتخاب کنید.");
                });

                txt_reject.setOnClickListener(v13 -> dialog2.dismiss());
                HSH.dialog(dialog2);
                dialog2.show();
                break;

            /*case R.id.btn_sendComment:
             *//*if (!etComment.getText().toString().trim().equals(""))
                    if (NetworkUtils.getConnectivity(getActivity()) != false)
                        SendCommentAsynkTask(etComment.getText().toString().trim());
                    else
                        HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");*//*
                break;*/


            case R.id.btn_share:
                try {
                    String shareBody = BuildConfig.BaseUrl + "/getwebpostdetails/index/" + fFeed.getPostId();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, fFeed.getTitle() + "\n" + "را در پایه باش ببین");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, null));
                } catch (Exception e) {
                }
                break;

            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;

            case R.id.cl_event_organizer:
                if (Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true"))
                    try {
                        Intent i = new Intent(getActivity(), UserProfileActivity.class);
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
            case R.id.img_profile:
                try {
                    final Bundle bundle = new Bundle();
                    Intent i = new Intent(getActivity(), ViewPagerActivity.class);
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

        }
    }

    private void showBottomsheetNavigation() {

        View view = getLayoutInflater().inflate(R.layout.dialog_upgrade_event, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
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
        //ConstraintLayout c1 = view.rootView.findViewById(R.id.constraintLayout1);

    }

    private void showBottomsheetFollwoers() {

        if (followerView == null) {
            followerView = getLayoutInflater().inflate(R.layout.dialog_friend_in_event, null);
            dialogFollower = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
            dialogFollower.setContentView(followerView);

            ConstraintLayout csFollowers = followerView.findViewById(R.id.cs_followers);

            if (result.getFollowers().size() > 0) {
                csFollowers.setVisibility(View.VISIBLE);
                RecyclerView rv = followerView.findViewById(R.id.rv_followers);
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
                rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));
                FollowersAdapter adapter = new FollowersAdapter(getActivity());
                rv.setAdapter(adapter);

                adapter.addItems(result.getFollowers());

            } else
                followerView.findViewById(R.id.img_no_followers).setVisibility(View.VISIBLE);

            BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) followerView.getParent());
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            dialogFollower.show();
        } else
            dialogFollower.show();
        //ConstraintLayout c1 = view.rootView.findViewById(R.id.constraintLayout1);

    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        ImageLoader.getInstance().displayImage(url, view);
    }
}

