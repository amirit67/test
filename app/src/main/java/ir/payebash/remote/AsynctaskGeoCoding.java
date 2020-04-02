package ir.payebash.remote;


import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.parsijoo.ParsijooItem;
import retrofit2.Call;
import retrofit2.Callback;

public class AsynctaskGeoCoding {

    private IWebservice.IAddress del;
    private String x = "", y = "";

    public AsynctaskGeoCoding(IWebservice.IAddress del, String x, String y) {
        this.del = del;
        this.x = x;
        this.y = y;
    }

    public void getData() {
        Call<ParsijooItem> call =
                ApiClient.getClient().create(ApiInterface.class).getAddress(x, y);
        call.enqueue(new Callback<ParsijooItem>() {
            @Override
            public void onResponse(Call<ParsijooItem> call, retrofit2.Response<ParsijooItem> response) {
                try {
                    if(response.body().getReqStatus().getStatusCode() == 200)
                        del.getResult(response.body());
                    else
                        del.getError(response.errorBody().string());
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<ParsijooItem> call, Throwable t) {
                try {
                    del.getError("");
                } catch (Exception e) {
                }
            }
        });
    }


}


