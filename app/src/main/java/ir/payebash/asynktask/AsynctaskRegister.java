package ir.payebash.asynktask;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import javax.inject.Inject;

import ir.payebash.Activities.MainActivity;
import ir.payebash.Activities.Register2Activity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Models.UserItem;
import ir.payebash.Models.user.LoginModel;
import ir.payebash.R;
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
                    if (response.code() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.errorBody().string());
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


