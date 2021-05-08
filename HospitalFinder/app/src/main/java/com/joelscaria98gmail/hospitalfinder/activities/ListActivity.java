package com.joelscaria98gmail.hospitalfinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joelscaria98gmail.hospitalfinder.R;
import com.joelscaria98gmail.hospitalfinder.models.PlaceList;
import com.joelscaria98gmail.hospitalfinder.models.SinglePlace;
import com.joelscaria98gmail.hospitalfinder.recycler.HospitalListRecycler;
import com.joelscaria98gmail.hospitalfinder.rest_api.GooglePlacesApi;
import com.joelscaria98gmail.hospitalfinder.rest_api.HospitalListClient;
import com.wang.avi.AVLoadingIndicatorView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.joelscaria98gmail.hospitalfinder.utils.LoadingUtil.enableDisableView;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerHospital;
    ArrayList<SinglePlace> itemList;
    FrameLayout fader, listFrame;
    AVLoadingIndicatorView avi;
    TextView tvDisplayResult;

    GooglePlacesApi googlePlacesApi;
    HospitalListClient hospitalListClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerHospital = findViewById(R.id.recyclerHospital);
        recyclerHospital.setLayoutManager(new LinearLayoutManager(this));

        fader = findViewById(R.id.fader);
        listFrame = findViewById(R.id.content_main);
        avi = findViewById(R.id.avi);
        tvDisplayResult = findViewById(R.id.tvDisplayResult);


        stopLoadingAnimation();
        tvDisplayResult.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){

            setLoadingAnimation();
            String query = intent.getStringExtra(SearchManager.QUERY);

            toolbar.setTitle("Search results for '"+query+"'");

            googlePlacesApi = new GooglePlacesApi(getApplicationContext());
            hospitalListClient = googlePlacesApi.getHospitalListClient();

            HashMap<String, String > params = googlePlacesApi.getQueryParams(MainActivity.curLocation, GooglePlacesApi.TYPE_HOSPITAL, GooglePlacesApi.RANKBY_PROMINENCE);
            params.put("radius","50000");
            params.put("name", query);

            hospitalListClient.getNearbyHospitals(params).enqueue(new Callback<PlaceList>() {
                @Override
                public void onResponse(@NonNull Call<PlaceList> call, @NonNull Response<PlaceList> response) {
                    PlaceList placeList = response.body();

                    if(placeList != null){
                        stopLoadingAnimation();
                        itemList = placeList.places;
                        if(itemList.size() == 0)
                            tvDisplayResult.setVisibility(View.VISIBLE);
                        else
                            bindRecyclerView();

                    }

                }

                @Override
                public void onFailure(@NonNull Call<PlaceList> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(),"Unable to access server. Please try again later",Toast.LENGTH_SHORT).show();
                    tvDisplayResult.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            itemList = Parcels.unwrap(intent.getParcelableExtra("itemList"));
            bindRecyclerView();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    void bindRecyclerView(){
        HospitalListRecycler hospitalListRecycler = new HospitalListRecycler(itemList,this);
        recyclerHospital.setAdapter(hospitalListRecycler);
    }

    void setLoadingAnimation(){
        enableDisableView(listFrame, false);
        tvDisplayResult.setVisibility(View.INVISIBLE);
        fader.setVisibility(View.VISIBLE);
        avi.show();
    }

    void stopLoadingAnimation(){
        enableDisableView(listFrame, true);
        fader.setVisibility(View.GONE);
        avi.hide();
    }

}
