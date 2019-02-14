package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;



public class TermosECondicoesDeUsoActivity extends AppCompatActivity {

    DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_termos_e_condicoes_de_uso);

        dialogHelper = new DialogHelper(TermosECondicoesDeUsoActivity.this);
    }

    public void fecharJanela(View view) {

        finish();
    }

    public void abrirTermosDeUsoOnline(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.sincabs.com.br/termos-e-condicoes-de-uso/"));

                startActivity(i);
            }
        });
    }
}
