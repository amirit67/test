package ir.payebash.remote.repository;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.RegisterEventResponseModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Repository {

    /*
     * Get list of popular Events
     */
    Observable<List<EventModel>> getAllEvents(Map<String, String> params);


    Observable<RegisterEventResponseModel> RegisterEvent(@Part MultipartBody.Part[] surveyImage, @PartMap Map<String, RequestBody> params);
    /*
     * User Login & Register
     */


}
