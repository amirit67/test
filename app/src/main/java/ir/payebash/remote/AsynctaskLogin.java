package ir.payebash.remote;


import android.app.Activity;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.login.LoginModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskLogin {

    @Inject
    Retrofit retrofit;
    IWebservice.ILogin delegate;
    private Activity ac;
    private LoginModel params;

    public AsynctaskLogin(Activity ac,
                          LoginModel params,
                          IWebservice.ILogin delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<LoginModel> call =
                retrofit.create(ApiInterface.class).userLogin(params);
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


