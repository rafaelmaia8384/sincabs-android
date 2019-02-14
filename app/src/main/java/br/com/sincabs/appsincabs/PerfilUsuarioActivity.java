package br.com.sincabs.appsincabs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import com.snatik.storage.Storage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class PerfilUsuarioActivity extends AppCompatActivity {
    
    DialogHelper dialogHelper;
    SincabsServer sincabsServer;

    private int index = 1;
    private boolean solicitando = false;

    private String id_spt = "";

    private static final String TEXT_JA_COMPARTILHADO = "Compartilhe e ajude na expansão da plataforma";
    private static final String TEXT_COMPARTILHAR = "Aplicativo Sincabs para os profissionais de Segurança Pública. Confira:\n\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_perfil_usuario);

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

        dialogHelper = new DialogHelper(PerfilUsuarioActivity.this);
        sincabsServer = new SincabsServer(PerfilUsuarioActivity.this);

        final DataHolder dh = DataHolder.getInstance();
        final ImageView imageView = findViewById(R.id.img_perfil);
        final ImageView imageLogo = findViewById(R.id.img_logo);

        if (dh.getPerfilUsuarioData("id_usuario").equals(dh.getLoginData("id_usuario"))) {

            ((TextView)findViewById(R.id.title)).setText("Meu perfil");

            findViewById(R.id.layoutMeusCadastros).setVisibility(View.VISIBLE);

            Button buttonEdit = (Button)findViewById(R.id.buttonEditarPerfil);

            buttonEdit.setVisibility(View.VISIBLE);

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String[] opcoes = {"Trocar imagem do perfil", "Trocar email de cadastro", "Trocar senha de acesso", "Excluir meu perfil"};

                    dialogHelper.showList("Editar perfil", opcoes, new MaterialDialog.ListCallback() {

                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                            if (position == 0) {

                                dialogHelper.showProgress();

                                CropImage.activity()
                                        .setCropMenuCropButtonTitle("Concluir")
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1, 1)
                                        .start(PerfilUsuarioActivity.this);
                            }
                            else if (position == 1) { //Trocar email

                                dialogHelper.inputDialog("Trocar e-mail", "Digite seu novo endereço de e-mail", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, new MaterialDialog.InputCallback() {

                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {

                                        if (AppUtils.validarEmail(input.toString())) {

                                            dialogHelper.showProgress();

                                            sincabsServer.trocarEmail(input.toString(), new SincabsResponse() {

                                                @Override
                                                void onResponseNoError(String msg, JSONObject extra) {

                                                    new Handler().postDelayed(new Runnable() {

                                                        @Override
                                                        public void run() {

                                                            ((TextView)findViewById(R.id.email)).setText(input.toString());
                                                        }
                                                    }, 500);
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
                                        else {

                                            dialogHelper.showError("Verifique se seu e-mail foi digitado corretamente.");
                                        }
                                    }
                                });
                            }
                            else if (position == 2) {

                                dialogHelper.inputDialog("Trocar senha", "Digite sua nova senha de acesso.\n\n(6 a 20 dígitos)", InputType.TYPE_TEXT_VARIATION_PASSWORD, new MaterialDialog.InputCallback() {

                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                        String senha = input.toString();

                                        if (senha.length() < 6 || senha.length() > 20) {

                                            dialogHelper.showError("A senha deve conter entre 6 e 20 dígitos.");

                                            return;
                                        }

                                        dialogHelper.showProgress();

                                        sincabsServer.trocarSenha(senha, new SincabsResponse() {

                                            @Override
                                            void onResponseNoError(String msg, JSONObject extra) {

                                                Storage storage = new Storage(PerfilUsuarioActivity.this);

                                                storage.deleteFile(storage.getInternalFilesDirectory() + File.separator + LoginActivity.LAST_LOGIN_FILE);

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
                            else { //excluir conta do usuário

                                dialogHelper.confirmDialog(true, "Excluir perfil", "Tem certeza que deseja excluir seu perfil de usuário?\n\nTodos os seus dados pessoais serão apagados da plataforma.", "Cancelar", new MaterialDialog.SingleButtonCallback() {

                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        dialogHelper.showProgress();

                                        sincabsServer.excluirPerfilUsuario(new SincabsResponse() {

                                            @Override
                                            void onResponseNoError(String msg, JSONObject extra) {

                                                Storage storage = new Storage(PerfilUsuarioActivity.this);

                                                storage.deleteFile(storage.getInternalFilesDirectory() + File.separator + LoginActivity.LAST_LOGIN_FILE);

                                                Intent i = new Intent(PerfilUsuarioActivity.this, LoginActivity.class);
                                                startActivity(i);

                                                finish();
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
                        }
                    });
                }
            });
        }
        else {

            findViewById(R.id.layoutDenunciarPerfil).setVisibility(View.VISIBLE);
        }

        if (!dh.getPerfilUsuarioData("img_busca").equals("null")) {

            ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(dh.getPerfilUsuarioData("img_busca")), imageView);
        }

        ImageLoader.getInstance().displayImage(SincabsServer.getLogoAddress(dh.getPerfilUsuarioData("instituicao"), dh.getPerfilUsuarioData("uf")), imageLogo);

        ImageLoader.getInstance().displayImage(SincabsServer.getIconPointAddress(dh.getPerfilUsuarioData("pontuacao_icon")), (ImageView)findViewById(R.id.imageIconPoints));

        String ocupacao = dh.getPerfilUsuarioData("instituicao");

        if (ocupacao.equals("3") || ocupacao.equals("4") || ocupacao.equals("6") || ocupacao.equals("8") || ocupacao.equals("9") || ocupacao.equals("10")) {

            ocupacao = pegarOcupacao(ocupacao);
        }
        else {

            if (ocupacao.equals("2") && dh.getPerfilUsuarioData("uf").equals("23")) {

                ocupacao = "Brigada Militar, " + pegarUF(dh.getPerfilUsuarioData("uf"));
            }
            else {

                ocupacao = pegarOcupacao(ocupacao) + ", " + pegarUF(dh.getPerfilUsuarioData("uf"));
            }
        }

        ((TextView)findViewById(R.id.textViewPontuacao)).setText(dh.getPerfilUsuarioData("pontuacao"));
        ((TextView)findViewById(R.id.instituicao_uf)).setText(ocupacao);
        ((TextView)findViewById(R.id.nome_completo)).setText(dh.getPerfilUsuarioData("nome_completo"));
        ((TextView)findViewById(R.id.email)).setText(dh.getPerfilUsuarioData("email_cadastro"));
        ((TextView)findViewById(R.id.num_suspeitos_cadastrados)).setText(dh.getPerfilUsuarioData("num_suspeitos"));
        ((TextView)findViewById(R.id.num_buscas)).setText(dh.getPerfilUsuarioData("total_buscas"));
        ((TextView)findViewById(R.id.num_acessos)).setText(dh.getPerfilUsuarioData("total_logins"));
        ((TextView)findViewById(R.id.num_comentarios)).setText(dh.getPerfilUsuarioData("num_comentarios"));
        ((TextView)findViewById(R.id.ultimo_acesso)).setText(pegarDataPerfil(dh.getPerfilUsuarioData("ultima_atividade")));
        ((TextView)findViewById(R.id.primeiro_acesso)).setText(pegarDataPerfil(dh.getPerfilUsuarioData("data_registro")));

        findViewById(R.id.view_img_perfil).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!dh.getPerfilUsuarioData("img_principal").equals("null")) {

                    Intent i = new Intent(PerfilUsuarioActivity.this, ImageViewActivity.class);
                    i.putExtra("img_principal", dh.getPerfilUsuarioData("img_principal"));
                    startActivity(i);
                }
            }
        });

        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressMeusCadastros);
        final TextView nenhumCadastro = (TextView) findViewById(R.id.nenhumCadastro);
        final LinearLayout layoutMeusCadastrosLista = (LinearLayout) findViewById(R.id.layoutMeusCadastrosLista);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        if (findViewById(R.id.layoutMeusCadastros).getVisibility() == View.VISIBLE) {

            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {

                    if (scrollView.getChildAt(0).getBottom() <= scrollView.getHeight() + scrollView.getScrollY() + 40) {

                        if (index == 0) {

                            return;
                        }

                        if (solicitando) {

                            return;
                        }

                        progress.setVisibility(View.VISIBLE);
                        nenhumCadastro.setVisibility(View.GONE);

                        solicitando = true;

                        sincabsServer.meusCadastros(index, new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

                                index++;

                                try {

                                    JSONArray jsonArray = extra.getJSONArray("Resultado");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        final JSONObject json = jsonArray.getJSONObject(i);

                                        View child = getLayoutInflater().inflate(R.layout.layout_lista_suspeito_perfil_usuario, null);

                                        try {

                                            id_spt = json.getString("id_suspeito").toString();
                                        }
                                        catch (Exception e) { }

                                        final String id_suspeito = id_spt;

                                        child.findViewById(R.id.itemSuspeito).setTag(id_spt);
                                        child.findViewById(R.id.itemSuspeito).setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                id_spt = view.getTag().toString();

                                                dialogHelper.showProgress();

                                                sincabsServer.perfilSuspeito(id_suspeito, new SincabsResponse() {

                                                    @Override
                                                    void onResponseNoError(String msg, JSONObject extra) {

                                                        DataHolder.getInstance().setPerfilSuspeitoData(extra);

                                                        Intent i = new Intent(PerfilUsuarioActivity.this, PerfilSuspeitoActivity.class);
                                                        startActivityForResult(i, 400);
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

                                        child.findViewById(R.id.imagemPerfil).setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                String img_principal = "";

                                                try {

                                                    img_principal = json.getString("img_principal").toString();
                                                }
                                                catch (Exception e) { }

                                                Intent i = new Intent(PerfilUsuarioActivity.this, ImageViewActivity.class);
                                                i.putExtra("img_principal", img_principal);
                                                startActivity(i);
                                            }
                                        });

                                        ((TextView)child.findViewById(R.id.nomeAlcunha)).setText(json.getString("nome_alcunha").toString());
                                        ((TextView)child.findViewById(R.id.areasAtuacao)).setText(json.getString("areas_de_atuacao").toString());
                                        ((TextView)child.findViewById(R.id.dataCadastro)).setText(pegarData(json.getString("data_registro").toString()));

                                        final ImageView imageView = (ImageView) child.findViewById(R.id.imagemPerfil);

                                        ImageLoader.getInstance().loadImage(SincabsServer.getImageAddress(json.getString("img_busca")), new SimpleImageLoadingListener() {

                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {

                                                imageView.setImageResource(R.drawable.img_perfil);

                                                super.onLoadingStarted(imageUri, view);
                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                                                imageView.setImageBitmap(loadedImage);

                                                super.onLoadingComplete(imageUri, view, loadedImage);
                                            }
                                        });

                                        layoutMeusCadastrosLista.addView(child);
                                    }
                                }
                                catch (JSONException e) { }
                            }

                            @Override
                            void onResponseError(String error) {

                                if (index == 1) {

                                    nenhumCadastro.setVisibility(View.VISIBLE);
                                }

                                index = 0;
                            }

                            @Override
                            void onNoResponse(String error) {

                                if (index == 1) {

                                    nenhumCadastro.setVisibility(View.VISIBLE);
                                }

                                index = 0;
                            }

                            @Override
                            void onPostResponse() {

                                progress.setVisibility(View.GONE);

                                solicitando = false;
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                dialogHelper.showProgress();

                DataHolder.getInstance().salvarImagem(PerfilUsuarioActivity.this, dialogHelper, result.getUri(), false, false, new Runnable() {

                    @Override
                    public void run() {

                        sincabsServer.trocarImagemPerfilUsuario(DataHolder.getInstance().getImgPrincipalSaved(), DataHolder.getInstance().getImgBuscaSaved(), new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, final JSONObject extra) {

                                try {

                                    ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(extra.getString("img_busca")), (ImageView)findViewById(R.id.img_perfil));
                                }
                                catch (Exception e) { }

                                findViewById(R.id.view_img_perfil).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        try {

                                            if (!extra.getString("img_principal").equals("null")) {

                                                Intent i = new Intent(PerfilUsuarioActivity.this, ImageViewActivity.class);
                                                i.putExtra("img_principal", extra.getString("img_principal"));
                                                startActivity(i);
                                            }
                                        }
                                        catch (Exception e) { }
                                    }
                                });
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
        }
        else if (requestCode == 400) {

            if (resultCode == 400) { //suspeito foi deletado, retirar da lista

                LinearLayout view = findViewById(R.id.layoutMeusCadastrosLista);

                view.removeView(view.findViewWithTag(id_spt));

                TextView num_suspeitos = (TextView)findViewById(R.id.num_suspeitos_cadastrados);

                int num = Integer.parseInt(num_suspeitos.getText().toString()) - 1;

                num_suspeitos.setText(Integer.toString(num));

                setResult(500);
            }
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {

        dialogHelper.dismissProgress();

        super.onResume();
    }

    public void denunciarPerfil(View view) {

        MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                int id = ((RadioGroup)dialog.getCustomView().findViewById(R.id.radioGroup)).getCheckedRadioButtonId();

                if (id == -1) {

                    dialogHelper.showError("Sua denúncia não foi registrada.\n\nVocê deve selecionar um motivo para denunciar o usuário.");

                    return;
                }

                String motivo_denuncia = dialog.getCustomView().findViewById(id).getTag().toString();

                dialogHelper.showProgress();

                sincabsServer.denunciarUsuario(motivo_denuncia, new SincabsResponse() {

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

        new MaterialDialog.Builder(PerfilUsuarioActivity.this)
                .title("Denunciar Perfil")
                .customView(R.layout.layout_denunciar_perfil_usuario, true)
                .positiveText("Denunciar")
                .negativeText("Cancelar")
                .onPositive(positiveCallback)
                .show();
    }

    public void buttonPontuacao(View view) {

        dialogHelper.showProgress();

        sincabsServer.sincabsCompartilhado(new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                final DataHolder dh = DataHolder.getInstance();

                final String pontuacao = dh.getPerfilUsuarioData("pontuacao_comentario").split("#")[0];
                final String mensagem = dh.getPerfilUsuarioData("pontuacao_comentario").split("#")[1];

                boolean comp = false;

                try {

                    comp = extra.getString("sincabs_compartilhado").equals("1");
                }
                catch (Exception e) { }

                final boolean compartilhado = comp;

                new MaterialDialog.Builder(PerfilUsuarioActivity.this)
                        .customView(R.layout.layout_pontuacao, true)
                        .positiveText("OK")
                        .showListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialog) {

                                Dialog d = (Dialog) dialog;

                                ImageLoader.getInstance().displayImage(SincabsServer.getIconPointAddress(dh.getPerfilUsuarioData("pontuacao_icon")), (ImageView)d.findViewById(R.id.imagePontuacao));

                                if (compartilhado) {

                                    ((TextView)d.findViewById(R.id.buttonCompartilhar)).setText(TEXT_JA_COMPARTILHADO);
                                }

                                ((TextView)d.findViewById(R.id.textPontuacao)).setText(pontuacao);
                                ((TextView)d.findViewById(R.id.textMessage)).setText(mensagem);
                            }
                        })
                        .show();
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

    public void buttonCompartilharSincabs(View view) {

        dialogHelper.showProgress();

        boolean compartilhado = ((Button)view).getText().toString().equals(TEXT_JA_COMPARTILHADO);

        shareSincabs(compartilhado);
    }

    public String ipToString(String ipAddress) {

        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {

            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);

        }

        return Long.toString(result);
    }

    public void shareSincabs(final boolean compartilhado) {

        final String id_usuario = DataHolder.getInstance().getLoginData("id_usuario");

        sincabsServer.getRequest("https://api.ipify.org/?format=json", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                dialogHelper.dismissProgress();

                String result = "";
                String ip = "";

                try {

                    result = new String(responseBody, "UTF-8");
                    ip = new JSONObject(result).getString("ip");
                }
                catch (Exception e) { }

                if (ip.length() > 0) {

                    ip = ipToString(ip);
                }

                String code = ip + "-" + id_usuario;

                final String shareText = TEXT_COMPARTILHAR + SincabsServer.HOST_SHARE_LINK + "?code=" + code + "-" + AppUtils.crc32(code);

                new MaterialDialog.Builder(PerfilUsuarioActivity.this)
                        .customView(R.layout.layout_compartilhar_copiar, true)
                        .positiveText("OK")
                        .showListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialog) {

                                Dialog d = (Dialog) dialog;

                                if (compartilhado) {

                                    ((TextView)d.findViewById(R.id.textEnviarLink)).setText("Copie e envie o texto abaixo para um colega.");
                                }

                                TextView textLink = (TextView)d.findViewById(R.id.textLink);

                                textLink.setText(shareText);
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                dialogHelper.dismissProgress();

                dialogHelper.showError("Não foi possível gerar o link de compartilhamento. Tente novamente em instantes.");
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

    public String pegarData(String data) {

        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat saida = new SimpleDateFormat("dd/MM/yyyy");

        try {

            Date dataEntrada = entrada.parse(data);

            return saida.format(dataEntrada);
        }
        catch (Exception e) {

            return "erro";
        }
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
