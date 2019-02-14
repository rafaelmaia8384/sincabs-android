package br.com.sincabs.appsincabs;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;

public class SincabsServer {

    public static final String HOST_WEBSERVICE = "webservice-v-1.4/"; //versão atual do webservice
    public static final String HOST_BASE = "http://www.sincabs.com.br/app/";
    public static final String HOST_ROOT = "https://www.sincabs.com.br/app/";
    public static final String HOST_LOGO = HOST_ROOT + "logo/";
    public static final String HOST_ICON_POINTS = HOST_ROOT + "icon_points/";
    public static final String HOST_EXECUTAR = HOST_ROOT + HOST_WEBSERVICE + "executar.php";
    public static final String HOST_ENVIAR_IMAGEM = HOST_ROOT + HOST_WEBSERVICE + "enviar-imagem.php";
    public static final String HOST_SHARE_LINK = HOST_BASE + HOST_WEBSERVICE + "share.php";

    private static final String OPT_VERIFICAR_CPF = "101";
    private static final String OPT_CADASTRAR_USUARIO = "102";
    private static final String OPT_LOGIN = "103";
    private static final String OPT_ENVIAR_DOCUMENTO_FRENTE = "104";
    private static final String OPT_ENVIAR_DOCUMENTO_VERSO = "105";
    private static final String OPT_CANCELAR_CADASTRO = "106";
    private static final String OPT_RECUPERAR_SENHA_ID_APARELHO = "107";
    private static final String OPT_RECUPERAR_SENHA_SENHA = "108";
    private static final String OPT_USUARIOS_MAIS_ATIVOS = "109";
    private static final String OPT_PERFIL_USUARIO = "110";
    private static final String OPT_DATE_TIME_SERVIDOR = "111";
    private static final String OPT_MEUS_CADASTROS = "112";
    private static final String OPT_VERIFICAR_ANALISE_DOCUMENTAL = "113";
    private static final String OPT_VERIFICAR_UF = "114";
    private static final String OPT_TROCAR_IMAGEM_PERFIL_USUARIO = "115";
    private static final String OPT_TROCAR_EMAIL = "116";
    private static final String OPT_ENVIAR_SUGESTAO = "117";
    private static final String OPT_EXCLUIR_PERFIL_USUARIO = "118";
    private static final String OPT_DENUNCIAR_USUARIO = "119";
    private static final String OPT_BUSCAR_USUARIO = "120";
    private static final String OPT_VERIFICAR_MENSAGEM_DO_SISTEMA = "121";
    private static final String OPT_ENVIAR_TELEFONE = "122";
    private static final String OPT_ENVIAR_EMAIL_INSTITUCIONAL = "123";
    private static final String OPT_ENVIAR_CODIGO_CONFIRMACAO = "124";
    private static final String OPT_SINCABS_COMPARTILHADO = "125";

    private static final String OPT_CADASTRAR_SUSPEITO = "201";
    private static final String OPT_CADASTROS_RECENTES = "202";
    private static final String OPT_PERFIL_SUSPEITO = "203";
    private static final String OPT_PERFIL_SUSPEITO_IMAGENS = "204";
    private static final String OPT_ENVIAR_IMAGEM_SUSPEITO = "205";
    private static final String OPT_EXCLUIR_IMAGEM_SUSPEITO = "206";
    private static final String OPT_TROCAR_IMAGEM_PERFIL_SUSPEITO = "207";
    private static final String OPT_EXCLUIR_PERFIL_SUSPEITO = "208";
    private static final String OPT_BUSCAR_SUSPEITO = "209";
    private static final String OPT_OBTER_COMENTARIOS = "210";
    private static final String OPT_ENVIAR_COMENTARIO = "211";
    private static final String OPT_EXCLUIR_COMENTARIO = "212";
    private static final String OPT_EDITAR_RELATO = "213";
    private static final String OPT_EDITAR_NOME_ALCUNHA = "214";
    private static final String OPT_EDITAR_AREA_DE_ATUACAO = "215";
    private static final String OPT_EDITAR_HISTORICO_CRIMINAL = "216";
    private static final String OPT_EDITAR_NOME_COMPLETO = "217";
    private static final String OPT_EDITAR_NOME_DA_MAE = "218";
    private static final String OPT_EDITAR_CPF = "219";
    private static final String OPT_EDITAR_RG = "220";
    private static final String OPT_EDITAR_DATA_NASCIMENTO = "221";
    private static final String OPT_DENUNCIAR_SUSPEITO = "222";

