package com.example.gasstorelocater.UI;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gasstorelocater.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.gasstorelocater.Constants.ERROR_DIALOG_REQUEST;
import static com.example.gasstorelocater.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.gasstorelocater.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener
{

    private static final String TAG = "MainActivity";

    //widgets
    private ProgressBar mProgressBar;

    //vars
    private FirebaseFirestore mDb;
    private boolean mLocationPermissionGranted = false;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private UserLocation mUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);

        mDb = FirebaseFirestore.getInstance();
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        initSupportActionBar();
    }


//    private void getUserDetails(){
//        if(mUserLocation == null){
//            mUserLocation = new UserLocation();
//            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
//                    .document(FirebaseAuth.getInstance().getUid());
//
//            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful()){
//                        Log.d(TAG, "onComplete: successfully set the user client.");
//                        User user = task.getResult().toObject(User.class);
//                        mUserLocation.setUser(user);
//                        ((UserClient)(getApplicationContext())).setUser(user);
//                        getLastKnownLocation();
//                    }
//                }
//            });
//        }
//        else{
//            getLastKnownLocation();
//        }
//    }

//    private void getLastKnownLocation() {
//        Log.d(TAG, "getLastKnownLocation: called.");
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
//            @Override
//            public void onComplete(@NonNull Task<android.location.Location> task) {
//                if (task.isSuccessful()) {
//                    Location location = task.getResult();
//                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                    mUserLocation.setGeo_point(geoPoint);
//                    mUserLocation.setTimestamp(null);
//                    saveUserLocation();
//                }
//            }
//        });
//
//    }

//    private void saveUserLocation(){
//
//        if(mUserLocation != null){
//            DocumentReference locationRef = mDb
//                    .collection(getString(R.string.collection_user_locations))
//                    .document(FirebaseAuth.getInstance().getUid());
//
//            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
//                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
//                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
//                    }
//                }
//            });
//        }
//    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
//            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
//                    getUserDetails();

                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
//                getUserDetails();
            }
            else{
                getLocationPermission();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.action_sign_out:{
//                signOut();
//                return true;
//            }
//            case R.id.action_profile:{
//                startActivity(new Intent(this, ProfileActivity.class));
//                return true;
//            }
//            default:{
//                return super.onOptionsItemSelected(item);
//            }
//        }
//
//    }

    /*private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                MainActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }*/

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.codingwithmitch.googledirectionstest.Service.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    @Override
    public void onClick(View view) {
//        // Create a Uri from an intent string. Use the result to create an Intent.
//        Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//
//        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        // Make the Intent explicit by setting the Google Maps package
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        // Attempt to start an activity that can handle the Intent
//        startActivity(mapIntent);

        if (R.id.txt_add_store==view.getId()){
                Intent intent = new Intent(this, AddStores.class);
                startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, ListStoresActivity.class);
            startActivity(intent);
        }
    }


}
