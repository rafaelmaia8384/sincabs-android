package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;




public class FragmentLoginActivityCadastroPasso4 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_cadastro_passo4, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String senha1 = ((EditText)getActivity().findViewById(R.id.editTextSenha1)).getText().toString();
                String senha2 = ((EditText)getActivity().findViewById(R.id.editTextSenha2)).getText().toString();

                if (senha1.length() < 6 || senha1.length() > 20) {

                    LoginActivity.dialogHelper.showError("A senha deve conter entre 6 e 20 dígitos.");

                    return;
                }

                if (!senha1.equals(senha2)) {

                    LoginActivity.dialogHelper.showError("As senhas devem ser iguais.");

                    return;
                }

                DataHolder.getInstance().setCadastrarPasso4(senha1);

                LoginActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Fragment fragment = new FragmentLoginActivityCadastroPasso5();

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                        ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                        ft.replace(R.id.frameLayoutLoginActivity, fragment);
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();
                    }
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
