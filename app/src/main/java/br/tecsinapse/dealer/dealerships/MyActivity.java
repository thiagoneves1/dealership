package br.tecsinapse.dealer.dealerships;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.ConectorComponente;
import br.tecsinapse.checklist.Parametros;

public class MyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Parametros parametros = new Parametros(Parametros.TIPO_REST);
        parametros.setUrlGetUsuarios("http://192.168.2.104:8080/jersey-tutorial/nomes");
        parametros.setUrlPutIdUsuarioRecebeJson("http://192.168.2.104:8080/jersey-tutorial/json");
        parametros.setUrlPostJsonResposta("http://192.168.2.104:8080/jersey-tutorial/enviarJson");


        Intent irParaModulo = new Intent(MyActivity.this, ConectorComponente.class);
        irParaModulo.putExtra("parametros",parametros);

        startActivity(irParaModulo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coletor, menu);
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
