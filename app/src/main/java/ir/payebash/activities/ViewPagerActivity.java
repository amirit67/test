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

package ir.payebash.activities;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileOutputStream;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ir.payebash.classes.BaseFragmentActivity;
import ir.payebash.classes.HSH;
import ir.payebash.classes.PermissionHandler;
import ir.payebash.fragments.ViewPagerFragment;
import ir.payebash.models.PayeItem;
import ir.payebash.R;

public class ViewPagerActivity extends BaseFragmentActivity {

    public static int number = 0;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ViewPager pager;
    private PayeItem feedgallery = new PayeItem();
    private RadioGroup RgIndicator;
    private RadioGroup.LayoutParams rprms;
    private String jpg = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        PagerAdapter pagerAdapter = null;
        try {
            feedgallery = (PayeItem) getIntent().getExtras().getSerializable("feed");
        } catch (Exception e) {
        }

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                RgIndicator.check(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pager.setAdapter(pagerAdapter);
        RgIndicator = findViewById(R.id.indicator);
        for (int i = 0; i < feedgallery.getImages().split(",").length; i++) {

            RadioButton rd = new RadioButton(ViewPagerActivity.this);
            rd.setButtonDrawable(R.drawable.selector_radio);
            rd.setPadding(0, 0, 0, 2);
            rd.setId(i);
            rprms = new RadioGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            RgIndicator.addView(rd, rprms);
        }
        RgIndicator.check(pager.getCurrentItem());
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            //RgIndicator.check(0);
            super.onBackPressed();
        } else {
            //RgIndicator.check(page.getCurrentItem());
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void shareimage(final String shareimg) {
        new PermissionHandler().checkPermission(ViewPagerActivity.this, permissions, new PermissionHandler.OnPermissionResponse() {
            @Override
            public void onPermissionGranted() {
                File dir = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath());

                if (!dir.exists())
                    dir.mkdirs();

                File file = new File(Environment.getExternalStoragePublicDirectory("PayeBash/Images").getPath() + "/" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg");

                try {

                    FileOutputStream outStream = new FileOutputStream(file);
                    ViewPagerFragment.myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    HSH.showtoast(getApplicationContext(), "در حافظه داخلی (Sdcard/PayeBash/Images)ذخیره گردید.");
                } catch (Exception e) {
                }

                if (!shareimg.equals("")) {
                    try {
                        if (file.exists()) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            Uri uri = Uri.fromFile(file);
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(share, "Share Image via"));
                        } else
                            HSH.showtoast(getApplicationContext(), "تصویری موجود نیست.");
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onPermissionDenied() {
                HSH.showtoast(ViewPagerActivity.this, "برای ذخیره و به اشتراک گذاری عکس دسترسی را صادر نمایید.");
            }
        });
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_save:
                shareimage("");
                break;
            case R.id.img_share:
                shareimage("share");
                break;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ViewPagerFragment fragment = new ViewPagerFragment();
            if (feedgallery.getImages().split(",")[position].startsWith("http://") ||
                    feedgallery.getImages().split(",")[position].startsWith("https://"))
                fragment.setAsset(feedgallery.getImages().split(",")[position]);
            else if (feedgallery.getImages().split(",")[position].contains("Thumb"))
                fragment.setAsset(getString(R.string.image) + "Thumbnail/" +
                        feedgallery.getImages().split(",")[position] +
                        jpg);
            else
                fragment.setAsset(getString(R.string.image) +
                        feedgallery.getImages().split(",")[position] +
                        jpg);
            return fragment;
        }

        @Override
        public int getCount() {
            number = feedgallery.getImages().split(",").length;
            return number;
        }
    }
}
