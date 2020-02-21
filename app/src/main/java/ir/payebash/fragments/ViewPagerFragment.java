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

package ir.payebash.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import ir.payebash.R;

public class ViewPagerFragment extends Fragment {

    private static final String BUNDLE_ASSET = "asset";
    public static SubsamplingScaleImageView imageView;
    public static Bitmap myBitmap;
    private String asset;

    public void setAsset(String asset) {
        this.asset = asset;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);

        if (savedInstanceState != null) {
            if (asset == null && savedInstanceState.containsKey(BUNDLE_ASSET)) {
                asset = savedInstanceState.getString(BUNDLE_ASSET);
            }
        }
        try {
            if (asset != null) {
                imageView = rootView.findViewById(R.id.imageView);
                File file = ImageLoader.getInstance().getDiskCache().get(asset);
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                try {
                    imageView.setDoubleTapZoomScale(25);
                    imageView.setMaxScale(7);
                    imageView.setImage(ImageSource.bitmap(myBitmap));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }


        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        View rootView = getView();
        if (rootView != null) {
            outState.putString(BUNDLE_ASSET, asset);
        }
    }
}
