package com.example.myapplication;

import com.example.myapplication.earthquake_data_retrieval.DataModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MyApiCall {

    //http://192.168.43.133:5000/

    @GET("two")
    Call<DataModel> getData();


}
