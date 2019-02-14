package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;



public class SobreActivity extends AppCompatActivity {
    
    DialogHelper dialogHelper;
    SincabsServer sincabsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sobre);
        
        dialogHelper = new DialogHelper(SobreActivity.this);
        sincabsServer = new SincabsServer(SobreActivity.this);
    }

    public void fecharJanela(View view) {

        finish();
    }

    public void abrirTermosDeUso(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SobreActivity.this, TermosECondicoesDeUsoActivity.class);
                startActivity(i);
            }
        });
    }
}
