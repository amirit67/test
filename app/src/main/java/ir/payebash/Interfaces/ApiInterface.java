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

import io.reactivex.Observable;
import ir.payebash.BuildConfig;
import ir.payebash.models.BaseResponse;
import ir.payebash.models.CommentModel;
import ir.payebash.models.ForgotPasswordModel;
import ir.payebash.models.GoogleOuathItem;
import ir.payebash.models.InputPostDetailsParamsModel;
import ir.payebash.models.MobileItem;
import ir.payebash.models.NotifItem;
import ir.payebash.models.NotifyData;
import ir.payebash.models.RequestItem;
import ir.payebash.models.contacts.ContactItem;
import ir.payebash.models.contacts.FollowItem;
import ir.payebash.models.event.RegisterEventResponseModel;
import ir.payebash.models.event.detail.EventOwnerItem;
import ir.payebash.models.event.detail.RequestStateItem;
import ir.payebash.models.googlePlus.PlusItem;
import ir.payebash.models.ProfileItem;
import ir.payebash.models.TkModel;
import ir.payebash.models.UserItem;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.models.event.story.StoryModel;
import ir.payebash.models.login.LoginModel;
import ir.payebash.models.parsijoo.ParsijooItem;
import ir.payebash.models.user.UserInfoModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
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

    @POST("api/login")
    Call<LoginModel> userLogin(@Body LoginModel data);

    @GET("api/ForgotPasswordSendSms/{mobile}")
    Call<BaseResponse> forgotPassword1(@Path("mobile") String mobile);

    @POST("api/ForgotPasswordCheckSms")
    Call<BaseResponse> forgotPassword2(@Body ForgotPasswordModel data);

    @POST("api/ForgotPasswordSubmitPassword")
    Call<BaseResponse> forgotPassword3(@Body ForgotPasswordModel data);

    @POST("api/registeruser")
        //insertuser
    Call<LoginModel> inesrtUser(/*@Header ("Set-Cookie") String cookie, */@Body UserItem data);

    @FormUrlEncoded
    @POST(BuildConfig.getTkn)
    Call<TkModel> getToken(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/getposts")
    Observable<List<EventModel>> getPosts(@FieldMap Map<String, String> data);

    @GET("api/getStoryEvents/{cityCode}")
    Call<List<StoryModel>> getStoryEvents(@Path("cityCode") String cityCode);

    @GET("api/getmyevents/{userId}")
    Observable<Response<List<EventModel>>> getMyEvents(@Path("userId") String userId);

    @GET("api/getUserInformation/{userId}")
    Call<UserInfoModel> getUserInformation(@Path("userId") String userId);

    @FormUrlEncoded
    @POST("api/getUncomingPosts")
    Call<List<EventModel>> getUncomingPosts(@FieldMap Map<String, String> data);

    @GET("api/getRequestToJoin/{userId}")
    Call<List<RequestItem>> getRequestToJoin(@Path("userId") String userId);

    @FormUrlEncoded
    @POST("api/InsertPayment")
    Call<ResponseBody> InsertPayment(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("api/InsertComment")
    Call<CommentModel> InsertComment(@FieldMap Map<String, String> data);

    @GET("api/GetPostComments/{PostId}")
    Call<List<CommentModel>> GetComments(@Path("PostId") String UserId);

    @POST("api/UpdateProfile")
    Call<ResponseBody>  UpdateProfile(@Body EventOwnerItem data);

    @FormUrlEncoded
    @POST("api/GetUserVerification")
    Call<UserItem> checkPhoneNumber(@FieldMap Map<String, String> data);

    @POST("api/checkcontacts")
    Call<List<ContactItem>> checkContacts(@Body MobileItem data);

    @GET("api/following/{followingId}")
    Call<FollowItem> following(@Path("followingId") String followingId);

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
    Observable<List<NotifItem>> GetServices();

    @POST("/fcm/send")
    Call<NotifyData.Message> sendMessage(@Body NotifyData.Message message);

    @Multipart
    @POST("api/InsertProfilePic")
    Call<ResponseBody> saveProfileAccount(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("api/InsertImageEvent")
    Call<ResponseBody> saveImageEvent(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("api/insertpost")
    Observable<RegisterEventResponseModel> saveRequest(@Part MultipartBody.Part[] surveyImage, @PartMap Map<String, RequestBody> params);

    @GET("api/updateapp/")
    Call<ResponseBody> GetUpdate();

    @POST("api/getpostDetails")
    Call<EventDetailsModel> GetEventDetails(@Body InputPostDetailsParamsModel data);

    @GET("api/getpostDetailsUpdate/{postId}")
    Call<ResponseBody> GetPostDetailsUpdate(@Path("postId") String postId);

    @FormUrlEncoded
    @POST("api/Deletepost")
    Call<ResponseBody> RemovePost(@FieldMap Map<String, String> data);

    @GET("api/Cancelpost/{postId}")
    Call<ResponseBody> CancelPost(@Path("postId") String postId);

    @GET("api/updateApplicants/{postId}")
    Call<RequestStateItem> UpdateApplicants(@Path("postId") String postId);

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

    @GET("/v1/people/skills")
    Call<PlusItem> Plus(@Query("access_token") String searchString);

    @GET("/v1/people/me/?personFields=birthdays&fields=birthdays%2Fdate")
    Call<PlusItem> birthDay(@Query("access_token") String searchString);

    @GET("web-service/v1/map/?type=address")
    Call<ParsijooItem> getAddress(@Query("x") String x, @Query("y") String y);

    //https://accounts.google.com/o/oauth2/revoke?token=
}