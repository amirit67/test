package ir.payebash.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class AboutUsActivity extends Activity {

    TextView btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        btn = findViewById(R.id.btn_support);
        btn.setPaintFlags(btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void click(View v) {

        if (v.getId() == R.id.btn_support) {
            try {
                final Dialog textEntryView = new Dialog(AboutUsActivity.this);
                textEntryView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                textEntryView.setContentView(R.layout.dialog_support);
                final EditText txt_message = textEntryView.findViewById(R.id.txt_message);
                final Button btn_send = textEntryView.findViewById(R.id.btn_send);
                final Button btn_rejct = textEntryView.findViewById(R.id.btn_rejct);
                final Spinner spiner_subject = textEntryView.findViewById(R.id.spinner1);
                ArrayList<String> data = new ArrayList<>();
                data.add("ارائه پیشنهاد");
                data.add("گزارش خطا");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AboutUsActivity.this, R.layout.item_spinner, data);
                spiner_subject.setAdapter(adapter);

                btn_send.setOnClickListener(v1 -> {

                    if (txt_message.getText().toString().equals(""))
                        HSH.showtoast(getApplicationContext(), "متن ارسالی نمی تواند خالی باشد.");

                    else if (NetworkUtils.getConnectivity(AboutUsActivity.this) != false) {
                        Map<String, String> params = new HashMap<>();
                        params.put(getString(R.string.Message), txt_message.getText().toString());
                        params.put(getString(R.string.Subject), spiner_subject.getSelectedItem().toString());
                        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                        sendFeedBack(params);
                        textEntryView.dismiss();
                    } else
                        HSH.showtoast(AboutUsActivity.this, "خطا در اتصال به اینترنت");
                });
                btn_rejct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textEntryView.dismiss();
                    }
                });
                HSH.dialog(textEntryView);
                textEntryView.show();
            } catch (Exception e) {
            }
        }
    }

    public void sendFeedBack(final Map<String, String> params) {

        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).sendFeedBack(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    HSH.showtoast(AboutUsActivity.this, "ارسال گردید");
                else
                    sendFeedBack(params);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendFeedBack(params);
            }
        });
    }
}