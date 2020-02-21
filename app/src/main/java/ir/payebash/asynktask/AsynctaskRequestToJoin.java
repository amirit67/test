package ir.payebash.asynktask;


import android.app.Activity;

import java.util.List;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.RequestItem;
import ir.payebash.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskRequestToJoin {

    @Inject
    Retrofit retrofit;
    IWebservice.IRequestJoin delegate;
    private Activity ac;

    public AsynctaskRequestToJoin(Activity ac,
                                  IWebservice.IRequestJoin delegate) {
        this.ac = ac;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<List<RequestItem>> call =
                retrofit.create(ApiInterface.class).getRequestToJoin(Application.preferences.getString(ac.getString(R.string.UserId), ""));
        call.enqueue(new Callback<List<RequestItem>>() {
            @Override
            public void onResponse(Call<List<RequestItem>> call, retrofit2.Response<List<RequestItem>> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.errorBody().string());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<List<RequestItem>> call, Throwable t) {
                if (NetworkUtils.getConnectivity(ac) != false)
                    getData();
                else {
                    try {
                        delegate.getError("خطا در ارتباط با سرور");
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}


