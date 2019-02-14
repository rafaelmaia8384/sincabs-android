package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONObject;


public class FragmentLoginActivityPosCadastroAnaliseEmailInstitucional extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_pos_cadastro_analise_email_institucional, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email1 = ((EditText)getActivity().findViewById(R.id.editTextEmail1)).getText().toString();
                String email2 = ((EditText)getActivity().findViewById(R.id.editTextEmail2)).getText().toString();

                if (!email1.equals(email2)) {

                    LoginActivity.dialogHelper.showError("Os e-mails não conferem.");

                    return;
                }

                if (email1.length() == 0) {

                    LoginActivity.dialogHelper.showError("Digite os e-mails para continuar.");

                    return;
                }

                if (!AppUtils.validarEmail(email1)) {

                    LoginActivity.dialogHelper.showError("E-mails inválidos.");

                    return;
                }

                /*String parts[] = email1.split("@");

                if (!parts[1].contains(".gov.br")) {

                    LoginActivity.dialogHelper.showError("Os e-mails informados não parecem ser de uma instituição pública.");

                    return;
                }*/

                LoginActivity.dialogHelper.showProgress();

                LoginActivity.sincabsServer.enviarEmailInstitucional(email1, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        FragmentManager fm = getActivity().getSupportFragmentManager();

                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {

                            fm.popBackStack();
                        }

                        Fragment fragment = new FragmentLoginActivityPosCadastroAnaliseEmailInstitucionalCodigo();

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
