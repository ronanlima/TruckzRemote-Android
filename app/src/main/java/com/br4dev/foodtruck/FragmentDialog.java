package com.br4dev.foodtruck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

/**
 * Created by Ronan.lima on 28/03/16.
 */
public class FragmentDialog extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_logo_fragment);
        showLogoDialog();
    }

    private void showLogoDialog() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final ShowLogoDialog logoDialog = new ShowLogoDialog();
        logoDialog.setCancelable(false);
        logoDialog.show(fm, "fragment_logo_dialog");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2 * 1000);
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                    logoDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("FoodTruck","Deu ruim na thread para mostrar a logo do app. "+e.getMessage());
                }
            }
        };
        thread.start();
    }

}
