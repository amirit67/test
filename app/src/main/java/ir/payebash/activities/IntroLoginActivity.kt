package ir.payebash.activities

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView

import ir.payebash.classes.BaseActivity
import ir.payebash.classes.HSH
import ir.payebash.fragments.registerUser.Register2Activity
import ir.payebash.R
import kotlinx.android.synthetic.main.activity_intro_login.*

class IntroLoginActivity : BaseActivity(), View.OnClickListener {

    //String[] permissions = {Manifest.permission.RECEIVE_SMS};

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_intro_login)

            txtGuest.paintFlags = txtGuest.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            btnLogin.setOnClickListener(this)
            btnRegister.setOnClickListener(this)
            txtGuest.setOnClickListener(this)
        } catch (e: Exception) {
        }

        try {
            val ss = SpannableString("با عضویت و ورود به پایه باش کلیه قوانین و شرایط استفاده\n از آن را می پذیرم.")
            ss.setSpan(ForegroundColorSpan(Color.parseColor("#026d37")), 28, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            txtRuls.text = ss
        } catch (e: Exception) {
        }

    }

    override fun onClick(v: View) {
        HSH.editor("Materialintro", "True")
        when (v.id) {
            R.id.btnLogin -> HSH.onOpenPage(this@IntroLoginActivity, Login2Activity::class.java)
            R.id.btn_register -> HSH.openFragment(this, Register2Activity())
            R.id.txtGuest -> HSH.onOpenPage(this@IntroLoginActivity, GuestActivity::class.java)
        }/* new PermissionHandler().checkPermission(IntroLoginActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                    @Override
                    public void onPermissionGranted() {
                        HSH.onOpenPage(IntroLoginActivity.this, LoginActivity.class);
                    }

                    @Override
                    public void onPermissionDenied() {
                        HSH.showtoast(IntroLoginActivity.this, "برای ورود دسترسی  را صادر نمایید.");
                    }
                });*//*new PermissionHandler().checkPermission(IntroLoginActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        HSH.showtoast(IntroLoginActivity.this, "برای ورود دسترسی  را صادر نمایید.");
                    }
                });*/
    }

    fun clickRules(view: View) {
        HSH.Ruls(this@IntroLoginActivity)
    }

}
