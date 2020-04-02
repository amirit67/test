package ir.payebash.remote;


import android.app.Activity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ir.payebash.Application;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;
import ir.payebash.models.event.EventModel;
import ir.payebash.remote.repository.RemoteRepository;
import retrofit2.Retrofit;

public class AsynctaskGetPost {

    @Inject
    Retrofit retrofit;
    IWebservice delegate;
    private Activity ac;
    private Map<String, String> params;
    private ApiInterface call;

    public AsynctaskGetPost(Activity ac,
                            Map<String, String> params,
                            IWebservice delegate) {
        this.ac = ac;
        this.params = params;
        this.delegate = delegate;
        Application.getComponent().Inject(this);
        call = retrofit.create(ApiInterface.class);
    }

    public void getData() {

        params.put("", Application.preferences.getString("stateCode", ""));

        RemoteRepository remoteRepository = new RemoteRepository();
        remoteRepository.getAllEvents(params)
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


