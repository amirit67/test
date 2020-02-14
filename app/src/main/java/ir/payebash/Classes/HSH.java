package ir.payebash.Classes;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Fragments.subject.CategoriesFilterActivity;
import ir.payebash.Application;
import ir.payebash.moudle.Roozh;
import ir.payebash.moudle.Utils;
import ir.payebash.R;

/**
 * Created by hossein1 on 6/28/2014.
 */
public class HSH {

    private static final String TIMEPICKER = "TimePickerDialog",
            DATEPICKER = "DatePickerDialog";
    public static Snackbar _snackbar;
    private static String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
    private static String[] englishNumbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static void vectorRight(Context cn, TextView view, int a) {
        Drawable drawable = AppCompatResources.getDrawable(cn, a);
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public static void editor(String a, String b) {
        try {
            Application.editor.putString(a, b);
            Application.editor.apply();
            Application.editor.commit();
        } catch (Exception e) {
        }
    }

    public static void dialog(Dialog dialog) {
        Window window = dialog.getWindow();
        ViewGroup.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes((android.view.WindowManager.LayoutParams) params);
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    public static String toEnglishNumber(String text) {
        if ("".equals(text)) return "";
        String ch, str = "";
        int i = 0;
        while (text.length() > i) {
            ch = String.valueOf(text.charAt(i));
            if (TextUtils.isDigitsOnly(ch)) str += englishNumbers[Integer.parseInt(ch)];
            else str += ch;
            i++;
        }
        return str;
    }

    public static SpannableStringBuilder setTypeFace(Context cn, String s) {
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(s);
        ssbuilder.setSpan(new CustomTypefaceSpan("font/yekanmedium.ttf", cn), 0, ssbuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return ssbuilder;
    }

    public static void onOpenPage(Context context, @SuppressWarnings("rawtypes") Class tow_class) {
        Intent intent = new Intent(context, tow_class);
        context.startActivity(intent);
    }

    public static boolean isNetworkConnection(Context context) {
        ConnectivityManager con =
                (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = con.getActiveNetworkInfo();
        if (info == null)
            return false;
        else
            return true;
    }

    public static SweetAlertDialog onProgress_Dialog(Context context, String text) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(text);
        pDialog.setCancelable(true);
        //pDialog.show();

        /*IOSDialog dialog = new IOSDialog.Builder(context)
                .setSpinnerDuration(400)
                .setSpinnerColorRes(R.color.google_blue)
                .setMessageColorRes(R.color.mdtp_white)
                .setMessageContent(HSH.setTypeFace(context, text))
                .setCancelable(true)
                .setMessageContentGravity(Gravity.END)
                .build();*/
        return pDialog;
    }

    public static void showtoast(Context cn, String s) {
        LayoutInflater inflater = (LayoutInflater) cn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastRoot = inflater.inflate(R.layout.item_toast, null);
        TextView t = toastRoot.findViewById(R.id.text);
        t.setText(s);
        Toast toast = new Toast(cn);
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                0, 150);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static String setTimeDate(final Context ctx, final TextView et) {
        //final Roozh cln = new Roozh();
        final PersianCalendar now = new PersianCalendar();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {

                    String deadline = String.valueOf(year)
                            + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + (monthOfYear + 1) : monthOfYear + 1);

                    String nowdate = (String.valueOf(now.getPersianYear())
                            + (String.valueOf(now.getPersianMonth() + 1).length() == 1 ? "0" + (now.getPersianMonth() + 1) : now.getPersianMonth() + 1));

                    int diff = Integer.parseInt(deadline) - Integer.parseInt(nowdate);
                    int day1 = dayOfMonth;
                    int day12 = now.getPersianDay();
                    if (et.getId() == R.id.et_deadline) {
                        if (diff == 1) {
                            if (day1 - day12 <= 0)
                                et.setText(toPersianNumber(
                                        String.valueOf(year).substring(2, 4) + "/"
                                                + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + (monthOfYear + 1) : monthOfYear + 1)
                                                + "/" +
                                                (String.valueOf(dayOfMonth).length() == 1 ? "0" + (dayOfMonth) : dayOfMonth)));
                            else
                                HSH.showtoast(ctx, "حداکثر زمان برای هم پا شدن یک ماه می باشد");
                        } else if (diff == 0) {
                            if (day1 - day12 >= 0)
                                et.setText(toPersianNumber(
                                        String.valueOf(year).substring(2, 4) + "/"
                                                + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + (monthOfYear + 1) : monthOfYear + 1)
                                                + "/" +
                                                (String.valueOf(dayOfMonth).length() == 1 ? "0" + (dayOfMonth) : dayOfMonth)));
                            else
                                HSH.showtoast(ctx, "زمان وارد شده صحیح نمی باشد.");
                        } else if (diff < 0)
                            HSH.showtoast(ctx, "زمان وارد شده صحیح نمی باشد.");
                        else if (diff > 1)
                            HSH.showtoast(ctx, "حداکثر زمان برای هم پا شدن یک ماه می باشد");
                    } else
                        et.setText(toPersianNumber(
                                String.valueOf(year).substring(2, 4) + "/"
                                        + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + (monthOfYear + 1) : monthOfYear + 1)
                                        + "/" +
                                        (String.valueOf(dayOfMonth).length() == 1 ? "0" + (dayOfMonth) : dayOfMonth)));


                    Roozh jCal = new Roozh();
                    jCal.PersianToGregorian(year, (monthOfYear + 1), dayOfMonth);
                    //HSH.showtoast(ctx, jCal.toString());
                },
                now.getPersianYear(),
                now.getPersianMonth(),
                now.getPersianDay()
        );
        dpd.setMinDate(now);
        dpd.setThemeDark(false);
        dpd.show(((AppCompatActivity) ctx).getSupportFragmentManager(), DATEPICKER);

        return et.getText().toString().trim();
    }

    public static String setTime(final Context ctx, final TextView et) {

        final PersianCalendar now = new PersianCalendar();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                (view, hourOfDay, minute) -> et.setText(
                        (String.valueOf(hourOfDay).length() == 1 ? "0" + (hourOfDay) : hourOfDay)
                                + ":" +
                                (String.valueOf(minute).length() == 1 ? "0" + (minute) : minute)),
                now.get(PersianCalendar.HOUR_OF_DAY),
                now.get(PersianCalendar.MINUTE),
                true
        );
        tpd.setThemeDark(false);
        tpd.setOnCancelListener(dialogInterface -> Log.d(TIMEPICKER, "Dialog was cancelled"));
        tpd.show(((AppCompatActivity) ctx).getSupportFragmentManager(), DATEPICKER);

        return et.getText().toString().trim();
    }

    public static void selectSubject(Context ctx, View v) {
        ((Activity) ctx).startActivityForResult(new Intent(ctx, CategoriesFilterActivity.class), 456);
    }

    @SuppressLint("NewApi")
    public static String toPersianNumber(String text) {
        if (text.isEmpty()) return "";
        String out = "";
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫') {
                out += '،';
            } else {
                out += c;
            }
        }
        return out;
    }

    public static String getCompleteAddress(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Locale lHebrew = new Locale("fa");
        Geocoder geocoder = new Geocoder(context, lHebrew);
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {

                Address address = addresses.get(0);
                String street = address.getThoroughfare();
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
//				String country    = addresses.get(0).getCountryName();
//				String postalCode = addresses.get(0).getPostalCode();
//				String knownName  = addresses.get(0).getFeatureName();

                if (street != null)
                    strAdd = street;
                else
                    strAdd = city;
            } else {
            }

        } catch (Exception e) {
        }
        return strAdd;
    }

    //////////////////////////////////////////////////////////////////////////
    public static void setTextViewDrawableColor(TextView textView, int x, int y, int z) {
        try {
            for (Drawable drawable : textView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(x, y, z), PorterDuff.Mode.MULTIPLY));
                }
            }
            textView.setTextColor(Color.rgb(x, y, z));
        } catch (Exception e) {
        }
    }

    public static void setMainDrawableColor(LinearLayout layout, View view) {
        for (int j = 0; j < layout.getChildCount(); j++) {
            View v = layout.getChildAt(j);
            if (v.getId() == view.getId() && v instanceof TextView) {
                setTextViewDrawableColor((TextView) v, 0, 0, 0);
            } else if (v instanceof TextView) {
                setTextViewDrawableColor((TextView) v, 150, 150, 150);
            }
        }
    }

    public static void openFragment(Activity activity, Fragment fragment) {
        String fragmentTag = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = ((AppCompatActivity) activity)
                .getSupportFragmentManager();

        boolean fragmentPopped = fragmentManager
                .popBackStackImmediate(fragmentTag, 0);

        try {
            FragmentTransaction ftx = fragmentManager.beginTransaction();

            if ((!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) || fragmentTag.contains("Search"))
                ftx.addToBackStack(fragment.getClass().getSimpleName());

            ftx.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);
            ftx.replace(R.id.frame, fragment, fragmentTag);
            ftx.commit();
        } catch (Exception e) {
            HSH.showtoast(activity, e.getMessage());
        }

        new Handler().postDelayed(() -> {
            try {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
            }
        }, 500);

    }

    public static void display(final Context ctx, final View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Animator animator = ViewAnimationUtils.createCircularReveal(v,
                        v.getWidth() - Utils.dpToPx(ctx, 56),
                        Utils.dpToPx(ctx, 23),
                        0,
                        (float) Math.hypot(v.getWidth(), v.getHeight()));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setEnabled(true);
                        if (v instanceof EditText)
                            ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                v.setVisibility(View.VISIBLE);
                if (v.getVisibility() == View.VISIBLE) {
                    animator.setDuration(500);
                    animator.start();
                    v.setEnabled(true);
                }
            } catch (Exception e) {
            }
        } else {
            v.setVisibility(View.VISIBLE);
            v.setEnabled(true);
        }
    }

    public static void hide(final Context ctx, final View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(v,
                        v.getWidth() - Utils.dpToPx(ctx, 56),
                        Utils.dpToPx(ctx, 23),
                        (float) Math.hypot(v.getWidth(), v.getHeight()),
                        0);
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorHide.setDuration(500);
                animatorHide.start();
            } catch (Exception e) {
            }
        } else {
            v.setVisibility(View.GONE);
        }
    }

    public static void Ruls(Activity ac) {
        final Dialog dialog = new Dialog(ac);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rules);
        final WebView webView = dialog.findViewById(R.id.webView);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusableInTouchMode(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadUrl(ac.getString(R.string.url) + "rules.html");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                dialog.findViewById(R.id.pb).setVisibility(View.GONE);
            }
        });
        HSH.dialog(dialog);
        dialog.show();
    }


    //1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long days = TimeUnit.DAYS.convert(different, TimeUnit.MILLISECONDS);

        if (TimeUnit.SECONDS.convert(different, TimeUnit.MILLISECONDS) < 60)
            return (TimeUnit.SECONDS.convert(different, TimeUnit.MILLISECONDS) + " لحظاتی");
        else if (TimeUnit.MINUTES.convert(different, TimeUnit.MILLISECONDS) < 60)
            return (TimeUnit.MINUTES.convert(different, TimeUnit.MILLISECONDS) + " دقیقه");
        else if (TimeUnit.HOURS.convert(different, TimeUnit.MILLISECONDS) < 24)
            return (TimeUnit.HOURS.convert(different, TimeUnit.MILLISECONDS) + " ساعت");
        else if (days == 1)
            return ("دیروز");
        else if (days > 1)
            return (days + " روز");
        else if (days > 6)
            return (days / 7 + " هفته");
        else if (days > 29)
            return (days / 30 + " ماه");

        return TimeUnit.DAYS.convert(different, TimeUnit.MILLISECONDS) + "";
    }


    public static String ecr(String input) {
        // Simple encryption, not very strong!
        //return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }

    }


}