package ir.payebash.asynktask;


import android.app.Activity;

import java.util.List;
import java.util.Map;

import ir.payebash.classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.event.EventModel;
import retrofit2.Call;
import retrofit2.Callback;

public class AsynctaskUncomingGetPost {

    private Activity ac;
    private Map<String, String> params;
    private IWebservice delegate;

    public AsynctaskUncomingGetPost(Activity ac,
                                    Map<String, String> params,
                                    IWebservice delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
    }

    public void getData() {
        Call<List<EventModel>> call =
                ApiClient.getClient().create(ApiInterface.class).getUncomingPosts(params);
        call.enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, retrofit2.Response<List<EventModel>> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.errorBody().string());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                if (NetworkUtils.getConnectivity(ac) != false)
                    getData();
                else {
                    try {
                        delegate.getError(t.getLocalizedMessage());
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}