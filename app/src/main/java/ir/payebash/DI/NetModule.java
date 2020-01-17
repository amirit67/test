package ir.payebash.DI;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.FireBaseMessagingInteface;
import ir.payebash.R;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by KingStar on 3/2/2018.
 */

@Module
public class NetModule {
    String fbToken;

    public NetModule() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        try {
                            fbToken = task.getResult().getToken();
                        } catch (Exception e) {
                        }
                        return;
                    }
                });
    }

    @Provides
    @Singleton
    Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder
                //.setLenient()
                .create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(Cache cache) {
        /*OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        return client.build();*/
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Id",  "")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization",
                            "bearer " + Application.preferences.getString(Application.resources.getString(R.string.Token), "0000"))
                    .header(Application.resources.getString(R.string.UserId),
                            Application.preferences.getString(Application.resources.getString(R.string.UserId), "0000"))
                   /* .header("fbToken",
                            fbToken)*/
                    /*.header(BuildConfig.UserId,
                            Application.getInstance().getPreferences().getString(BuildConfig.UserId, ""))*/;
            Request request = requestBuilder.build();
            //long requestLength = request.body().contentLength();
            return chain.proceed(request);
        });
        return httpClient
                .cache(cache)
                .retryOnConnectionFailure(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    /*@Named("BaseUrl")*/
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return retrofit(okHttpClient);
    }

   /* @Provides
    @Singleton
    @Named("SendMessageUrl")
    Retrofit provideRetrofitSendMessage(OkHttpClient okHttpClient) {
        return retrofit(okHttpClient);
    }

    @Provides
    @Singleton
    @Named("SubscribeUrl")
    Retrofit provideRetrofitSubscribe(OkHttpClient okHttpClient) {
        return retrofit(okHttpClient);
    }

    @Provides
    @Singleton
    @Named("BirthDayUrl")
    Retrofit provideRetrofitBirthDay(OkHttpClient okHttpClient) {
        return retrofit(okHttpClient);
    }

    @Provides
    @Singleton
    @Named("GooglePlusUrl")
    Retrofit provideRetrofitGooglePlus(OkHttpClient okHttpClient) {
        return retrofit(okHttpClient);
    }*/

    private Retrofit retrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    @Provides
    @Singleton
    ApiInterface provideApiInterface(@Named("BaseUrl")  Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

    @Provides
    @Singleton
    FireBaseMessagingInteface.ISendMessage provideISendMessageInterface(@Named("SendMessageUrl") Retrofit retrofit) {
        return retrofit.create(FireBaseMessagingInteface.ISendMessage.class);
    }

    @Provides
    @Singleton
    FireBaseMessagingInteface.ISubscribe provideISubscribeInterface(@Named("SubscribeUrl") Retrofit retrofit) {
        return retrofit.create(FireBaseMessagingInteface.ISubscribe.class);
    }

    @Provides
    @Singleton
    FireBaseMessagingInteface.IBirthDay provideIBirthDayInterface(@Named("BirthDayUrl") Retrofit retrofit) {
        return retrofit.create(FireBaseMessagingInteface.IBirthDay.class);
    }

    @Provides
    @Singleton
    FireBaseMessagingInteface.IGooglePlus provideIGooglePlusInterface(@Named("GooglePlusUrl") Retrofit retrofit) {
        return retrofit.create(FireBaseMessagingInteface.IGooglePlus.class);
    }
}