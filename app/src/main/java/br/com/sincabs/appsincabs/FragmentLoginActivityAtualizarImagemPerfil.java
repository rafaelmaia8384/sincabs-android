package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;


public class FragmentLoginActivityAtualizarImagemPerfil extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_atualizar_imagem_perfil, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.imagemPerfilClick).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.selecionarImagem = 4;

                LoginActivity.dialogHelper.showProgress();

                CropImage.activity()
                        .setCropMenuCropButtonTitle("Concluir")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getActivity());
            }
        });

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (getActivity().findViewById(R.id.fragment_loginactivity_atualizar_imagem_perfil_imagemPerfil).getTag() == null) {

                    LoginActivity.dialogHelper.showError("Selecione uma imagem de perfil.");

                    return;
                }

                final DataHolder dh = DataHolder.getInstance();

                dh.salvarImagem(getActivity(), LoginActivity.dialogHelper, LoginActivity.imagemUri, true, false, new Runnable() {

                    @Override
                    public void run() {

                        LoginActivity.sincabsServer.atualizarImagemPerfilUsuario(dh.getLoginData("id_usuario"), dh.getLoginData("protect_hash"), dh.getLoginData("online_hash"), dh.getImgPrincipalSaved(), dh.getImgBuscaSaved(), new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

                                LoginActivity.dialogHelper.showSuccess(msg);

                                Fragment fragment = new FragmentLoginActivityLogin();

                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                                ft.replace(R.id.frameLayoutLoginActivity, fragment);
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
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
