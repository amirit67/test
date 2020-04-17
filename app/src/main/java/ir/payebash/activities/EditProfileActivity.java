package ir.payebash.activities;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ir.payebash.R;
import ir.payebash.classes.BaseFragment;
import ir.payebash.classes.HSH;
import ir.payebash.databinding.FragmentEditProfileBinding;
import ir.payebash.models.user.UserInfoModel;
import ir.payebash.remote.repository.RemoteRepository;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class EditProfileActivity extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private UserInfoModel userInfoModel;
    public static EditProfileActivity fragment = null;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private View rootView = null;

    public static EditProfileActivity newInstance(UserInfoModel userInfoModel) {
        if (fragment == null)
            fragment = new EditProfileActivity();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, userInfoModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userInfoModel = (UserInfoModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            FragmentEditProfileBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_edit_profile, container, false);
            rootView = binding.getRoot();
            binding.setUserInfo(userInfoModel);
            rootView.findViewById(R.id.img_close).setOnClickListener(v -> getFragmentManager().popBackStack());
            rootView.findViewById(R.id.img_update).setOnClickListener(v -> SendUserInfo());
        }
        return rootView;
    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        ImageLoader.getInstance().displayImage(url, view);
    }
    /*private void getProfileInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.VoterUserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).getProfileDetails(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        setMainInfo(obj);
                        if (etGmail.getText().toString().length() > 10)
                            etGmail.setEnabled(false);
                        sv.setVisibility(View.VISIBLE);
                        cpv.setVisibility(View.GONE);
                        btnProfileUpdate.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }
                else
                    getProfileInfo();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                getProfileInfo();
            }
        });
    }*/

    private void SendUserInfo() {
        Call<ResponseBody> call =
                new RemoteRepository().UpdateProfile(userInfoModel.getEventOwner());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        HSH.showtoast(getActivity(), "اطلاعات شما ثبت گردید");
                        getFragmentManager().popBackStack();
                    } else
                        HSH.showtoast(getActivity(), response.errorBody().string());
                } catch (IOException e) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
