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

import java.util.List;
import java.util.Map;

import ir.payebash.Models.CommentModel;
import ir.payebash.Models.GoogleOuathItem;
import ir.payebash.Models.NotifItem;
import ir.payebash.Models.NotifyData;
import ir.payebash.Models.PayeDetailsModel;
import ir.payebash.Models.PayeItem;
import ir.payebash.Models.PlusItem;
import ir.payebash.Models.ProfileItem;
import ir.payebash.Models.UserItem;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("api/getposts")
    Call<List<PayeItem>> getPosts(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/getUncomingPosts")
    Call<List<PayeItem>> getUncomingPosts(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/insertuser")
    Call<ResponseBody> inesrtUser(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/InsertPayment")
    Call<ResponseBody> InsertPayment(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/InsertComment")
    Call<CommentModel> InsertComment(@FieldMap Map<String, String> data);

    @GET("api/GetPostComments/{PostId}")
    Call<List<CommentModel>> GetComments(@Path("PostId") String UserId);

    @FormUrlEncoded
    @POST("api/UpdateProfile")
    Call<ResponseBody> UpdateProfile(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/GetUserVerification")
    Call<UserItem> checkPhoneNumber(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/InsertVote")
    Call<ResponseBody> Vote(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/PostFeedback")
    Call<ResponseBody> sendFeedBack(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/UpdateUserFbToken")
    Call<ResponseBody> UpdateUserFbToken(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/GetMyServices")
    Call<ResponseBody> UpdateMyServices(@FieldMap Map<String, String> data);

    @GET("api/GetMyServices/{UserId}")
    Call<ResponseBody> GetMyServices(@Path("UserId") String UserId);

    @GET("api/getServices/")
    Call<List<NotifItem>> GetServices();

    @POST("/fcm/send")
    Call<NotifyData.Message> sendMessage(@Body NotifyData.Message message);

    @Multipart
    @POST("api/InsertProfilePic")
    Call<ResponseBody> saveProfileAccount(@Part("UserId") RequestBody requestBody, @Part MultipartBody.Part filePart);

    @Multipart
    @POST("api/InsertImageEvent")
    Call<ResponseBody> saveImageEvent(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("api/insertpost")
    Call<ResponseBody> saveRequest(@PartMap Map<String, RequestBody> params);

    @GET("api/updateapp/")
    Call<ResponseBody> GetUpdate();

    @FormUrlEncoded
    @POST("api/getpostDetails/")
    Call<PayeDetailsModel> GetPostDetails(@FieldMap Map<String, String> data);

    @GET("api/getpostDetailsUpdate/{postId}")
    Call<ResponseBody> GetPostDetailsUpdate(@Path("postId") String postId);

    @FormUrlEncoded
    @POST("api/Deletepost")
    Call<ResponseBody> RemovePost(@FieldMap Map<String, String> data);

    @GET("api/Cancelpost/{postId}")
    Call<ResponseBody> CancelPost(@Path("postId") String postId);

    @FormUrlEncoded
    @POST("api/updateApplicants")
    Call<ResponseBody> UpdateApplicants(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/getprofiledetails")
    Call<ResponseBody> getProfileDetails(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/getprofiledetails")
    Call<ProfileItem> getProfileDetails2(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/GetUserIsAuthenticate/")
        //@GET("api/GetUserIsAuthenticate/{userId}")
    Call<UserItem> getAuthenticate(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/PostReport")
    Call<ResponseBody> SendingReportService(@FieldMap Map<String, String> data);

    @POST("/iid/v1/{token}/rel/topics/{topic}")
    Call<ResponseBody> Subscribe(@Path("token") String token, @Path("topic") String topic);

    @POST("/iid/v1:batchRemove")
    Call<ResponseBody> unSubscribe(@Body NotifyData.Data data);

    @GET
    Call<ResponseBody> fetchImage(@Url String url);

    @GET("oauth2/v3/tokeninfo")
    Call<GoogleOuathItem> OAuth2(@Query("id_token") String searchString);

    @FormUrlEncoded
    @POST("api/GetUserVerificationViaGoogle")
    Call<UserItem> GetUserVerificationViaGoogle(@FieldMap Map<String, String> data);

    @GET("plus/v1/people/me")
    Call<PlusItem> Plus(@Query("access_token") String searchString);

    @GET("/v1/people/me/?personFields=birthdays&fields=birthdays%2Fdate")
    Call<PlusItem> birthDay(@Query("access_token") String searchString);

    //https://accounts.google.com/o/oauth2/revoke?token=
}