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

package ir.payebash.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import ir.payebash.Activities.ViewPagerActivity;
import ir.payebash.Application;
import ir.payebash.DI.MainComponent;
import ir.payebash.Models.PayeItem;
import ir.payebash.R;


public class SlideShowFragment extends Fragment {

    private static final String BUNDLE_ASSET = "asset";
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private String asset;
    private PayeItem item;

    public SlideShowFragment() {
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public void setItem(PayeItem item) {
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_slideshow, container, false);
        Application.getComponent().Inject(this);
        if (savedInstanceState != null) {
            if (asset == null && savedInstanceState.containsKey(BUNDLE_ASSET)) {
                asset = savedInstanceState.getString(BUNDLE_ASSET);
            }
        }
        if (asset != null) {
            MainComponent component = Application.get((AppCompatActivity) getActivity()).getComponent();
            final ImageView imageView = rootView.findViewById(R.id.imgView);
            imageView.setOnClickListener(
                    view -> {
                        final Bundle bundle = new Bundle();
                        Intent i = new Intent(getActivity(), ViewPagerActivity.class);
                        bundle.putSerializable("feed", item);
                        i.putExtras(bundle);
                        startActivity(i);
                    }

            );
            imageLoader.displayImage(asset, imageView, options);
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
