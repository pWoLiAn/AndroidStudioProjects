package com.joelscaria98gmail.hospitalfinder.activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joelscaria98gmail.hospitalfinder.R;
import com.joelscaria98gmail.hospitalfinder.models.DistanceDuration;
import com.joelscaria98gmail.hospitalfinder.models.DistanceResult;
import com.joelscaria98gmail.hospitalfinder.models.PlaceList;
import com.joelscaria98gmail.hospitalfinder.models.SinglePlace;
import com.joelscaria98gmail.hospitalfinder.rest_api.GooglePlacesApi;
import com.joelscaria98gmail.hospitalfinder.rest_api.HospitalListClient;
import com.wang.avi.AVLoadingIndicatorView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.joelscaria98gmail.hospitalfinder.utils.LoadingUtil.enableDisableView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker curmarker;
    static LatLng defLocation = new LatLng(28.5, 77); //Delhi
    static LatLng curLocation = defLocation;

    int locationType = GooglePlacesApi.TYPE_HOSPITAL;
    int locationRankBy = GooglePlacesApi.RANKBY_PROMINENCE;

    public static final String  privacy_policy_url = "https://del.dog/lapherenul";

    LocationManager locMan;
    LocationListener locLis;
    Context ctx = this;

    boolean mapReady = false;
    float mapAccuracy = 10000;

    FrameLayout fader;
    AVLoadingIndicatorView avi;

    GooglePlacesApi googlePlacesApi;
    HospitalListClient hospitalListClient;
    PlaceList placeList;
    DistanceResult distanceResult;

    FrameLayout mainFrame;
    Button btnFilter, btnDetails;

    Spinner spinnerType, spinnerRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fader =  findViewById(R.id.fader);
        avi =  findViewById(R.id.avi);

        btnFilter =  findViewById(R.id.btnFilter);
        btnDetails = findViewById(R.id.btnDetails);
        mainFrame = findViewById(R.id.mainFrame);

        setLoadingAnimation();

        googlePlacesApi = new GooglePlacesApi(ctx);
        hospitalListClient = googlePlacesApi.getHospitalListClient();


        btnDetails.setOnClickListener(v -> showDetailList());

        btnFilter.setOnClickListener(v -> showOptionDialog());

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    123);
        } else {
            locMan = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean gpsEnabled, networkEnabled;

            gpsEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setMessage(getResources().getString(R.string.wifi_not_enabled));

                dialog.setPositiveButton(getResources().getString(R.string.open_network_settings),
                        (dialog1, which) -> {
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                            Toast.makeText(ctx, "Restart app after enabling Wi-Fi", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                dialog.show();
            } else {

                locLis = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mapAccuracy = location.getAccuracy();
                        if(mMap != null)
                            initMapPointer(curLocation);
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                locMan.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        100,
                        1,
                        locLis
                );

                locMan.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        100,
                        1,
                        locLis
                );
            }
        }

    }


    void setLoadingAnimation(){
        enableDisableView(mainFrame, false);
        fader.setVisibility(View.VISIBLE);
        avi.show();
    }

    void stopLoadingAnimation(){
        enableDisableView(mainFrame, true);
        fader.setVisibility(View.GONE);
        avi.hide();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMapPointer(curLocation);
    }


    void initMapPointer(LatLng loc){

        initCurrentPointer(loc);
        if(mapAccuracy!= 10000) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locMan.removeUpdates(locLis);
            getHospitalLocation(curLocation);
        }
    }

    void initCurrentPointer(LatLng loc){
        mMap.clear();

        curmarker = mMap.addMarker(new MarkerOptions().position(loc).title("Current Location")
                .snippet("("+loc.latitude+","+loc.longitude+")")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    void addMapMarker(LatLng loc, String name, String vicinity){
        mMap.addMarker(new MarkerOptions().position(loc).title(name)
                .snippet(vicinity)
        );

    }

    void getHospitalLocation(LatLng loc){

        HashMap<String,String> params = googlePlacesApi.getQueryParams(loc, locationType, locationRankBy);

        hospitalListClient.getNearbyHospitals(params).enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(@NonNull Call<PlaceList> call, @NonNull Response<PlaceList> response) {
                placeList = response.body();

                if(placeList != null && placeList.places != null && !placeList.places.isEmpty()){
                    int s = placeList.places.size();
                    int len = Math.min(s, 10);
                    for(int i = 0; i<len; i++){
                        SinglePlace place = placeList.places.get(i);
                        addMapMarker(place.getLoc(),place.getName(),place.getVicinity());
                    }

                    getDistance();
                }
                else {

                    Toast.makeText(ctx, "No results found in a 5km radius.",Toast.LENGTH_SHORT).show();
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation,14));

                mapReady = true;
                curmarker.showInfoWindow();
                stopLoadingAnimation();
            }

            @Override
            public void onFailure(@NonNull Call<PlaceList> call, @NonNull Throwable t) {
                Toast.makeText(ctx,"Unable to access server. Please try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showDetailList(){
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("itemList", Parcels.wrap(placeList.places));
        startActivity(i);
    }

    void getDistance(){
        if(placeList.places.isEmpty())
            return;

        String destination = "";

        for(int i=0; i<placeList.places.size()-1; i++){
            SinglePlace place = placeList.places.get(i);
            destination += place.getLoc().latitude+ "," +place.getLoc().longitude+ "|";
        }

        SinglePlace place = placeList.places.get(placeList.places.size()-1);
        destination += place.getLoc().latitude+ "," +place.getLoc().longitude;


        HashMap<String,String> params = new HashMap<>();
        params.put("key", GooglePlacesApi.WEB_KEY);
        params.put("origins",curLocation.latitude+","+curLocation.longitude);
        params.put("destinations",destination);

        hospitalListClient.getHospitalDistances(params).enqueue(new Callback<DistanceResult>() {
            @Override
            public void onResponse(@NonNull Call<DistanceResult> call, @NonNull Response<DistanceResult> response) {
                distanceResult = response.body();

                if(distanceResult != null){
                    ArrayList<DistanceDuration> distanceDurations = distanceResult.getRows().get(0).getElements();
                    if(distanceDurations == null)
                        return;

                    for(int i=0; i<distanceDurations.size(); i++){
                        DistanceDuration d = distanceDurations.get(i);

                        placeList.places.get(i).setDistance(d.getDistance().getValue());
                        placeList.places.get(i).setDistanceString(d.getDistance().getText());
                        placeList.places.get(i).setTimeMinutes(d.getDuration().getValue());
                        placeList.places.get(i).setTimeString(d.getDuration().getText());
                    }
                }
                else{
                    Toast.makeText(ctx, "Unable to fetch data from the server. Please try again later",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DistanceResult> call, @NonNull Throwable t) {
                Toast.makeText(ctx,"Unable to access server. Please try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showOptionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View optionView = inflater.inflate(R.layout.option_dialog_layout,null);

        spinnerType = optionView.findViewById(R.id.spinnerType);
        spinnerRank = optionView.findViewById(R.id.spinnerRank);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.array_option_type));
        ArrayAdapter<String> adapterRank = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.array_option_rank));

        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerRank.setAdapter(adapterRank);

        builder.setTitle("Filter Search");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Search", (dialog, which) -> {
            String typeString = spinnerType.getSelectedItem().toString();
            String rankString = spinnerRank.getSelectedItem().toString();

            if(googlePlacesApi.getType(typeString) == locationType && googlePlacesApi.getRank(rankString) == locationRankBy){
                Toast.makeText(ctx,"The selected filter has already been applied",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else {
                locationType = googlePlacesApi.getType(typeString);
                locationRankBy = googlePlacesApi.getRank(rankString);

                mapReady = false;
                setLoadingAnimation();
                initCurrentPointer(curLocation);
                getHospitalLocation(curLocation);
            }
        });

        builder.setView(optionView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 123){
            Toast.makeText(this,"Restart app to use permissions",Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.privacy) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(privacy_policy_url));
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
