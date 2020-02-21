package ir.payebash.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView

import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope

import java.io.IOException
import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import ir.payebash.Application
import ir.payebash.classes.HSH
import ir.payebash.classes.NetworkUtils
import ir.payebash.fragments.loginRegister.ForgotPasswordFragment
import ir.payebash.fragments.registerUser.Register2Activity
import ir.payebash.Interfaces.ApiClient
import ir.payebash.Interfaces.ApiInterface
import ir.payebash.Interfaces.IWebservice
import ir.payebash.models.TkModel
import ir.payebash.models.googlePlus.PlusItem
import ir.payebash.models.UserItem
import ir.payebash.models.login.LoginModel
import ir.payebash.R
import ir.payebash.asynktask.AsynctaskLogin
import ir.payebash.asynktask.GetTokenAsynkTask
import ir.payebash.helpers.PrefsManager
import ir.payebash.helpers.Utils
import kotlinx.android.synthetic.main.activity_login3.*
import microsoft.aspnet.signalr.client.http.CookieCredentials
import retrofit2.Call
import retrofit2.Callback


class Login2Activity : BaseActivity(), View.OnClickListener, TextWatcher {

    private val params = HashMap<String, String>()
    //private FirebaseAuth mAuth;
    private var googleApiClient: GoogleApiClient? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private fun initViews() {
        etUsername.addTextChangedListener(this)
        etPassword.addTextChangedListener(this)
        btLogin.setOnClickListener(this)
        txtRegister.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
        sign_in_button.setOnClickListener(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login3)
        // Load Auth cookies/credentials

        val loginCredentials = PrefsManager.loadAuthCookie(this)
        if (loginCredentials != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Application.activity = this
            initViews()

            val scope = "oauth2:" + "https://www.googleapis.com/auth/plus.login " +
                    "https://www.googleapis.com/auth/plus.me " +
                    "https://www.googleapis.com/auth/userinfo.email " +
                    "https://www.googleapis.com/auth/userinfo.profile " +
                    "https://www.googleapis.com/auth/user.birthday.read " +
                    "https://www.googleapis.com/auth/user.phonenumbers.read"

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    //.requestIdToken(getString(R.string.CI))
                    .requestEmail()
                    .requestProfile()

                    .requestScopes(Scope(Scopes.PLUS_ME), Scope("https://www.googleapis.com/auth/user.birthday.read"), Scope("https://www.googleapis.com/auth/user.phonenumbers.read"))
                    /*.requestServerAuthCode(getString(R.string.CI))*/
                    .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            googleApiClient = GoogleApiClient.Builder(this)
                    .enableAutoManage(this) { connectionResult ->
                        // connectionResult.
                    }
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
            //mGoogleSignInClient.signOut();
            //mAuth = FirebaseAuth.getInstance();

            /*Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,RC_SIGN_IN);*/
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            //handleSignInResult(result);
            result.signInAccount
            if (result.isSuccess) {
                //gotoProfile();
            } else {
                Toast.makeText(applicationContext, "Sign in cancel", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == 888) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                GetaccessToken(this).execute()
            }
        }
        if (requestCode == RC_SIGN_IN) {
            //https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=
            GetaccessToken(this).execute()
        }
    }


    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.sign_in_button) {
            signIn()
        } else if (i == R.id.btLogin) {

            Observable.fromArray(etUsername.text.toString())
                    .filter { true }.subscribe(object : Observer<String> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(integer: String) {
                            println("onNext: $integer")
                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onComplete() {

                        }
                    })


            if (Utils.isOnline(this)) {

                val loginModel = LoginModel()
                loginModel.username = etUsername.text.toString()
                loginModel.password = etPassword.text.toString()

                progressBar.visibility = View.VISIBLE
                btLogin.visibility = View.GONE

                val del = object : IWebservice.ILogin {
                    @Throws(Exception::class)
                    override fun getResult(user: LoginModel) {
                        progressBar.visibility = View.GONE
                        btLogin.visibility = View.VISIBLE
                        if (user.statusCode == 200) {
                            HSH.editor(getString(R.string.FullName), user.fullName)
                            HSH.editor(getString(R.string.UserName), user.username)
                            HSH.editor(getString(R.string.UserId), user.userId)
                            HSH.editor(getString(R.string.ProfileImage), user.profileImage)
                            HSH.editor(getString(R.string.Password), HSH.ecr(etPassword.text.toString()))
                            GetToken()
                        } else
                            HSH.showtoast(this@Login2Activity, user.message)
                    }

                    @Throws(Exception::class)
                    override fun getError(error: String) {
                        progressBar!!.visibility = View.GONE
                        btLogin.visibility = View.VISIBLE
                    }
                }
                AsynctaskLogin(this, loginModel, del).getData()

            } else {
                Toast.makeText(this, "خطا در اتصال به اینترنت!", Toast.LENGTH_SHORT).show()
            }
        } else if (i == R.id.txtForgotPassword) {
            HSH.openFragment(this, ForgotPasswordFragment())
        } else if (i == R.id.txt_register) {
            HSH.openFragment(this, Register2Activity())
        }
    }

    private fun GetToken() {

        try {
            val params = HashMap<String, String>()
            params[getString(R.string.client_id)] = Application.preferences.getString(getString(R.string.UserId), "")!!
            params[getString(R.string.client_secret)] = Application.preferences.getString(getString(R.string.UserName), "")!!
            params[getString(R.string.grant_type)] = getString(R.string.password).toLowerCase()
            val del = object : IWebservice.ITkModel {
                @Throws(Exception::class)
                override fun getResult(token: TkModel) {
                    HSH.editor(getString(R.string.Token), token.accessToken)
                    startActivity(Intent(this@Login2Activity, MainActivity::class.java))
                    finish()
                }

                @Throws(Exception::class)
                override fun getError(error: Boolean) {
                }
            }
            GetTokenAsynkTask(this@Login2Activity, del, params).GetData()
        } catch (e: Exception) {
        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun getUserInfo(token: String) {
        val call = ApiClient.getClient4().create(ApiInterface::class.java).Plus(token)
        call.enqueue(object : Callback<PlusItem> {
            override fun onResponse(call: Call<PlusItem>, response: retrofit2.Response<PlusItem>) {
                val user = response.body()

                HSH.editor(getString(R.string.mobile), params[getString(R.string.mobile)])
                HSH.editor(getString(R.string.FullName), user!!.getdisplayName())
                if (!user.getImage().getUrl().contains("https://"))
                    HSH.editor(getString(R.string.ProfileImage), getString(R.string.url) + "Images/Users/" + user.getImage().getUrl() + ".jpg")
                else
                    HSH.editor(getString(R.string.ProfileImage), user.getImage().getUrl())
                HSH.editor(getString(R.string.ServicesIds), "")

                params[getString(R.string.UserId)] = preferences.getString(getString(R.string.UserId), "")!!
                //params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
                params[getString(R.string.Type)] = "Login"
                params[getString(R.string.GivenName)] = user.getDisplayName().getGivenName()
                params[getString(R.string.familyName)] = user.getDisplayName().getFamilyName()
                params[getString(R.string.Email)] = user.getEmails()[0].getValue()
                params[getString(R.string.Images)] = user.getImage().getUrl()
                if (user.isPlusUser) {
                    var s = "داستان زندگی : " + "\n" +
                            "توضیحات : " + user.getTagline() + "\n" +
                            "معرفی : " + user.getAboutMe() + "\n" +
                            "وضعیت تاهل : " + user.getRelationshipStatus() + "\n" +
                            "جنسیت : " + user.getGender() + "\n"
                    if (user.getOrganizations().size > 0) {
                        s += "تحصیلات : " + "\n" +
                                "نام محل تحصیلی : " + user.getOrganizations()[0].getName() + "\n" +
                                "رشته تحصیلی : " + user.getOrganizations()[0].getTitle() + "\n" +
                                "شروع : " + user.getOrganizations()[0].getStartDate() +
                                "پایان : " + user.getOrganizations()[0].getEndDate()
                    }
                    if (user.getOrganizations().size > 1) {
                        s += "سابقه کاری : " + "\n" +
                                "نام : " + user.getOrganizations()[1].getName() + "\n" +
                                "عنوان : " + user.getOrganizations()[1].getTitle() + "\n" +
                                "شروع : " + user.getOrganizations()[1].getStartDate() +
                                "پایان : " + user.getOrganizations()[1].getEndDate() + "\n"
                    }
                    s += "شغل : " + user.getOccupation() + "\n"
                    if (user.getPlacesLived().size > 0) {
                        try {
                            s += "محل سکونت : " + user.getPlacesLived()[0].getValue() + " - " + user.getPlacesLived()[user.getPlacesLived().size - 1].getValue()
                        } catch (e: Exception) {
                        }

                    }
                    params[getString(R.string.Aboutme)] = s
                }
                if (user.getPlacesLived().size > 0)
                    params[getString(R.string.city)] = user.getPlacesLived()[0].getValue() + " - " + user.getPlacesLived()[user.getPlacesLived().size - 1].getValue()
                else
                    params[getString(R.string.city)] = ""
                //getAge(token);
                if (NetworkUtils.getConnectivity(this@Login2Activity) != false)
                    GetUserVerificationViaGoogle(user.getEmails()[0].getValue())
                else
                    HSH.showtoast(this@Login2Activity, "خطا در اتصال به اینترنت")
            }

            override fun onFailure(call: Call<PlusItem>, t: Throwable) {}
        })
    }

    private fun GetUserVerificationViaGoogle(gmail: String) {
        val call = ApiClient.getClient().create(ApiInterface::class.java).GetUserVerificationViaGoogle(params)
        call.enqueue(object : Callback<UserItem> {
            override fun onResponse(call: Call<UserItem>, response: retrofit2.Response<UserItem>) {
                if (response.code() == 200) {
                    try {
                        val user = response.body()
                        val UserId = user!!.userId
                        if (UserId == "0") {
                            val Message = user.message
                            HSH.showtoast(this@Login2Activity, Message)
                        } else {
                            try {
                                HSH.editor(getString(R.string.Email), gmail)
                                HSH.editor(getString(R.string.UserId), user.userId)
                                HSH.editor(getString(R.string.mobile), params[getString(R.string.mobile)])
                                HSH.editor(getString(R.string.FullName), user.fullName)
                                if (!user.profileImage.contains("https://"))
                                    HSH.editor(getString(R.string.ProfileImage), getString(R.string.url) + "Images/Users/" + user.profileImage + ".jpg")
                                else
                                    HSH.editor(getString(R.string.ProfileImage), user.profileImage)
                                HSH.editor(getString(R.string.ServicesIds), user.servicesIds.trim { it <= ' ' })
                                try {
                                    HSH.editor(getString(R.string.IsAuthenticate), "true")
                                    val intent = Intent(this@Login2Activity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } catch (e: Exception) {
                                }

                            } catch (e: Exception) {
                            }

                        }
                    } catch (e: Exception) {
                    }

                }
            }

            override fun onFailure(call: Call<UserItem>, t: Throwable) {
                //loading.dismiss();
                if (t.toString().contains("java.net.SocketTimeoutException")) {
                    //  loading.show();
                    GetUserVerificationViaGoogle(gmail)
                }
            }
        })
    }

    inner class GetaccessToken(private val context: Context) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg voids: Void): String? {
            try {
                try {
                    //https://www.googleapis.com/plus/v1/people/me?access_token=ya29.Il-6B1YXJZvm6RRrt7dlshk_wu-wel0RU4HnhDedHVZzMY2cHSgI7uBEONQ0yh9I91ooHkKbAJ-567iZ8LvHvu0IrGfpAWXZc_ppzW4jaoCKcfJLsPGZBZa-GKqneQh7gg
                    val scope = "oauth2:" + "https://www.googleapis.com/auth/plus.login " +
                            "https://www.googleapis.com/auth/plus.me " +
                            "https://www.googleapis.com/auth/userinfo.email " +
                            "https://www.googleapis.com/auth/userinfo.profile " +
                            "https://www.googleapis.com/auth/user.birthday.read " +
                            "https://www.googleapis.com/auth/user.phonenumbers.read"
                    val account = GoogleSignIn.getLastSignedInAccount(context)
                    return GoogleAuthUtil.getToken(context, account!!.account!!, scope, Bundle())
                } catch (e: UserRecoverableAuthException) {
                    startActivityForResult(e.intent, 888)
                    e.printStackTrace()
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            } catch (e: Exception) {
                return null
            }

        }

        override fun onPostExecute(result: String?) {
            if (null != result) {
                //loading.show();
                getUserInfo(result)
            } else
                HSH.showtoast(this@Login2Activity, "مجددا تلاش نمایید")
        }
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {

        if (etUsername.text.length > 5 && etPassword.text.toString().length > 5) {
            btLogin.isEnabled = true
            btLogin.background = resources.getDrawable(R.drawable.rounded_corners_solid_black)
        } else {
            btLogin.isEnabled = false
            btLogin.background = resources.getDrawable(R.drawable.rounded_corners_solid_gray)

        }
    }

    companion object {
        private val RC_SIGN_IN = 9001
    }
}
