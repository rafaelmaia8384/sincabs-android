package br.com.sincabs.appsincabs;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import id.zelory.compressor.Compressor;

public class DataHolder extends Application {

    private static final DataHolder holder = new DataHolder();

    private JSONObject loginData;
    private boolean loggedIn;

    private JSONObject perfilUsuarioData;
    private JSONObject perfilUsuarioAdminData;
    private JSONObject perfilSuspeitoData;

    private String cadastroCPF;
    private String cadastroNome;
    private String cadastroOcupacaoProfissional;

    private String cadastroTelefone;
    private String cadastroEmail;
    private String cadastroMatricula;
    private String cadastroUFOndeTrabalha;

    private String cadastroSuspeitoNomeAlcunha;
    private String cadastroSuspeitoNomeCompleto;
    private String cadastroSuspeitoNomeDaMae;
    private String cadastroSuspeitoRelato;
    private String cadastroSuspeitoCrtCorPele;
    private String cadastroSuspeitoCrtCorOlhos;
    private String cadastroSuspeitoCrtCorCabelos;
    private String cadastroSuspeitoCrtTipoCabelos;
    private String cadastroSuspeitoCrtPorteFisico;
    private String cadastroSuspeitoCrtEstatura;
    private String cadastroSuspeitoCrtDeficiente;
    private String cadastroSuspeitoCrtPossuiTatuagem;
    private String cadastroSuspeitoHistoricoCriminal;
    private String cadastroSuspeitoAreasDeAtuacao;
    private String cadastroSuspeitoCPF;
    private String cadastroSuspeitoRG;
    private String cadastroSuspeitoDataNascimento;

    private String buscaSuspeitoNomeAlcunha;
    private String buscaSuspeitoCrtCorPele;
    private String buscaSuspeitoCrtCorOlhos;
    private String buscaSuspeitoCrtCorCabelos;
    private String buscaSuspeitoCrtTipoCabelos;
    private String buscaSuspeitoCrtPorteFisico;
    private String buscaSuspeitoCrtEstatura;
    private String buscaSuspeitoCrtDeficiente;
    private String buscaSuspeitoCrtPossuiTatuagem;
    private String buscaSuspeitoHistoricoCriminal;
    private String buscaSuspeitoAreasDeAtuacao;

    private String buscarUsuarioNome;
    private String buscarUsuarioOcupacaoProfissional;

    private File imgPrincipalSaved;
    private File imgBuscaSaved;

    private String cadastroSenha;

    private String[] recuperarSenhaData;

    public static DataHolder getInstance() {

        return holder;
    }

    public void setCadastrarPasso1(String cpf, String nome, String ocupacao) {

        cadastroCPF = cpf;
        cadastroNome = nome;
        cadastroOcupacaoProfissional = ocupacao;
    }

    public void setCadastrarPasso2(String telefone, String email, String matricula, String ufTrabalho) {

        cadastroTelefone = telefone;
        cadastroEmail = email;
        cadastroMatricula = matricula;
        cadastroUFOndeTrabalha = ufTrabalho;
    }

    public void setCadastrarPasso4(String senha) {

        cadastroSenha = senha;
    }

    public void setCadastrarSuspeitoPasso1(String nomeAlcunha, String nomeCompleto, int crtCorPele, int crtCorOlhos, int crtCorCabelos, int crtTipoCabelos, int crtPorteFisico, int crtEstatura, int crtDeficiente, int crtTatuagem, String relato) {

        cadastroSuspeitoNomeAlcunha = nomeAlcunha;
        cadastroSuspeitoNomeCompleto = nomeCompleto;
        cadastroSuspeitoCrtCorPele = Integer.toString(crtCorPele);
        cadastroSuspeitoCrtCorOlhos = Integer.toString(crtCorOlhos);
        cadastroSuspeitoCrtCorCabelos = Integer.toString(crtCorCabelos);
        cadastroSuspeitoCrtTipoCabelos = Integer.toString(crtTipoCabelos);
        cadastroSuspeitoCrtPorteFisico = Integer.toString(crtPorteFisico);
        cadastroSuspeitoCrtEstatura = Integer.toString(crtEstatura);
        cadastroSuspeitoCrtDeficiente = Integer.toString(crtDeficiente);
        cadastroSuspeitoCrtPossuiTatuagem = Integer.toString(crtTatuagem);
        cadastroSuspeitoRelato = relato;
    }

    public void setCadastrarSuspeitoPasso2(int historico, int areas_de_atuacao) {

        cadastroSuspeitoHistoricoCriminal = Integer.toString(historico);
        cadastroSuspeitoAreasDeAtuacao = Integer.toString(areas_de_atuacao);
    }

