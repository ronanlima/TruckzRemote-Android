package com.br4dev.foodtruck.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ronan.lima on 29/03/16.
 */
public class ServiceFactory {
    private static ServiceFactory instance;
    private static final String BASE_URL = "http://45.79.95.61/truckws/";

    private static FoodTruckService ftService;

    private ServiceFactory(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept","application/json")
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);
                return response;
            }
        });

        OkHttpClient client = okHttpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.ftService = retrofit.create(FoodTruckService.class);
    }

    public static synchronized ServiceFactory getInstance(){
        if (instance == null){
            instance = new ServiceFactory();
        }
        return instance;
    }

    public FoodTruckService getFtService(){
        return ftService;
    }

    public static void setFtService(FoodTruckService ftService){
        ServiceFactory.ftService = ftService;
    }
}
