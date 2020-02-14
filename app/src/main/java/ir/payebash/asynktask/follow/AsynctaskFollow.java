package ir.payebash.asynktask.follow;


import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.contacts.FollowItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskFollow {

    @Inject
    Retrofit retrofit;
    IWebservice.IFollow delegate;
    private String followingId;

    public AsynctaskFollow(IWebservice.IFollow delegate,
                           String followingId) {
        this.followingId = followingId;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        Call<FollowItem> call =
                retrofit.create(ApiInterface.class).following(followingId);
        call.enqueue(new Callback<FollowItem>() {
            @Override
            public void onResponse(Call<FollowItem> call, retrofit2.Response<FollowItem> response) {
                try {
                    if (response.code() == 200) {
                        delegate.getResult(response.body());
                    } else
                        delegate.getError(response.errorBody().string());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<FollowItem> call, Throwable t) {
                try {
                    delegate.getError(t.getLocalizedMessage());
                } catch (Exception e) {
                }
            }
        });
    }
}