    private static final String OPT_ADMIN_OBTER_ANALISES = "301";
    private static final String OPT_ADMIN_OBTER_INFORMACOES_USUARIO = "302";
    private static final String OPT_ADMIN_CONCLUIR_ANALISE = "303";
    private static final String OPT_ADMIN_BLOQUEAR_USUARIO = "304";
    private static final String OPT_ADMIN_DESBLOQUEAR_USUARIO = "305";
    private static final String OPT_ADMIN_OBTER_SUGESTOES = "306";

    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    private static MCrypt mcrypt = new MCrypt();
    private static Context context;

    public SincabsServer(Context context) {

        this.context = context;
        client.setUserAgent(getUserAgent());
    }

    public void login(String cpf, String senha, AsyncHttpResponseHandler responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        params.put("cpf", cpf);
        params.put("senha", senha);
        params.put("id_aparelho", getAparelhoId());

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_LOGIN);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void veririfcarCPF(String cpf, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        params.put("cpf", cpf);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_VERIFICAR_CPF);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void getRequest(String url, AsyncHttpResponseHandler responseHandler) {

        client.get(url, responseHandler);
    }

    public void veririfcarUF(String uf, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        params.put("uf", uf);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_VERIFICAR_UF);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarImagem(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        try {

            params.put("img_principal", dh.getImgPrincipalSaved());
            params.put("img_busca", dh.getImgBuscaSaved());
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_ENVIAR_IMAGEM, params, responseHandler);
    }

    public void cadastrarUsuario(String nome, String cpf, String email, String senha, String instituicao, String uf, String matricula, String idAparelho, String telefone, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("img_principal", dh.getImgPrincipalSaved().getName());
        params.put("img_busca", dh.getImgBuscaSaved().getName());
        params.put("nome_completo", nome);
        params.put("cpf", cpf);
        params.put("email", email);
        params.put("senha", senha);
        params.put("instituicao", instituicao);
        params.put("uf", uf);
        params.put("matricula", matricula);
        params.put("id_aparelho", idAparelho);
        params.put("telefone", telefone);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_CADASTRAR_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void cadastrarSuspeito(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));
        params.put("online_hash", dh.getLoginData("online_hash"));

        String[] infoSuspeito = dh.getInfoCadastroSuspeito();

