package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;



import org.json.JSONObject;


public class FragmentLoginActivityCadastroPasso2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_cadastro_passo2, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String ddd = ((EditText)getActivity().findViewById(R.id.editTextDDD)).getText().toString();
                final String telefone = ((EditText)getActivity().findViewById(R.id.editTextTelefone)).getText().toString();
                final String email = ((EditText)getActivity().findViewById(R.id.editTextEmail)).getText().toString();
                final String matricula = ((EditText)getActivity().findViewById(R.id.editTextMatricula)).getText().toString();
                final int UFTrabalho = ((Spinner)getActivity().findViewById(R.id.spinnerUfTrabalho)).getSelectedItemPosition();

                if (ddd.length() < 3) {

                    LoginActivity.dialogHelper.showError("Informe o DDD do seu telefone (3 dígitos).");

                    return;
                }

                if (telefone.length() < 8) {

                    LoginActivity.dialogHelper.showError("Verifique seu número de telefone.");

                    return;
                }

                if (!AppUtils.validarEmail(email)) {

                    LoginActivity.dialogHelper.showError("Verifique seu endereço de e-mail.");

                    return;
                }

                if (!AppUtils.validarMatricula(matricula)) {

                    LoginActivity.dialogHelper.showError("Verifique sua matrícula.");

                    return;
                }

                if (UFTrabalho == 0) {

                    LoginActivity.dialogHelper.showError("Selecione sua região de trabalho.");

                    return;
                }

                LoginActivity.dialogHelper.showProgress();

                LoginActivity.sincabsServer.veririfcarUF(Integer.toString(UFTrabalho), new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        String numero = ddd + "-" + telefone;

                        DataHolder.getInstance().setCadastrarPasso2(numero, email, matricula, Integer.toString(UFTrabalho));

                        Fragment fragment = new FragmentLoginActivityCadastroPasso3();

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                        ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                        ft.replace(R.id.frameLayoutLoginActivity, fragment);
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();
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
