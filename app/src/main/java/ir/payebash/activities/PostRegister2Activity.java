package ir.payebash.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import ir.payebash.adapters.ImagesAdapter;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.databinding.ActivityPostRegisterBinding;
import ir.payebash.fragments.registerEvents.MobileConfirmStep1Fragment;
import ir.payebash.models.CustomGallery;
import ir.payebash.R;
import ir.payebash.models.event.detail.EventDetailsModel;
import ir.payebash.utils.FragmentStack;
import ir.payebash.utils.toggleSwitch.ToggleSwitch;


public class PostRegister2Activity extends AppCompatActivity implements View.OnClickListener {

    private Uri uri;
    public ArrayList<CustomGallery> map = new ArrayList<>();
    private ImagesAdapter adapter;
    private RecyclerView rv;
    //private TextView tvRegister;
    private EventDetailsModel eventDetailsModel = new EventDetailsModel();
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void initViews() {
        findViewById(R.id.bt_register).setOnClickListener(this::onClick);
        rv = findViewById(R.id.rv_images);
        rv.setHasFixedSize(true);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        rv.setLayoutManager(layoutManager);*/
        rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new ItemDecorationAlbumColumns(this, ItemDecorationAlbumColumns.VERTICAL_LIST));

        ToggleSwitch toggleSwitch = findViewById(R.id.textView55);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("رایگان");
        labels.add("پولی");
        toggleSwitch.setLabels(labels);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(this, "مجوز دسترسی به تصاویر از جانب شما رد شد", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        if (map.size() < 5) {
            File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath());
            if (!dir.exists())
                dir.mkdirs();

            View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
            BottomSheetDialog dialog = new BottomSheetDialog(PostRegister2Activity.this, R.style.BottomSheetDialog);
            dialog.setContentView(view);
            dialog.findViewById(R.id.tvGallery).
                    setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult
                                (Intent.createChooser
                                        (intent, HSH.setTypeFace(PostRegister2Activity.this, "انتخاب عکس")), 1);
                        dialog.dismiss();
                    });
            dialog.findViewById(R.id.tvCamera).
                    setOnClickListener(v -> {
                        try {
                            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images"), "file" + String.valueOf(System.currentTimeMillis() + ".jpg"));
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                uri = Uri.fromFile(file);
                            else
                                uri = FileProvider.getUriForFile(PostRegister2Activity.this, getPackageName() + ".provider", file);
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
            HSH.showtoast(PostRegister2Activity.this, "حداکثر 5 عکس قابل بارگذاری می باشد");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register);

        ActivityPostRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_post_register);
        binding.setItemEventRegister(eventDetailsModel);

        initViews();
        adapter = new ImagesAdapter(this, position -> ActivityCompat.requestPermissions(this, permissions, 123));
        rv.setAdapter(adapter);
        //initViews();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_register) {
            FragmentStack stack = new FragmentStack(PostRegister2Activity.this, getSupportFragmentManager(), R.id.frame);
            stack.replace(MobileConfirmStep1Fragment.newInstance(eventDetailsModel));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (requestCode == 100) {
                    ActivityCompat.requestPermissions(this, permissions, 123);
                }
            break;

            case 0:
                if (resultCode == PostRegister2Activity.this.RESULT_OK) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PostRegister2Activity.this);
                }
                break;
            case 1:
                if (resultCode == PostRegister2Activity.this.RESULT_OK && null != data.getData()) {
                    uri = data.getData();
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PostRegister2Activity.this);
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