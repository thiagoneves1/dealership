package br.com.modulochecagem;

import java.util.ArrayList;
import java.util.List;

public class ItemChecagem {

    private int id;
    private int idExterno;
    private String tituloItem;
    private int status;
    private String app;


    public int getStatus() {
        return status;
    }

    private List<Categoria> listaCategorias = new ArrayList<Categoria>();

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Categoria> getListaCategorias() {
        return listaCategorias;
    }

    public void setListaCategorias(List<Categoria> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }

    public void setIdExterno(int idExterno) {
        this.idExterno = idExterno;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTituloItem() {
        return tituloItem;
    }

    public void setTituloItem(String tituloItem) {
        this.tituloItem = tituloItem;
    }

}
