package br.tecsinapse.dealer.dealerships;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.Configuracao;
import br.tecsinapse.checklist.Constantes;
import br.tecsinapse.checklist.Controle;
import br.tecsinapse.checklist.DataBaseHelper;
import br.tecsinapse.checklist.ListaItens;
import br.tecsinapse.checklist.Sincronizar;
import br.tecsinapse.checklist.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class JsonResposta extends ActionBarActivity {
    private Bundle extras;
    private EditText editTextResposta;
    private Utils utils;
    private DataBaseHelper banco;
    private List<Integer> listaIDsFoto;
    private List<String> listaCaminhosFoto;
    private int idExterno = 0;
    private ProgressDialog progressBar;
    private boolean criarArquivosFotosBase64 = false;
    private boolean criarArquivosFotosSHA1 = false;
    private boolean criarArquivoJsonUnico = false;
    private Controle controle;
    private String base64;
    private OkHttpClient client;
    private String TAG = "JsonResposta";
    private String SHA1;
    private String urlPostJsonRespostas;
    private JSONObject jsonResposta;
    private String resposta;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_resposta);

        utils = new Utils(JsonResposta.this);

        banco = new DataBaseHelper(getApplicationContext());
        urlPostJsonRespostas = banco.getUrlPostJsonResposta();

        listaIDsFoto = new ArrayList<Integer>();
        listaCaminhosFoto = new ArrayList<String>();

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                idExterno = 0;
            } else {
                idExterno = extras.getInt(Constantes.ID_EXTERNO);
            }
        }
        editTextResposta = (EditText) findViewById(R.id.txt_json_resposta);

        new CarregaDados().execute();
    }

    private File caminhoArquivoDaFoto(String nomeFoto) {

        File diretorio =  Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!diretorio.exists()) {
            if (!diretorio.mkdirs() ) {
                return null;
            }
        }
        Log.i("nomeFoto", nomeFoto);
        return new File(diretorio.getPath() + File.separator
                + nomeFoto.trim()+ ".jpg");
    }

    private boolean escreverArquivo(String valor, String nomeArquivo) {

            File root = android.os.Environment.getExternalStorageDirectory();

            File dir = new File (root.getAbsolutePath() + "/"+ getApplicationContext().getResources().getString(R.string.app_name_pasta));
            dir.mkdirs();
            File file = new File(dir, nomeArquivo);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.print(valor);
            pw.flush();
            pw.close();
            f.close();
            return true;
        } catch (IOException e) {
            Log.e("IOException", "Erro ao criar arquivo: " + e.toString());
            return false;
        }
    }

    private class CarregaDados extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = new ProgressDialog(JsonResposta.this);

            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setTitle("Gerando Arquivos");
            progressBar.setMessage("Aguarde...");
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                processamentoPrincipal();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG + "erro", e.getMessage());
            }

           return null;
        }

        public void processamentoPrincipal() throws IOException {
            try {

                String nomeApp = banco.obterNomeApp(idExterno);
                controle = new Controle(getApplicationContext());

                jsonResposta = controle.obterJsonRespostas(nomeApp);
                criarArquivoJsonUnico = escreverArquivo(jsonResposta.toString(), "jsonResposta.txt");

                int idItemChecagem = banco.obterIdItemChecagem(nomeApp);
                listaIDsFoto = banco.obterListaIdFotos(idItemChecagem);

                for (int i : listaIDsFoto) {
                    String caminhoFoto = banco.obterCaminhoFoto(i);
                    listaCaminhosFoto.add(caminhoFoto);
                }

                for(String string:listaCaminhosFoto){
                File file = caminhoArquivoDaFoto(string);
                Log.i("listaCaminhosFoto", string);

                if (file.exists()) {


                    try {
                        SHA1 = utils.gerarSHA1(file);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                     base64 = utils.converteImagemParaString(file);

                    Log.i("tamanho arquivo", String.valueOf(file.length()));

                    criarArquivosFotosSHA1 = escreverArquivo(SHA1, "SHA1_" + string + ".txt");

                } else {
                    Log.i("arquivo nao criado", "erroa o criar arquivo");
                }
            }

                int counter = 0;
                while (counter < 100) {
                    Thread.sleep(100);
                    counter++;
                    publishProgress(counter);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

          resposta =   enviaJsonResposta(urlPostJsonRespostas, jsonResposta.toString());
            Log.i(TAG + "3", resposta);

        }


        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(Integer.valueOf(values[0]));

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Log.i(TAG + "2", resposta);
            progressBar.dismiss();

            if(resposta.equals("Json recebido com sucesso")){
                banco.atualizaStatusSincronizadoDoitem(idExterno);
                Toast.makeText(JsonResposta.this, "Sincronizacao Efetuada com sucesso", Toast.LENGTH_LONG).show();
                Intent irParaLista = new Intent(JsonResposta.this, ListaItens.class);
                startActivity(irParaLista);
            }

            }


        }

    private String enviaJsonResposta(String url, String json) throws IOException {
        client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = null;


            response = client.newCall(request).execute();
            return response.body().string();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_resposta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent irParaConfiguracao = new Intent(JsonResposta.this, Configuracao.class);
                startActivity(irParaConfiguracao);
                return true;

            case R.id.action_refresh:
                Intent irParaSincronia = new Intent(JsonResposta.this, Sincronizar.class);
                startActivity(irParaSincronia);
                return true;

            case android.R.id.home:
                JsonResposta.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}



