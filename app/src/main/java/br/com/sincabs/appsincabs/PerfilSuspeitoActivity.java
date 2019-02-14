package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import com.snatik.storage.Storage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilSuspeitoActivity extends AppCompatActivity {

    private DialogHelper dialogHelper;
    private SincabsServer sincabsServer;

    private int selecionarImagem;

    private Storage storage;

    private int index = 1;
    private boolean solicitando = false;

    public static final String CLOSED_IMAGE_WARNING = "close-image-warning.data";
    private static final String AVISO_REGRAS_ENVIAR_IMAGEM = "aviso-regras-enviar-imagem.data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_perfil_suspeito);

        storage = new Storage(PerfilSuspeitoActivity.this);

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

        dialogHelper = new DialogHelper(PerfilSuspeitoActivity.this);
        sincabsServer = new SincabsServer(PerfilSuspeitoActivity.this);

        sincabsServer.perfilSuspeitoImagens(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                findViewById(R.id.avisoImagens).setVisibility(View.GONE);

                try {

                    final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                    JSONArray jsonArray = extra.getJSONArray("Resultado");

                    for (int a = 0; a < jsonArray.length(); a++) {

                        final View child = LayoutInflater.from(PerfilSuspeitoActivity.this).inflate(R.layout.layout_nova_imagem, null);

                        vg.addView(child);

                        final ImageView novaImagem = child.findViewById(R.id.imageNew);

                        final JSONObject jsonObject = jsonArray.getJSONObject(a);

                        ImageLoader.getInstance().loadImage(SincabsServer.getImageAddress(jsonObject.getString("img_busca")), new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                                novaImagem.setImageResource(R.drawable.icon_images);

                                super.onLoadingStarted(imageUri, view);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                                novaImagem.setImageBitmap(loadedImage);

                                super.onLoadingComplete(imageUri, view, loadedImage);
                            }
                        });

                        child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                try {

                                    Intent i = new Intent(PerfilSuspeitoActivity.this, ImageViewActivity.class);
                                    i.putExtra("img_principal", jsonObject.getString("img_principal"));
                                    startActivity(i);
                                }
                                catch (Exception e) { }
                            }
                        });

                        child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View view) {

                                String[] opcoes = {"Excluir imagem"};

                                dialogHelper.showList("Imagem", opcoes, new MaterialDialog.ListCallback() {

                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                        try {

                                            if (DataHolder.getInstance().getLoginData("id_usuario").equals(jsonObject.getString("id_usuario"))) {

                                                dialogHelper.showProgress();

                                                sincabsServer.excluirImagemSuspeito(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), jsonObject.getString("img_principal"), new SincabsResponse() {

                                                    @Override
                                                    void onResponseNoError(String msg, JSONObject extra) {

                                                        new Handler().postDelayed(new Runnable() {

                                                            @Override
                                                            public void run() {

                                                                vg.removeView(child);
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

                                                dialogHelper.showError("Você não pode excluir uma imagem que foi adicionada por outro usuário.");
                                            }
                                        }
                                        catch (Exception e) { }
                                    }
                                });

                                return false;
                            }
                        });
                    }
                }
                catch (Exception e) { }
            }

            @Override
            void onResponseError(String error) {

                if (!avisoImagensFechado()) {

                    findViewById(R.id.avisoImagens).setAlpha(0f);
                    findViewById(R.id.avisoImagens).setVisibility(View.VISIBLE);
                    ((ViewGroup)findViewById(R.id.avisoImagens)).animate().alpha(1.0f);
                }
            }

            @Override
            void onNoResponse(String error) {

                if (!avisoImagensFechado()) {

                    findViewById(R.id.avisoImagens).setAlpha(0f);
                    findViewById(R.id.avisoImagens).setVisibility(View.VISIBLE);
                    ((ViewGroup)findViewById(R.id.avisoImagens)).animate().alpha(1.0f);
                }
            }

            @Override
            void onPostResponse() {

                dialogHelper.dismissProgress();
            }
        });

        ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(DataHolder.getInstance().getPerfilSuspeitoData("img_busca")), (ImageView)findViewById(R.id.img_perfil));

        findViewById(R.id.view_img_perfil).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(PerfilSuspeitoActivity.this, ImageViewActivity.class);
                i.putExtra("img_principal", DataHolder.getInstance().getPerfilSuspeitoData("img_principal"));
                startActivity(i);
            }
        });

        findViewById(R.id.viewAddImage).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String[] opcoes = {"Adicionar imagem"};

                dialogHelper.showList("Imagem", opcoes, new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        if (!storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + AVISO_REGRAS_ENVIAR_IMAGEM)) {

                            MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    if (((CheckBox)dialog.findViewById(R.id.checkNaoMostrarMais)).isChecked()) {

                                        storage.createFile(storage.getInternalFilesDirectory() + File.separator + AVISO_REGRAS_ENVIAR_IMAGEM, "{ok}");
                                    }

                                    selecionarImagem = 1; //enviar imagem no perfil do suspeito

                                    dialogHelper.showProgress();

                                    CropImage.activity()
                                            .setCropMenuCropButtonTitle("Concluir")
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(PerfilSuspeitoActivity.this);
                                }
                            };

                            new MaterialDialog.Builder(PerfilSuspeitoActivity.this)
                                    .title("Atenção")
                                    .customView(R.layout.layout_aviso_regras_enviar_imagem_suspeito, true)
                                    .positiveText("OK")
                                    .onPositive(positiveCallback)
                                    .show();
                        }
                        else {

                            selecionarImagem = 1; //enviar imagem no perfil do suspeito

                            dialogHelper.showProgress();

                            CropImage.activity()
                                    .setCropMenuCropButtonTitle("Concluir")
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(PerfilSuspeitoActivity.this);
                        }
                    }
                });
            }
        });

        final DataHolder dh = DataHolder.getInstance();

        ((TextView)findViewById(R.id.textViewVisualizacoes)).setText(dh.getPerfilSuspeitoData("num_visualizacoes"));

        ((TextView)findViewById(R.id.nomeAlcunha)).setText(dh.getPerfilSuspeitoData("nome_alcunha"));
        ((TextView)findViewById(R.id.areasAtuacao)).setText(dh.getPerfilSuspeitoData("areas_de_atuacao"));

        TextView tempTextView;

        if (dh.getPerfilSuspeitoData("nome_completo").length() > 0) {

            tempTextView = (TextView) findViewById(R.id.nomeCompleto);

            tempTextView.setText(dh.getPerfilSuspeitoData("nome_completo"));
            tempTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (dh.getPerfilSuspeitoData("nome_da_mae").length() > 0) {

            tempTextView = (TextView) findViewById(R.id.nomeDaMae);

            tempTextView.setText(dh.getPerfilSuspeitoData("nome_da_mae"));
            tempTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (!dh.getPerfilSuspeitoData("cpf").equals("0")) {

            tempTextView = (TextView) findViewById(R.id.cpf);

            tempTextView.setText(AppUtils.formatarCPF(dh.getPerfilSuspeitoData("cpf")));
            tempTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (!dh.getPerfilSuspeitoData("rg").equals("0")) {

            tempTextView = (TextView) findViewById(R.id.rg);

            tempTextView.setText(dh.getPerfilSuspeitoData("rg"));
            tempTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (!dh.getPerfilSuspeitoData("data_nascimento").equals("0000-00-00")) {

            tempTextView = (TextView) findViewById(R.id.dataNascimento);

            String data = dh.getPerfilSuspeitoData("data_nascimento");

            String split[] = data.split("-");

            data = split[2] + "/" + split[1] + "/" + split[0];

            tempTextView.setText(data);
            tempTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        int historico_criminal = Integer.parseInt(dh.getPerfilSuspeitoData("historico_criminal"));

        int furto       = 1 << 0;
        int roubo       = 1 << 1;
        int roubo_banco = 1 << 2;
        int trafico     = 1 << 3;
        int homicidio   = 1 << 4;
        int latrocinio  = 1 << 5;
        int estupro     = 1 << 6;
        int estelionado = 1 << 7;
        int posse_porte = 1 << 8;
        int receptacao  = 1 << 9;
        int contrabando = 1 << 10;
        int outros      = 1 << 11;

        CheckBox tempCheckBox;

        if ((historico_criminal & furto) > 0) {

            tempCheckBox = findViewById(R.id.check_furto);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & roubo) > 0) {

            tempCheckBox = findViewById(R.id.check_roubo);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & roubo_banco) > 0) {

            tempCheckBox = findViewById(R.id.check_roubo_bancos);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & trafico) > 0) {

            tempCheckBox = findViewById(R.id.check_trafico);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & homicidio) > 0) {

            tempCheckBox = findViewById(R.id.check_homicidio);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & latrocinio) > 0) {

            tempCheckBox = findViewById(R.id.check_latrocinio);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & estupro) > 0) {

            tempCheckBox = findViewById(R.id.check_estupro);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & estelionado) > 0) {

            tempCheckBox = findViewById(R.id.check_estelionato);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & posse_porte) > 0) {

            tempCheckBox = findViewById(R.id.check_posse_porte);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & receptacao) > 0) {

            tempCheckBox = findViewById(R.id.check_receptacao);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & contrabando) > 0) {

            tempCheckBox = findViewById(R.id.check_contrabando);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        if ((historico_criminal & outros) > 0) {

            tempCheckBox = findViewById(R.id.check_outros);

            tempCheckBox.setChecked(true);
            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
        }

        ((TextView)findViewById(R.id.txt_relato)).setText(dh.getPerfilSuspeitoData("txt_relato"));

        if (dh.getPerfilSuspeitoData("id_usuario").equals(dh.getLoginData("id_usuario")) || dh.getLoginData("admin").equals("1")) {

            findViewById(R.id.buttonEditarRelato).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonEditarDadosRelevantes).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonEditarDadosComplementares).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonEditarHistorico).setVisibility(View.VISIBLE);

            Button buttonEdit = (Button)findViewById(R.id.buttonEditarPerfil);

            buttonEdit.setVisibility(View.VISIBLE);

            buttonEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String[] list = {"Trocar imagem do perfil", "Excluir perfil do suspeito"};

                    dialogHelper.showList("Editar perfil", list, new MaterialDialog.ListCallback() {

                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                            if (position == 0) {

                                selecionarImagem = 2; //opcao para mudar a imagem de perfil do suspeito

                                dialogHelper.showProgress();

                                CropImage.activity()
                                        .setCropMenuCropButtonTitle("Concluir")
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1, 1)
                                        .start(PerfilSuspeitoActivity.this);
                            }
                            else { //excluir perfil do suspeito

                                dialogHelper.confirmDialog(true, "Excluir perfil", "Tem certeza que deseja excluir o perfil do suspeito?\n\nTodas as imagens e informações do suspeito serão apagadas da plataforma.", "Cancelar", new MaterialDialog.SingleButtonCallback() {

                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        dialogHelper.showProgress();

                                        sincabsServer.excluirPerfilSuspeito(dh.getPerfilSuspeitoData("id_suspeito"), new SincabsResponse() {

                                            @Override
                                            void onResponseNoError(String msg, JSONObject extra) {

                                                findViewById(R.id.layoutPrincipal).setVisibility(View.GONE);
                                                findViewById(R.id.layoutPerfilExcluido).setVisibility(View.VISIBLE);

                                                setResult(400); //perfil de suspeito deletado
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

        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressComentarios);
        final TextView nenhumComentario = (TextView) findViewById(R.id.nenhumComentario);
        final TextView adicionarComentario = (TextView) findViewById(R.id.adicionarComentario);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        final LinearLayout layoutComentarios = (LinearLayout) findViewById(R.id.layoutComentarios);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                if (scrollView.getChildAt(0).getBottom() <= scrollView.getHeight() + scrollView.getScrollY() + 100) {

                    if (index == 0) {

                        return;
                    }

                    if (solicitando) {

                        return;
                    }

                    progress.setVisibility(View.VISIBLE);
                    nenhumComentario.setVisibility(View.GONE);

                    solicitando = true;

                    sincabsServer.obterComentarios(index, DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), new SincabsResponse() {

                        @Override
                        void onResponseNoError(String msg, JSONObject extra) {

                            index++;

                            try {

                                JSONArray jsonArray = extra.getJSONArray("Resultado");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject json = jsonArray.getJSONObject(i);

                                    View child = getLayoutInflater().inflate(R.layout.layout_comentario, null);

                                    if (json.getString("id_usuario").equals(DataHolder.getInstance().getLoginData("id_usuario"))) {

                                        child.findViewById(R.id.buttonFechar).setVisibility(View.VISIBLE);
                                    }

                                    child.findViewById(R.id.imagemPerfil).setTag(json.getString("id_usuario").toString());
                                    child.findViewById(R.id.buttonFechar).setTag(json.getString("id").toString());
                                    ((TextView)child.findViewById(R.id.dataCadastro)).setText(pegarData(json.getString("data_registro").toString()));
                                    ((TextView)child.findViewById(R.id.textComentario)).setText(json.getString("comentario").toString());

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

                                    layoutComentarios.addView(child);
                                }
                            }
                            catch (JSONException e) { }
                        }

                        @Override
                        void onResponseError(String error) {

                            if (index == 1) {

                                nenhumComentario.setVisibility(View.VISIBLE);
                            }

                            index = 0;
                        }

                        @Override
                        void onNoResponse(String error) {

                            if (index == 1) {

                                nenhumComentario.setVisibility(View.VISIBLE);
                            }

                            index = 0;
                        }

                        @Override
                        void onPostResponse() {

                            progress.setVisibility(View.GONE);
                            adicionarComentario.setVisibility(View.VISIBLE);

                            solicitando = false;
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            dialogHelper.dismissProgress();

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                dialogHelper.showProgress();

                if (selecionarImagem == 1) { // enviar imagem no perfil do suspeito

                    final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                    dialogHelper.showProgress();

                    DataHolder.getInstance().salvarImagem(this, dialogHelper, result.getUri(), false, false, new Runnable() {

                        @Override
                        public void run() {

                            sincabsServer.enviarImagemSuspeito(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), DataHolder.getInstance().getImgPrincipalSaved(), DataHolder.getInstance().getImgBuscaSaved(), new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, final JSONObject extra) {

                                    findViewById(R.id.avisoImagens).setVisibility(View.GONE);

                                    try {

                                        final View child = LayoutInflater.from(PerfilSuspeitoActivity.this).inflate(R.layout.layout_nova_imagem, null);

                                        vg.addView(child);

                                        final ImageView novaImagem = child.findViewById(R.id.imageNew);

                                        //dialogHelper.showSuccess(SincabsServer.getImageAddress(extra.getString("img_busca")));

                                        ImageLoader.getInstance().loadImage(SincabsServer.getImageAddress(extra.getString("img_busca")), new SimpleImageLoadingListener() {

                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {

                                                novaImagem.setImageResource(R.drawable.icon_images);

                                                super.onLoadingStarted(imageUri, view);
                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                                                novaImagem.setImageBitmap(loadedImage);

                                                ((HorizontalScrollView)findViewById(R.id.newImageScrollView)).fullScroll(HorizontalScrollView.FOCUS_RIGHT);

                                                super.onLoadingComplete(imageUri, view, loadedImage);
                                            }
                                        });

                                        child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                try {

                                                    Intent i = new Intent(PerfilSuspeitoActivity.this, ImageViewActivity.class);
                                                    i.putExtra("img_principal", extra.getString("img_principal"));
                                                    startActivity(i);
                                                }
                                                catch (Exception e) { }
                                            }
                                        });

                                        child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                                            @Override
                                            public boolean onLongClick(View view) {

                                                String[] opcoes = {"Excluir imagem"};

                                                dialogHelper.showList("Imagem", opcoes, new MaterialDialog.ListCallback() {

                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                                        try {

                                                            if (DataHolder.getInstance().getLoginData("id_usuario").equals(extra.getString("id_usuario"))) {

                                                                dialogHelper.showProgress();

                                                                sincabsServer.excluirImagemSuspeito(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), extra.getString("img_principal"), new SincabsResponse() {

                                                                    @Override
                                                                    void onResponseNoError(String msg, JSONObject extra) {

                                                                        new Handler().postDelayed(new Runnable() {

                                                                            @Override
                                                                            public void run() {

                                                                                vg.removeView(child);
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

                                                                dialogHelper.showError("Você não pode excluir uma imagem que foi adicionada por outro usuário.");
                                                            }
                                                        }
                                                        catch (Exception e) { }
                                                    }
                                                });

                                                return false;
                                            }
                                        });
                                    }
                                    catch (Exception e) { }
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
                else { // trocar imagem do perfil do suspeito

                    DataHolder.getInstance().salvarImagem(PerfilSuspeitoActivity.this, dialogHelper, result.getUri(), false, true, new Runnable() {

                        @Override
                        public void run() {

                            sincabsServer.trocarImagemPerfilSuspeito(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), DataHolder.getInstance().getImgPrincipalSaved(), DataHolder.getInstance().getImgBuscaSaved(), new SincabsResponse() {

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

                                                    Intent i = new Intent(PerfilSuspeitoActivity.this, ImageViewActivity.class);
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
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fecharAvisoImagens(View view) {

        findViewById(R.id.avisoImagens).setVisibility(View.GONE);

        storage.createFile(storage.getInternalFilesDirectory() + File.separator + CLOSED_IMAGE_WARNING, "{aviso fechado}");
    }

    private boolean avisoImagensFechado() {

        return storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + CLOSED_IMAGE_WARNING);
    }

    public void buttonFecharComentarioClick(final View view) {

        dialogHelper.confirmDialog(true, "Excluir comentário", "Deseja excluir este comentário?", "Cancelar", new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialogHelper.showProgress();

                sincabsServer.excluirComentario(view.getTag().toString(), new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        LinearLayout layoutComentarios = (LinearLayout) findViewById(R.id.layoutComentarios);

                        layoutComentarios.removeView((View)view.getParent().getParent().getParent().getParent());

                        if (layoutComentarios.getChildCount() == 0) {

                            findViewById(R.id.nenhumComentario).setVisibility(View.VISIBLE);
                        }
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

    public void buttonEnviarComentario(View view) {

        final MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                String comentario = ((EditText)dialog.getCustomView().findViewById(R.id.editTextComentario)).getText().toString();

                comentario = AppUtils.formatarTexto(comentario);

                if (comentario.length() < 2) {

                    dialogHelper.showError("Verifique o comentário digitado.");

                    return;
                }

                dialogHelper.showProgress();

                sincabsServer.enviarComentario(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), comentario, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        index = 1;

                        final LinearLayout layoutComentarios = (LinearLayout) findViewById(R.id.layoutComentarios);

                        solicitando = true;

                        sincabsServer.obterComentarios(index, DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), new SincabsResponse() {

                            @Override
                            void onResponseNoError(String msg, JSONObject extra) {

                                index++;

                                layoutComentarios.removeAllViews();

                                try {

                                    JSONArray jsonArray = extra.getJSONArray("Resultado");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject json = jsonArray.getJSONObject(i);

                                        View child = getLayoutInflater().inflate(R.layout.layout_comentario, null);

                                        if (json.getString("id_usuario").equals(DataHolder.getInstance().getLoginData("id_usuario"))) {

                                            child.findViewById(R.id.buttonFechar).setVisibility(View.VISIBLE);
                                        }

                                        child.findViewById(R.id.imagemPerfil).setTag(json.getString("id_usuario").toString());
                                        child.findViewById(R.id.buttonFechar).setTag(json.getString("id").toString());
                                        ((TextView)child.findViewById(R.id.dataCadastro)).setText(pegarData(json.getString("data_registro").toString()));
                                        ((TextView)child.findViewById(R.id.textComentario)).setText(json.getString("comentario").toString());

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

                                        layoutComentarios.addView(child);
                                    }
                                }
                                catch (JSONException e) { }

                                findViewById(R.id.nenhumComentario).setVisibility(View.GONE);
                            }

                            @Override
                            void onResponseError(String error) {

                            }

                            @Override
                            void onNoResponse(String error) {

                            }

                            @Override
                            void onPostResponse() {

                                dialogHelper.dismissProgress();

                                solicitando = false;
                            }
                        });
                    }

                    @Override
                    void onResponseError(String error) {

                        dialogHelper.showError(error);
                        dialogHelper.dismissProgress();
                    }

                    @Override
                    void onNoResponse(String error) {

                        dialogHelper.showError(error);
                        dialogHelper.dismissProgress();
                    }

                    @Override
                    void onPostResponse() {

                    }
                });
            }
        };

        new MaterialDialog.Builder(PerfilSuspeitoActivity.this)
                .title("Adicionar comentário")
                .customView(R.layout.layout_enviar_comentario, true)
                .positiveText("Enviar")
                .onPositive(positiveCallback)
                .negativeText("Cancelar")
                .show();
    }

    public void imagemPerfilComentarioClick(View view) {

        dialogHelper.showProgress();

        sincabsServer.perfilUsuario(view.getTag().toString(), new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                DataHolder.getInstance().setPerfilUsuarioData(extra);

                Intent i = new Intent(PerfilSuspeitoActivity.this, PerfilUsuarioActivity.class);
                startActivity(i);
            }

            @Override
            void onResponseError(String error) {

            }

            @Override
            void onNoResponse(String error) {

            }

            @Override
            void onPostResponse() {

                dialogHelper.dismissProgress();
            }
        });
    }

    public void buttonEditarRelato(View view) {

        final MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                final String relato = AppUtils.formatarTexto(((EditText)dialog.getCustomView().findViewById(R.id.editTextRelato)).getText().toString());

                if (relato.length() < 2) {

                    dialogHelper.showError("Verifique o relato digitado.");

                    return;
                }

                dialogHelper.showProgress();

                sincabsServer.editarRelato(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), relato, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        ((TextView)findViewById(R.id.txt_relato)).setText(relato);
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

        new MaterialDialog.Builder(PerfilSuspeitoActivity.this)
                .title("Editar relato")
                .customView(R.layout.layout_editar_relato, true)
                .positiveText("OK")
                .onPositive(positiveCallback)
                .negativeText("Cancelar")
                .show();
    }

    public void buttonEditarDadosRelevantes(View view) {

        String[] opcoes = {"Editar nome/alcunha", "Editar área de atuação"};

        dialogHelper.showList("Dados relevantes", opcoes, new MaterialDialog.ListCallback() {

            @Override
            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                if (position == 0) {

                    dialogHelper.inputDialog("Editar nome/alcunha", "", InputType.TYPE_TEXT_VARIATION_PERSON_NAME, new MaterialDialog.InputCallback() {

                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            final String nome_alcunha = input.toString();

                            if (nome_alcunha.length() < 2) {

                                dialogHelper.showError("Verifique o nome/alcunha digitado.");

                                return;
                            }

                            dialogHelper.showProgress();

                            sincabsServer.editarNomeAlcunha(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), nome_alcunha, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    ((TextView)findViewById(R.id.nomeAlcunha)).setText(nome_alcunha);
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
                else {

                    //abrir dialog para marcar novas UF da área de atuação.

                    final MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            boolean checkAC = ((CheckBox)dialog.findViewById(R.id.checkBoxAC)).isChecked();
                            boolean checkAL = ((CheckBox)dialog.findViewById(R.id.checkBoxAL)).isChecked();
                            boolean checkAM = ((CheckBox)dialog.findViewById(R.id.checkBoxAM)).isChecked();
                            boolean checkAP = ((CheckBox)dialog.findViewById(R.id.checkBoxAP)).isChecked();
                            boolean checkBA = ((CheckBox)dialog.findViewById(R.id.checkBoxBA)).isChecked();
                            boolean checkCE = ((CheckBox)dialog.findViewById(R.id.checkBoxCE)).isChecked();
                            boolean checkDF = ((CheckBox)dialog.findViewById(R.id.checkBoxDF)).isChecked();
                            boolean checkES = ((CheckBox)dialog.findViewById(R.id.checkBoxES)).isChecked();
                            boolean checkGO = ((CheckBox)dialog.findViewById(R.id.checkBoxGO)).isChecked();
                            boolean checkMA = ((CheckBox)dialog.findViewById(R.id.checkBoxMA)).isChecked();
                            boolean checkMG = ((CheckBox)dialog.findViewById(R.id.checkBoxMG)).isChecked();
                            boolean checkMS = ((CheckBox)dialog.findViewById(R.id.checkBoxMS)).isChecked();
                            boolean checkMT = ((CheckBox)dialog.findViewById(R.id.checkBoxMT)).isChecked();
                            boolean checkPA = ((CheckBox)dialog.findViewById(R.id.checkBoxPA)).isChecked();
                            boolean checkPB = ((CheckBox)dialog.findViewById(R.id.checkBoxPB)).isChecked();
                            boolean checkPE = ((CheckBox)dialog.findViewById(R.id.checkBoxPE)).isChecked();
                            boolean checkPI = ((CheckBox)dialog.findViewById(R.id.checkBoxPI)).isChecked();
                            boolean checkPR = ((CheckBox)dialog.findViewById(R.id.checkBoxPR)).isChecked();
                            boolean checkRJ = ((CheckBox)dialog.findViewById(R.id.checkBoxRJ)).isChecked();
                            boolean checkRN = ((CheckBox)dialog.findViewById(R.id.checkBoxRN)).isChecked();
                            boolean checkRO = ((CheckBox)dialog.findViewById(R.id.checkBoxRO)).isChecked();
                            boolean checkRR = ((CheckBox)dialog.findViewById(R.id.checkBoxRR)).isChecked();
                            boolean checkRS = ((CheckBox)dialog.findViewById(R.id.checkBoxRS)).isChecked();
                            boolean checkSC = ((CheckBox)dialog.findViewById(R.id.checkBoxSC)).isChecked();
                            boolean checkSE = ((CheckBox)dialog.findViewById(R.id.checkBoxSE)).isChecked();
                            boolean checkSP = ((CheckBox)dialog.findViewById(R.id.checkBoxSP)).isChecked();
                            boolean checkTO = ((CheckBox)dialog.findViewById(R.id.checkBoxTO)).isChecked();

                            int areas_de_atuacao = 0;

                            if (checkAC) areas_de_atuacao |= 1 << 0;
                            if (checkAL) areas_de_atuacao |= 1 << 1;
                            if (checkAM) areas_de_atuacao |= 1 << 2;
                            if (checkAP) areas_de_atuacao |= 1 << 3;
                            if (checkBA) areas_de_atuacao |= 1 << 4;
                            if (checkCE) areas_de_atuacao |= 1 << 5;
                            if (checkDF) areas_de_atuacao |= 1 << 6;
                            if (checkES) areas_de_atuacao |= 1 << 7;
                            if (checkGO) areas_de_atuacao |= 1 << 8;
                            if (checkMA) areas_de_atuacao |= 1 << 9;
                            if (checkMG) areas_de_atuacao |= 1 << 10;
                            if (checkMS) areas_de_atuacao |= 1 << 11;
                            if (checkMT) areas_de_atuacao |= 1 << 12;
                            if (checkPA) areas_de_atuacao |= 1 << 13;
                            if (checkPB) areas_de_atuacao |= 1 << 14;
                            if (checkPE) areas_de_atuacao |= 1 << 15;
                            if (checkPI) areas_de_atuacao |= 1 << 16;
                            if (checkPR) areas_de_atuacao |= 1 << 17;
                            if (checkRJ) areas_de_atuacao |= 1 << 18;
                            if (checkRN) areas_de_atuacao |= 1 << 19;
                            if (checkRO) areas_de_atuacao |= 1 << 20;
                            if (checkRR) areas_de_atuacao |= 1 << 21;
                            if (checkRS) areas_de_atuacao |= 1 << 22;
                            if (checkSC) areas_de_atuacao |= 1 << 23;
                            if (checkSE) areas_de_atuacao |= 1 << 24;
                            if (checkSP) areas_de_atuacao |= 1 << 25;
                            if (checkTO) areas_de_atuacao |= 1 << 26;

                            if (areas_de_atuacao == 0) {

                                dialogHelper.showError("Marque pelo menos uma Unidade da Federação.");

                                return;
                            }

                            dialogHelper.showProgress();

                            sincabsServer.editarAreaAtuacao(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), areas_de_atuacao, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    String area_de_atuacao = "";

                                    try {

                                        area_de_atuacao = extra.getString("area_de_atuacao");
                                    }
                                    catch (Exception e) { }

                                    ((TextView)findViewById(R.id.areasAtuacao)).setText(area_de_atuacao);
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

                    new MaterialDialog.Builder(PerfilSuspeitoActivity.this)
                            .title("Editar área de atuação")
                            .customView(R.layout.layout_editar_area_atuacao, true)
                            .positiveText("OK")
                            .onPositive(positiveCallback)
                            .negativeText("Cancelar")
                            .show();
                }
            }
        });
    }

    public void buttonEditarDadosComplementares(View view) {

        String[] opcoes = {"Editar nome completo", "Editar nome da mãe", "Editar CPF", "Editar RG", "Editar data de nascimento"};

        dialogHelper.showList("Dados complementares", opcoes, new MaterialDialog.ListCallback() {

            @Override
            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                if (position == 0) { //nome completo

                    dialogHelper.inputDialog("Editar nome completo", "", InputType.TYPE_TEXT_VARIATION_PERSON_NAME, new MaterialDialog.InputCallback() {

                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            final String nome_completo = input.toString();

                            if (nome_completo.length() < 2) {

                                dialogHelper.showError("Verifique o nome completo digitado.");

                                return;
                            }

                            dialogHelper.showProgress();

                            sincabsServer.editarNomeCompleto(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), nome_completo, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    ((TextView)findViewById(R.id.nomeCompleto)).setText(nome_completo);
                                    ((TextView) findViewById(R.id.nomeCompleto)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
                else if (position == 1) { //nome da mae

                    dialogHelper.inputDialog("Editar nome da mãe", "", InputType.TYPE_TEXT_VARIATION_PERSON_NAME, new MaterialDialog.InputCallback() {

                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            final String nome_da_mae = input.toString();

                            if (nome_da_mae.length() < 2) {

                                dialogHelper.showError("Verifique o nome digitado.");

                                return;
                            }

                            dialogHelper.showProgress();

                            sincabsServer.editarNomeDaMae(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), nome_da_mae, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    ((TextView)findViewById(R.id.nomeDaMae)).setText(nome_da_mae);
                                    ((TextView) findViewById(R.id.nomeDaMae)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
                else if (position == 2) { //cpf

                    dialogHelper.inputDialog("Editar CPF", "Apenas números:", InputType.TYPE_NUMBER_FLAG_DECIMAL, new MaterialDialog.InputCallback() {

                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            final String cpf = input.toString();

                            if (!AppUtils.validarCPF(cpf)) {

                                dialogHelper.showError("Verifique o CPF digitado.");

                                return;
                            }

                            dialogHelper.showProgress();

                            sincabsServer.editarCPF(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), cpf, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    ((TextView)findViewById(R.id.cpf)).setText(AppUtils.formatarCPF(cpf));
                                    ((TextView) findViewById(R.id.cpf)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
                else if (position == 3) { //rg

                    dialogHelper.inputDialog("Editar RG", "Apenas números:", InputType.TYPE_NUMBER_FLAG_DECIMAL, new MaterialDialog.InputCallback() {

                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            final String rg = input.toString();

                            if (!rg.matches("[0-9]+") || rg.length() < 5) {

                                dialogHelper.showError("Verifique o RG digitado.");

                                return;
                            }

                            dialogHelper.showProgress();

                            sincabsServer.editarRG(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), rg, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    ((TextView)findViewById(R.id.rg)).setText(rg);
                                    ((TextView) findViewById(R.id.rg)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
                else { //data de nascimento

                    final com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener onDateSetListener = new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            String dia;
                            String mes;

                            if (dayOfMonth < 10) {

                                dia = "0" + dayOfMonth;
                            }
                            else {

                                dia = "" + dayOfMonth;
                            }

                            if (++monthOfYear < 10) {

                                mes = "0" + monthOfYear;
                            }
                            else {

                                mes = "" + monthOfYear;
                            }

                            final String data_ptbr = dia + "/" + mes + "/" + year;
                            final String data = year + "-" + mes + "-" + dia;

                            dialogHelper.showProgress();

                            sincabsServer.editarDataDeNascimento(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), data, new SincabsResponse() {

                                @Override
                                void onResponseNoError(String msg, JSONObject extra) {

                                    ((TextView)findViewById(R.id.dataNascimento)).setText(data_ptbr);
                                    ((TextView) findViewById(R.id.dataNascimento)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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

                    new SpinnerDatePickerDialogBuilder()
                            .context(PerfilSuspeitoActivity.this)
                            .callback(onDateSetListener)
                            .showTitle(true)
                            .defaultDate(1980, 0, 1)
                            .maxDate(2025, 0, 1)
                            .minDate(1920, 0, 1)
                            .build()
                            .show();
                }
            }
        });
    }

    public void buttonEditarHistorico(View view) {

        final MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                boolean checkFutro = ((CheckBox)dialog.findViewById(R.id.check_furto)).isChecked();
                boolean checkRoubo = ((CheckBox)dialog.findViewById(R.id.check_roubo)).isChecked();
                boolean checkRouboBanco = ((CheckBox)dialog.findViewById(R.id.check_roubo_bancos)).isChecked();
                boolean checkTrafico = ((CheckBox)dialog.findViewById(R.id.check_trafico)).isChecked();
                boolean checkHomicidio = ((CheckBox)dialog.findViewById(R.id.check_homicidio)).isChecked();
                boolean checkLatrocinio = ((CheckBox)dialog.findViewById(R.id.check_latrocinio)).isChecked();
                boolean checkEstupro = ((CheckBox)dialog.findViewById(R.id.check_estupro)).isChecked();
                boolean checkEstelionato = ((CheckBox)dialog.findViewById(R.id.check_estelionato)).isChecked();
                boolean checkPossePorte = ((CheckBox)dialog.findViewById(R.id.check_posse_porte)).isChecked();
                boolean checkReceptacao = ((CheckBox)dialog.findViewById(R.id.check_receptacao)).isChecked();
                boolean checkContrabando = ((CheckBox)dialog.findViewById(R.id.check_contrabando)).isChecked();
                boolean checkOutros = ((CheckBox)dialog.findViewById(R.id.check_outros)).isChecked();

                int historico = 0;

                if (checkFutro) historico |= 1 << 0;
                if (checkRoubo) historico |= 1 << 1;
                if (checkRouboBanco) historico |= 1 << 2;
                if (checkTrafico) historico |= 1 << 3;
                if (checkHomicidio) historico |= 1 << 4;
                if (checkLatrocinio) historico |= 1 << 5;
                if (checkEstupro) historico |= 1 << 6;
                if (checkEstelionato) historico |= 1 << 7;
                if (checkPossePorte) historico |= 1 << 8;
                if (checkReceptacao) historico |= 1 << 9;
                if (checkContrabando) historico |= 1 << 10;
                if (checkOutros) historico |= 1 << 11;

                if (historico == 0) {

                    dialogHelper.showError("Selecione pelo menos uma atividade criminosa.");

                    return;
                }

                final int historico_criminal = historico;

                dialogHelper.showProgress();

                sincabsServer.editarHistoricoCriminal(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), historico, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        int furto       = 1 << 0;
                        int roubo       = 1 << 1;
                        int roubo_banco = 1 << 2;
                        int trafico     = 1 << 3;
                        int homicidio   = 1 << 4;
                        int latrocinio  = 1 << 5;
                        int estupro     = 1 << 6;
                        int estelionado = 1 << 7;
                        int posse_porte = 1 << 8;
                        int receptacao  = 1 << 9;
                        int contrabando = 1 << 10;
                        int outros      = 1 << 11;

                        CheckBox tempCheckBox;

                        tempCheckBox = findViewById(R.id.check_furto);

                        if ((historico_criminal & furto) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_roubo);

                        if ((historico_criminal & roubo) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_roubo_bancos);

                        if ((historico_criminal & roubo_banco) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_trafico);

                        if ((historico_criminal & trafico) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_homicidio);

                        if ((historico_criminal & homicidio) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_latrocinio);

                        if ((historico_criminal & latrocinio) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_estupro);

                        if ((historico_criminal & estupro) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_estelionato);

                        if ((historico_criminal & estelionado) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_posse_porte);

                        if ((historico_criminal & posse_porte) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_receptacao);

                        if ((historico_criminal & receptacao) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_contrabando);

                        if ((historico_criminal & contrabando) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }

                        tempCheckBox = findViewById(R.id.check_outros);

                        if ((historico_criminal & outros) > 0) {

                            tempCheckBox.setChecked(true);
                            tempCheckBox.setTextColor(Color.parseColor("#FF111111"));
                        }
                        else {

                            tempCheckBox.setChecked(false);
                            tempCheckBox.setTextColor(getResources().getColor(R.color.colorWhite));
                        }
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

        new MaterialDialog.Builder(PerfilSuspeitoActivity.this)
                .title("Editar histórico criminal")
                .customView(R.layout.layout_editar_historico_criminal, true)
                .positiveText("OK")
                .onPositive(positiveCallback)
                .negativeText("Cancelar")
                .show();
    }

    public void denunciarPerfil(View view) {

        MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                int id = ((RadioGroup)dialog.getCustomView().findViewById(R.id.radioGroup)).getCheckedRadioButtonId();

                if (id == -1) {

                    dialogHelper.showError("Sua denúncia não foi registrada.\n\nVocê deve selecionar um motivo para denunciar o perfil do suspeito.");

                    return;
                }

                String motivo_denuncia = dialog.getCustomView().findViewById(id).getTag().toString();

                dialogHelper.showProgress();

                sincabsServer.denunciarSuspeito(DataHolder.getInstance().getPerfilSuspeitoData("id_suspeito"), motivo_denuncia, new SincabsResponse() {

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

        new MaterialDialog.Builder(PerfilSuspeitoActivity.this)
                .title("Denunciar Perfil")
                .customView(R.layout.layout_denunciar_perfil_suspeito, true)
                .positiveText("Denunciar")
                .negativeText("Cancelar")
                .onPositive(positiveCallback)
                .show();
    }

    public void abrirTermosDeUso(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(PerfilSuspeitoActivity.this, TermosECondicoesDeUsoActivity.class);
                startActivity(i);
            }
        });
    }

    public String pegarData(String data) {

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
