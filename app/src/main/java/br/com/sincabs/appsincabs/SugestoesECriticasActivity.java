package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;



import org.json.JSONObject;

public class SugestoesECriticasActivity extends AppCompatActivity {

    DialogHelper dialogHelper;
    SincabsServer sincabsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sugestoes_e_criticas);

        dialogHelper = new DialogHelper(SugestoesECriticasActivity.this);
        sincabsServer = new SincabsServer(SugestoesECriticasActivity.this);

        findViewById(R.id.buttonSugestao).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final EditText editTextTexto = (EditText)findViewById(R.id.editTextColaborar);

                String text = editTextTexto.getText().toString();

                text = AppUtils.formatarTexto(text);

                if (text.length() < 4) {

                    dialogHelper.showError("Escreva seu texto para enviar.");
                }
                else {

                    dialogHelper.showProgress();

                    sincabsServer.enviarSugestao(text, new SincabsResponse() {

                        @Override
                        void onResponseNoError(String msg, JSONObject extra) {

                            dialogHelper.showSuccess(msg);

                            editTextTexto.setText("");
                        }

                        @Override
                        void onResponseError(String error) {

                            dialogHelper.showError(error);
                        }

                        @Override
                        void onNoResponse(String error) {

                            dialogHelper.showError(error);
                        }

                        @Override
                        void onPostResponse() {

                            dialogHelper.dismissProgress();
                        }
                    });
                }
            }
        });
    }

    public void fecharJanela(View view) {

        finish();
    }
}
