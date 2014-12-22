package br.tecsinapse.checklist.entidades;

public class ItemChecagemDaCategoria {

    private String titulo;
    private int id;
    private int idExternoItemDaCategoria;
    private int idCategoria;

    public int getIdExternoItemDaCategoria() {
        return idExternoItemDaCategoria;
    }

    public void setIdExternoItemDaCategoria(int idExternoItemDaCategoria) {
        this.idExternoItemDaCategoria = idExternoItemDaCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
