package br.com.modulochecagem;

import java.util.ArrayList;
import java.util.List;


public class Categoria {

    private int id;
    private String nome;
    private int idItem;

    private List<ItemChecagemDaCategoria> listaItemChecagemDaCategoria = new ArrayList<ItemChecagemDaCategoria>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public List<ItemChecagemDaCategoria> getListaItemChecagemDaCategoria() {
        return listaItemChecagemDaCategoria;
    }

    public void setListaItemChecagemDaCategoria(List<ItemChecagemDaCategoria> listaItemChecagemDaCategoria) {
        this.listaItemChecagemDaCategoria = listaItemChecagemDaCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
