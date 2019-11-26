package ir.payebash.asynktask;


import android.app.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Models.PayeItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskGetPost {

    @Inject
    Retrofit retrofit;
    IWebservice delegate;
    private Activity ac;
    private Map<String, String> params;

    public AsynctaskGetPost(Activity ac,
                            Map<String, String> params,
                            IWebservice delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<List<PayeItem>> call =
                retrofit.create(ApiInterface.class).getPosts(params);
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


