package com.example.gasstorelocater.UI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasstorelocater.Model.User;
import com.example.gasstorelocater.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.example.gasstorelocater.Constants.MAPVIEW_BUNDLE_KEY;

public class UserListFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "UserListFragment";

    //widgets
    private RecyclerView mUserListRecyclerView;
    private MapView mMapView;


    //vars
    private ArrayList<User> mUserList = new ArrayList<>();
//    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
//    private UserRecyclerAdapter mUserRecyclerAdapter;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
//    private UserLocation mUserPosition;
//    private ClusterManager mClusterManager;
//    private MyClusterManagerRenderer mClusterManagerRenderer;
//    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(mUserLocations.size() == 0){ // make sure the list doesn't duplicate by navigating back
//            if(getArguments() != null){
//                final ArrayList<User> users = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
//                mUserList.addAll(users);
//
//                final ArrayList<UserLocation> locations = getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
//                mUserLocations.addAll(locations);
//            }
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view);
        mMapView = view.findViewById(R.id.user_list_map);

//        initUserListRecyclerView();
        initGoogleMap(savedInstanceState);

//        for(UserLocation userLocation: mUserLocations){
//            Log.d(TAG, "onCreateView: user location: " + userLocation.getUser().getUsername());
//        }

//        setUserPosition();

        return view;
    }

    //loop through all the user in the chatroom and create cluster marker for them
    /*private void addMapMarkers(){
        if (mGoogleMap != null){
            if (mClusterManager != null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if (mClusterManager == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(getActivity(), mGoogleMap, mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            //adding markers in the google map
            for (UserLocation userLocation: mUserLocations){
                Log.d(TAG, "addMapMarkers: location: " +userLocation.getGeo_point().toString());
                try {
                    String snippet = "";
                    if (userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                        snippet = "This is you";
                    }
                    else {
                        snippet = "Determine route to "+userLocation.getUser().getUsername()+ " ?";
                    }
                    int avatar = R.drawable.cwm_logo;
                    try {
                        avatar = Integer.parseInt(userLocation.getUser().getAvatar());
                    }catch (NumberFormatException ex){
                        Log.d(TAG, "addMapMarkers: no avatar for "+ userLocation.getUser().getUsername()+ ", setting default avatar");
                    }

                    //add cluster marker to the map
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
                            userLocation.getUser().getUsername(),
                            snippet,
                            avatar,
                            userLocation.getUser()
                    );

                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                }catch (NullPointerException ex){
                    Log.e(TAG, "addMapMarkers: NullPointException: "+ ex.getMessage());
                }
            }
        }
    }

    private void setCameraView(){

        //Overall map view window 0.2 * 0.2 = 0.04
        double bottomBoundary = mUserPosition.getGeo_point().getLatitude() - .1;
        double leftBoundary = mUserPosition.getGeo_point().getLongitude() - .1;
        double topBoundary = mUserPosition.getGeo_point().getLatitude() + .1;
        double rightBoundary = mUserPosition.getGeo_point().getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    private void setUserPosition(){
        for (UserLocation userLocation:mUserLocations){
            if (userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                mUserPosition = userLocation;
            }
        }
    }*/

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    /*private void initUserListRecyclerView() {
        mUserRecyclerAdapter = new UserRecyclerAdapter(mUserList);
        mUserListRecyclerView.setAdapter(mUserRecyclerAdapter);
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        mGoogleMap = map;
//        addMapMarkers();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}



















