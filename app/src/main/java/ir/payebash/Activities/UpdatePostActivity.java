package ir.payebash.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Adapters.GalleryAdapter;
import ir.payebash.Application;
import ir.payebash.Classes.BaseActivity;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Classes.PermissionHandler;
import ir.payebash.DI.DaggerMainComponent;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.CustomGallery;
import ir.payebash.Models.PayeItem;
import ir.payebash.Moudle.ExpandableHeightGridView;
import ir.payebash.Moudle.Roozh;
import ir.payebash.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UpdatePostActivity extends BaseActivity implements View.OnClickListener {

    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Inject
    ImageLoader imageLoader;
    private ArrayList<CustomGallery> map = new ArrayList<>();
    private LinearLayout ll_parent;
    private JSONObject result;
    private Uri uri;
    private ExpandableHeightGridView gv;
    private GalleryAdapter adapter;
    private CardView cd_phone, cd_link;
    private EditText etTitle, etTag, etTime_start, et_phone, et_link,
            etTime_finish, et_cost, et_count, et_deadline, et_description;
    private Button btn_subject_post, btn_location_post, btn_register;
    private ImageButton btn_calender, btn_calender2;
    private SwitchCompat compatSwitch, compatSwitchImmediate;
    private Intent _data;
    private Map<String, String> params = new HashMap<>();
    private Roozh jCal;
    private SweetAlertDialog dialog;
    private PayeItem fFeed;
    private String lat = "00", lon = "00", imagesName = "";
    private ScrollView sv;
    private ProgressWheel pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);
        DaggerMainComponent.builder()
                .imageLoaderMoudle(new ImageLoaderMoudle(this))
                .build()
                .Inject(this);
        dialog = HSH.onProgress_Dialog(UpdatePostActivity.this, "لطفا شکیبا باشید ...");
        dialog.setCancelable(true);
        jCal = new Roozh(); // ایجاد یک نمونه از کلاس تبدیل تاریخ

        DeclareElements();

        gv.setExpanded(true);
        adapter = new GalleryAdapter(UpdatePostActivity.this, imageLoader);
        adapter.setMultiplePick(false);
        adapter.notifyDataSetChanged();
        adapter.addAll(map);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener((parent, view, position, id) -> {
            map.remove(position);
            adapter.notifyDataSetChanged();
            adapter.addAll(map);
            gv.setAdapter(adapter);

        });

        btn_register.setOnClickListener(this);
        btn_calender.setOnClickListener(this);
        btn_calender2.setOnClickListener(this);
        et_deadline.setOnClickListener(this);


        ll_parent = findViewById(R.id.ll_parent);
        fFeed = (PayeItem) getIntent().getExtras().getSerializable("feedItem");

        if (NetworkUtils.getConnectivity(UpdatePostActivity.this) != false)
            AdvertisementDetails();
        else
            HSH.showtoast(UpdatePostActivity.this, "خطا در اتصال به اینترنت");
    }

    public void AdvertisementDetails() {
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).GetPostDetailsUpdate(fFeed.getPostId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        sv.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        result = new JSONObject(response.body().string());
                        setMainDrawableColor(ll_parent);

                        //////////////////////////////////Images//////////////////////////////////
                        try {
                            if (!result.getString(getString(R.string.Latitude)).trim().equals("00"))
                                ((Button) findViewById(R.id.btn_location_map)).setText(HSH.getCompleteAddress(
                                        UpdatePostActivity.this,
                                        Double.parseDouble(result.getString(getString(R.string.Latitude)).trim()),
                                        Double.parseDouble(result.getString(getString(R.string.Longitude)).trim())));

                            String[] Images = result.getString(getString(R.string.Images)).split(",");
                            for (int i = 0; i < Images.length; i++) {
                                if (!Images[i].trim().equals("")) {
                                    CustomGallery item = new CustomGallery();
                                    item.sdcardPath = getString(R.string.url) + "Images/payebash/Thumbnail/" + Images[i] + ".jpg";
                                    map.add(item);
                                }
                            }
                            adapter.addAll(map);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }

                    } catch (Exception e) {
                    }
                else
                    AdvertisementDetails();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AdvertisementDetails();
            }
        });
    }

    //https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594#file-volleymultipartrequest-java
    private void saveRequest() {
        dialog.show();
        if (map.size() > 0)
            for (int i = 0; i < map.size(); i++) {
                String path = map.get(i).sdcardPath;
                if (!path.contains("http://")) {
                    saveImageEvent(i, String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    break;
                } else {
                    imagesName += path.substring(path.lastIndexOf("/") + 1, path.length() - 4) + ",";
                }
                if (i == map.size() - 1) {
                    params.put(getString(R.string.Images), imagesName);
                    UploadData();
                }
            }
        else
            UploadData();

    }

    private void saveImageEvent(final int pos, final String filename) {
        File file = new File(map.get(pos).sdcardPath);
        //final String path = map.get(pos).sdcardPath;
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(filename, file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).saveImageEvent(filePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        if (pos == map.size() - 1) {
                            imagesName += response.body().string() + ",";
                            params.put(getString(R.string.Images), imagesName);
                            UploadData();
                        } else /*if (!path.contains("http://"))*/ {
                            imagesName += response.body().string() + ",";
                            uploadNextImage(pos);
                        } /*else
                            imagesName += path.substring(path.lastIndexOf("/") + 1, path.length() - 4) + ",";*/
                    } catch (Exception e) {
                    }
                } else
                    saveImageEvent(pos, filename);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                saveImageEvent(pos, filename);
            }
        });
    }

    private void uploadNextImage(int pos) {
        pos++;
        if (map.size() > pos)
            saveImageEvent(pos, String.valueOf(Calendar.getInstance().getTimeInMillis()));
    }

    private void UploadData() {
        Map<String, RequestBody> params2 = new HashMap<>();
        for (String key : params.keySet())
            params2.put(key, RequestBody.create(okhttp3.MultipartBody.FORM, HSH.toEnglishNumber(params.get(key))));

        Call<ResponseBody> call2 =
                ApiClient.getClient().create(ApiInterface.class).saveRequest(params2);
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    dialog.setTitleText("بروزرسانی");
                    dialog.setContentText("درخواست شما با موفقیت ویرایش گردید و پس از بررسی منتشر خواهد شد")
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
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    saveRequest();
                } catch (Exception e) {
                }
            }
        });
    }

    private void DeclareElements() {

        gv = findViewById(R.id.gridview);
        compatSwitch = findViewById(R.id.compatSwitch);
        compatSwitchImmediate = findViewById(R.id.compatSwitchImmediate);
        sv = findViewById(R.id.sv);
        pb = findViewById(R.id.pb);
        etTitle = findViewById(R.id.et_title);
        etTag = findViewById(R.id.et_tag);
        etTime_start = findViewById(R.id.et_time_start);
        etTime_finish = findViewById(R.id.et_time_finish);
        et_cost = findViewById(R.id.et_cost);
        et_count = findViewById(R.id.et_count);
        et_deadline = findViewById(R.id.et_deadline);
        et_phone = findViewById(R.id.et_phone);
        et_link = findViewById(R.id.et_link);
        et_description = findViewById(R.id.et_description);
        btn_calender = findViewById(R.id.btn_calender);
        btn_calender2 = findViewById(R.id.btn_calender2);
        cd_phone = findViewById(R.id.cd_phone);
        cd_link = findViewById(R.id.cd_link);

        etTitle.addTextChangedListener(new addListenerOnTextChange(etTitle));
        etTag.addTextChangedListener(new addListenerOnTextChange(etTag));
        etTime_start.addTextChangedListener(new addListenerOnTextChange(etTime_start));
        etTime_finish.addTextChangedListener(new addListenerOnTextChange(etTime_finish));
        et_cost.addTextChangedListener(new addListenerOnTextChange(et_cost));
        et_count.addTextChangedListener(new addListenerOnTextChange(et_count));
        et_deadline.addTextChangedListener(new addListenerOnTextChange(et_deadline));
        et_phone.addTextChangedListener(new addListenerOnTextChange(et_phone));
        et_link.addTextChangedListener(new addListenerOnTextChange(et_link));
        et_description.addTextChangedListener(new addListenerOnTextChange(et_description));

        btn_subject_post = findViewById(R.id.btn_subject_post);
        btn_location_post = findViewById(R.id.btn_location_post);
        btn_register = findViewById(R.id.btn_register);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:
                if (resultCode == UpdatePostActivity.this.RESULT_OK) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(UpdatePostActivity.this);
                }
                break;
            case 1:
                if (resultCode == UpdatePostActivity.this.RESULT_OK && null != data.getData()) {
                    uri = data.getData();
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(UpdatePostActivity.this);
                }
                break;
            case 456:
                if (null != data) {
                    _data = data;
                    try {
                        final Cursor cr = Application.database.rawQuery("SELECT name from categories where id = '" + data.getStringExtra(getString(R.string.CategoryId)) + "'", null);
                        if (cr.moveToFirst()) {
                            btn_subject_post.setText(cr.getString(cr.getColumnIndex("name")));
                            btn_subject_post.setTag(data.getStringExtra(getString(R.string.CategoryId)));
                            params.put("Activity", data.getStringExtra(getString(R.string.CategoryId)));
                            params.put(getString(R.string.IsCommercial), cr.getString(cr.getColumnIndex("isCommercial")));
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }

        if (resultCode == -1 && (requestCode != 0 && requestCode != 1 && requestCode != 456) && null != data) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                showimg(new File(result.getUri().getPath()));
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {

            case R.id.et_deadline: {
                HSH.setTimeDate(UpdatePostActivity.this, et_deadline);
                break;
            }
            case R.id.btn_calender: {
                HSH.setTimeDate(UpdatePostActivity.this, etTime_start);
                break;
            }

            case R.id.btn_calender2: {
                HSH.setTimeDate(UpdatePostActivity.this, etTime_finish);
                break;
            }

            case R.id.btn_subject_post:
                HSH.selectSubject(UpdatePostActivity.this, btn_subject_post);
                //SelectUnit();
                break;

            case R.id.btn_location_post:
                HSH.selectLocation(UpdatePostActivity.this, 1, btn_location_post);
                break;

            case R.id.btn_location_map:
                setPoint();
                break;
            ////////////////////////////////////////////////////////////////////////////////////////
            case R.id.pick_images:
                new PermissionHandler().checkPermission(UpdatePostActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                    @Override
                    public void onPermissionGranted() {
                        if (map.size() < 5) {
                            File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath());
                            if (!dir.exists())
                                dir.mkdirs();

                            final SweetAlertDialog dialog = new SweetAlertDialog(UpdatePostActivity.this, SweetAlertDialog.NORMAL_TYPE);
                            dialog.setTitleText("انتخاب عکس");
                            dialog.setContentText("");
                            dialog.setConfirmText("دوربین");
                            dialog.setCancelText("انتخاب از گالری");
                            dialog.setConfirmClickListener(sDialog -> {
                                try {
                                    Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                        uri = Uri.fromFile(file);
                                    else
                                        uri = FileProvider.getUriForFile(UpdatePostActivity.this, getPackageName() + ".provider", file);
                                    camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                    camIntent.putExtra("return-data", true);
                                    startActivityForResult(camIntent, 0);
                                    dialog.dismissWithAnimation();
                                } catch (Exception e) {
                                }
                            });
                            dialog.setCancelClickListener(sweetAlertDialog -> {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult
                                        (Intent.createChooser
                                                (intent, HSH.setTypeFace(UpdatePostActivity.this, "انتخاب عکس")), 1);
                                dialog.dismissWithAnimation();
                            });
                            dialog.setCancelable(true);
                            HSH.dialog(dialog);
                        } else
                            HSH.showtoast(UpdatePostActivity.this, "حداکثر 5 عکس قابل بارگذاری می باشد");
                    }

                    @Override
                    public void onPermissionDenied() {
                        HSH.showtoast(UpdatePostActivity.this, "برای انتخاب عکس دسترسی  را صادر نمایید.");
                    }
                });
                break;

            case R.id.btn_register:
                //ارسال سوال
                try {
                    String title = etTitle.getText().toString().trim();
                    String description = et_description.getText().toString().trim();
                    //PushNotifictionHelper.sendPushNotification(PostRegisterActivity.this, "/topics/2-01");
                    if (btn_subject_post.getTag().toString().trim().equals("")) {
                        HSH.showtoast(UpdatePostActivity.this, "موضوع رویداد را وارد نمایید");
                        HSH.selectSubject(UpdatePostActivity.this, btn_subject_post);
                        btn_subject_post.requestFocus();
                    } else if (btn_location_post.getTag().toString().trim().equals("")) {
                        HSH.showtoast(UpdatePostActivity.this, "مکان رویداد را وارد نمایید");
                        HSH.selectLocation(UpdatePostActivity.this, 1, btn_location_post);
                        btn_location_post.requestFocus();
                    } else if (title.equals("") || title.length() < 3) {
                        error(etTitle, "عنوان رویداد را وارد نمایید(حداقل 3 حرف)");
                    } else if (etTime_start.getText().toString().trim().equals(""))
                        error(etTime_start, "زمان شروع رویداد را وارد نمایید");
                    else if (etTime_finish.getText().toString().trim().equals(""))
                        error(etTime_finish, "زمان پایان رویداد را وارد نمایید");
                    else if (et_cost.getText().toString().trim().equals(""))
                        error(et_cost, "هزینه رویداد را وارد نمایید");
                    else if (et_count.getText().toString().trim().equals(""))
                        error(et_count, "تعداد پایه رویداد را وارد نمایید");
                    else if (et_deadline.getText().toString().trim().equals(""))
                        error(et_deadline, "مهلت اتمام رویداد را وارد نمایید");
                    else if (description.equals("") || description.length() < 10)
                        error(et_description, "توضیحات رویداد را وارد نمایید(حداقل 10 حرف)");
                    else {
                        params.put(getString(R.string.PostId), fFeed.PostId);
                        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                        params.put(getString(R.string.Subject), btn_subject_post.getTag().toString());
                        params.put(getString(R.string.city), btn_location_post.getTag().toString());
                        params.put(getString(R.string.IsWoman), String.valueOf(compatSwitch.isChecked()));
                        params.put(getString(R.string.IsImmediate), String.valueOf(compatSwitchImmediate.isChecked()));
                        params.put(getString(R.string.Title), etTitle.getText().toString().trim());
                        params.put(getString(R.string.Tag), etTag.getText().toString().trim());
                        params.put(getString(R.string.PhoneNumber), et_phone.getText().toString().trim());
                        params.put(getString(R.string.Link), et_link.getText().toString().trim());
                        params.put(getString(R.string.StartDate), etTime_start.getText().toString().trim());
                        params.put(getString(R.string.finishDate), etTime_finish.getText().toString().trim());
                        params.put(getString(R.string.Deadline), convertToMiladi(et_deadline));
                        try {
                            params.put(getString(R.string.Longitude), lon);
                            params.put(getString(R.string.Latitude), lat);
                        } catch (Exception e) {
                        }
                        params.put(getString(R.string.Cost), et_cost.getText().toString().trim().toString());
                        params.put(getString(R.string.NumberFollowers), et_count.getText().toString().trim());
                        params.put(getString(R.string.Description), et_description.getText().toString().trim());
                        if (HSH.isNetworkConnection(UpdatePostActivity.this))
                            saveRequest();
                        else
                            HSH.showtoast(UpdatePostActivity.this, "خطا در اتصال به اینترنت");
                    }
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }

    private void error(EditText et, String s) {
        et.setError(HSH.setTypeFace(UpdatePostActivity.this, s));
        et.requestFocus();
    }

    private void showimg(File fils) {
        CustomGallery item = new CustomGallery();
        item.sdcardPath = fils.getAbsolutePath();
        map.add(item);
        adapter.addAll(map);
        gv.setAdapter(adapter);
    }

    private void setPoint() {
        final Dialog dialog1 = new Dialog(UpdatePostActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_maps);
        HSH.dialog(dialog1);

        try {
            switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UpdatePostActivity.this)) {
                case ConnectionResult.SUCCESS:
                    try {
                        ((SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map)).getMapAsync(googleMap -> {
                            LatLng latLng = new LatLng(35.70555520, 51.38485568);

                            CameraPosition cameraPosition = new CameraPosition
                                    .Builder()
                                    .target(latLng)
                                    .zoom(10)
                                    .build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            googleMap.setOnMapLongClickListener(point -> {
                                try {
                                    if (googleMap.getCameraPosition().zoom < 16)
                                        HSH.showtoast(UpdatePostActivity.this, "لطفا بیشتر زوم کنید");

                                    else {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(UpdatePostActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                        dialog.setTitleText("تنظیم مکان رویداد");
                                        dialog.setContentText("می توانید روی مکان نما فشار داده و محل آن را بیشتر تنظیم کنید");
                                        dialog.setConfirmText("تایید و ادامه");
                                        dialog.setCancelText("تنظیم مجدد");
                                        dialog.setConfirmClickListener(sDialog -> {
                                            googleMap.clear();
                                            lat = String.valueOf(point.latitude);
                                            lon = String.valueOf(point.longitude);
                                            ((Button) findViewById(R.id.btn_location_map)).setText(HSH.getCompleteAddress(UpdatePostActivity.this, point.latitude, point.longitude));
                                            dialog1.dismiss();
                                            dialog.dismissWithAnimation();
                                        });
                                        dialog.setCancelClickListener(sweetAlertDialog -> dialog.dismissWithAnimation());
                                        dialog.setCancelable(true);
                                        HSH.dialog(dialog);
                                    }
                                } catch (Exception e) {
                                }
                            });

                        });
                    } catch (Exception e) {
                    }

                    break;
                case ConnectionResult.SERVICE_MISSING: {
                    HSH.showtoast(getApplicationContext(), "گوگل پلی سرویس در دستگاه شما نصب نمی باشد.");
                }
                break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    HSH.showtoast(getApplicationContext(), "گوگل پلی سرویس باید آپدیت شود.");
                    break;
                default:
            }
            dialog1.show();
        } catch (Exception e) {
        }

    }

    private void setMainDrawableColor(LinearLayout layout) {
        int a = layout.getChildCount();
        for (int i = 0; i < a; i++) {
            if (layout.getChildAt(i) instanceof CardView)
                for (int j = 0; j < 1; j++) {
                    View v = ((CardView) (layout.getChildAt(i))).getChildAt(j);
                    if (v instanceof RelativeLayout)
                        for (int b = 0; b < 1; b++) {
                            v = ((RelativeLayout) v).getChildAt(b);
                            if (v instanceof TextInputLayout)
                                for (int c = 0; c < 1; c++) {
                                    v = ((TextInputLayout) v).getChildAt(c);
                                    if (v instanceof FrameLayout) {
                                        try {
                                            ((EditText) (((FrameLayout) v).getChildAt(0))).setText(HSH.toPersianNumber(result.getString(((FrameLayout) v).getChildAt(0).getTag().toString().trim())));
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                        }
                }
            else if (layout.getChildAt(i) instanceof Button) {
                View v = layout.getChildAt(i);
                if (v instanceof Button) {
                    try {
                        if (v.getTag().toString().equals(getString(R.string.Subject))) {
                            Cursor cr = Application.database.rawQuery("SELECT * from categories where id = '" + result.getString(v.getTag().toString()).trim() + "' ", null);
                            cr.moveToFirst();
                            ((Button) v).setText(cr.getString(cr.getColumnIndex("name")));
                            v.setTag(cr.getString(cr.getColumnIndex("id")));

                            if (cr.getString(cr.getColumnIndex(getString(R.string.addedFeild))).contains(getString(R.string.mobile)))
                                cd_phone.setVisibility(View.VISIBLE);
                            else
                                cd_phone.setVisibility(View.GONE);

                            if (cr.getString(cr.getColumnIndex(getString(R.string.addedFeild))).contains(getString(R.string.Link)))
                                cd_link.setVisibility(View.VISIBLE);
                            else
                                cd_link.setVisibility(View.GONE);

                        } else if (v.getTag().toString().equals(getString(R.string.city))) {
                            Cursor cr = Application.database.rawQuery("SELECT id,StateCity from Citys where id = '" + fFeed.getCity() + "' ", null);
                            cr.moveToFirst();
                            ((Button) v).setText(cr.getString(cr.getColumnIndex("StateCity")));
                            v.setTag(cr.getString(cr.getColumnIndex("id")));
                        } else if (v.getTag().toString().equals(getString(R.string.IsWoman))) {
                            compatSwitch.setChecked(fFeed.getIsWoman());
                        } else if (v.getTag().toString().equals(getString(R.string.IsImmediate))) {
                            compatSwitchImmediate.setChecked(fFeed.getIsImmediate());
                        }
                    } catch (Exception e) {
                    }
                }
            }

        }
    }

    private String convertToMiladi(EditText et) {
        String temp[] = et.getText().toString().trim().split("/");
        jCal.PersianToGregorian(Integer.parseInt(temp[0]) + 1300, Integer.parseInt(temp[1]), Integer.parseInt(temp[2].substring(0, 2)));
        String s = jCal.toString();
        return s + " " + et.getText().toString().split(" ")[1];
    }

    public class addListenerOnTextChange implements TextWatcher {
        EditText mEdittextview;

        public addListenerOnTextChange(EditText edittextview) {
            super();
            this.mEdittextview = edittextview;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEdittextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
    }
}