    public void setCadastrarSuspeitoPasso3(String nome_da_mae, String cpf, String rg, String data_nascimento) {

        cadastroSuspeitoNomeDaMae = nome_da_mae;
        cadastroSuspeitoCPF = cpf;
        cadastroSuspeitoRG = rg;
        cadastroSuspeitoDataNascimento = data_nascimento;
    }

    public String[] getInfoCadastroSuspeito() {

        String[] result = {cadastroSuspeitoNomeAlcunha, cadastroSuspeitoNomeCompleto, cadastroSuspeitoCrtCorPele, cadastroSuspeitoCrtCorOlhos, cadastroSuspeitoCrtCorCabelos, cadastroSuspeitoCrtTipoCabelos, cadastroSuspeitoCrtPorteFisico, cadastroSuspeitoCrtEstatura, cadastroSuspeitoCrtDeficiente, cadastroSuspeitoCrtPossuiTatuagem, cadastroSuspeitoRelato, cadastroSuspeitoHistoricoCriminal, cadastroSuspeitoAreasDeAtuacao, cadastroSuspeitoNomeDaMae, cadastroSuspeitoCPF, cadastroSuspeitoRG, cadastroSuspeitoDataNascimento};

        return result;
    }

    public void salvarImagem(Context context, DialogHelper dh, Uri imagemUri, boolean isUser, boolean isFace, Runnable run) {

        new SalvarImagemTask(context, dh, imagemUri, isUser, isFace, run).execute();
    }

    public File getImgPrincipalSaved() {

        return imgPrincipalSaved;
    }

    public File getImgBuscaSaved() {

        return imgBuscaSaved;
    }

    public String[] getInfoCadastro() {

        String[] result = {cadastroNome, cadastroCPF, cadastroEmail, cadastroSenha, cadastroOcupacaoProfissional, cadastroUFOndeTrabalha, cadastroMatricula, SincabsServer.getAparelhoId(), cadastroTelefone};

        return result;
    }

    public boolean salvarImagem(Context context, Uri imagemUri, boolean isUser, boolean isFace) {

        Bitmap bitmapPrincipal;
        Bitmap bitmapBusca;

        File imgPrincipal;
        File imgBusca;

        imgPrincipalSaved = null;
        imgBuscaSaved = null;

        try {

            bitmapPrincipal = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imagemUri);

            imgPrincipal = new File(context.getExternalCacheDir(), "dataPrincipal.jpg");
            FileOutputStream osPrincipal = new FileOutputStream(imgPrincipal);
            bitmapPrincipal.compress(Bitmap.CompressFormat.JPEG, 90, osPrincipal);

            if (imgPrincipal.length() > 1024*100) {

                Compressor compressor = new Compressor(context)
                        .setDestinationDirectoryPath(context.getExternalCacheDir().getPath().toString())
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setQuality(90);

                imgPrincipal = compressor.compressToFile(imgPrincipal, "garbage.jpg");

                if (imgPrincipal.length() > 1024*512) { //se a imagem for maior que 512 kb, comprimir novamente

                    imgPrincipal = compressor.compressToFile(imgPrincipal, "garbage.jpg");
                }
            }

            imgBusca = new File(context.getExternalCacheDir(), "dataBusca.jpg");
            FileOutputStream osBusca = new FileOutputStream(imgBusca);

            int dimension = Math.min(bitmapPrincipal.getWidth(), bitmapPrincipal.getHeight());

            bitmapBusca = ThumbnailUtils.extractThumbnail(bitmapPrincipal, dimension, dimension);
            bitmapBusca = Bitmap.createScaledBitmap(bitmapBusca, 128, 128, true);
            bitmapBusca.compress(Bitmap.CompressFormat.JPEG, 90, osBusca);

            osPrincipal.close();
            bitmapPrincipal.recycle();

            osBusca.close();
            bitmapBusca.recycle();
        }
        catch (Exception e) {

            return false;
        }

        String nomePrincipal = FileHash.getHashSHA1(imgPrincipal);
        String imgPrincipalNome = (isUser ? "USR" : "SPT") + "-P-" + (isFace ? "FACE" : "ETC") + "-" + String.valueOf(imgPrincipal.length()) + "-" + nomePrincipal + ".jpg";

        String nomeBusca = FileHash.getHashSHA1(imgBusca);
        String imgBuscaNome = (isUser ? "USR" : "SPT") + "-B-" + (isFace ? "FACE" : "ETC") + "-" + String.valueOf(imgBusca.length()) + "-" + nomeBusca + ".jpg";

        File novoPrincipal = new File(context.getExternalCacheDir(), imgPrincipalNome);
        imgPrincipal.renameTo(novoPrincipal);

        File novoBusca = new File(context.getExternalCacheDir(), imgBuscaNome);
        imgBusca.renameTo(novoBusca);

        imgPrincipalSaved = novoPrincipal;
        imgBuscaSaved = novoBusca;

