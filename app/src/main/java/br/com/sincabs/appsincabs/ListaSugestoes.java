package br.com.sincabs.appsincabs;

public class ListaSugestoes {

    public String img_perfil_principal;
    public String img_perfil_busca;
    public String id_usuario;
    public String texto;
    public String data_cadastro;

    public ListaSugestoes(String img_perfil_principal, String img_perfil_busca, String id_usuario, String texto, String data_cadastro) {

        this.img_perfil_principal = img_perfil_principal;
        this.img_perfil_busca = img_perfil_busca;
        this.id_usuario = id_usuario;
        this.texto = texto;
        this.data_cadastro = data_cadastro;
    }
}
