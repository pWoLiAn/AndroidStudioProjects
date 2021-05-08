package com.joelscaria98gmail.hospitalfinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joelscaria98gmail.hospitalfinder.R;
import com.joelscaria98gmail.hospitalfinder.models.DetailResult;
import com.joelscaria98gmail.hospitalfinder.models.DetailSinglePlace;
import com.joelscaria98gmail.hospitalfinder.rest_api.GooglePlacesApi;
import com.joelscaria98gmail.hospitalfinder.rest_api.HospitalListClient;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    GooglePlacesApi googlePlacesApi;
    HospitalListClient hospitalListClient;

    Context ctx;
    DetailSinglePlace place;


    ImageView imageView2;
    TextView tvName, tvAddress, tvPhone, tvWebsite, tvRating, tvOpening;
    Button btnCall, btnDirections;
    FrameLayout fader;
    AVLoadingIndicatorView avi;

    String placeId;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        lat = intent.getDoubleExtra("latitude", 28.5);
        lng = intent.getDoubleExtra("longitude", 77);

        imageView2 = findViewById(R.id.imageView2);
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvRating = findViewById(R.id.tvRating);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvOpening = findViewById(R.id.tvOpening);
        btnCall = findViewById(R.id.btnCall);
        btnDirections = findViewById(R.id.btnDirections);

        fader = findViewById(R.id.fader);
        avi = findViewById(R.id.avi);
        setLoadingAnimation();

        ctx = this;
        googlePlacesApi = new GooglePlacesApi(ctx);
        hospitalListClient = googlePlacesApi.getHospitalListClient();

        getDetails(placeId);
    }

    void setDetails() {
        btnCall.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + place.getPhone()));
            startActivity(i);
        });

        final String latitude = String.valueOf(lat), longitude = String.valueOf(lng);
        btnDirections.setOnClickListener(v -> {
            Uri navUri = Uri.parse("google.navigation:q=" + latitude + ',' + longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        tvName.setText(place.getName());

        checkAndSet(tvAddress, place.getAddress());
        checkAndSet(tvPhone, place.getPhone());
        checkAndSet(tvWebsite, place.getWebsite());
        checkAndSet(tvRating, String.valueOf(place.getRating()));

        if (place.getWebsite() != null) {
            tvWebsite.setTextColor(getResources().getColor(R.color.color_url));
            tvWebsite.setOnClickListener(view -> {
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(place.getWebsite()));
                startActivity(webIntent);
            });
        }

        String opening = "";
        if (place.getOpeningHours() != null) {
            for (String s : place.getOpeningHours().getWeekday()) {
                opening += s + "\n";
            }
            tvOpening.setText(opening);
        } else
            tvOpening.setText("-");

        stopLoadingAnimation();
    }

    void checkAndSet(TextView tv, String value) {
        if (value == null)
            tv.setText("-");
        else tv.setText(value);
    }

    void getDetails(String placeId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", GooglePlacesApi.WEB_KEY);
        params.put("placeid", placeId);

        hospitalListClient.getHospitalDetails(params).enqueue(new Callback<DetailResult>() {
            @Override
            public void onResponse(@NonNull Call<DetailResult> call, @NonNull Response<DetailResult> response) {
                assert response.body() != null;
                place = response.body().getResult();

                if (place == null)
                    Toast.makeText(ctx, "Unable to find hospital.", Toast.LENGTH_SHORT).show();
                else {
                    if (place.getPhotos() != null)
                        setPhoto(place.getPhotos().get(0).getPhotoReference());

                    setDetails();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailResult> call, @NonNull Throwable t) {
                Toast.makeText(ctx, "Unable to fetch details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setLoadingAnimation() {
        fader.setVisibility(View.VISIBLE);
        avi.show();
    }

    void stopLoadingAnimation() {
        fader.setVisibility(View.GONE);
        avi.hide();
    }

    void setPhoto(String photoReference) {
        if (photoReference == null)
            return;

        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=" + photoReference +
                "&key=" + GooglePlacesApi.WEB_KEY;

        Picasso.with(ctx)
                .load(url)
                .fit()
                .placeholder(R.drawable.medical_placeholder)
                .into(imageView2);
    }
}
