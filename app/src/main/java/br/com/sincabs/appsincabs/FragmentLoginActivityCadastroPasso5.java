package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import org.json.JSONObject;


public class FragmentLoginActivityCadastroPasso5 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_cadastro_passo_5, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.buttonAceito).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.dialogHelper.showProgress();

                LoginActivity.sincabsServer.enviarImagem(new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        String[] infoCadastro = DataHolder.getInstance().getInfoCadastro();

                        LoginActivity.sincabsServer.cadastrarUsuario(infoCadastro[0], infoCadastro[1], infoCadastro[2], infoCadastro[3], infoCadastro[4], infoCadastro[5], infoCadastro[6], infoCadastro[7], infoCadastro[8], new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

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

                    @Override
                    void onResponseError(String error) {

                        LoginActivity.dialogHelper.dismissProgress();
                        LoginActivity.dialogHelper.showError(error);
                    }

                    @Override
                    void onNoResponse(String error) {

                        LoginActivity.dialogHelper.dismissProgress();
                        LoginActivity.dialogHelper.showError(error);
                    }

                    @Override
                    void onPostResponse() {

                    }
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
