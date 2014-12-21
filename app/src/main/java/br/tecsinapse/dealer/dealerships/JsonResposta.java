package br.tecsinapse.dealer.dealerships;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.EditText;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.Constantes;
import br.tecsinapse.checklist.Controle;
import br.tecsinapse.checklist.DataBaseHelper;
import br.tecsinapse.checklist.Utilitario;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class JsonResposta extends ActionBarActivity {
    private Bundle extras;
    EditText editTextResposta;
    private Utilitario conversor;
    DataBaseHelper banco;
    List<Integer> listaIDsFoto;
    List<String> listaCaminhosFoto;
    int idExterno = 0;
    ProgressDialog progressBar;
    boolean criarArquivosFotosBase64 = false;
    boolean criarArquivosFotosSHA1 = false;
    boolean criarArquivoJsonUnico = false;
    boolean criarArquivoUnicoIDDevice = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resposta);




        conversor = new Utilitario(JsonResposta.this);

        banco = new DataBaseHelper(getApplicationContext());

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

    private boolean writeToFile(String valor, String nomeArquivo) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(nomeArquivo, Context.MODE_PRIVATE ));
            outputStreamWriter.write(valor);
            outputStreamWriter.close();
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
            progressBar.setMessage("Aguarde");
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {


            processamentoPrincipal();

            return null;
        }

        public void processamentoPrincipal() {
            try {

                String nomeApp = banco.obterNomeApp(idExterno);
                Controle controle = new Controle(getApplicationContext());

                JSONObject jsonResposta = controle.obterJsonRespostas(nomeApp);
                criarArquivoJsonUnico = writeToFile(jsonResposta.toString(), "jsonResposta.txt");

                String stringIdAndroid = conversor.getUniquePsuedoID();
                criarArquivoUnicoIDDevice = writeToFile(stringIdAndroid, "idDevice.txt");

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

                    String SHA1 = "";
                    try {
                        SHA1 = conversor.gerarSHA1(file);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String base64 = conversor.converteImagemParaString(file);

                    Log.i("tamanho arquivo", String.valueOf(file.length()));
                    Log.i("stringIdAndroid", stringIdAndroid);

                    criarArquivosFotosSHA1 = writeToFile(SHA1, "SHA1_" + string + ".txt");
                    criarArquivosFotosBase64 = writeToFile(base64, "Base64_" + string + ".txt");

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

        }


        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(Integer.valueOf(values[0]));

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            progressBar.dismiss();

            if (criarArquivoJsonUnico) {
                editTextResposta.append("\n" +
                        " arquivo 'json_resposta.txt' (resposta formatada anteriormente) - criado com sucesso ");
            } else {
                editTextResposta.append("\n" +
                        " arquivo 'json_resposta.txt' (resposta formatada anteriormente) - Nao criado Erro ");
            }

            if (criarArquivoUnicoIDDevice){
                editTextResposta.append("\n" +
                        " arquivo 'idDevice.txt' (id Unico Device) - criado com sucesso ");
            }
            else {
                editTextResposta.append("\n" +
                        " arquivo 'idDevice.txt' (id Unico Device) - Nao criado Erro ");
            }


                if (criarArquivosFotosSHA1) {
                    editTextResposta.append("\n arquivos 'SHA1_...txt' (valor para checksum) - criados com sucesso SHA1");
                } else {
                    editTextResposta.append("\n arquivos 'SHA1' (valor para checksum) - Nao criados Erro");
                }


                if (criarArquivosFotosBase64) {
                    editTextResposta.append("\n" +
                            "  arquivos 'Base64_...txt' (Valor Hash Fotos) -  criados com sucesso");
                } else {
                    editTextResposta.append("\n" +
                            "  arquivos 'Base64_...txt' (Valor Hash Fotos) -  Nao criados Erro");
                }

            }





    }
}



