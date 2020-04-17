package ir.payebash.fragments.user;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;
import ir.payebash.activities.EditProfileActivity;
import ir.payebash.activities.MyEventDetailsActivity;
import ir.payebash.activities.PostDetailsActivity;
import ir.payebash.activities.PostRegister2Activity;
import ir.payebash.adapters.ContactsAdapter;
import ir.payebash.adapters.PayeAdapter;
import ir.payebash.adapters.userInfo.userInfoAdapter;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.models.contacts.FollowItem;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.user.UserInfoModel;
import ir.payebash.remote.follow.AsynctaskFollow;
import ir.payebash.remote.repository.RemoteRepository;
import ir.payebash.remote.user.AsynctaskGetMyEvents;
import ir.payebash.remote.user.AsynctaskGetUserInfo;
import ir.payebash.utils.FragmentStack;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;

public class ActivitiesFragment extends Fragment {


    @Inject
    ImageLoader imageLoader;
    private Uri uri;
    private RecyclerView rv;
    /* private TextView txt_myads, txt_wanted;*/
    private SwipeRefreshLayout swipeContainer;
    private PayeAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Bundle bundle = null;
    private IWebservice m;
    private AsynctaskGetMyEvents getPost;

    private TextView tvUsername;
    private ImageView nav, imgAuth;
    private View rootView = null;
    private ProgressWheel pb;
    private RelativeLayout progressBar;
    private UserInfoModel userInfoModel;
    private Bundle bnd;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_activities, container, false);

            bnd = getActivity().getIntent().getExtras();
            userId = Application.preferences.getString(getString(R.string.UserId), "");

            DeclareElements();
            getUserInfo();
            getMyEvents();
            nav.setOnClickListener(v -> ((IWebservice.IBottomSheetNavigation) getActivity()).showBottomSheetNavigation());
            ((TitleMain) getContext()).FragName("رویدادها");
        }
        return rootView;
    }

    private void DeclareElements() {
        Application.getComponent().Inject(this);
        pb = rootView.findViewById(R.id.pb);
        progressBar = rootView.findViewById(R.id.progressBar);
        nav = rootView.findViewById(R.id.img_navigation);
        tvUsername = rootView.findViewById(R.id.tv_username);
        imgAuth = rootView.findViewById(R.id.img_auth);


        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));
        rv.setLayoutManager(layoutManager);
        adapter = new PayeAdapter(getActivity(), eventModel -> {
            FragmentStack fragmentStack = new FragmentStack(getActivity(), getActivity().getSupportFragmentManager(), R.id.main_frame);
            fragmentStack.replace(MyEventDetailsActivity.newInstance(eventModel));
        });
        rv.setAdapter(adapter);
        adapter.EditProfile((UserInfoHolder, x) -> {
            if(x == 1) {
                File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Profile").getPath());
                if (!dir.exists())
                    dir.mkdirs();

                View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
                BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
                dialog.setContentView(view);
                dialog.findViewById(R.id.tvGallery).
                        setOnClickListener(v -> {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            getActivity().startActivityForResult(pickPhoto, 1);
                            dialog.dismiss();
                        });
                dialog.findViewById(R.id.tvCamera).
                        setOnClickListener(v -> {
                            try {
                                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Profile"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                    uri = Uri.fromFile(file);
                                else
                                    uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                camIntent.putExtra("return-data", true);
                                getActivity().startActivityForResult(camIntent, 0);
                                dialog.dismiss();
                            } catch (Exception e) {
                            }
                        });
                dialog.show();
            }
            else if(x == 2)
            {
                FragmentStack fragmentStack = new FragmentStack(getActivity(), getActivity().getSupportFragmentManager(), R.id.main_frame);
                fragmentStack.replace(EditProfileActivity.newInstance(userInfoModel));
            }
            else
                follow(UserInfoHolder, userInfoModel.getEventOwner().getId());
        });
    }

    private void getUserInfo() {
        IWebservice.IUserInfo del = new IWebservice.IUserInfo() {
            @Override
            public void getResult(UserInfoModel userInfo) throws Exception {
                userInfoModel = new UserInfoModel();
                userInfoModel = userInfo;
                tvUsername.setText(userInfoModel.getEventOwner().getUserName());
                imgAuth.setVisibility(userInfoModel.getEventOwner().getVerifiedAccount() ? View.VISIBLE : View.GONE);
                progressBar.setVisibility(View.GONE);
                adapter.addUserInfo(userInfoModel);
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };
        new AsynctaskGetUserInfo(del, null == bnd
        ? userId : bnd.getString("UserId")).getData();
    }

    private void getMyEvents() {
        m = new IWebservice() {
            @Override
            public void getResult(List< EventModel > list) throws Exception {
                try {
                    List< EventModel > feed = new ArrayList<>();
                    swipeContainer.setRefreshing(false);
                    pb.setVisibility(View.GONE);
                    feed.add(null);
                    feed.addAll(list);
                    adapter.addItems(feed);
                } catch (Exception e) {
                }
            }

            @Override
            public void getError(String s) throws Exception {
                HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");
                swipeContainer.setRefreshing(false);
                pb.setVisibility(View.GONE);
            }
        };
        getPost = new AsynctaskGetMyEvents(getActivity(), m,
                null == bnd ? userId : bnd.getString("UserId"));
        getPost.getData();
        swipeContainer.setOnRefreshListener(() -> {
            adapter.ClearFeed();
            getPost.getData();
        });
    }

    private void saveProfileAccount(final Uri imageUri) {
        File file = new File(imageUri.getPath());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(String.valueOf(Calendar.getInstance().getTimeInMillis()), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));

        new RemoteRepository().saveProfileAccount(filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        String s = response.body().string();
                        if (!s.contains("https://"))
                            HSH.editor(getString(R.string.ProfileImage), BuildConfig.BaseUrl + "/Images/Users/" + s + ".jpg");
                        else
                            HSH.editor(getString(R.string.ProfileImage), s);
                        HSH.showtoast(getActivity(), "با موفقیت بروزرسانی گردید");
                    } catch (Exception e) {
                    }
                else
                    saveProfileAccount(imageUri);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                saveProfileAccount(imageUri);
            }
        });
    }

    private void follow(PayeAdapter.UserInfoHolder contactHolder, String followingId) {
        IWebservice.IFollow del = new IWebservice.IFollow() {
            @Override
            public void getResult(FollowItem followItem) throws Exception {

                contactHolder.pb.setVisibility(View.GONE);
                contactHolder.tvFollow.setVisibility(View.VISIBLE);
                contactHolder.tvFollow.setBackgroundResource(followItem.getIsFollow().equals("0")
                        ? R.drawable.rounded_corners_solid_strok_gray : R.drawable.rounded_corners_solid_blue);
                contactHolder.tvFollow.setText(followItem.getIsFollow().equals("0")
                        ? "فالو کن" : "فالو کردی");
                contactHolder.tvFollow.setTextColor(followItem.getIsFollow().equals("0")
                        ? Color.BLACK : Color.WHITE);
            }

            @Override
            public void getError(String error) throws Exception {
                swipeContainer.setRefreshing(false);
                pb.setVisibility(View.GONE);
                HSH.showtoast(getActivity(), error);
            }
        };
        new AsynctaskFollow(del, followingId).getData();
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //txt_name.setText(Application.preferences.getString(getString(R.string.FullName), ""));
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        uri = data.getData();
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());
                    }
                    break;
            }
        } catch (Exception e) {
        }

        if (resultCode == -1 && (requestCode != 0 && requestCode != 1 && null != data))
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                //img_profile.setImageURI(result.getUri());
                if (NetworkUtils.getConnectivity(getActivity()) != false)
                    saveProfileAccount(result.getUri());
                else
                    HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");

            } catch (Exception e) {
            }
    }

    //https://github.com/square/retrofit/issues/662

}