        params.put("nome_alcunha", infoSuspeito[0]);
        params.put("nome_completo", infoSuspeito[1]);
        params.put("crt_cor_pele", infoSuspeito[2]);
        params.put("crt_cor_olhos", infoSuspeito[3]);
        params.put("crt_cor_cabelos", infoSuspeito[4]);
        params.put("crt_tipo_cabelos", infoSuspeito[5]);
        params.put("crt_porte_fisico", infoSuspeito[6]);
        params.put("crt_estatura", infoSuspeito[7]);
        params.put("crt_deficiente", infoSuspeito[8]);
        params.put("crt_tatuagem", infoSuspeito[9]);
        params.put("relato", infoSuspeito[10]);
        params.put("historico_criminal", infoSuspeito[11]);
        params.put("areas_de_atuacao", infoSuspeito[12]);
        params.put("nome_da_mae", infoSuspeito[13]);
        params.put("cpf", infoSuspeito[14]);
        params.put("rg", infoSuspeito[15]);
        params.put("data_nascimento", infoSuspeito[16]);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_CADASTRAR_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_principal", dh.getImgPrincipalSaved());
            paramsFinal.put("img_busca", dh.getImgBuscaSaved());
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarDocumentoFrente(File imgFrentePrincipal, File imgFrenteBusca, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_DOCUMENTO_FRENTE);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_id_frente_principal", imgFrentePrincipal);
            paramsFinal.put("img_id_frente_busca", imgFrenteBusca);
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarDocumentoVerso(File imgVersoPrincipal, File imgVersoBusca, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_DOCUMENTO_VERSO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_id_verso_principal", imgVersoPrincipal);
            paramsFinal.put("img_id_verso_busca", imgVersoBusca);
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarImagemSuspeito(String id_suspeito, File imgPrincipal, File imgBusca, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_suspeito", id_suspeito);
        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_IMAGEM_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_principal", imgPrincipal);
            paramsFinal.put("img_busca", imgBusca);
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void excluirImagemSuspeito(String id_suspeito, String img_principal, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));
        params.put("id_suspeito", id_suspeito);
        params.put("img_principal", img_principal);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EXCLUIR_IMAGEM_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void excluirPerfilSuspeito(String id_suspeito, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EXCLUIR_PERFIL_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void buscarSuspeito(int index, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();
        String[] data = dh.getBuscarSuspeitoData();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("index", index);

        params.put("nome_alcunha", data[0]);
        params.put("areas_de_atuacao", data[1]);
        params.put("historico_criminal", data[2]);
        params.put("crt_cor_pele", data[3]);
        params.put("crt_cor_olhos", data[4]);
        params.put("crt_cor_cabelos", data[5]);
        params.put("crt_tipo_cabelos", data[6]);
        params.put("crt_porte_fisico", data[7]);
        params.put("crt_estatura", data[8]);
        params.put("crt_deficiente", data[9]);
        params.put("crt_tatuagem", data[10]);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_BUSCAR_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void buscarUsuario(int index, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();
        String[] data = dh.getBuscarUsuarioData();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("index", index);
        params.put("nome", data[0]);
        params.put("ocupacao_profissional", data[1]);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_BUSCAR_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void excluirPerfilUsuario(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EXCLUIR_PERFIL_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void denunciarUsuario(String motivo_denuncia, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("id_usuario_denunciado", dh.getPerfilUsuarioData("id_usuario"));
        params.put("motivo_denuncia", motivo_denuncia);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_DENUNCIAR_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void denunciarSuspeito(String id_suspeito, String motivo_denuncia, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("id_suspeito", id_suspeito);
        params.put("motivo_denuncia", motivo_denuncia);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_DENUNCIAR_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void trocarImagemPerfilUsuario(File imgPrincipal, File imgBusca, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_TROCAR_IMAGEM_PERFIL_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_principal", imgPrincipal);
            paramsFinal.put("img_busca", imgBusca);
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void atualizarImagemPerfilUsuario(String id_usuario, String protect_hash, String online_hash, File imgPrincipal, File imgBusca, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", id_usuario);
        params.put("online_hash", online_hash);
        params.put("protect_hash", protect_hash);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_TROCAR_IMAGEM_PERFIL_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_principal", imgPrincipal);
            paramsFinal.put("img_busca", imgBusca);
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void obterComentarios(int index, String id_suspeito, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("index", index);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_OBTER_COMENTARIOS);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarComentario(String id_suspeito, String comentario, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("comentario", comentario);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_COMENTARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarTelefone(String telefone, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("telefone", telefone);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_TELEFONE);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarEmailInstitucional(String email_institucional, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("email_institucional", email_institucional);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_EMAIL_INSTITUCIONAL);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarCodigoConfirmacao(String codigo, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("codigo", codigo);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_CODIGO_CONFIRMACAO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void sincabsCompartilhado(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_SINCABS_COMPARTILHADO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarRelato(String id_suspeito, String relato, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("relato", relato);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_RELATO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarNomeAlcunha(String id_suspeito, String nome_alcunha, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("nome_alcunha", nome_alcunha);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_NOME_ALCUNHA);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarAreaAtuacao(String id_suspeito, int area_de_atuacao, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("area_de_atuacao", area_de_atuacao);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_AREA_DE_ATUACAO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarHistoricoCriminal(String id_suspeito, int historico, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("historico", historico);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_HISTORICO_CRIMINAL);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarNomeCompleto(String id_suspeito, String nome_completo, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("nome_completo", nome_completo);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_NOME_COMPLETO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarNomeDaMae(String id_suspeito, String nome_da_mae, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("nome_da_mae", nome_da_mae);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_NOME_DA_MAE);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarCPF(String id_suspeito, String cpf, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("cpf", cpf);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_CPF);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarRG(String id_suspeito, String rg, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("rg", rg);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_RG);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void editarDataDeNascimento(String id_suspeito, String data_nascimento, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("data_nascimento", data_nascimento);
        params.put("id_suspeito", id_suspeito);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EDITAR_DATA_NASCIMENTO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void excluirComentario(String id_comentario, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("id_comentario", id_comentario);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_EXCLUIR_COMENTARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void trocarImagemPerfilSuspeito(String id_suspeito, File imgPrincipal, File imgBusca, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_suspeito", id_suspeito);
        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_TROCAR_IMAGEM_PERFIL_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        try {

            paramsFinal.put("img_principal", imgPrincipal);
            paramsFinal.put("img_busca", imgBusca);
        }
        catch (Exception e) {

            return;
        }

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void trocarEmail(String email, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));
        params.put("email", email);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_TROCAR_EMAIL);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void cancelarCadastro(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_CANCELAR_CADASTRO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void recuperarSenhaIdAparelho(String cpf, String matricula, String instituicao, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        params.put("cpf", cpf);
        params.put("matricula", matricula);
        params.put("instituicao", instituicao);
        params.put("id_aparelho", getAparelhoId());

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_RECUPERAR_SENHA_ID_APARELHO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void trocarSenha(String senha, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("senha", senha);
        params.put("cpf", dh.getLoginData("cpf"));
        params.put("matricula", dh.getLoginData("matricula"));
        params.put("instituicao", dh.getLoginData("instituicao"));
        params.put("id_aparelho", getAparelhoId());

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_RECUPERAR_SENHA_SENHA);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void recuperarSenhaSenha(String senha, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        String[] data = DataHolder.getInstance().getRecuperarSenhaData();

        params.put("senha", senha);
        params.put("cpf", data[0]);
        params.put("matricula", data[1]);
        params.put("instituicao", data[2]);
        params.put("id_aparelho", getAparelhoId());

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_RECUPERAR_SENHA_SENHA);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void dateTimeServidor(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_DATE_TIME_SERVIDOR);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void usuariosMaisAtivos(int index, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("index", index);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_USUARIOS_MAIS_ATIVOS);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void adminObterAnalises(int index, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("index", index);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ADMIN_OBTER_ANALISES);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    /*public void adminObterSugestoes(int index, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("index", index);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ADMIN_OBTER_SUGESTOES);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }*/

    public void adminObterInformacoesUsuario(String id_usuario, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("id_usuario_perfil", id_usuario);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ADMIN_OBTER_INFORMACOES_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void adminBloquearUsuario(String id_usuario, String motivo_bloqueio, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("motivo", motivo_bloqueio);
        params.put("id_usuario_perfil", id_usuario);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ADMIN_BLOQUEAR_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void adminDesbloquearUsuario(String id_usuario, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("id_usuario_perfil", id_usuario);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ADMIN_DESBLOQUEAR_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void adminConcluirAnaliseDocumental(String id_usuario, String analise, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("id_usuario_perfil", id_usuario);
        params.put("analise", analise);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ADMIN_CONCLUIR_ANALISE);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void ultimosCadastros(int index, String date_time_max, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("index", index);
        params.put("date_time_max", date_time_max);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_CADASTROS_RECENTES);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void meusCadastros(int index, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("index", index);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_MEUS_CADASTROS);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void perfilUsuario(String id_usuario, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("id_usuario_perfil", id_usuario);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_PERFIL_USUARIO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void perfilSuspeito(String id_suspeito, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("id_suspeito", id_suspeito);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_PERFIL_SUSPEITO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void perfilSuspeitoImagens(String id_suspeito, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("id_suspeito", id_suspeito);
        params.put("online_hash", dh.getLoginData("online_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_PERFIL_SUSPEITO_IMAGENS);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void verificarAnaliseDocumental(String id_usuario, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        params.put("id_usuario", id_usuario);

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_VERIFICAR_ANALISE_DOCUMENTAL);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void verificarMensagemDoSistema(SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        params.put("some", "key"); //necessário para nao dar erro no execute.php do servidor.

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_VERIFICAR_MENSAGEM_DO_SISTEMA);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public void enviarSugestao(String sugestao, SincabsResponse responseHandler) {

        RequestParams params = new RequestParams();
        RequestParams paramsFinal = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        params.put("sugestao", sugestao);
        params.put("id_usuario", dh.getLoginData("id_usuario"));
        params.put("online_hash", dh.getLoginData("online_hash"));
        params.put("protect_hash", dh.getLoginData("protect_hash"));

        paramsFinal.put("versao_app", Integer.toString(getVersaoApp()));
        paramsFinal.put("opcao", OPT_ENVIAR_SUGESTAO);
        paramsFinal.put("codigo", encryptString(params.toString()));
        paramsFinal.put("check", HashString.md5(params.toString()));

        client.post(HOST_EXECUTAR, paramsFinal, responseHandler);
    }

    public static String getLogoAddress(String instituicao, String uf) {

        if (instituicao.equals("1")) {

            return HOST_LOGO + "pc/" + uf + ".jpg";
        }
        else if (instituicao.equals("2")) {

            return HOST_LOGO + "pm/" + uf + ".jpg";
        }
        else if (instituicao.equals("3")) {

            return HOST_LOGO + "pf/pf.jpg";
        }
        else if (instituicao.equals("4")) {

            return HOST_LOGO + "prf/prf.jpg";
        }
        else if (instituicao.equals("5")) {

            return HOST_LOGO + "asp_es/asp_es.jpg";
        }
        else if (instituicao.equals("6")) {

            return HOST_LOGO + "asp_fe/asp_fe.jpg";
        }
        else if (instituicao.equals("7")) {

            return HOST_LOGO + "bm/" + uf + ".jpg";
        }
        else if (instituicao.equals("8")) {

            return HOST_LOGO + "ffaa/mar.jpg";
        }
        else if (instituicao.equals("9")) {

            return HOST_LOGO + "ffaa/eb.jpg";
        }
        else {

            return HOST_LOGO + "ffaa/fab.jpg";
        }
    }

    public static String getIconPointAddress(String icon) {

        return HOST_ICON_POINTS + icon;
    }

    public static String getAparelhoId() {

        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (id == null) {

            id = "ID-APARELHO-NULL";
        }

        return id;
    }

    private int getVersaoApp() {

        int versao_app;

        try {

            versao_app = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
        catch (Exception e) {

            versao_app = 1;
        }

        return versao_app;
    }

    private String encryptString(String code) {

        String response = Base64.encodeToString(code.getBytes(), Base64.DEFAULT);

        try {

            response = MCrypt.bytesToHex(mcrypt.encrypt(response));
        }
        catch (Exception e) { }

        return response;
    }

    public static String decryptString(byte[] bytes) {

        String response = "";

        try {

            response = new String(bytes, "UTF-8");
            response = new String(mcrypt.decrypt(response), "UTF-8");
            response = new String(Base64.decode(response, Base64.DEFAULT), "UTF-8");
        }
        catch (Exception e) { }

        return response;
    }

    private String getUserAgent() {

        return "SINCABS - Sistema Inteligente de Cadastro e Busca de Suspeitos. App Versão: " + getVersaoApp() + ", AparelhoID: " + this.getAparelhoId() + ".";
    }

    public static String getResponseError(byte[] response) {

        JSONObject json = null;

        try {

            json = new JSONObject(decryptString(response));

            return json.getString("Erro");
        }
        catch (Exception e) {

            return "-1";
        }
    }

    public static String getResponseMessage(byte[] response) {

        JSONObject json = null;

        try {

            json = new JSONObject(decryptString(response));

            return json.getString("Mensagem");
        }
        catch (Exception e) {

            return "Falha na conexão.";
        }
    }

    public static JSONObject getResponseExtra(byte[] response) {

        JSONObject json = null;

        try {

            json = new JSONObject(decryptString(response));

            return new JSONObject(json.getString("Extra"));
        }
        catch (Exception e) {

            return null;
        }
    }

    public static String getImageAddress(String imgName) {

        return HOST_ROOT + getImagePathInServer(imgName);
    }

    public static String getImagePathInServer(String imgName) {

        String parts[] = imgName.split("-");

        return "data/" + parts[0].toLowerCase() + "/" + (parts[1].equals("P") ? "principal" : "busca") + "/" +  parts[2] + "/" + getFolderBySize(parts[3]) + "/" +  parts[4].charAt(0) + "/" + parts[4].charAt(1) + "/" + parts[4].charAt(2) + "/" + parts[4].charAt(3) + "/" + imgName;
    }

    private static String getFolderBySize(String size) {

        int imgSize;
        String folderBySize;

        try {

            imgSize = Integer.parseInt(size);
        }
        catch (NumberFormatException e) {

            return null;
        }

        if (imgSize < 5120) {

            folderBySize = "a5kB";
        }
        else if (imgSize < 10240) {

            folderBySize = "b10kB";
        }
        else if (imgSize < 15360) {

            folderBySize = "c15kB";
        }
        else if (imgSize < 20480) {

            folderBySize = "d20kB";
        }
        else if (imgSize < 30720) {

            folderBySize = "e30kB";
        }
        else if (imgSize < 40960) {

            folderBySize = "f40kB";
        }
        else if (imgSize < 51200) {

            folderBySize = "g50kB";
        }
        else if (imgSize < 76800) {

            folderBySize = "h75kB";
        }
        else if (imgSize < 102400) {

            folderBySize = "i100kB";
        }
        else if (imgSize < 128000) {

            folderBySize = "j125kB";
        }
        else if (imgSize < 153600) {

            folderBySize = "k150kB";
        }
        else if (imgSize < 179200) {

            folderBySize = "l175kB";
        }
        else if (imgSize < 204800) {

            folderBySize = "m200kB";
        }
        else if (imgSize < 256000) {

            folderBySize = "n250kB";
        }
        else if (imgSize < 307200) {

            folderBySize = "o300kB";
        }
        else if (imgSize < 358400) {

            folderBySize = "p350kB";
        }
        else if (imgSize < 409600) {

            folderBySize = "q400kB";
        }
        else if (imgSize < 460800) {

            folderBySize = "r450kB";
        }
        else if (imgSize < 512000) {

            folderBySize = "s500kB";
        }
        else if (imgSize < 768000) {

            folderBySize = "t750kB";
        }
        else if (imgSize < 1024000) {

            folderBySize = "u1MB";
        }
        else {

            folderBySize = "v1MBover";
        }

        return folderBySize;
    }
}
