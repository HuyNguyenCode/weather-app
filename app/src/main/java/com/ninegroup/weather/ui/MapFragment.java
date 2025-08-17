package com.ninegroup.weather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;



import com.ninegroup.weather.R;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapboxMap mapboxMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = root.findViewById(R.id.mapView);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapboxMap = mapView.getMapboxMap();

        mapboxMap.setCamera(
                new CameraOptions.Builder()
                        .center(Point.fromLngLat(106.80280655508835, 10.869778736885038)) // UIT
                        .zoom(14.0)
                        .build()
        );
//
//        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS, style -> {
//            // Enable plugins correctly in v10
//            mapView.measure(10,10);
//            mapView.computeScroll();
//            mapView.setLeft(10);
//
//            mapView.getRotation();
//
//
//        });
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS);

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        mapView = null;
    }
}
