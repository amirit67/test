package ir.payebash.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ir.payebash.Application;
import ir.payebash.R;
import ir.payebash.adapters.ImagesAdapter;
import ir.payebash.classes.BaseFragment;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.databinding.ActivityPostRegisterBinding;
import ir.payebash.fragments.registerEvents.MobileConfirmStep1Fragment;
import ir.payebash.models.CustomGallery;
import ir.payebash.models.event.RegisterEventResponseModel;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.remote.repository.RemoteRepository;
import ir.payebash.utils.FragmentStack;
import ir.payebash.utils.SwitchButton;
import ir.payebash.utils.hashtaghelper.HashTagHelper;
import ir.payebash.utils.toggleSwitch.ToggleSwitch;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class PostRegister2Activity extends BaseFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String ARG_PARAM1 = "param1";
    private EventDetailsModel eventDetailsModel;
    private Uri uri;
    private Integer price = 0;
    public ArrayList<CustomGallery> map = new ArrayList<>();
    private ImagesAdapter adapter;

    private HashTagHelper mTextHashTagHelper;
    private ToggleSwitch toggleSwitch;
    private RecyclerView rv;
    private EditText etCost, etTitle, etDescription, etNumberOfFollowers;
    private TextView tvTimeToJoin;
    public static PostRegister2Activity fragment = null;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private View rootView = null;

    public static PostRegister2Activity newInstance(EventDetailsModel eventDetailsModel) {
        if (fragment == null)
            fragment = new PostRegister2Activity();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, eventDetailsModel);
        fragment.setArguments(args);
        return fragment;
    }

    public void initViews() {
        rootView.findViewById(R.id.img_back).setOnClickListener(this::onClick);
        rootView.findViewById(R.id.bt_register).setOnClickListener(this::onClick);
        rv = rootView.findViewById(R.id.rv_images);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));

        etTitle = rootView.findViewById(R.id.et_title);
        etDescription = rootView.findViewById(R.id.et_description);
        tvTimeToJoin = rootView.findViewById(R.id.tv_time_to_join);
        tvTimeToJoin.setOnClickListener(this::onClick);
        etNumberOfFollowers = rootView.findViewById(R.id.et_number_people);
        etCost = rootView.findViewById(R.id.et_cost);
        etCost.addTextChangedListener(textWatcher);
        toggleSwitch = rootView.findViewById(R.id.ts_cost);
        //toggleSwitch.setCheckedPosition(2);
        toggleSwitch.setOnChangeListener(position -> {
            eventDetailsModel.setCostType(position);
            if (position == 0)
                HSH.display(getActivity(), etCost);
            else
                HSH.hide(getActivity(), etCost);
        });

        ((SwitchButton) rootView.findViewById(R.id.switch1)).setOnCheckedChangeListener((view, isChecked) -> {
            eventDetailsModel.setWoman(isChecked);
        });

        etTitle.setOnFocusChangeListener(this::onFocusChange);
        etDescription.setOnFocusChangeListener(this::onFocusChange);
        tvTimeToJoin.setOnFocusChangeListener(this::onFocusChange);
        etNumberOfFollowers.setOnFocusChangeListener(this::onFocusChange);
        //https://github.com/danylovolokh/HashTagHelper
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), null);
        mTextHashTagHelper.handle(etDescription);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventDetailsModel = (EventDetailsModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            ActivityPostRegisterBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.activity_post_register, container, false);
            rootView = binding.getRoot();
            binding.setItemEventRegister(eventDetailsModel);

            initViews();
            adapter = new ImagesAdapter(getActivity(), position -> ActivityCompat.requestPermissions(getActivity(), permissions, 123));
            rv.setAdapter(adapter);
        }
        return rootView;
        //initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_back)
            getFragmentManager().popBackStack();
        else if (v.getId() == R.id.bt_register) {
            try {
                if (eventDetailsModel.getTitle().length() < 6) {
                    etTitle.requestFocus();
                    rootView.findViewById(R.id.et_title).setBackground(getResources().getDrawable(R.drawable.back_error));
                } else if (eventDetailsModel.getDescription().length() < 11) {
                    etDescription.requestFocus();
                    etDescription.setBackground(getResources().getDrawable(R.drawable.back_error));
                } else if (TextUtils.isEmpty(eventDetailsModel.getNumberFollowers())) {
                    etNumberOfFollowers.requestFocus();
                    etNumberOfFollowers.setBackground(getResources().getDrawable(R.drawable.back_error));
                } else if (TextUtils.isEmpty(eventDetailsModel.getTimeToJoin())) {
                    tvTimeToJoin.requestFocus();
                    tvTimeToJoin.setBackground(getResources().getDrawable(R.drawable.back_error));
                } else if (null == toggleSwitch.getCheckedPosition()) {
                    toggleSwitch.setCheckedBackgroundColor(Color.parseColor("#FFE5EC"));
                    toggleSwitch.setUncheckedBackgroundColor(Color.parseColor("#FFE5EC"));
                } else if (toggleSwitch.getCheckedPosition() == 0 &&
                        TextUtils.isEmpty(etCost.getText().toString()))
                    etCost.setBackground(getResources().getDrawable(R.drawable.back_error));
                else {
                    long startDate = Long.parseLong(HSH.toEnglishNumber(eventDetailsModel.getStartDate().replace("/", "").replace(":", "").replace(" ", "")));
                    long endDate = Long.parseLong(HSH.toEnglishNumber(eventDetailsModel.getEndDate().replace("/", "").replace(":", "").replace(" ", "")));
                    long timeToJoin = Long.parseLong(HSH.toEnglishNumber(tvTimeToJoin.getText().toString().replace("/", "").replace(":", "").replace(" ", "")));
                    if (startDate > timeToJoin) {
                        HSH.showtoast(getActivity(), "مهلت پیوستن باید بعد از تاریخ شروع باشد");
                        tvTimeToJoin.setBackground(getResources().getDrawable(R.drawable.back_error));
                    } else if (endDate < timeToJoin) {
                        HSH.showtoast(getActivity(), "مهلت پیوستن باید قبل از تاریخ پایان باشد");
                        tvTimeToJoin.setBackground(getResources().getDrawable(R.drawable.back_error));
                    } else if (NetworkUtils.getConnectivity(getActivity()) != false)
                        saveEvent();
                    else HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");
                }
            } catch (Exception e) {
                HSH.showtoast(getActivity(), e.getMessage());
            }

            /*FragmentStack stack = new FragmentStack(getActivity(), getSupportFragmentManager(), R.id.frame);
            stack.replace(MobileConfirmStep1Fragment.newInstance(eventDetailsModel));*/
        } else if (v.getId() == R.id.tv_time_to_join) {
            eventDetailsModel.setTimeToJoin(HSH.setTimeDate(getActivity(), tvTimeToJoin, "مهلت پیوستن"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (requestCode == 100) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, 123);
                }
                break;

            case 0:
                if (resultCode == getActivity().RESULT_OK) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity());
                }
                break;
            case 1:
                if (resultCode == getActivity().RESULT_OK && null != data.getData()) {
                    uri = data.getData();
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity());
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

    private void saveEvent() {
        //closeKeyboard();
        MultipartBody.Part[] files = new MultipartBody.Part[adapter.getData().size()];
        ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        if (adapter.getData().size() > 0) {

            int currentAPIVersion = Build.VERSION.SDK_INT;
            mProgressDialog.setMessage("در حال ارسال ...");
            if (currentAPIVersion < Build.VERSION_CODES.O)
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            for (int i = 0; i < adapter.getData().size(); i++) {
                String path = adapter.getData().get(i).sdcardPath;
                File file = new File(path);
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
                files[i] = MultipartBody.Part.createFormData(path.substring(path.lastIndexOf("/") + 1), file.getName(), surveyBody);
            }
        }

        Map<String, RequestBody> params2 = new HashMap<>();
        Map<String, Object> map = parameters(eventDetailsModel);
        eventDetailsModel.setTimeToJoin(HSH.toEnglishNumber(HSH.converttomiladi(eventDetailsModel.getTimeToJoin())));
        map.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params2.put(entry.getKey(), RequestBody.create(MultipartBody.FORM,
                    null != entry.getValue() ? entry.getValue().toString() : ""));
        }

        new RemoteRepository().RegisterEvent(files, params2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<RegisterEventResponseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RegisterEventResponseModel response) {
                        mProgressDialog.dismiss();
                        if (response.getMobileConfirmed()) {
                            final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setContentText("رویداد شما با موفقیت ثبت شد و پس از بررسی منتشر خواهد شد");
                            dialog.setConfirmText("باشه");
                            dialog.show();

                            dialog.showCancelButton(false);
                            dialog.setCancelable(false);
                            dialog.setConfirmClickListener(sweetAlertDialog -> {
                                Intent result = new Intent();
                                result.putExtra("data", response);
                                getActivity().setResult(Activity.RESULT_OK, result);
                                //finish();
                            });
                        } else {
                            FragmentStack fragmentStack = new FragmentStack(getActivity(), getFragmentManager(), R.id.main_frame);
                            fragmentStack.replace(MobileConfirmStep1Fragment.newInstance());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        /*pb.setVisibility(View.GONE);
                        HSH.getInstance().display(ActivityCreateQuestion.this, btnSendQuestion);
                        btnSendQuestion.setText("ثبت پرسش");
                        HSH.getInstance().showtoast(ActivityCreateQuestion.this, e.getLocalizedMessage());*/
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Map<String, Object> parameters(Object obj) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (Exception e) {
            }
        }
        return map;
    }

    private void showimg(File fils) {
        CustomGallery item = new CustomGallery();
        item.sdcardPath = fils.getAbsolutePath();
        map.add(item);
        adapter.addItems(item);
        //rv.setAdapter(adapter);
    }

    private void selectImage() {
        if (map.size() < 5) {
            File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath());
            if (!dir.exists())
                dir.mkdirs();

            View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
            dialog.setContentView(view);
            dialog.findViewById(R.id.tvGallery).
                    setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult
                                (Intent.createChooser
                                        (intent, HSH.setTypeFace(getActivity(), "انتخاب عکس")), 1);
                        dialog.dismiss();
                    });
            dialog.findViewById(R.id.tvCamera).
                    setOnClickListener(v -> {
                        try {
                            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                uri = Uri.fromFile(file);
                            else
                                uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            camIntent.putExtra("return-data", true);
                            startActivityForResult(camIntent, 0);
                            dialog.dismiss();
                        } catch (Exception e) {
                        }
                    });
            dialog.show();

        } else
            HSH.showtoast(getActivity(), "حداکثر 5 عکس قابل بارگذاری می باشد");
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.length() != 0) {
                price = Integer.valueOf(HSH.trimCommaOfString(etCost.getText().toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                etCost.removeTextChangedListener(this);
                String value = etCost.getText().toString();
                if (!value.equals("")) {
                    if (value.startsWith("0") && !value.startsWith("0.")) {
                        etCost.setText("");
                    }
                    String str = etCost.getText().toString().replaceAll("،", "");
                    etCost.setText(HSH.getDecimalFormattedString(str));
                    etCost.setSelection(etCost.getText().toString().length());
                }
                etCost.addTextChangedListener(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                etCost.addTextChangedListener(this);
            }


        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        v.setBackground(getResources().getDrawable(R.drawable.press_button_stroke_gray));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(getActivity(), "مجوز دسترسی به تصاویر از جانب شما رد شد", Toast.LENGTH_SHORT).show();
        }
    }
}