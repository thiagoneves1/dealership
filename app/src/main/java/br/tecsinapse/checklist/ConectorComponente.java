package br.tecsinapse.checklist;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.dealer.dealerships.R;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class ConectorComponente extends ActionBarActivity {

    ProgressBar progressBar;
    TextView textViewProgress;
    boolean inseriu = false;
    Utils utils;
    public static final String TAG = "Conector Componente";
    private Bundle extras;
    private Parametros parametros;
    private DataBaseHelper banco;
    private boolean existeUsuario;
    private OkHttpClient client;
    private String json;
    private String urlPutIdUsuarioRecebeJson;
    private String idDevice;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                parametros = null;
            } else {
                parametros = extras.getParcelable("parametros");
            }
        }

        banco = new DataBaseHelper(ConectorComponente.this);
        existeUsuario = banco.existeUsuario();

        setContentView(R.layout.activity_conector_componente);

        utils = new Utils(ConectorComponente.this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewProgress = (TextView) findViewById(R.id.txt_progress);

        new CarregaDados().execute();
    }

    private class CarregaDados extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            processamentoPrincipal();
            return null;
        }

        public void processamentoPrincipal() {
            try {
                Controle controle = new Controle(getApplicationContext());


                if(existeUsuario) {
                    Log.i(TAG, "existeUsuario");

                    urlPutIdUsuarioRecebeJson = banco.getUrlPutIdUsuarioRecebeJson();
                    idDevice = banco.getIdDevice();
                    usuario = banco.getUsuario();
                    json =  carregarJson(urlPutIdUsuarioRecebeJson, idDevice, usuario);
                    Log.i(TAG + "json", json);
                    inseriu = controle.insereJson(json);
                }
                else{
                    salvaDadosUrls();
                }

                Log.i(TAG, String.valueOf(getResources().getDisplayMetrics().density));

            // getApplicationContext().deleteDatabase("bancoModulo");//usado para deletar o banco em testes

                int counter = 0;
                while (counter < 100) {
                    Thread.sleep(25);
                    counter++;
                    publishProgress(counter);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(Integer.valueOf(values[0]));
            textViewProgress.setText(Integer.valueOf(values[0]) + " %");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(!existeUsuario){
                Log.e(TAG, "nao exite usuario");
                Intent irParaConfiguracoes = new Intent(ConectorComponente.this, Configuracao.class);
                irParaConfiguracoes.putExtra(Constantes.OK, 1);
                startActivity(irParaConfiguracoes);
            }
            else if (inseriu) {
                Log.e(TAG, "inseriu");
                Intent irParaListaItens = new Intent(ConectorComponente.this, ListaItens.class);
                irParaListaItens.putExtra(Constantes.OK, 1);
                startActivity(irParaListaItens);
            } else {
                Log.e(TAG, "nao inseriu");
                Intent irParaListaItens = new Intent(ConectorComponente.this, ListaItens.class);
                irParaListaItens.putExtra(Constantes.OK, 0);
                startActivity(irParaListaItens);
            }
        }
    }

    private void salvaDadosUrls() {
        Log.i(TAG, "salvaDadosUrls");
        if(parametros!=null) {
            Log.i(TAG, parametros.getTipo());
            Log.i(TAG, parametros.getUrlGetUsuarios());
            Log.i(TAG, parametros.getUrlPutIdUsuarioRecebeJson());
            Log.i(TAG, parametros.getUrlPostJsonResposta());
            banco.insereUrls(parametros.getTipo(),  parametros.getUrlGetUsuarios(), parametros.getUrlPutIdUsuarioRecebeJson(),parametros.getUrlPostJsonResposta() );
        }
    }

    private String carregarJson(String... urls)  {
        Log.i(TAG, "carregarJson");
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urls[0])
                .build();

        Response response = null;
        try {

            response = client.newCall(request).execute();

           // return response.body().string();
        }
        catch (Exception e){
            Log.i(TAG + "ERRO", e.getMessage());
        }

        return GET(urls[0], urls[1], urls[2]);
    }
    private String GET(String url, String idDevice, String usuario)  {
        Log.i(TAG, "GET");
             String result = "";

        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", idDevice));
            nameValuePairs.add(new BasicNameValuePair("usuario", usuario));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);

            String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            Log.e(TAG + "respStr", respStr);
            if (respStr != null) {
                result = respStr;
            } else {
                result = "Erro ao receber dados";
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return  result;

    }
}

