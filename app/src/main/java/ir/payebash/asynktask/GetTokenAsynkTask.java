package ir.payebash.asynktask;


import android.content.Context;

import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Models.TkModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class GetTokenAsynkTask {

    @Inject
    Retrofit retrofit;
    private Context cn;
    private IWebservice.ITkModel del;
    private Map<String, String> params;

    public GetTokenAsynkTask(Context cn, IWebservice.ITkModel del, Map<String, String> params) {
        this.cn = cn;
        this.del = del;
        this.params = params;
        Application.getComponent().Inject(this);
    }

    public void GetData() {
        try {
            Call<TkModel> call =
                    retrofit.create(ApiInterface.class).getToken(params);
            call.enqueue(new Callback<TkModel>() {
                @Override
                public void onResponse(Call<TkModel> call, retrofit2.Response<TkModel> response) {
                    try {
                        if (response.isSuccessful())
                            del.getResult(response.body());
                        else
                            del.getError(true);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<TkModel> call, Throwable t) {
                    try {
                        del.getError(false);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
            try {
                del.getError(false);
            } catch (Exception e1) {
            }
        }
    }
}


