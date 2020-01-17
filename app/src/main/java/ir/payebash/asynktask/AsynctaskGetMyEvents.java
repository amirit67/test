package ir.payebash.asynktask;


import android.app.Activity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Models.PayeItem;
import ir.payebash.Models.event.EventModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskGetMyEvents {

    @Inject
    Retrofit retrofit;
    IWebservice delegate;
    private Activity ac;
    //private Map<String, String> params;

    public AsynctaskGetMyEvents(Activity ac,/*
                                Map<String, String> params,*/
                                IWebservice delegate) {
        this.ac = ac;
        //this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<List<EventModel>> call =
                retrofit.create(ApiInterface.class).getMyEvents();
        call.enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, retrofit2.Response<List<EventModel>> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response);
                    } else
                        delegate.getError();
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
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


