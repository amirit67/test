package ir.payebash.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import ir.payebash.R;
import ir.payebash.utils.roundedimageview.GpsUtils;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class NewAddressActivity extends Fragment {

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private MapView mMapView;
    private GoogleMap googleMap;
    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_new_address, container, false);


            mMapView = rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            try {
                MapsInitializer.initialize(getActivity());
            } catch (Exception e) {
            }
            mMapView.getMapAsync(googleMap1 -> {
                googleMap = googleMap1;

                googleMap.setOnMapClickListener(v ->
                {

                });
                googleMap.setOnCameraMoveListener(() -> {

                });

                googleMap.setOnCameraIdleListener(() -> {

                });

                new GpsUtils(getActivity()).turnGPSOn(isGPSEnable -> {
                    // turn on GPS
                    if (isGPSEnable)
                        requestPermissions(permissions, 123);
                    else
                        Toast.makeText(getActivity(), "برای نمایش موقعیت روی نقشه نیاز دسترسی به GPS می باشد", Toast.LENGTH_LONG).show();
                });

                if (mMapView != null &&
                        mMapView.findViewById(Integer.parseInt("1")) != null) {
                    View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    locationButton.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            locationButton.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    layoutParams.setMargins(0, 200, 30, 0);
                }
                //setMyLocation();
            });
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //presenter.takeView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        //presenter.dropView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mMapView.onDestroy();
        } catch (Exception e) {
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

   /* @OnClick(R.id.marker)
    public void SelectPoint() {
        *//*if (googleMap.getCameraPosition().zoom < 16)
            Toast.makeText(this(), "لطفا بیشتر زوم کنید", Toast.LENGTH_SHORT).show();
        else*//*
        {
            lat = googleMap.getCameraPosition().target.latitude + "";
            lng = googleMap.getCameraPosition().target.longitude + "";
            Location loc = new Location();
            loc.setLat(lat);
            loc.setLng(lng);
            presenter.IsScopeOfService(loc);
            //Toast.makeText(this(), lat + "", Toast.LENGTH_SHORT).show();
            //polyline.setWidth(3);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                ActivityCompat.requestPermissions(getActivity(), permissions, 123);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setMyLocation();
        } else {
            Toast.makeText(getActivity(), "مجوز دسترسی به مکان از جانب شما رد شد", Toast.LENGTH_SHORT).show();
        }
    }

    private void setMyLocation() {
        MyLocation();
    }

    private void MyLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            if (null == getActivity().getIntent().getExtras()) {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(command -> {
                            try {
                                if (command != null) {
                                    double latitude = command.getLatitude();
                                    double longitude = command.getLongitude();
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    //googleMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                } else
                                    MyLocation();
                            } catch (Exception e) {
                            }
                        })
                        .addOnFailureListener(getActivity(), e -> {

                        });
            }
        }
    }

    public interface OnPermissionResponse {
        void onPermissionGranted();

        void onPermissionDenied();
    }
        
}
