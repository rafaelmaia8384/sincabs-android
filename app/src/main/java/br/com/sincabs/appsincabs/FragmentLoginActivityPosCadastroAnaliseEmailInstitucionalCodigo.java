package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONObject;


public class FragmentLoginActivityPosCadastroAnaliseEmailInstitucionalCodigo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_pos_cadastro_analise_email_institucional_codigo, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String codigo = ((EditText)getActivity().findViewById(R.id.editTextCodigo)).getText().toString();

                if (codigo.length() != 6) {

                    LoginActivity.dialogHelper.showError("O código deve ter 6 dígitos.");

                    return;
                }

                LoginActivity.dialogHelper.showProgress();

                LoginActivity.sincabsServer.enviarCodigoConfirmacao(codigo, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        LoginActivity.dialogHelper.showSuccess(msg);

                        getActivity().onBackPressed();
                    }

                    @Override
                    void onResponseError(String error) {

                        LoginActivity.dialogHelper.showError(error);
                    }

                    @Override
                    void onNoResponse(String error) {

                        LoginActivity.dialogHelper.showError(error);
                    }

                    @Override
                    void onPostResponse() {

                        LoginActivity.dialogHelper.dismissProgress();
                    }
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
