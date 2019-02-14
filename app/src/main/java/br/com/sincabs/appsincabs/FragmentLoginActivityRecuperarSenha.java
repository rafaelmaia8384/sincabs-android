package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;



import org.json.JSONObject;


public class FragmentLoginActivityRecuperarSenha extends Fragment {

    private TextWatcher mascaraCPF;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_recuperar_senha, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        EditText editTextCPF = (EditText) getActivity().findViewById(R.id.editTextCPF);

        mascaraCPF = MascaraCPF.insert("###.###.###-##", editTextCPF);
        editTextCPF.addTextChangedListener(mascaraCPF);

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String cpf = AppUtils.limparCPF(((EditText)getActivity().findViewById(R.id.editTextCPF)).getText().toString());
                final String matricula = ((EditText)getActivity().findViewById(R.id.editTextMatricula)).getText().toString();
                final int instituicao = ((Spinner)getActivity().findViewById(R.id.spinnerOcupacaoProfissional)).getSelectedItemPosition();

                if (!AppUtils.validarCPF(cpf)) {

                    LoginActivity.dialogHelper.showError("Verifique seu CPF.");

                    return;
                }

                if (!AppUtils.validarMatricula(matricula)) {

                    LoginActivity.dialogHelper.showError("Verifique sua matrícula.");

                    return;
                }

                if (instituicao == 0) {

                    LoginActivity.dialogHelper.showError("Selecione sua ocupação profissional.");

                    return;
                }

                LoginActivity.dialogHelper.showProgress();

                LoginActivity.sincabsServer.recuperarSenhaIdAparelho(cpf, matricula, Integer.toString(instituicao), new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        try {

                            if (extra.getString("id_aparelho").equals("confirmado")) {

                                String[] data = {cpf, matricula, Integer.toString(instituicao)};

                                DataHolder.getInstance().setRecuperarSenhaData(data);;

                                Fragment fragment = new FragmentLoginActivityRecuperarSenhaInserir();

                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                                ft.replace(R.id.frameLayoutLoginActivity, fragment);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                            }
                        }
                        catch (Exception e) {

                            FragmentManager fm = getActivity().getSupportFragmentManager();

                            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {

                                fm.popBackStack();
                            }

                            Fragment fragment = new FragmentLoginActivityLogin();

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                            ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                            ft.replace(R.id.frameLayoutLoginActivity, fragment);
                            ft.commitAllowingStateLoss();

                            LoginActivity.dialogHelper.showSuccess(msg);
                        }
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
