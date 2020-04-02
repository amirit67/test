package ir.payebash.activities;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
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
import ir.payebash.adapters.GalleryAdapter;
import ir.payebash.Application;
import ir.payebash.classes.BaseActivity;
import ir.payebash.classes.HSH;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.classes.PermissionHandler;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.models.CustomGallery;
import ir.payebash.moudle.ExpandableHeightGridView;
import ir.payebash.moudle.Roozh;
import ir.payebash.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PostRegisterActivity extends BaseActivity implements View.OnClickListener {

    public ArrayList<CustomGallery> map = new ArrayList<>();
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Inject
    ImageLoader imageLoader;
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
    private Map<String, String> DataParams = new HashMap<>();
    private Roozh jCal;
    private SweetAlertDialog dialog;
    private String lat = "00", imagesName = "";
    private String lon = "00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register3);
        Application.getComponent().Inject(this);
        dialog = HSH.onProgress_Dialog(PostRegisterActivity.this, "لطفا منتظر بمانید ...");
        jCal = new Roozh();

        DeclareElements();

        gv.setExpanded(true);
        adapter = new GalleryAdapter(PostRegisterActivity.this, imageLoader);
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
    }

    //https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594#file-volleymultipartrequest-java
    private void saveRequest() {
        dialog.show();
        if (map.size() > 0)
            saveImageEvent(0, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        else
            UploadData();
    }

    private void saveImageEvent(final int pos, final String filename) {
        File file = new File(map.get(pos).sdcardPath);
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
                            DataParams.put(getString(R.string.Images), imagesName);
                            UploadData();
                        } else {
                            imagesName += response.body().string() + ",";
                            uploadNextImage(pos);
                        }
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
        for (String key : DataParams.keySet())
            params2.put(key, RequestBody.create(MultipartBody.FORM, HSH.toEnglishNumber(DataParams.get(key))));

        /*Call<ResponseBody> call2 =
                ApiClient.getClient().create(ApiInterface.class).saveRequest(params2);
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    try {
                        String query = "INSERT INTO RecentVisit(Id,IsMine) VALUES " +
                                "('" + obj.getString("postId") + "' , 'true')";
                        Application.database.execSQL(query);
                    } catch (Exception e) {
                    }
                    dialog.setTitleText("");
                    dialog.setContentText("رویداد شما با موفقیت ثبت شد و پس از بررسی منتشر خواهد شد")
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
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    saveRequest();
                } catch (Exception e) {
                }
            }
        });*/
    }

    private void DeclareElements() {

        gv = findViewById(R.id.gridview);
        compatSwitch = findViewById(R.id.compatSwitch);
        compatSwitchImmediate = findViewById(R.id.compatSwitchImmediate);
        compatSwitchImmediate.setText("فوری (شامل هزینه ی " + Application.preferences.getInt("Feepayable", 1000) + " تومان)");
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

        (findViewById(R.id.img_back)).setOnClickListener(v -> finish());

    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {

            case R.id.et_deadline: {
                //HSH.setTimeDate(PostRegisterActivity.this, et_deadline);
                break;
            }
            case R.id.btn_calender: {
                //HSH.setTimeDate(PostRegisterActivity.this, etTime_start);
                break;
            }

            case R.id.btn_calender2: {
                //HSH.setTimeDate(PostRegisterActivity.this, etTime_finish);
                break;
            }

            case R.id.btn_subject_post:
                HSH.selectSubject(PostRegisterActivity.this, btn_subject_post);
                //SelectUnit();
                break;

            case R.id.btn_location_post:
                //HSH.selectLocation(PostRegisterActivity.this, 1, btn_location_post);
                break;

            case R.id.btn_location_map:
                try {
                    setPoint();
                } catch (Exception e) {
                }
                break;
            ////////////////////////////////////////////////////////////////////////////////////////
            case R.id.pick_images:
                new PermissionHandler().checkPermission(PostRegisterActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                    @Override
                    public void onPermissionGranted() {
                        if (map.size() < 5) {
                            File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath());
                            if (!dir.exists())
                                dir.mkdirs();

                            final SweetAlertDialog dialog = new SweetAlertDialog(PostRegisterActivity.this, SweetAlertDialog.NORMAL_TYPE);
                            dialog.setTitleText("انتخاب عکس");
                            dialog.setContentText("");
                            dialog.setConfirmText("دوربین");
                            dialog.setCancelText("انتخاب از گالری");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    try {
                                        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                            uri = Uri.fromFile(file);
                                        else
                                            uri = FileProvider.getUriForFile(PostRegisterActivity.this, getPackageName() + ".provider", file);
                                        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                        camIntent.putExtra("return-data", true);
                                        startActivityForResult(camIntent, 0);
                                        dialog.dismissWithAnimation();
                                    } catch (Exception e) {
                                    }
                                }
                            });
                            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult
                                            (Intent.createChooser
                                                    (intent, HSH.setTypeFace(PostRegisterActivity.this, "انتخاب عکس")), 1);
                                    dialog.dismissWithAnimation();
                                }
                            });
                            dialog.setCancelable(true);
                            HSH.dialog(dialog);
                        } else
                            HSH.showtoast(PostRegisterActivity.this, "حداکثر 5 عکس قابل بارگذاری می باشد");
                    }

                    @Override
                    public void onPermissionDenied() {
                        HSH.showtoast(PostRegisterActivity.this, "برای انتخاب عکس دسترسی  را صادر نمایید.");
                    }
                });
                break;

            case R.id.btn_register:
                //ارسال سوال
                try {
                    String title = etTitle.getText().toString().trim();
                    String description = et_description.getText().toString().trim();
                    if (btn_subject_post.getTag().toString().trim().equals("")) {
                        HSH.showtoast(PostRegisterActivity.this, "موضوع رویداد را وارد نمایید");
                        HSH.selectSubject(PostRegisterActivity.this, btn_subject_post);
                        btn_subject_post.requestFocus();
                    } else if (btn_location_post.getTag().toString().trim().equals("")) {
                        HSH.showtoast(PostRegisterActivity.this, "مکان رویداد را وارد نمایید");
                        //HSH.selectLocation(PostRegisterActivity.this, 1, btn_location_post);
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
                        DataParams.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                        DataParams.put(getString(R.string.Subject), btn_subject_post.getTag().toString());
                        DataParams.put(getString(R.string.city), btn_location_post.getTag().toString());
                        DataParams.put(getString(R.string.IsWoman), String.valueOf(compatSwitch.isChecked()));
                        DataParams.put(getString(R.string.IsImmediate), String.valueOf(compatSwitchImmediate.isChecked()));
                        DataParams.put(getString(R.string.Title), etTitle.getText().toString().trim());
                        DataParams.put(getString(R.string.Tag), etTag.getText().toString().trim());
                        DataParams.put(getString(R.string.StartDate), etTime_start.getText().toString().trim());
                        DataParams.put(getString(R.string.finishDate), etTime_finish.getText().toString().trim());
                        DataParams.put(getString(R.string.Deadline), convertToMiladi(et_deadline));
                        DataParams.put(getString(R.string.PhoneNumber), et_phone.getText().toString().trim());
                        DataParams.put(getString(R.string.Link), et_link.getText().toString().trim());
                        DataParams.put(getString(R.string.Tag), etTag.getText().toString().trim());
                        try {
                            DataParams.put(getString(R.string.Longitude), lon);
                            DataParams.put(getString(R.string.Latitude), lat);
                        } catch (Exception e) {
                        }
                        DataParams.put(getString(R.string.Cost), et_cost.getText().toString().trim().toString());
                        DataParams.put(getString(R.string.NumberFollowers), et_count.getText().toString().trim());
                        DataParams.put(getString(R.string.Description), et_description.getText().toString().trim());
                        if (NetworkUtils.getConnectivity(PostRegisterActivity.this) != false)
                            saveRequest();
                        else HSH.showtoast(PostRegisterActivity.this, "خطا در اتصال به اینترنت");


                    }
                } catch (Exception e) {
                }

                break;

            default:
                break;
        }
    }

    private void error(EditText et, String s) {
        et.setError(HSH.setTypeFace(PostRegisterActivity.this, s));
        et.requestFocus();
    }

    private String convertToMiladi(EditText et) {
        String temp[] = et.getText().toString().trim().split("/");
        jCal.PersianToGregorian(Integer.parseInt(temp[0]) + 1300, Integer.parseInt(temp[1]), Integer.parseInt(temp[2].substring(0, 2)));
        String s = jCal.toString();
        return s + " " + et.getText().toString().split(" ")[1];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == PostRegisterActivity.this.RESULT_OK) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PostRegisterActivity.this);
                }
                break;
            case 1:
                if (resultCode == PostRegisterActivity.this.RESULT_OK && null != data.getData()) {
                    uri = data.getData();
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PostRegisterActivity.this);
                }
                break;
            case 456:
                if (null != data) {
                    _data = data;
                    try {
                        final Cursor cr = Application.database.rawQuery("SELECT * from categories where id = '" + data.getStringExtra(getString(R.string.CategoryId)) + "'", null);
                        cr.close();
                        if (cr.moveToFirst()) {
                            btn_subject_post.setText(cr.getString(cr.getColumnIndex("name")));
                            btn_subject_post.setTag(data.getStringExtra(getString(R.string.CategoryId)));
                            DataParams.put("Activity", data.getStringExtra(getString(R.string.CategoryId)));
                            DataParams.put(getString(R.string.IsCommercial), cr.getString(cr.getColumnIndex("isCommercial")));

                            if (cr.getString(cr.getColumnIndex(getString(R.string.addedFeild))).contains(getString(R.string.mobile)))
                                cd_phone.setVisibility(View.VISIBLE);
                            else
                                cd_phone.setVisibility(View.GONE);

                            if (cr.getString(cr.getColumnIndex(getString(R.string.addedFeild))).contains(getString(R.string.Link)))
                                cd_link.setVisibility(View.VISIBLE);
                            else
                                cd_link.setVisibility(View.GONE);
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

    private void showimg(File fils) {
        CustomGallery item = new CustomGallery();
        item.sdcardPath = fils.getAbsolutePath();
        map.add(item);
        adapter.addAll(map);
        gv.setAdapter(adapter);
    }

    private void setPoint() {
        final Dialog dialog1 = new Dialog(PostRegisterActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_maps);
        HSH.dialog(dialog1);
        try {

            switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PostRegisterActivity.this)) {
                case ConnectionResult.SUCCESS:
                    try {

                        ((SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(final GoogleMap googleMap) {
                                LatLng latLng = new LatLng(35.70555520, 51.38485568);

                                CameraPosition cameraPosition = new CameraPosition
                                        .Builder()
                                        .target(latLng)
                                        .zoom(10)
                                        .build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                    @Override
                                    public void onMapLongClick(final LatLng point) {
                                        try {
                                            if (googleMap.getCameraPosition().zoom < 16)
                                                HSH.showtoast(PostRegisterActivity.this, "لطفا بیشتر زوم کنید");
                                            else {
                                                final SweetAlertDialog dialog = new SweetAlertDialog(PostRegisterActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                                dialog.setTitleText("تنظیم مکان رویداد");
                                                dialog.setContentText("می توانید روی مکان نما فشار داده و محل آن را بیشتر تنظیم کنید");
                                                dialog.setConfirmText("تایید و ادامه");
                                                dialog.setCancelText("تنظیم مجدد");
                                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        googleMap.clear();
                                                        lat = String.valueOf(point.latitude);
                                                        lon = String.valueOf(point.longitude);
                                                        ((Button) findViewById(R.id.btn_location_map)).setText(HSH.getCompleteAddress(PostRegisterActivity.this, point.latitude, point.longitude));
                                                        dialog1.dismiss();
                                                        dialog.dismissWithAnimation();
                                                    }
                                                });
                                                dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        dialog.dismissWithAnimation();
                                                    }
                                                });
                                                dialog.setCancelable(true);
                                                HSH.dialog(dialog);
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                            }
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

    @Override
    protected void onResume() {
        super.onResume();
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