package br.tecsinapse.checklist;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dealer.dealerships.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Configuracao extends ActionBarActivity {
    private static final String TAG = "Configuracao";
    private TextView textViewIdDevice;
    private TextView textViewUsuario;
    private Spinner spinnerUsuarios;
    private Button buttonOk;
    private DataBaseHelper banco;
    private boolean existeUsuario;
    private String idDevice;
    private String usuario;
    private  OkHttpClient client;
    private String urlUsuarios;

    Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_configuracao);

        banco = new DataBaseHelper(Configuracao.this);
        utils = new Utils(Configuracao.this);

        textViewIdDevice = (TextView) findViewById(R.id.text_view_id_device);
        textViewUsuario = (TextView) findViewById(R.id.text_view_usuario);
        spinnerUsuarios = (Spinner) findViewById(R.id.spinner_usuarios);
        buttonOk = (Button) findViewById(R.id.button_ok_usuarios);



        existeUsuario = banco.existeUsuario();


        if(existeUsuario){
            Log.i(TAG, "existeUsuario");
            usuario = banco.obterUsuario();
            textViewUsuario.append(" " + usuario);
            spinnerUsuarios.setVisibility(View.INVISIBLE);
            buttonOk.setVisibility(View.INVISIBLE);
        }
        else{
            //temporario
//            String nomesArquivo = utils.lerArquivo("arquivo_nomes");
//            String[] array = nomesArquivo.split(",");
//            //

            idDevice = utils.getUniquePsuedoID();
            textViewIdDevice.append(" " + idDevice);
            urlUsuarios = banco.getUrlUsuarios();

            new DownloadJsonAsyncTask().execute(urlUsuarios);

            Log.i(TAG, "nao existeUsuario");

            buttonOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    usuario = spinnerUsuarios.getSelectedItem().toString();
                    try {
                        banco.insereDeviceIdEUsuario(idDevice, usuario);
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }

                    textViewUsuario.append(" " + usuario);
                    banco.insereDeviceIdEUsuario(idDevice, usuario);
                    Toast.makeText(Configuracao.this, Constantes.USUÁRIO_SALVO_COM_SUCESSO,Toast.LENGTH_LONG).show();
                    Intent irParaConector = new Intent(Configuracao.this, ConectorComponente.class);
                    startActivity(irParaConector);

                    Configuracao.this.finish();
                }
            });
        }
    }

    private class DownloadJsonAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =
                    ProgressDialog.show(Configuracao.this, "Aguarde", "Baixando Nomes, Por Favor Aguarde...");
        }
        @Override
        protected String doInBackground(String... urls)  {

            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            try {

                response = client.newCall(request).execute();

                return response.body().string();
            }
            catch (Exception e){
                Log.i(TAG, e.getMessage());
            }

            return GET(urls[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(Configuracao.this, "Dados Recebidos!", Toast.LENGTH_LONG).show();
            dialog.dismiss();

           String nomesArquivo = result;
           String[] array = nomesArquivo.split(",");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Configuracao.this, android.R.layout.simple_spinner_item,array);

            spinnerUsuarios = (Spinner) findViewById(R.id.spinner_usuarios);

            spinnerUsuarios.setAdapter(adapter);
        }
    }
    private String GET(String url) {

        InputStream inputStream;
        String result = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null) {
                result = converterInputStreamParaString(inputStream);
            }
            else {
                result = "Erro ao receber dados";
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }
    private static String converterInputStreamParaString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String linha = "";
        StringBuffer concatena = new StringBuffer();
        String resultado = "";

        while((linha = bufferedReader.readLine()) != null) {
            concatena.append(linha + "\n");
        }
        inputStream.close();
        resultado = concatena.toString();
        return  resultado;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.configuracao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:

                return true;

            case R.id.action_refresh:
                Intent irParaSincronia = new Intent(Configuracao.this, Sincronizar.class);
                startActivity(irParaSincronia);
                return true;

            case android.R.id.home:
             Configuracao.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
