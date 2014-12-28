package br.tecsinapse.checklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class Sincronizar extends ActionBarActivity {

    public static final String TAG = "Sincronizar";
    private static boolean wifiConnectedo = false;
    private static boolean deviceConectado = false;
    private Button buttonSincronizar;
    private EditText editResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sincronizar);

        buttonSincronizar = (Button) findViewById(R.id.button_sincronizar);
        editResult = (EditText) findViewById(R.id.edit_sincronia_result);

        buttonSincronizar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarConexao();
                editResult.getText().clear();
            }
        });



    }


   private void verificarConexao() {

        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnectedo = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            deviceConectado = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnectedo) {
                Log.i(TAG, Constantes.WIFI_CONECTADO);
                Toast.makeText(getApplicationContext(),Constantes.WIFI_CONECTADO,Toast.LENGTH_SHORT ).show();
                new DownloadJsonAsyncTask().execute("http://reqr.es/api/users?page=2");
            } else if (deviceConectado){
                Log.i(TAG, Constantes.MOBILE_CONECTADO);
                Toast.makeText(getApplicationContext(),Constantes.MOBILE_CONECTADO,Toast.LENGTH_SHORT ).show();
                new DownloadJsonAsyncTask().execute("http://reqr.es/api/users?page=2");
            }
        } else {
            Log.i(TAG, Constantes.NAO_CONECTADO);
            Toast.makeText(getApplicationContext(),Constantes.NAO_CONECTADO,Toast.LENGTH_SHORT ).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sincronizar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent irParaConfiguracao = new Intent(Sincronizar.this, Configuracao.class);
                startActivity(irParaConfiguracao);
                return true;
            case android.R.id.home:
                Sincronizar.this.finish();
                return true;
            case R.id.action_refresh:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class DownloadJsonAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =
                    ProgressDialog.show(Sincronizar.this, "Aguarde", "Baixando JSON, Por Favor Aguarde...");
        }
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(Sincronizar.this, "Dados Recebidos!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            editResult.setText(result);
        }
    }

    private String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {


            HttpClient httpclient = new DefaultHttpClient();


            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));


            inputStream = httpResponse.getEntity().getContent();


            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "erro ao receber dados";

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }
}



