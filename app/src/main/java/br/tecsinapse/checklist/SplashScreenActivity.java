package br.tecsinapse.checklist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.dealer.dealerships.R;

public class SplashScreenActivity extends ActionBarActivity {

    ProgressBar progressBar;
    TextView textViewProgress;
    boolean inseriu = false;
    Utilitario utilitario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.activity_splash_screen);

      utilitario = new Utilitario(SplashScreenActivity.this);

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
                  String jsonArquivo = utilitario.lerArquivo();
                  inseriu = controle.insereJson(jsonArquivo);

          // getApplicationContext().deleteDatabase("bancoModulo");//usado para deletar o banco em testes

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
            textViewProgress.setText(Integer.valueOf(values[0]) + " %");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (inseriu) {
                Intent i = new Intent(SplashScreenActivity.this, ListaItens.class);
                i.putExtra("ok", 1);
                startActivity(i);
            }
            else{
                Intent i = new Intent(SplashScreenActivity.this, ListaItens.class);
                i.putExtra("ok", 0);
                startActivity(i);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
