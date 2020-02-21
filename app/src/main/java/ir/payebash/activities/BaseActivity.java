package ir.payebash.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.classes.HSH;
import ir.payebash.R;


public class BaseActivity extends AppCompatActivity {

    public Animation in;
    public Animation out;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;

    public Uri uri;
    public FragmentManager _frgManager;
    public Context mContext;
    public String[] permissions2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            /* Manifest.permission.RECEIVE_SMS,*/
    };
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CAMERA
    };
    String[] permissions3 = {Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this._frgManager = getSupportFragmentManager();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        in = AnimationUtils.loadAnimation(this,
                R.anim.zoom_in);

        out = AnimationUtils.loadAnimation(this,
                R.anim.zoom_out);
       /* new PermissionHandler().checkPermission(this, permissions, new PermissionHandler.OnPermissionResponse() {
            @Override
            public void onPermissionGranted() {
                return;
            }

            @Override
            public void onPermissionDenied() {
            }
        });*/
    }

    public void setError(LinearLayout childlinearlayout, TextView txt_title, String text) {
        txt_title.setText("");
        childlinearlayout.setBackgroundColor(Color.argb(100, 255, 128, 128));
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, text.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_title.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    /*public void addFragment_in_fragment(Fragment frg, int containerId, boolean addToBackStack) {
        FragmentTransaction ft = this._frgManager.beginTransaction().setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out, R.anim.fade_in,
                R.anim.fade_out).add(containerId, frg);
        if (addToBackStack) {
            ft.addToBackStack(frg.getClass().getName());
        }
        ft.commit();
    }*/

    public void addFragment(Fragment frg, int containerId, boolean addToBackStack) {
        //FragmentTransaction ft = this._frgManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).add(containerId, frg);

        String fragmentTag = frg.getClass().getSimpleName();
        boolean fragmentPopped = _frgManager
                .popBackStackImmediate(fragmentTag, 0);

        FragmentTransaction ftx = _frgManager.beginTransaction();

        if (addToBackStack && (!fragmentPopped
                && _frgManager.findFragmentByTag(fragmentTag) == null)
                || fragmentTag.contains("Search"))
            ftx.addToBackStack(frg.getClass().getSimpleName());

        ftx.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        ftx.replace(containerId, frg, fragmentTag);
        ftx.commit();


       /* if (addToBackStack) {
            ft.addToBackStack(frg.getClass().getName());
        }
        ft.commit();*/
    }

    public void closeKeyboard() {
        try {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (Exception e) {
        }
    }

    public Uri SelectImage() {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory("Investam/Images").getPath());
            if (!dir.exists())
                dir.mkdirs();
            final SweetAlertDialog dialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
            dialog.setTitleText("ارسال عکس به کارشناس");
            dialog.setContentText("در صورت درخواست کارشناس مورد نظر، برای ارسال عکس، از این گزینه استفاده کنید.");
            dialog.setConfirmText("دوربین");
            dialog.setCancelText("انتخاب از گالری");
            dialog.setConfirmClickListener((SweetAlertDialog sDialog) ->
            {
                try {
                    Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStoragePublicDirectory("Investam/Images"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                        uri = Uri.fromFile(file);
                    else
                        uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
                    camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    camIntent.putExtra("return-data", true);
                    ((Activity) mContext).startActivityForResult(camIntent, 0);
                    dialog.dismiss();
                } catch (Exception e) {
                }
            });
            dialog.setCancelClickListener((SweetAlertDialog sweetAlertDialog) ->
            {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((Activity) mContext).startActivityForResult
                        (Intent.createChooser
                                (intent, "ارسال تصویر به کارشناس"), 1);
                dialog.dismiss();
            });
            dialog.setCancelable(true);
            HSH.dialog(dialog);
        } catch (Exception e) {
        }
        return uri;
    }

}
