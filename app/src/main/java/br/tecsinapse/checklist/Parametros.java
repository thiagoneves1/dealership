package br.tecsinapse.checklist;


import android.os.Parcel;
import android.os.Parcelable;

    public class Parametros implements Parcelable {
        public final static String TIPO_S3 = "S3";
        public final static String TIPO_REST = "REST";


        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        private String tipo;
        private String urlGetUsuarios;

        public String getUrlPutIdUsuarioRecebeJson() {
            return urlPutIdUsuarioRecebeJson;
        }

        public void setUrlPutIdUsuarioRecebeJson(String urlPutIdUsuarioRecebeJson) {
            this.urlPutIdUsuarioRecebeJson = urlPutIdUsuarioRecebeJson;
        }

        public String getUrlGetUsuarios() {
            return urlGetUsuarios;
        }

        public void setUrlGetUsuarios(String urlGetUsuarios) {
            this.urlGetUsuarios = urlGetUsuarios;
        }

        public String getUrlPostJsonResposta() {
            return urlPostJsonResposta;
        }

        public void setUrlPostJsonResposta(String urlPostJsonResposta) {
            this.urlPostJsonResposta = urlPostJsonResposta;
        }

        private String urlPutIdUsuarioRecebeJson;
        private String urlPostJsonResposta;




        public Parametros (String tipo){
        this.tipo = tipo;
    }



        public Parametros(Parcel in){
            String[] data = new String[4];

            in.readStringArray(data);
            this.tipo = data[0];
            this.urlGetUsuarios = data[1];
            this.urlPutIdUsuarioRecebeJson = data[2];
            this.urlPostJsonResposta = data[3];

        }

        public static final Parcelable.Creator<Parametros> CREATOR = new Parcelable.Creator<Parametros>() {
            public Parametros createFromParcel(Parcel in) {
                return new Parametros(in);
            }

            public Parametros[] newArray(int size) {
                return new Parametros[size];
            }
        };


            @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {

        dest.writeStringArray(new String[] {this.tipo, this.urlGetUsuarios, this.urlPutIdUsuarioRecebeJson, this.urlPostJsonResposta});
        }
    }
