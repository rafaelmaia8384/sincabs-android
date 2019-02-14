package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;


public class FragmentLoginActivityPosCadastroAnaliseDocumental2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_pos_cadastro_analise_documental2, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.imagemVersoClick).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.selecionarImagem = 3;

                LoginActivity.dialogHelper.showProgress();

                CropImage.activity()
                        .setCropMenuCropButtonTitle("Concluir")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity());
            }
        });

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (getActivity().findViewById(R.id.fragment_loginactivity_pos_cadastro_analise_documental3_imagemVerso).getTag() == null) {

                    LoginActivity.dialogHelper.showError("Selecione a imagem do documento.");

                    return;
                }

                DataHolder.getInstance().salvarImagem(getActivity().getApplicationContext(), LoginActivity.dialogHelper, LoginActivity.imagemUri, true, false, new Runnable() {

                    @Override
                    public void run() {

                        LoginActivity.sincabsServer.enviarDocumentoVerso(DataHolder.getInstance().getImgPrincipalSaved(), DataHolder.getInstance().getImgBuscaSaved(), new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

                                //iniciar service para verificar se a análise documental foi concluída

                                Intent service = new Intent(getActivity(), SincabsServiceAnaliseDocumental.class);
                                service.putExtra("id_usuario", DataHolder.getInstance().getLoginData("id_usuario"));
                                getActivity().startService(service);

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
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
