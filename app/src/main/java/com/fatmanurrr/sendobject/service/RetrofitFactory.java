package com.fatmanurrr.sendobject.service;

import com.fatmanurrr.sendobject.item.Message;
import com.fatmanurrr.sendobject.item.PostResponse;
import com.fatmanurrr.sendobject.item.MessageRO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Lenovo on 2.09.2019.
 */

public class RetrofitFactory {

    private static RetrofitServices retrofitServices;

    private static String baseUrl = "http://192.168.1.35:8082/";

    public static RetrofitServices getService() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        int timeoutInterval = 60;
        OkHttpClient.Builder httpClientWithHeader = new OkHttpClient().newBuilder();
        httpClientWithHeader.addInterceptor(logging);
        httpClientWithHeader.connectTimeout((long) timeoutInterval, TimeUnit.SECONDS);
        httpClientWithHeader.readTimeout((long) timeoutInterval, TimeUnit.SECONDS);

        OkHttpClient client = httpClientWithHeader.build();
        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofitServices == null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            retrofitServices = retrofit.create(RetrofitServices.class);
        }
        return retrofitServices;
    }

    public interface RetrofitServices {
        @GET("likewp/v1/api.php?request=messages")
        Call<ArrayList<MessageRO>> getMessage();

        @FormUrlEncoded
        @POST("likewp/v1/api.php?request=messages")
        Call<PostResponse> addMessages(@FieldMap  Map<String,String> message );

    }
}


