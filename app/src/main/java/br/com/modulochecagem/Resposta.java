package br.com.modulochecagem;


import java.util.ArrayList;
import java.util.List;

public class Resposta {
    int id;
    int idItemDaCategoria;
    String tipo;
    int opcional;
    int respondida;
    int idExterno;
    Condicao condicao;
    String valorResposta;
    int condicional;

    public List<Opcao> getListaOpcoes() {
        return listaOpcoes;
    }

    public void setListaOpcoes(List<Opcao> listaOpcoes) {
        this.listaOpcoes = listaOpcoes;
    }

    List<Opcao> listaOpcoes = new ArrayList<Opcao>();

    public int getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(int idExterno) {
        this.idExterno = idExterno;
    }

    public Condicao getCondicao() {
        return condicao;
    }

    public void setCondicao(Condicao condicao) {
        this.condicao = condicao;
    }

    public String getValorResposta() {
        return valorResposta;
    }

    public void setValorResposta(String resposta) {
        this.valorResposta = resposta;
    }

    public int getRespondida() {
        return respondida;
    }

    public void setRespondida(int respondida) {
        this.respondida = respondida;
    }

    public int getCondicional() {
        return condicional;
    }

    public void setCondicional(int condicional) {
        this.condicional = condicional;
    }

    public int getOpcional() {
        return opcional;
    }

    public void setOpcional(int opcional) {
        this.opcional = opcional;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setIdItemDaCategoria(int idItemDaCategoria) {
        this.idItemDaCategoria = idItemDaCategoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
