package com.br4dev.foodtruck.webservice;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Ronan.lima on 29/03/16.
 */
public interface FoodTruckService {
    @POST("rest/json/trucklocation/apontar/{id}/{latitude}/{longitude}")
    Call<Void> marcaLocalizacaoUsuario(@Path("id")Integer id, @Path("latitude")double latitude,
                                              @Path("longitude")double longitutde);
}
