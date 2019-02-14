package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class FragmentLoginActivityBemvindo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_bemvindo, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.buttonJaSouCadastrado).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Fragment fragment = new FragmentLoginActivityLogin();

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

        getActivity().findViewById(R.id.buttonQueroMeCadastrar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Fragment fragment = new FragmentLoginActivityCadastroPasso1();

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
