package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;


public class FragmentLoginActivityPosCadastroAnaliseDocumentalFinal extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_pos_cadastro_analise_documental_final, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.textViewCancelarCadastro).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.dialogHelper.confirmDialog(true, "Cancelar cadastro", "Deseja cancelar seu cadastro?\n\nSuas informações serão excluídas da plataforma e você não poderá concluir seu cadastro atual.", "Cancelar", new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        LoginActivity.dialogHelper.showProgress();

                        LoginActivity.sincabsServer.cancelarCadastro(new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

                                Storage storage = new Storage(getActivity());

                                storage.deleteFile(storage.getInternalFilesDirectory() + File.separator + LoginActivity.LAST_LOGIN_FILE);

                                FragmentManager fm = getActivity().getSupportFragmentManager();

                                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {

                                    fm.popBackStack();
                                }

                                Fragment fragment = new FragmentLoginActivityBemvindo();

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
