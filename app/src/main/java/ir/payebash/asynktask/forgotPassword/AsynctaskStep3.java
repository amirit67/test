package ir.payebash.asynktask.forgotPassword;


import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.BaseResponse;
import ir.payebash.models.ForgotPasswordModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskStep3 {

    @Inject
    Retrofit retrofit;
    IWebservice.IForgotPassword delegate;
    private ForgotPasswordModel params;

    public AsynctaskStep3(ForgotPasswordModel params,
                          IWebservice.IForgotPassword delegate) {
        this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<BaseResponse> call =
                retrofit.create(ApiInterface.class).forgotPassword3(params);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.errorBody().string());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                try {
                    delegate.getError(t.getLocalizedMessage());
                } catch (Exception e) {
                }
            }
        });
    }
}