        return true;
    }

    public boolean isLoggedIn() {

        return loggedIn;
    }

    public void logOut() {

        loggedIn = false;
        loginData = null;
    }

    public void setLoginData(JSONObject data) {

        loginData = data;
        loggedIn = true;
    }

    public void setLoggedIn(boolean logged) {

        loggedIn = logged;
    }

    public String getLoginData(String param) {

        try {

            return loginData.getString(param);
        }
        catch (Exception e) {

            return "Erro";
        }
    }

    public void setPerfilUsuarioData(JSONObject data) {

        perfilUsuarioData = data;
    }

    public void setPerfilUsuarioAdminData(JSONObject data) {

        perfilUsuarioAdminData = data;
    }

    public void setPerfilSuspeitoData(JSONObject data) {

        perfilSuspeitoData = data;
    }

    public String getPerfilUsuarioData(String param) {

        try {

            return perfilUsuarioData.getString(param);
        }
        catch (Exception e) {

            return "Erro";
        }
    }

    public String getPerfilUsuarioAdminData(String param) {

        try {

            return perfilUsuarioAdminData.getString(param);
        }
        catch (Exception e) {

            return "Erro";
        }
    }

    public String getPerfilSuspeitoData(String param) {

        try {

            return perfilSuspeitoData.getString(param);
        }
        catch (Exception e) {

            return "Erro";
        }
    }

    public void setRecuperarSenhaData(String[] data) {

        String[] result = {data[0], data[1], data[2]};

        recuperarSenhaData = result;
    }

    public String[] getRecuperarSenhaData() {

        return recuperarSenhaData;
    }

    public void setBuscarSuspeitoData(String nome_alcunha, int area_de_atuacao, int historico, int crtCorPele, int crtCorOlhos, int crtCorCabelos, int crtTipoCabelos, int crtPorteFisico, int crtEstatura, int crtDeficiente, int crtTatuagem) {

        buscaSuspeitoNomeAlcunha = nome_alcunha;
        buscaSuspeitoAreasDeAtuacao = Integer.toString(area_de_atuacao);
        buscaSuspeitoHistoricoCriminal = Integer.toString(historico);
        buscaSuspeitoCrtCorPele = Integer.toString(crtCorPele);
        buscaSuspeitoCrtCorOlhos = Integer.toString(crtCorOlhos);
        buscaSuspeitoCrtCorCabelos = Integer.toString(crtCorCabelos);
        buscaSuspeitoCrtTipoCabelos = Integer.toString(crtTipoCabelos);
        buscaSuspeitoCrtPorteFisico = Integer.toString(crtPorteFisico);
        buscaSuspeitoCrtEstatura = Integer.toString(crtEstatura);
        buscaSuspeitoCrtDeficiente = Integer.toString(crtDeficiente);
        buscaSuspeitoCrtPossuiTatuagem = Integer.toString(crtTatuagem);
    }

    public void setBuscarUsuarioData(String nome, int ocupacaoProfissional) {

        buscarUsuarioNome = nome;
        buscarUsuarioOcupacaoProfissional = Integer.toString(ocupacaoProfissional);
    }

    public String[] getBuscarUsuarioData() {

        String[] result = {buscarUsuarioNome, buscarUsuarioOcupacaoProfissional};

        return result;
    }

    public String[] getBuscarSuspeitoData() {

        String[] result = {buscaSuspeitoNomeAlcunha, buscaSuspeitoAreasDeAtuacao, buscaSuspeitoHistoricoCriminal, buscaSuspeitoCrtCorPele, buscaSuspeitoCrtCorOlhos, buscaSuspeitoCrtCorCabelos, buscaSuspeitoCrtTipoCabelos, buscaSuspeitoCrtPorteFisico, buscaSuspeitoCrtEstatura, buscaSuspeitoCrtDeficiente, buscaSuspeitoCrtPossuiTatuagem};

        return result;
    }

    private class SalvarImagemTask extends AsyncTask<Void, Void, Boolean> {

        private Context ctx;
        private DialogHelper dh;
        private Uri uri;
        private boolean user;
        private boolean face;
        private Runnable runnable;

        public SalvarImagemTask(Context context, DialogHelper dialogHelper, Uri imageUri, boolean isUser, boolean isFace, Runnable run) {

            ctx = context;
            dh = dialogHelper;
            uri = imageUri;
            user = isUser;
            face = isFace;
            runnable = run;
        }

        @Override
        protected void onPreExecute(){

            dh.showProgress();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            return salvarImagem(ctx, uri, user, face);
        }

        @Override
        protected void onPostExecute(Boolean result){

            if (result) {

                if (runnable == null) {

                    dh.dismissProgress();
                }
                else {

                    runnable.run();
                }
            }
            else {

                dh.dismissProgress();
                dh.showError("Infelizmente encontramos um erro neste aplicativo. Por favor, reinicie o aplicativo e tente novamente.");
            }
        }
    }
}
