package ir.payebash.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.jaredrummler.android.widget.AnimatedSvgView;

import ir.payebash.Application;
import ir.payebash.Classes.BaseActivity;
import ir.payebash.Classes.HSH;
import ir.payebash.R;

public class IntroLoginActivity extends BaseActivity implements View.OnClickListener {

    //String[] permissions = {Manifest.permission.RECEIVE_SMS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*PackageManager pm = getPackageManager();
        ComponentName componentName = new ComponentName(IntroLoginActivity.this, IncomingSms.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/
        try {
            setContentView(R.layout.activity_intro_login);

            TextView txt_guest = findViewById(R.id.txt_guest);
            txt_guest.setPaintFlags(txt_guest.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

           /* AnimatedSvgView svgView = findViewById(R.id.animated_svg_view);
            svgView.start();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimatedSvgView svgView3 = findViewById(R.id.font_svg_view);
                    svgView3.start();
                }
            }, 1000);*/
            findViewById(R.id.btn_login).setOnClickListener(this);
            findViewById(R.id.btn_register).setOnClickListener(this);
            findViewById(R.id.txt_guest).setOnClickListener(this);
        } catch (Exception e) {
        }

        try {
            SpannableString ss = new SpannableString("با عضویت و ورود به پایه باش کلیه قوانین و شرایط استفاده\n از آن را می پذیرم.");
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#026d37")), 28, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView txt_ruls = findViewById(R.id.txt_ruls);
            txt_ruls.setText(ss);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        HSH.editor("Materialintro", "True");
        switch (v.getId()) {
            case R.id.btn_login:
                HSH.onOpenPage(IntroLoginActivity.this, Login2Activity.class);
               /* new PermissionHandler().checkPermission(IntroLoginActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                    @Override
                    public void onPermissionGranted() {
                        HSH.onOpenPage(IntroLoginActivity.this, LoginActivity.class);
                    }

                    @Override
                    public void onPermissionDenied() {
                        HSH.showtoast(IntroLoginActivity.this, "برای ورود دسترسی  را صادر نمایید.");
                    }
                });*/
                break;
            case R.id.btn_register:
                HSH.openFragment(this, new Register2Activity());
                /*new PermissionHandler().checkPermission(IntroLoginActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        HSH.showtoast(IntroLoginActivity.this, "برای ورود دسترسی  را صادر نمایید.");
                    }
                });*/
                break;
            case R.id.txt_guest:
                HSH.onOpenPage(IntroLoginActivity.this, GuestActivity.class);
                break;
        }
    }

    public void clickRules(View view) {
        HSH.Ruls(IntroLoginActivity.this);
    }

}
