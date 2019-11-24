package ir.payebash.asynktask;


import android.app.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Models.PayeItem;
import retrofit2.Call;
import retrofit2.Callback;

public class AsynctaskGetPost {

    IWebservice delegate;
    private Activity ac;
    private Map<String, String> params = new HashMap<>();

    public AsynctaskGetPost(Activity ac,
                            Map<String, String> params,
                            IWebservice delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
    }

    public void getData() {
        Call<List<PayeItem>> call =
                ApiClient.getClient().create(ApiInterface.class).getPosts(params);
        call.enqueue(new Callback<List<PayeItem>>() {
            @Override
            public void onResponse(Call<List<PayeItem>> call, retrofit2.Response<List<PayeItem>> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response);
                    } else
                        delegate.getError();
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<List<PayeItem>> call, Throwable t) {
                if (NetworkUtils.getConnectivity(ac) != false)
                    getData();
                else {
                    try {
                        delegate.getError();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}


