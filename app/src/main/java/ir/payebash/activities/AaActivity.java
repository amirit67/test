package ir.payebash.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import ir.payebash.Adapters.ImagesAdapter;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.ItemDecorationAlbumColumns;
import ir.payebash.Classes.PermissionHandler;
import ir.payebash.Fragments.loginRegister.ForgotPasswordFragment;
import ir.payebash.models.CustomGallery;
import ir.payebash.R;


public class AaActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri uri;
    public ArrayList<CustomGallery> map = new ArrayList<>();
    TextView txtForgotPassword;
    ImagesAdapter adapter;
    RecyclerView rv;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void initViews() {
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register2);

        rv = findViewById(R.id.rv_images);
        rv.setHasFixedSize(true);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        rv.setLayoutManager(layoutManager);*/
        rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new ItemDecorationAlbumColumns(this, ItemDecorationAlbumColumns.VERTICAL_LIST));
        adapter = new ImagesAdapter(this, position -> {

            new PermissionHandler().checkPermission(AaActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
                @Override
                public void onPermissionGranted() {
                    if (map.size() < 5) {
                        File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath());
                        if (!dir.exists())
                            dir.mkdirs();

                        View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
                        BottomSheetDialog dialog = new BottomSheetDialog(AaActivity.this, R.style.BottomSheetDialog);
                        dialog.setContentView(view);
                        view.findViewById(R.id.tvGallery).
                                setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult
                                            (Intent.createChooser
                                                    (intent, HSH.setTypeFace(AaActivity.this, "انتخاب عکس")), 1);
                                    dialog.dismiss();
                                });
                        view.findViewById(R.id.tvCamera).
                                setOnClickListener(v -> {
                                    try {
                                        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                            uri = Uri.fromFile(file);
                                        else
                                            uri = FileProvider.getUriForFile(AaActivity.this, getPackageName() + ".provider", file);
                                        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                        camIntent.putExtra("return-data", true);
                                        startActivityForResult(camIntent, 0);
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                    }
                                });
                        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
                        dialog.show();

                    } else
                        HSH.showtoast(AaActivity.this, "حداکثر 5 عکس قابل بارگذاری می باشد");
                }

                @Override
                public void onPermissionDenied() {
                    HSH.showtoast(AaActivity.this, "برای انتخاب عکس دسترسی  را صادر نمایید.");
                }
            });
        });
        rv.setAdapter(adapter);
        //initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_forgot_password) {
            HSH.openFragment(this, new ForgotPasswordFragment());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == AaActivity.this.RESULT_OK) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AaActivity.this);
                }
                break;
            case 1:
                if (resultCode == AaActivity.this.RESULT_OK && null != data.getData()) {
                    uri = data.getData();
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AaActivity.this);
                }
                break;
        }

        if (resultCode == -1 && (requestCode != 0 && requestCode != 1 && requestCode != 456) && null != data) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                showimg(new File(result.getUri().getPath()));
            } catch (Exception e) {
            }
        }
    }

    private void showimg(File fils) {
        CustomGallery item = new CustomGallery();
        item.sdcardPath = fils.getAbsolutePath();
        map.add(item);
        adapter.addItems(item);
        //rv.setAdapter(adapter);
    }
}