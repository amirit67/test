package ir.payebash.asynktask;


import android.app.Activity;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.UserItem;
import ir.payebash.models.login.LoginModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskRegister {

    @Inject
    Retrofit retrofit;
    IWebservice.ILogin delegate;
    private Activity ac;
    private UserItem params;

    public AsynctaskRegister(Activity ac,
                             UserItem params,
                             IWebservice.ILogin delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<LoginModel> call =
                ApiClient.getClient().create(ApiInterface.class).inesrtUser(params);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                try {
                    if (response.body().getStatusCode() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.body().getMessage());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                try {
                    delegate.getError(t.getLocalizedMessage());
                } catch (Exception e) {
                }
            }
        });
    }
}


