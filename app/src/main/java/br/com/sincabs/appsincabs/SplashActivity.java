package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Intent i = getIntent();

        Intent intent = new Intent(SplashActivity.this, SincabsActivity.class);

        /*if (i.hasExtra("SincabsMessage")) {

            intent.putExtra("SincabsMessage", i.getStringExtra("SincabsMessage"));
        }*/

        startActivity(intent);

        finish();
    }
}