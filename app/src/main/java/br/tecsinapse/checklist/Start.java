package br.tecsinapse.checklist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.dealer.dealerships.R;

public class Start extends ActionBarActivity {

    ProgressBar progressBar;
    TextView textViewProgress;
    boolean inseriu = false;
    Utils utils;
    public static final String TAG = "Splash Screen Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //ler configuracoes...
        setContentView(R.layout.activity_splash_screen);

        utils = new Utils(Start.this);
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
                String jsonArquivo = utils.lerArquivo("arquivo_json");//apenas teste
                inseriu = controle.insereJson(jsonArquivo);

                Log.i(TAG, String.valueOf(getResources().getDisplayMetrics().density));

             // getApplicationContext().deleteDatabase("bancoModulo");//usado para deletar o banco em testes

                int counter = 0;
                while (counter < 100) {
                    Thread.sleep(20);
                    counter++;
                    publishProgress(counter);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(Integer.valueOf(values[0]));
            textViewProgress.setText(Integer.valueOf(values[0]) + " %");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (inseriu) {
                Intent i = new Intent(Start.this, ListaItens.class);
                i.putExtra(Constantes.OK, 1);
                startActivity(i);
            } else {
                Intent i = new Intent(Start.this, ListaItens.class);
                i.putExtra(Constantes.OK, 0);
                startActivity(i);
            }
        }
    }

}

