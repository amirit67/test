package ir.payebash.asynktask;


import android.app.Activity;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ir.payebash.Application;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;
import ir.payebash.models.event.EventModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AsynctaskGetPost {

    @Inject
    Retrofit retrofit;
    IWebservice delegate;
    private Activity ac;
    private Map<String, String> params;

    public AsynctaskGetPost(Activity ac,
                            Map<String, String> params,
                            IWebservice delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
    }

    public void getData() {
        params.put("", Application.preferences.getString("stateCode", ""));

        Observable<List<EventModel>> call =
                retrofit.create(ApiInterface.class).getPosts(params);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(events -> {
                    //Collections.sort(notes, (n1, n2) -> n2.getCity() - n1.getCity());
                    for (int i = 0; i < events.size(); i++)
                        events.get(i).setCity(ac.getResources().getStringArray(R.array.Citys)[Integer.parseInt(events.get(i).getCity())]);
                    return events;
                })
                .subscribe(new io.reactivex.Observer<List<EventModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<EventModel> response) {
                        try {
                            delegate.getResult(response);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            delegate.getError(e.getLocalizedMessage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onComplete() {
                    }
                });


    }
}


