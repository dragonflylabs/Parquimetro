package dflabs.io.parquimetro.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import dflabs.io.parquimetro.R;
import lib.lennken.utils.GPSTracker;

/**
 * Created by danielgarcia on 5/26/15.
 */
public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private GPSTracker gps;
    private Location mLocation;
    private UpdateLocation mLocationResult;
    private Marker mMarkerPosition;

    class UpdateLocation extends GPSTracker.LocationResult{

        @Override
        public void getLocation(Location location) {
            mLocation = location;
            setUpMap();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gps = new GPSTracker();
        mLocationResult = new UpdateLocation();
        gps.getLocation(getActivity(), mLocationResult);
        setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fr_maps_map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if(mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            if (mMarkerPosition == null) {
                mMarkerPosition = mMap.addMarker(new MarkerOptions().position(latLng).title("Mi posición"));
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 12)));
            } else {
                mMarkerPosition.setPosition(latLng);
            }
        }

    }
}
