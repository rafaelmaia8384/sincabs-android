package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilUsuarioAdminActivity extends AppCompatActivity {
    
    DialogHelper dialogHelper;
    SincabsServer sincabsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil_usuario_admin);

        if (!ImageLoader.getInstance().isInited()) {

            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(imageOptions)
                    .diskCacheExtraOptions(640, 640, null)
                    .memoryCacheSize(16 * 1024 * 1024) // 16MB
                    .diskCacheSize(128 * 1024 * 1024)  // 128MB
                    .imageDownloader(new SincabsImageDownloader())
                    .build();

            ImageLoader.getInstance().init(config);
        }

        dialogHelper = new DialogHelper(PerfilUsuarioAdminActivity.this);
        sincabsServer = new SincabsServer(PerfilUsuarioAdminActivity.this);

        final DataHolder dh = DataHolder.getInstance();
        final ImageView imageView = findViewById(R.id.img_perfil);
        final ImageView imageIdFrente = findViewById(R.id.img_id_frente);
        final ImageView imageIdVerso = findViewById(R.id.img_id_verso);
        final ImageView imageLogo = findViewById(R.id.img_logo);

        if (!dh.getPerfilUsuarioAdminData("img_busca").equals("null")) {

            ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(dh.getPerfilUsuarioAdminData("img_busca")), imageView);
        }

        if (!dh.getPerfilUsuarioAdminData("img_id_frente_busca").equals("null") && !dh.getPerfilUsuarioAdminData("img_id_frente_busca").equals("fail")) {

            ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(dh.getPerfilUsuarioAdminData("img_id_frente_busca")), imageIdFrente);
        }

        if (!dh.getPerfilUsuarioAdminData("img_id_verso_busca").equals("null") && !dh.getPerfilUsuarioAdminData("img_id_verso_busca").equals("fail")) {

            ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(dh.getPerfilUsuarioAdminData("img_id_verso_busca")), imageIdVerso);
        }

        ImageLoader.getInstance().displayImage(SincabsServer.getLogoAddress(dh.getPerfilUsuarioAdminData("instituicao"), dh.getPerfilUsuarioAdminData("uf")), imageLogo);

        String ocupacao = dh.getPerfilUsuarioAdminData("instituicao");

        if (ocupacao.equals("3") || ocupacao.equals("4") || ocupacao.equals("6") || ocupacao.equals("8") || ocupacao.equals("9") || ocupacao.equals("10")) {

            ocupacao = pegarOcupacao(ocupacao);
        }
        else {

            if (ocupacao.equals("2") && dh.getPerfilUsuarioAdminData("uf").equals("23")) {

                ocupacao = "Brigada Militar, " + pegarUF(dh.getPerfilUsuarioAdminData("uf"));
            }
            else {

                ocupacao = pegarOcupacao(ocupacao) + ", " + pegarUF(dh.getPerfilUsuarioAdminData("uf"));
            }
        }

        ((TextView)findViewById(R.id.instituicao_uf)).setText(ocupacao);
        ((TextView)findViewById(R.id.nome_completo)).setText(dh.getPerfilUsuarioAdminData("nome_completo"));
        ((TextView)findViewById(R.id.cpf)).setText(AppUtils.formatarCPF(dh.getPerfilUsuarioAdminData("cpf")));
        ((TextView)findViewById(R.id.matricula)).setText(dh.getPerfilUsuarioAdminData("matricula"));
        ((TextView)findViewById(R.id.telefone)).setText(dh.getPerfilUsuarioAdminData("telefone"));
        ((TextView)findViewById(R.id.email)).setText(dh.getPerfilUsuarioAdminData("email_cadastro"));

        if (dh.getPerfilUsuarioAdminData("img_id_frente_principal").equals("null")) {

            findViewById(R.id.layoutAguardandoEnvioDocumentos).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDenunciarPerfil).setVisibility(View.GONE);
        }
        else if (dh.getPerfilUsuarioAdminData("img_id_frente_principal").equals("fail")) {

            findViewById(R.id.layoutAnaliseFalha).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDenunciarPerfil).setVisibility(View.GONE);
        }
        else if (!dh.getPerfilUsuarioAdminData("analise_documental_concluida").equals("1")) {

            findViewById(R.id.layoutAnaliseDocumental).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDenunciarPerfil).setVisibility(View.GONE);
        }
        else if (dh.getPerfilUsuarioAdminData("conta_bloqueada").equals("1")) {

            findViewById(R.id.textDesbloquearUsuario).setVisibility(View.VISIBLE);
            findViewById(R.id.textBloquearUsuario).setVisibility(View.GONE);
        }

        findViewById(R.id.view_img_perfil).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!dh.getPerfilUsuarioAdminData("img_principal").equals("null")) {

                    Intent i = new Intent(PerfilUsuarioAdminActivity.this, ImageViewActivity.class);
                    i.putExtra("img_principal", dh.getPerfilUsuarioAdminData("img_principal"));
                    startActivity(i);
                }
            }
        });

        findViewById(R.id.view_img_id_frente).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!dh.getPerfilUsuarioAdminData("img_id_frente_principal").equals("null")) {

                    Intent i = new Intent(PerfilUsuarioAdminActivity.this, ImageViewActivity.class);
                    i.putExtra("img_principal", dh.getPerfilUsuarioAdminData("img_id_frente_principal"));
                    startActivity(i);
                }
            }
        });

        findViewById(R.id.view_img_id_verso).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!dh.getPerfilUsuarioAdminData("img_id_verso_principal").equals("null")) {

                    Intent i = new Intent(PerfilUsuarioAdminActivity.this, ImageViewActivity.class);
                    i.putExtra("img_principal", dh.getPerfilUsuarioAdminData("img_id_verso_principal"));
                    startActivity(i);
                }
            }
        });

        findViewById(R.id.buttonConcluirAnalise).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                RadioButton radio1 = findViewById(R.id.radio1);
                RadioButton radio2 = findViewById(R.id.radio2);

                String analise = "";

                if (radio1.isChecked()) {

                    analise = "1";
                }
                else if (radio2.isChecked()) {

                    analise = "2";
                }
                else {

                    dialogHelper.showError("Selecione uma opção para concluir a análise documental.");

                    return;
                }

                dialogHelper.showProgress();

                sincabsServer.adminConcluirAnaliseDocumental(DataHolder.getInstance().getPerfilUsuarioAdminData("id_usuario"), analise, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        dialogHelper.showSuccess(msg);
                    }

                    @Override
                    void onResponseError(String error) {

                        dialogHelper.showError(error);
                    }

                    @Override
                    void onNoResponse(String error) {

                        dialogHelper.showError(error);
                    }

                    @Override
                    void onPostResponse() {

                        dialogHelper.dismissProgress();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void bloquearUsuario(View view) {

        final MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                String motivo_bloqueio = ((EditText)dialog.getCustomView().findViewById(R.id.editTextMotivoBloqueio)).getText().toString();

                motivo_bloqueio = AppUtils.formatarTexto(motivo_bloqueio);

                if (motivo_bloqueio.length() < 2) {

                    dialogHelper.showError("Verifique o motivo digitado.");

                    return;
                }

                dialogHelper.showProgress();

                sincabsServer.adminBloquearUsuario(DataHolder.getInstance().getPerfilUsuarioAdminData("id_usuario"), motivo_bloqueio, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        dialogHelper.showSuccess(msg);
                    }

                    @Override
                    void onResponseError(String error) {

                        dialogHelper.showError(error);
                    }

                    @Override
                    void onNoResponse(String error) {

                        dialogHelper.showError(error);
                    }

                    @Override
                    void onPostResponse() {

                        dialogHelper.dismissProgress();
                    }
                });
            }
        };

        new MaterialDialog.Builder(PerfilUsuarioAdminActivity.this)
                .title("Bloquear usuário")
                .customView(R.layout.layout_bloquear_usuario, true)
                .positiveText("Enviar")
                .onPositive(positiveCallback)
                .negativeText("Cancelar")
                .show();
    }

    public void desbloquearUsuario(View view) {

        dialogHelper.confirmDialog(true, "Desbloquear usuário", "Deseja desbloquear este usuário?", "Cancelar", new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialogHelper.showProgress();

                sincabsServer.adminDesbloquearUsuario(DataHolder.getInstance().getPerfilUsuarioAdminData("id_usuario"), new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        dialogHelper.showSuccess(msg);
                    }

                    @Override
                    void onResponseError(String error) {

                        dialogHelper.showError(error);
                    }

                    @Override
                    void onNoResponse(String error) {

                        dialogHelper.showError(error);
                    }

                    @Override
                    void onPostResponse() {

                        dialogHelper.dismissProgress();
                    }
                });
            }
        });
    }

    private String pegarOcupacao(String i) {

        if (i.equals("1")) {

            return "Policial Civil";
        }
        else if (i.equals("2")) {

            return "Policial Militar";
        }
        else if (i.equals("3")) {

            return "Policial Federal";
        }
        else if (i.equals("4")) {

            return "Policial Rodoviário Federal";
        }
        else if (i.equals("5")) {

            return "ASP Estadual";
        }
        else if (i.equals("6")) {

            return "ASP Federal";
        }
        else if (i.equals("7")) {

            return "Bombeiro Militar";
        }
        else if (i.equals("8")) {

            return "Militar da Marinha";
        }
        else if (i.equals("9")) {

            return "Militar do Exército";
        }
        else {

            return "Militar da Aeronáutica";
        }
    }

    private String pegarUF(String i) {

        int uf = Integer.parseInt(i);

        return getResources().getStringArray(R.array.uf_cadastro)[uf];
    }

    public String pegarDataPerfil(String data) {

        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat saida = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss");

        try {

            Date dataEntrada = entrada.parse(data);

            return saida.format(dataEntrada);
        }
        catch (Exception e) {

            return "erro";
        }
    }

    public void fecharJanela(View view) {

        finish();
    }
}
