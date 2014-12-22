package br.tecsinapse.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import br.com.dealer.dealerships.R;

public class Sincronizar extends ActionBarActivity {

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_sincronizar);
        DataBaseHelper banco = new DataBaseHelper(getApplicationContext());

        int idExterno =0;
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if(extras == null) {
                idExterno= 0;
            } else {
                idExterno= extras.getInt(Constantes.ID_EXTERNO);
            }
        }
        Log.i("Sincronizar item id: ", String.valueOf(idExterno));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sincronizar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent irParaLista = new Intent(Sincronizar.this, ListaItens.class);
                startActivity(irParaLista);
                return true;
            case R.id.action_refresh:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
