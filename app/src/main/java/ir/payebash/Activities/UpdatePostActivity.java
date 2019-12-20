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
    private EditText etTitle, etTag, etTimeStart, etPhone, etLink,
            etTimeFinish, etCost, etCount, etDeadline, etDescription;
    private Button btnSubjectPost, btnLocationPost, btnRegister;
    private ImageButton btnCalender, btnCalender2;
    private SwitchCompat compatSwitch, compatSwitchImmediate;
    private Map<String, String> params = new HashMap<>();
    private Roozh jCal;
    private SweetAlertDialog dialog;
    private PayeItem fFeed;
    private String lat = "00", lon = "00", imagesName = "";
    private ScrollView sv;
    private ProgressWheel pb;

    private void DeclareElements() {
        ll_parent = findViewById(R.id.ll_parent);
        gv = findViewById(R.id.gridview);
        compatSwitch = findViewById(R.id.compatSwitch);
        compatSwitchImmediate = findViewById(R.id.compatSwitchImmediate);
        sv = findViewById(R.id.sv);
        pb = findViewById(R.id.pb);
        etTitle = findViewById(R.id.et_title);
        etTag = findViewById(R.id.et_tag);
        etTimeStart = findViewById(R.id.et_time_start);
        etTimeFinish = findViewById(R.id.et_time_finish);
        etCost = findViewById(R.id.et_cost);
        etCount = findViewById(R.id.et_count);
        etDeadline = findViewById(R.id.et_deadline);
        etPhone = findViewById(R.id.et_phone);
        etLink = findViewById(R.id.et_link);
        etDescription = findViewById(R.id.et_description);
        btnCalender = findViewById(R.id.btn_calender);
        btnCalender2 = findViewById(R.id.btn_calender2);
        cd_phone = findViewById(R.id.cd_phone);
        cd_link = findViewById(R.id.cd_link);

        etTitle.addTextChangedListener(new addListenerOnTextChange(etTitle));
        etTag.addTextChangedListener(new addListenerOnTextChange(etTag));
        etTimeStart.addTextChangedListener(new addListenerOnTextChange(etTimeStart));
        etTimeFinish.addTextChangedListener(new addListenerOnTextChange(etTimeFinish));
        etCost.addTextChangedListener(new addListenerOnTextChange(etCost));
        etCount.addTextChangedListener(new addListenerOnTextChange(etCount));
        etDeadline.addTextChangedListener(new addListenerOnTextChange(etDeadline));
        etPhone.addTextChangedListener(new addListenerOnTextChange(etPhone));
        etLink.addTextChangedListener(new addListenerOnTextChange(etLink));
        etDescription.addTextChangedListener(new addListenerOnTextChange(etDescription));

        btnSubjectPost = findViewById(R.id.btn_subject_post);
        btnLocationPost = findViewById(R.id.btn_location_post);
        btnRegister = findViewById(R.id.btn_register);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(this);
        btnCalender.setOnClickListener(this);
        btnCalender2.setOnClickListener(this);
        etDeadline.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);
        Application.getComponent().Inject(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    try {
                        final Cursor cr = Application.database.rawQuery("SELECT name from categories where id = '" + data.getStringExtra(getString(R.string.CategoryId)) + "'", null);
                        cr.close();
                        if (cr.moveToFirst()) {
                            btnSubjectPost.setText(cr.getString(cr.getColumnIndex("name")));
                            btnSubjectPost.setTag(data.getStringExtra(getString(R.string.CategoryId)));
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
                HSH.setTimeDate(UpdatePostActivity.this, etDeadline);
                break;
            }
            case R.id.btn_calender: {
                HSH.setTimeDate(UpdatePostActivity.this, etTimeStart);
                break;
            }

            case R.id.btn_calender2: {
                HSH.setTimeDate(UpdatePostActivity.this, etTimeFinish);
                break;
            }

            case R.id.btn_subject_post:
                HSH.selectSubject(UpdatePostActivity.this, btnSubjectPost);
                //SelectUnit();
                break;

            case R.id.btn_location_post:
                HSH.selectLocation(UpdatePostActivity.this, 1, btnLocationPost);
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
                    String description = etDescription.getText().toString().trim();
                    //PushNotifictionHelper.sendPushNotification(PostRegisterActivity.this, "/topics/2-01");
                    if (btnSubjectPost.getTag().toString().trim().equals("")) {
                        HSH.showtoast(UpdatePostActivity.this, "موضوع رویداد را وارد نمایید");
                        HSH.selectSubject(UpdatePostActivity.this, btnSubjectPost);
                        btnSubjectPost.requestFocus();
                    } else if (btnLocationPost.getTag().toString().trim().equals("")) {
                        HSH.showtoast(UpdatePostActivity.this, "مکان رویداد را وارد نمایید");
                        HSH.selectLocation(UpdatePostActivity.this, 1, btnLocationPost);
                        btnLocationPost.requestFocus();
                    } else if (title.equals("") || title.length() < 3) {
                        error(etTitle, "عنوان رویداد را وارد نمایید(حداقل 3 حرف)");
                    } else if (etTimeStart.getText().toString().trim().equals(""))
                        error(etTimeStart, "زمان شروع رویداد را وارد نمایید");
                    else if (etTimeFinish.getText().toString().trim().equals(""))
                        error(etTimeFinish, "زمان پایان رویداد را وارد نمایید");
                    else if (etCost.getText().toString().trim().equals(""))
                        error(etCost, "هزینه رویداد را وارد نمایید");
                    else if (etCount.getText().toString().trim().equals(""))
                        error(etCount, "تعداد پایه رویداد را وارد نمایید");
                    else if (etDeadline.getText().toString().trim().equals(""))
                        error(etDeadline, "مهلت اتمام رویداد را وارد نمایید");
                    else if (description.equals("") || description.length() < 10)
                        error(etDescription, "توضیحات رویداد را وارد نمایید(حداقل 10 حرف)");
                    else {
                        params.put(getString(R.string.PostId), fFeed.getPostId());
                        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                        params.put(getString(R.string.Subject), btnSubjectPost.getTag().toString());
                        params.put(getString(R.string.city), btnLocationPost.getTag().toString());
                        params.put(getString(R.string.IsWoman), String.valueOf(compatSwitch.isChecked()));
                        params.put(getString(R.string.IsImmediate), String.valueOf(compatSwitchImmediate.isChecked()));
                        params.put(getString(R.string.Title), etTitle.getText().toString().trim());
                        params.put(getString(R.string.Tag), etTag.getText().toString().trim());
                        params.put(getString(R.string.PhoneNumber), etPhone.getText().toString().trim());
                        params.put(getString(R.string.Link), etLink.getText().toString().trim());
                        params.put(getString(R.string.StartDate), etTimeStart.getText().toString().trim());
                        params.put(getString(R.string.finishDate), etTimeFinish.getText().toString().trim());
                        params.put(getString(R.string.Deadline), convertToMiladi(etDeadline));
                        try {
                            params.put(getString(R.string.Longitude), lon);
                            params.put(getString(R.string.Latitude), lat);
                        } catch (Exception e) {
                        }
                        params.put(getString(R.string.Cost), etCost.getText().toString().trim().toString());
                        params.put(getString(R.string.NumberFollowers), etCount.getText().toString().trim());
                        params.put(getString(R.string.Description), etDescription.getText().toString().trim());
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
                            cr.close();
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
                            cr.close();
                            cr.moveToFirst();
                            ((Button) v).setText(cr.getString(cr.getColumnIndex("StateCity")));
                            v.setTag(cr.getString(cr.getColumnIndex("id")));
                        } else if (v.getTag().toString().equals(getString(R.string.IsWoman))) {
                            compatSwitch.setChecked(fFeed.IsWoman());
                        } else if (v.getTag().toString().equals(getString(R.string.IsImmediate))) {
                            compatSwitchImmediate.setChecked(fFeed.IsImmediate());
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
