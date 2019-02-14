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


public class FragmentLoginActivityPosCadastroAnaliseDocumental1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_pos_cadastro_analise_documental1, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.imagemFrenteClick).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.selecionarImagem = 2;

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

                if (getActivity().findViewById(R.id.fragment_loginactivity_pos_cadastro_analise_documental2_imagemFrente).getTag() == null) {

                    LoginActivity.dialogHelper.showError("Selecione a imagem do documento.");

                    return;
                }

                DataHolder.getInstance().salvarImagem(getActivity().getApplicationContext(), LoginActivity.dialogHelper, LoginActivity.imagemUri, true, false, new Runnable() {

                    @Override
                    public void run() {

                        LoginActivity.sincabsServer.enviarDocumentoFrente(DataHolder.getInstance().getImgPrincipalSaved(), DataHolder.getInstance().getImgBuscaSaved(), new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

                                Fragment fragment = new FragmentLoginActivityPosCadastroAnaliseDocumental2();

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
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
