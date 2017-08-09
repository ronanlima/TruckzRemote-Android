package com.br4dev.foodtruck.servidor;

import android.util.Log;

import com.br4dev.foodtruck.webservice.ServiceFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ronan.lima on 29/03/16.
 */
public class PonteComunicacaoServidor {

    public static final String TAG = "FoodTruck";

    public static void callWsToMarkLocaleUser(Integer id, double latitude, double longitude){
        Call<Void> call = ServiceFactory.getInstance().getFtService().marcaLocalizacaoUsuario(id, latitude, longitude);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccess()){
                    if (response.code() == 201){
                        Log.d(TAG,"Localização gravada com sucesso!");
                    } else {
                        Log.d(TAG,"A localização foi gravada com sucesso, porém houve algum problema com a resposta dessa solicitação.");
                    }
                } else {
                    Log.d(TAG,"Motivo do erro = "+response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "Falha a gravar localização do usuário. Causa = "+t.getMessage());
            }
        });
    }
}
