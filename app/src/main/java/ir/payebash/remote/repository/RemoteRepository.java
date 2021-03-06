package ir.payebash.remote.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.R;
import ir.payebash.classes.HSH;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.RegisterEventResponseModel;
import ir.payebash.models.event.detail.EventOwnerItem;
import ir.payebash.models.user.UserInfoModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RemoteRepository implements Repository {

    @Inject
    Retrofit retrofit;
    ApiInterface apiInterface;

    public RemoteRepository() {
        Application.getComponent().Inject(this);
        apiInterface = retrofit.create(ApiInterface.class);
    }


    @Override
    public Observable<List<EventModel>> getAllEvents(Map<String, String> params) {

        Observable<List<EventModel>> observable =
                apiInterface.getPosts(params)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

        return observable
                .flatMap((Function<List<EventModel>, Observable<List<EventModel>>>) persons ->
                        //Observable.fromArray(persons)
                        Observable.create(emitter -> {
                            try {
                                /*for (EventModel todo : eventModels) */
                                {
                                    emitter.onNext(persons);
                                }
                                emitter.onComplete();
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        })
                );
        /*.flatMap((Function<ExpertItem, Observable<ExpertItem>>) expertItem -> Observable.fromArray())*/
    }

    @Override
    public Observable<RegisterEventResponseModel> RegisterEvent(MultipartBody.Part[] surveyImage, Map<String, RequestBody> params) {
        Observable<RegisterEventResponseModel> observable =
                apiInterface.saveRequest(surveyImage, params)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

        return observable
                .flatMap((Function<RegisterEventResponseModel, Observable<RegisterEventResponseModel>>) persons ->
                        //Observable.fromArray(persons)
                        Observable.create(emitter -> {
                            try {
                                {
                                    emitter.onNext(persons);
                                }
                                emitter.onComplete();
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        })
                );
    }

    @Override
    public Call<ResponseBody> saveProfileAccount(MultipartBody.Part filePart) {
        Call<ResponseBody> call = retrofit.create(ApiInterface.class).saveProfileAccount(filePart);
        return call;
    }

    @Override
    public Call<ResponseBody> UpdateProfile(EventOwnerItem data) {
        Call<ResponseBody> call = retrofit.create(ApiInterface.class).UpdateProfile(data);
        return call;
    }


}
