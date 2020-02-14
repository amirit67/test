package ir.payebash.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.activities.AboutUsActivity;
import ir.payebash.activities.EditProfileActivity;
import ir.payebash.activities.LoginActivity;
import ir.payebash.Fragments.registerUser.RegisterActivity;
import ir.payebash.activities.ViewPagerActivity;
import ir.payebash.activities.WelcomeActivity;
import ir.payebash.Application;
import ir.payebash.Classes.BaseFragment;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Classes.PermissionHandler;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.models.PayeItem;
import ir.payebash.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    public static final String GOOGLE_CLIENT_ID = "642093830131-gkpp28n0vrl6jpgs2p4njq43tie6f2lq.apps.googleusercontent.com";
    public static TextView txt_name;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //private MainComponent component;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private Uri uri;
    private ImageView img_profile;
    private TextView txt_pic, txt_advantage, txt_profile, txt_notification,
    /*txt_myads, txt_wanted,*/ txt_history, txt_favorite, txt_rulls,
            txt_support, txt_share, txt_exit;
    //private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private View rootView = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            Application.getComponent().Inject(this);
            //component = Application.get((AppCompatActivity) getActivity()).getComponent();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(GOOGLE_CLIENT_ID)
                    .requestEmail()
                    .requestProfile()
                    .requestScopes(new Scope(Scopes.PLUS_ME), new Scope("https://www.googleapis.com/auth/user.phonenumbers.read"), new Scope("https://www.googleapis.com/auth/user.birthday.read"))
                    .requestServerAuthCode(GOOGLE_CLIENT_ID)
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            //mAuth = FirebaseAuth.getInstance();

            txt_pic = rootView.findViewById(R.id.txt_pic);
            img_profile = rootView.findViewById(R.id.img_profile);
            txt_name = rootView.findViewById(R.id.txt_name);
            txt_name.setText(Application.preferences.getString(getString(R.string.FullName), ""));
            imageLoader.displayImage(Application.preferences.getString(getString(R.string.ProfileImage), "0"), img_profile, options);

            img_profile.setOnClickListener(v -> {
                String imgPath = Application.preferences.getString(getString(R.string.ProfileImage), "0");
                if (!imgPath.equals("0")) {
                    PayeItem feed = new PayeItem();
                    feed.setImages(Application.preferences.getString(getString(R.string.ProfileImage), "0"));
                    final Bundle bundle = new Bundle();
                    bundle.putSerializable("feed", feed);
                    Intent i = new Intent(getActivity(), ViewPagerActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            txt_pic.setPaintFlags(txt_pic.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txt_pic.setOnClickListener(v -> new PermissionHandler().checkPermission(getActivity(), permissions, new PermissionHandler.OnPermissionResponse() {
                @Override
                public void onPermissionGranted() {
                    final Dialog dialog_image_profile = new Dialog(getActivity());
                    dialog_image_profile.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_image_profile.setContentView(R.layout.dialog_select_image_profile);
                    dialog_image_profile.setCancelable(true);
                    File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Profile").getPath());
                    if (!dir.exists())
                        dir.mkdirs();
                    dialog_image_profile.findViewById(R.id.rb1).setOnClickListener(v12 -> {
                        try {
                            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Profile"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                uri = Uri.fromFile(file);
                            else
                                uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            camIntent.putExtra("return-data", true);
                            getActivity().startActivityForResult(camIntent, 0);
                            dialog_image_profile.dismiss();
                        } catch (Exception e) {
                        }
                    });

                    dialog_image_profile.findViewById(R.id.rb2).setOnClickListener(v1 -> {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        getActivity().startActivityForResult(pickPhoto, 1);
                        dialog_image_profile.dismiss();
                    });

                    //HSH.dialog(dialog_image_profile);
                    dialog_image_profile.show();
                }

                @Override
                public void onPermissionDenied() {
                    HSH.showtoast(getActivity(), "برای تغییر تصویر پروفایل دسترسی را صادر نمایید.");
                }
            }));

            txt_profile = rootView.findViewById(R.id.txt_profile);
            txt_notification = rootView.findViewById(R.id.txt_notification);
            txt_advantage = rootView.findViewById(R.id.txt_advantage);
            txt_history = rootView.findViewById(R.id.txt_history);
            txt_favorite = rootView.findViewById(R.id.txt_favorite);
            txt_rulls = rootView.findViewById(R.id.txt_rulls);
            txt_support = rootView.findViewById(R.id.txt_support);
            txt_share = rootView.findViewById(R.id.txt_share);
            txt_exit = rootView.findViewById(R.id.txt_exit);
            txt_profile.setOnClickListener(this);
            txt_notification.setOnClickListener(this);
            txt_advantage.setOnClickListener(this);
            txt_history.setOnClickListener(this);
            txt_favorite.setOnClickListener(this);
            txt_rulls.setOnClickListener(this);
            txt_support.setOnClickListener(this);
            txt_share.setOnClickListener(this);
            txt_exit.setOnClickListener(this);

            HSH.vectorRight(getActivity(), txt_profile, R.drawable.ic_profile2);
            HSH.vectorRight(getActivity(), txt_notification, R.drawable.ic_notifications2);
            HSH.vectorRight(getActivity(), txt_advantage, R.drawable.ic_benefit);
            HSH.vectorRight(getActivity(), txt_history, R.drawable.ic_history);
            HSH.vectorRight(getActivity(), txt_favorite, R.drawable.ic_bookmark);
            HSH.vectorRight(getActivity(), txt_rulls, R.drawable.ic_rules);
            HSH.vectorRight(getActivity(), txt_support, R.drawable.ic_support);
            HSH.vectorRight(getActivity(), txt_share, R.drawable.ic_share);
            HSH.vectorRight(getActivity(), txt_exit, R.drawable.ic_sign_out);
        }
        ((TitleMain) getContext()).FragName("پروفایل");
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.txt_profile:
                final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
                dialog.setTitleText("ویرایش پروفایل کاربری");
                dialog.setContentText("");
                dialog.setConfirmText("اطلاعات اصلی");
                dialog.setCancelText("اطلاعات تکمیلی");
                dialog.setConfirmClickListener(sDialog -> {
                    Intent i1 = new Intent(getActivity(), RegisterActivity.class);
                    i1.putExtra("Type", "Update");
                    startActivityForResult(i1, 123);
                    dialog.dismissWithAnimation();
                });
                dialog.setCancelClickListener(sweetAlertDialog -> {
                    HSH.onOpenPage(getActivity(), EditProfileActivity.class);
                    dialog.dismissWithAnimation();
                });
                dialog.setCancelable(true);
                HSH.dialog(dialog);
                break;
            case R.id.txt_notification:
                Dialog dialogNotif = new Dialog(getActivity());
                dialogNotif.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogNotif.setContentView(R.layout.dialog_notification);
                final SwitchCompat compatSwitch = dialogNotif.findViewById(R.id.compatSwitch);
                final SwitchCompat compatSwitch2 = dialogNotif.findViewById(R.id.compatSwitch2);
                final SwitchCompat compatSwitch3 = dialogNotif.findViewById(R.id.compatSwitch3);
                compatSwitch.setChecked(Boolean.valueOf(Application.preferences.getString("BeupNotif", "true")));
                compatSwitch2.setChecked(Boolean.valueOf(Application.preferences.getString("CommentNotif", "true")));
                compatSwitch3.setChecked(Boolean.valueOf(Application.preferences.getString("EventRequestNotif", "true")));
                compatSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> HSH.editor("BeupNotif", String.valueOf(compatSwitch.isChecked())));
                compatSwitch2.setOnCheckedChangeListener((buttonView, isChecked) -> HSH.editor("CommentNotif", String.valueOf(compatSwitch2.isChecked())));
                compatSwitch3.setOnCheckedChangeListener((buttonView, isChecked) -> HSH.editor("EventRequestNotif", String.valueOf(compatSwitch3.isChecked())));
                HSH.dialog(dialogNotif);
                dialogNotif.show();
                break;
            case R.id.txt_advantage:
                i = new Intent(getActivity(), WelcomeActivity.class);
                i.putExtra("advantage", "advantage");
                startActivity(i);
                break;
           /* case R.id.txt_myads:
                Application.myAds = 42907631;
                MyAds();
                break;
            case R.id.txt_wanted:
                Application.myAds = 1;
                FavoriteOrRecent("Wanted");
                break;*/
            case R.id.txt_history:
                FavoriteOrRecent("History");
                break;
            case R.id.txt_favorite:
                FavoriteOrRecent("Favorite");
                break;
            case R.id.txt_rulls:
                HSH.Ruls(getActivity());
                break;
            case R.id.txt_support:
                HSH.onOpenPage(getActivity(), AboutUsActivity.class);
                break;
            case R.id.txt_share:
                String shareBody = "سلام.این برنامه خیلی باحاله.با پایه باش دیگه تنها نیستی و میتونی زندگی جدیدی رو تجربه کنی\n " + Application.preferences.getString("ApkUrl", "http://cafebazaar.ir/app/ir.payebash/?l=fa") + "";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "پایه باش\n\n");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "اشتراک گذاری"));
                break;
            case R.id.txt_exit:
                final SweetAlertDialog _dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("خروج از حساب")
                        .setContentText(getString(R.string.sure))
                        .setConfirmText("بله")
                        .setCancelText("فعلا نه")
                        .setConfirmClickListener(sDialog -> {
                            try {
                                String query = "DELETE FROM RecentVisit " +
                                        "WHERE IsFavorite != 'false' or IsMine = 'true' ";
                                Application.database.execSQL(query);
                            } catch (Exception e1) {
                            }
                            Application.editor.clear();
                            Application.editor.apply();
                            Application.editor.commit();
                            //mAuth.signOut();
                            mGoogleSignInClient.signOut();
                            HSH.showtoast(getActivity(), "از حساب خود خارج شدید");
                            HSH.onOpenPage(getActivity(), LoginActivity.class);
                            getActivity().finish();
                        });
                _dialog.setCancelClickListener(sweetAlertDialog -> _dialog.dismissWithAnimation());
                _dialog.setCancelable(true);
                HSH.dialog(_dialog);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            txt_name.setText(Application.preferences.getString(getString(R.string.FullName), ""));
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        uri = data.getData();
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());
                    }
                    break;
            }
        } catch (Exception e) {
        }

        if (resultCode == -1 && (requestCode != 0 && requestCode != 1 && null != data))
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                img_profile.setImageURI(result.getUri());
                if (NetworkUtils.getConnectivity(getActivity()) != false)
                    saveProfileAccount(result.getUri());
                else
                    HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");

            } catch (Exception e) {
            }
    }

    //https://github.com/square/retrofit/issues/662
    private void saveProfileAccount(final Uri imageUri) {
        File file = new File(imageUri.getPath());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(String.valueOf(Calendar.getInstance().getTimeInMillis()), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        RequestBody UserId =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, Application.preferences.getString(getString(R.string.UserId), "0"));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).saveProfileAccount(UserId, filePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        String s = response.body().string();
                        if (!s.contains("https://"))
                            HSH.editor(getString(R.string.ProfileImage), getString(R.string.url) + "Images/Users/" + s + ".jpg");
                        else
                            HSH.editor(getString(R.string.ProfileImage), s);
                        HSH.showtoast(getActivity(), "با موفقیت بروزرسانی گردید");
                    } catch (Exception e) {
                    }
                else
                    saveProfileAccount(imageUri);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                saveProfileAccount(imageUri);
            }
        });
    }

    private void MyAds() {
        MyPayeFragment fra = new MyPayeFragment();
        HSH.openFragment(getActivity(), fra);
    }

    private void FavoriteOrRecent(String state) {
        MyPayeFragment fra = new MyPayeFragment();
        Bundle bnd = new Bundle();
        bnd.putString("FavoriteOrRecent", state);
        fra.setArguments(bnd);
        HSH.openFragment(getActivity(), fra);
    }
}