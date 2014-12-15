package br.com.modulochecagem;


public class Opcao {

    int id;
    int idResposta;
    String valorTexto;

    public String getValorResposta() {
        return valorResposta;
    }

    String valorResposta;


    public void setValorResposta(String valorResposta) {
        this.valorResposta = valorResposta;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    public void setIdResposta(int idResposta) {
        this.idResposta = idResposta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
