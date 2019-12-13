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

import ir.payebash.Models.GoogleOuathItem;
import ir.payebash.Models.NotifyData;
import ir.payebash.Models.PlusItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FireBaseMessagingInteface {

    interface ISubscribe {
        //https://iid.googleapis.com
        @POST("/iid/v1/{token}/rel/topics/{topic}")
        Call<ResponseBody> Subscribe(@Path("token") String token, @Path("topic") String topic);

        //https://iid.googleapis.com
        @POST("/iid/v1:batchRemove")
        Call<ResponseBody> unSubscribe(@Body NotifyData.Data data);
    }

    interface IGooglePlus {
        //https://www.googleapis.com/
        @GET("plus/v1/people/me")
        Call<PlusItem> Plus(@Query("access_token") String searchString);
    }

    interface IBirthDay {
        //https://www.people.googleapis.com
        @GET("/v1/people/me/?personFields=birthdays&fields=birthdays%2Fdate")
        Call<PlusItem> birthDay(@Query("access_token") String searchString);
    }

    interface ISendMessage {
        //https://fcm.googleapis.com
        @POST("/fcm/send")
        Call<NotifyData.Message> sendMessage(@Body NotifyData.Message message);
    }

    @GET("oauth2/v3/tokeninfo")
    Call<GoogleOuathItem> OAuth2(@Query("id_token") String searchString);








    //https://accounts.google.com/o/oauth2/revoke?token=
}