package ir.payebash.asynktask;


import android.app.Activity;

import java.util.List;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Models.event.story.StoryModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskStoryEvents {

    @Inject
    Retrofit retrofit;
    IWebservice.IStoriesEvents delegate;
    private Activity ac;
    private String cityCode;

    public AsynctaskStoryEvents(Activity ac,
                                IWebservice.IStoriesEvents delegate,
                                String cityCode) {
        this.ac = ac;
        this.cityCode = cityCode;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<List<StoryModel>> call =
                retrofit.create(ApiInterface.class).getStoryEvents(cityCode);
        call.enqueue(new Callback<List<StoryModel>>() {
            @Override
            public void onResponse(Call<List<StoryModel>> call, retrofit2.Response<List<StoryModel>> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.errorBody().string());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<List<StoryModel>> call, Throwable t) {
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


