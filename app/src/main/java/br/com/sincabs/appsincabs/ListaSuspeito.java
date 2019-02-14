package br.com.sincabs.appsincabs;

public class ListaSuspeito {

    public String img_perfil_principal;
    public String img_perfil_busca;
    public String id_suspeito;
    public String nome_alcunha;
    public String areas_atuacao;
    public String data_cadastro;

    public ListaSuspeito(String img_perfil_principal, String img_perfil_busca, String id_suspeito, String nome_alcunha, String areas_atuacao, String data_cadastro) {

        this.img_perfil_principal = img_perfil_principal;
        this.img_perfil_busca = img_perfil_busca;
        this.id_suspeito = id_suspeito;
        this.nome_alcunha = nome_alcunha;
        this.areas_atuacao = areas_atuacao;
        this.data_cadastro = data_cadastro;
    }
}
