/*
Copyright 2014 David Morrissey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package ir.payebash.Interfaces;

import java.io.IOException;

import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL =
            Application.preferences.getString(
                    Application.resources.getString(R.string.baseUrl),
                    "http://developers.parsijoo.ir/"
            );
    private static Retrofit retrofit = null;
    private static Retrofit retrofit2 = null;
    private static Retrofit retrofit3 = null;
    private static Retrofit retrofit4 = null;
    private static Retrofit retrofit5 = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header(Application.resources.getString(R.string.app_name_en),
                                Application.resources.getString(R.string.Authorization)
                                        + Application.resources.getString(R.string.b).toUpperCase())
                .header("api-key",
                        Application.resources.getString(R.string.pr3ijo));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient2() {
        if (retrofit2 == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header(Application.resources.getString(R.string.auth),
                                    Application.resources.getString(R.string.Authorization)
                                            + Application.resources.getString(R.string.c)
                                            + Application.resources.getString(R.string.a)
                                            + Application.resources.getString(R.string.b))
                            .header(Application.resources.getString(R.string.contenttype), Application.resources.getString(R.string.applicationjson));
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient client = httpClient.build();
            retrofit2 = new Retrofit.Builder()
                    .baseUrl(Application.resources.getString(R.string.Notifurl))//url of FCM message server
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())//use for convert JSON file into object
                    .build();
        }
        return retrofit2;
    }

    public static Retrofit getClient3() {
        if (retrofit3 == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header(Application.resources.getString(R.string.auth),
                                    Application.resources.getString(R.string.Authorization)
                                            + Application.resources.getString(R.string.c)
                                            + Application.resources.getString(R.string.a)
                                            + Application.resources.getString(R.string.b))
                            .header(Application.resources.getString(R.string.contenttype), Application.resources.getString(R.string.applicationjson));
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient client = httpClient.build();
            retrofit3 = new Retrofit.Builder()
                    .baseUrl(Application.resources.getString(R.string.Notifurl2))//url of FCM message server
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())//use for convert JSON file into object
                    .build();
        }
        return retrofit3;
    }


    public static Retrofit getClient4() {
        if (retrofit4 == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header(Application.resources.getString(R.string.auth),
                                    Application.resources.getString(R.string.Authorization)
                                            + Application.resources.getString(R.string.c)
                                            + Application.resources.getString(R.string.a)
                                            + Application.resources.getString(R.string.b))
                            .header(Application.resources.getString(R.string.contenttype), Application.resources.getString(R.string.applicationjson));
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient client = httpClient.build();
            retrofit4 = new Retrofit.Builder()
                    .baseUrl(Application.resources.getString(R.string.oauth))//url of FCM message server
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())//use for convert JSON file into object
                    .build();
        }
        return retrofit4;
    }

    public static Retrofit getClient5() {
        if (retrofit5 == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header(Application.resources.getString(R.string.auth),
                                    Application.resources.getString(R.string.Authorization)
                                            + Application.resources.getString(R.string.c)
                                            + Application.resources.getString(R.string.a)
                                            + Application.resources.getString(R.string.b))
                            .header(Application.resources.getString(R.string.contenttype), Application.resources.getString(R.string.applicationjson));
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient client = httpClient.build();
            retrofit5 = new Retrofit.Builder()
                    .baseUrl("https://www.people.googleapis.com")//url of FCM message server
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())//use for convert JSON file into object
                    .build();
        }
        return retrofit5;
    }
}