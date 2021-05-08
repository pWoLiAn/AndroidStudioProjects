package com.joelscaria98gmail.hospitalfinder.rest_api;

import com.joelscaria98gmail.hospitalfinder.models.DetailResult;
import com.joelscaria98gmail.hospitalfinder.models.DistanceResult;
import com.joelscaria98gmail.hospitalfinder.models.PlaceList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface HospitalListClient {

    @GET("place/nearbysearch/json")
    Call<PlaceList> getNearbyHospitals (@QueryMap Map<String,String> params);

    @GET("distancematrix/json")
    Call<DistanceResult> getHospitalDistances(@QueryMap Map<String,String> params);

    @GET("place/details/json")
    Call<DetailResult> getHospitalDetails(@QueryMap Map<String ,String> params);
}
