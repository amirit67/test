package ir.payebash.Classes;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Activities.CategoriesFilterDialog;
import ir.payebash.Adapters.CitiesAdapter;
import ir.payebash.Application;
import ir.payebash.Fragments.NotificationFragment;
import ir.payebash.Interfaces.IWebservice.setListenerCity;
import ir.payebash.Models.CityItem;
import ir.payebash.Moudle.Roozh;
import ir.payebash.Moudle.Utils;
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

    public static void vectorTop(Context cn, TextView view, int a) {
        Drawable drawable = AppCompatResources.getDrawable(cn, a);
        view.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

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

    public static void myCustomSnackbar(Snackbar snackbar) {
        _snackbar = snackbar;
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_action);
        textView.setGravity(Gravity.LEFT);
        TextView textView2 = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
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

    public static void setTypeFace2(ViewGroup viewGroup, Typeface typeface) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) {
                //((TextView) view).setTypeface(Application.font);
                ((TextView) view).setTextSize(13);
                //((TextView) view).setSingleLine();
            }
            if (view instanceof ViewGroup) {
                setTypeFace2(((ViewGroup) view), typeface);
            }
        }
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

    public static void setTimeDate(final Context ctx, final EditText et) {
        //final Roozh cln = new Roozh();
        final PersianCalendar now = new PersianCalendar();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {

                        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(RadialPickerLayout view, final int hourOfDay, final int minute) {
                                        String deadline = String.valueOf(year)
                                                + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + String.valueOf(monthOfYear + 1) : monthOfYear + 1);

                                        String nowdate = (String.valueOf(now.getPersianYear())
                                                + (String.valueOf(now.getPersianMonth() + 1).length() == 1 ? "0" + String.valueOf(now.getPersianMonth() + 1) : now.getPersianMonth() + 1));

                                        int diff = Integer.parseInt(deadline) - Integer.parseInt(nowdate);
                                        int day1 = dayOfMonth;
                                        int day12 = now.getPersianDay();
                                        if (et.getId() == R.id.et_deadline) {
                                            if (diff == 1) {
                                                if (day1 - day12 <= 0)
                                                    et.setText(toPersianNumber(
                                                            String.valueOf(year).substring(2, 4) + "/"
                                                                    + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + String.valueOf(monthOfYear + 1) : monthOfYear + 1)
                                                                    + "/" +
                                                                    (String.valueOf(dayOfMonth).length() == 1 ? "0" + String.valueOf(dayOfMonth) : dayOfMonth)
                                                                    + " " +
                                                                    (String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : hourOfDay)
                                                                    + ":" +
                                                                    (String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : minute)));
                                                else
                                                    HSH.showtoast(ctx, "حداکثر زمان برای هم پا شدن یک ماه می باشد");
                                            } else if (diff == 0) {
                                                if (day1 - day12 >= 0)
                                                    et.setText(toPersianNumber(
                                                            String.valueOf(year).substring(2, 4) + "/"
                                                                    + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + String.valueOf(monthOfYear + 1) : monthOfYear + 1)
                                                                    + "/" +
                                                                    (String.valueOf(dayOfMonth).length() == 1 ? "0" + String.valueOf(dayOfMonth) : dayOfMonth)
                                                                    + " " +
                                                                    (String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : hourOfDay)
                                                                    + ":" +
                                                                    (String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : minute)));
                                                else
                                                    HSH.showtoast(ctx, "زمان وارد شده صحیح نمی باشد.");
                                            } else if (diff < 0)
                                                HSH.showtoast(ctx, "زمان وارد شده صحیح نمی باشد.");
                                            else if (diff > 1)
                                                HSH.showtoast(ctx, "حداکثر زمان برای هم پا شدن یک ماه می باشد");
                                        } else
                                            et.setText(toPersianNumber(
                                                    String.valueOf(year).substring(2, 4) + "/"
                                                            + (String.valueOf(monthOfYear + 1).length() == 1 ? "0" + String.valueOf(monthOfYear + 1) : monthOfYear + 1)
                                                            + "/" +
                                                            (String.valueOf(dayOfMonth).length() == 1 ? "0" + String.valueOf(dayOfMonth) : dayOfMonth)
                                                            + " " +
                                                            (String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : hourOfDay)
                                                            + ":" +
                                                            (String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : minute)));


                                        Roozh jCal = new Roozh();
                                        jCal.PersianToGregorian(year, (monthOfYear + 1), dayOfMonth);
                                        //HSH.showtoast(ctx, jCal.toString());
                                    }
                                },
                                now.get(PersianCalendar.HOUR_OF_DAY),
                                now.get(PersianCalendar.MINUTE),
                                true
                        );
                        tpd.setThemeDark(false);
                        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d(TIMEPICKER, "Dialog was cancelled");
                            }
                        });
                        tpd.show(((Activity) ctx).getFragmentManager(), TIMEPICKER);
                    }
                },
                now.getPersianYear(),
                now.getPersianMonth(),
                now.getPersianDay()
        );
        dpd.setThemeDark(false);
        dpd.show(((Activity) ctx).getFragmentManager(), DATEPICKER);
    }

    public static void selectLocation(final Context ctx, int type, final View v) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_city);
        dialog.setCancelable(true);
        final LinearLayout ll_dialog = dialog.findViewById(R.id.ll_dialog);
        final ListView lv_city = dialog.findViewById(R.id.lv_city);
        final EditText txt_city = dialog.findViewById(R.id.txt_city);

        final CitiesAdapter adapterCity = new CitiesAdapter(ctx, type, (new setListenerCity() {

            @Override
            public void onItemCheck(CityItem item) {
                if (v instanceof Button)
                    ((Button) v).setText(item.getCityNameFa());
                else if (v instanceof EditText)
                    ((EditText) v).setText(item.getCityNameFa());
                v.setTag(item.getId());
                dialog.dismiss();
                try {
                    NotificationFragment.adapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }

        }));
        lv_city.setAdapter(adapterCity);

        txt_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapterCity.filter(txt_city.getText().toString().trim());
            }
        });

        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                display(ctx, ll_dialog);
            }
        }, 50);
    }

    public static void selectSubject(Context ctx, View v) {
        ((Activity) ctx).startActivityForResult(new Intent(ctx, CategoriesFilterDialog.class), 456);
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

        FragmentTransaction ftx = fragmentManager.beginTransaction();

        if ((!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) || fragmentTag.contains("Search"))
            ftx.addToBackStack(fragment.getClass().getSimpleName());

        ftx.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        ftx.replace(R.id.frame, fragment, fragmentTag);
        ftx.commit();
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
        final WebView webView = (WebView) dialog.findViewById(R.id.webView);
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
}