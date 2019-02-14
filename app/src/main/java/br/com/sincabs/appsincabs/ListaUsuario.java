package br.com.sincabs.appsincabs;

public class ListaUsuario {

    public String img_perfil_principal;
    public String img_perfil_busca;
    public String id_usuario;
    public String nome;
    public String ocupacao;
    public String uf;
    public String data_cadastro;
    public String pontuacao;

    public ListaUsuario(String img_perfil_principal, String img_perfil_busca, String id_usuario, String nome, String ocupacao, String uf, String data_cadastro, String pontuacao) {

        this.img_perfil_principal = img_perfil_principal;
        this.img_perfil_busca = img_perfil_busca;
        this.id_usuario = id_usuario;
        this.nome = nome;
        this.ocupacao = ocupacao;
        this.uf = uf;
        this.data_cadastro = data_cadastro;
        this.pontuacao = pontuacao;
    }
}
