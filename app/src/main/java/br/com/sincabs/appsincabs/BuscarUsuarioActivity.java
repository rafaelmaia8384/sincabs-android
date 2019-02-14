package br.com.sincabs.appsincabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;



public class BuscarUsuarioActivity extends AppCompatActivity {

    DialogHelper dialogHelper;
    SincabsServer sincabsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buscar_usuario);

        dialogHelper = new DialogHelper(BuscarUsuarioActivity.this);
        sincabsServer = new SincabsServer(BuscarUsuarioActivity.this);

        findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String nome = ((EditText)findViewById(R.id.editTextNome)).getText().toString();

                int ocupacaoPrifissional = ((Spinner)findViewById(R.id.spinnerOcupacaoProfissional)).getSelectedItemPosition();

                if (nome.length() < 2) {

                    dialogHelper.showError("Verifique o nome digitado.");

                    return;
                }

                if (ocupacaoPrifissional == 0) {

                    dialogHelper.showError("Selecione a ocupação profissional.");

                    return;
                }

                DataHolder.getInstance().setBuscarUsuarioData(nome, ocupacaoPrifissional);

                hideKeyboard(BuscarUsuarioActivity.this);

                setResult(200);

                finish();
            }
        });
    }

    public void fecharJanela(View view) {

        finish();
    }

    public static void hideKeyboard(Activity activity) {

        View view = activity.findViewById(android.R.id.content);

        if (view != null) {

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
