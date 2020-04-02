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
}
