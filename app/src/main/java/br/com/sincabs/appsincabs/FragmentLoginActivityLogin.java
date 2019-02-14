package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;


public class FragmentLoginActivityLogin extends Fragment {

    private TextWatcher mascaraCPF;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loginactivity_login, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        EditText editTextCPF = (EditText) getActivity().findViewById(R.id.fragment_loginactivity_login_editTextCPF);

        mascaraCPF = MascaraCPF.insert("###.###.###-##", editTextCPF);
        editTextCPF.addTextChangedListener(mascaraCPF);

        if (LoginActivity.storage.isFileExist(LoginActivity.storage.getInternalFilesDirectory() + File.separator + LoginActivity.LAST_LOGIN_FILE)) {

            String jsonString = LoginActivity.storage.readTextFile(LoginActivity.storage.getInternalFilesDirectory() + File.separator + LoginActivity.LAST_LOGIN_FILE);

            try {

                JSONObject jsonObject = new JSONObject(jsonString);

                ((EditText)getActivity().findViewById(R.id.fragment_loginactivity_login_editTextCPF)).setText(jsonObject.getString("cpf"));
                ((EditText)getActivity().findViewById(R.id.fragment_loginactivity_login_editTextSenha)).setText(jsonObject.getString("senha"));
            }
            catch (Exception e) { }
        }

        getActivity().findViewById(R.id.buttonVoltar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Fragment fragment = new FragmentLoginActivityBemvindo();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                ft.replace(R.id.frameLayoutLoginActivity, fragment);
                ft.commitAllowingStateLoss();
            }
        });

        getActivity().findViewById(R.id.textViewEsqueciSenha).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LoginActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Fragment fragment = new FragmentLoginActivityRecuperarSenha();

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

        getActivity().findViewById(R.id.buttonEntrar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String senha = ((EditText) getActivity().findViewById(R.id.fragment_loginactivity_login_editTextSenha)).getText().toString();
                final String cpf = ((EditText) getActivity().findViewById(R.id.fragment_loginactivity_login_editTextCPF)).getText().toString();

                if (!AppUtils.validarCPF(cpf)) {

                    LoginActivity.dialogHelper.showError("Verifique seu CPF.");

                    return;
                }

                if (senha.length() < 6) {

                    LoginActivity.dialogHelper.showError("Verifique sua senha.");

                    return;
                }

                LoginActivity.dialogHelper.showProgress();

                LoginActivity.sincabsServer.login(AppUtils.limparCPF(cpf), senha, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        DataHolder dh = DataHolder.getInstance();

                        dh.setLoginData(extra);

                        Storage storage = new Storage(getActivity());

                        // caso o usuário consiga entrar, criar arquivo "last_login.data"
                        String loggedInFile = storage.getInternalFilesDirectory() + File.separator + LoginActivity.LAST_LOGIN_FILE;
                        storage.createFile(loggedInFile, "{\"cpf\":\"" + cpf + "\", \"senha\":\"" + senha + "\"}");

                        if (dh.getLoginData("analise_documental_concluida").equals("0")) {

                            LoginActivity.dialogHelper.dismissProgress();

                            dh.setLoggedIn(false);

                            Fragment fragment;

                            if (!dh.getLoginData("img_id_frente_principal").equals("null") && !dh.getLoginData("img_id_verso_principal").equals("null")) {

                                fragment = new FragmentLoginActivityPosCadastroAnaliseDocumentalFinal();
                            }
                            else if (!dh.getLoginData("img_id_frente_principal").equals("null") && dh.getLoginData("img_id_verso_principal").equals("null")) {

                                fragment = new FragmentLoginActivityPosCadastroAnaliseDocumental2();
                            }
                            else {

                                fragment = new FragmentLoginActivityPosCadastroAnaliseCadastral();
                            }

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                            ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                            ft.replace(R.id.frameLayoutLoginActivity, fragment);
                            ft.addToBackStack(null);
                            ft.commitAllowingStateLoss();
                        }
                        else {

                            if (dh.getLoginData("img_id_frente_principal").equals("fail")) {

                                LoginActivity.dialogHelper.dismissProgress();

                                dh.setLoggedIn(false);

                                Fragment fragment = new FragmentLoginActivityPosCadastroAnaliseDocumentalFalha();

                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                                ft.replace(R.id.frameLayoutLoginActivity, fragment);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                            }
                            else if (dh.getLoginData("img_principal").equals("null")) {

                                LoginActivity.dialogHelper.dismissProgress();

                                dh.setLoggedIn(false);

                                Fragment fragment = new FragmentLoginActivityAtualizarImagemPerfil();

                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                                ft.replace(R.id.frameLayoutLoginActivity, fragment);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                            }
                            else {

                                if (dh.getLoginData("telefone").length() < 2) {

                                    LoginActivity.dialogHelper.inputDialog("Telefone", "Precisamos do seu telefone para contato.\n\nDigite abaixo o número com DDD:", InputType.TYPE_NUMBER_FLAG_DECIMAL, new MaterialDialog.InputCallback() {

                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                            if (!TextUtils.isDigitsOnly(input)) {

                                                LoginActivity.dialogHelper.showError("Insira apenas números.");

                                                return;
                                            }

                                            if (input.length() < 10) {

                                                LoginActivity.dialogHelper.showError("Verifique o número digitado e forneça seu telefone com DDD.");

                                                return;
                                            }

                                            LoginActivity.dialogHelper.showProgress();

                                            LoginActivity.sincabsServer.enviarTelefone(input.toString(), new SincabsResponse() {

                                                @Override
                                                void onResponseNoError(String msg, JSONObject extra) {

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
                                else if (dh.getLoginData("admin").equals("1")) {

                                    MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            int id = ((RadioGroup)dialog.getCustomView().findViewById(R.id.radioGroup)).getCheckedRadioButtonId();

                                            if (id == -1) {

                                                LoginActivity.dialogHelper.showError("Escolha uma opção.");

                                                return;
                                            }

                                            String opcao = dialog.getCustomView().findViewById(id).getTag().toString();

                                            if (opcao.equals("1")) {

                                                Intent service = new Intent(getActivity(), SincabsServiceMensagemSistema.class);
                                                getActivity().startService(service);

                                                Intent i = new Intent(getActivity(), SincabsActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                getActivity().startActivity(i);
                                            }
                                            else {

                                                Intent i = new Intent(getActivity(), AdminActivity.class);
                                                getActivity().startActivity(i);
                                            }

                                            getActivity().finish();
                                        }
                                    };

                                    new MaterialDialog.Builder(getActivity())
                                            .title("Admin")
                                            .customView(R.layout.layout_opcao_admin, true)
                                            .positiveText("OK")
                                            .negativeText("Cancelar")
                                            .onPositive(positiveCallback)
                                            .show();
                                }
                                else {

                                    Intent service = new Intent(getActivity(), SincabsServiceMensagemSistema.class);
                                    getActivity().startService(service);

                                    Intent i = new Intent(getActivity(), SincabsActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    getActivity().startActivity(i);

                                    getActivity().finish();
                                }
                            }
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
