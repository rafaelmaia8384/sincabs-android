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


public class FragmentLoginActivityCadastroPasso3 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_cadastro_passo3, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.imagemPerfilClick).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.selecionarImagem = 1;

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

                if (getActivity().findViewById(R.id.fragment_loginactivity_cadastro_passo3_imagemPerfil).getTag() == null) {

                    LoginActivity.dialogHelper.showError("Selecione uma imagem de perfil.");

                    return;
                }

                DataHolder.getInstance().salvarImagem(getActivity(), LoginActivity.dialogHelper, LoginActivity.imagemUri, true, false, null);

                Fragment fragment = new FragmentLoginActivityCadastroPasso4();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                ft.replace(R.id.frameLayoutLoginActivity, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
