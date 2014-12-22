package br.tecsinapse.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.entidades.ItemChecagem;
import java.util.ArrayList;
import java.util.List;


public class ListaItens extends ActionBarActivity {

    List<ItemChecagem> listaItemChecagem = new ArrayList<ItemChecagem>();

    DataBaseHelper banco;

    ListView listViewTitulos;

    List<Integer> listaIdExterno = new ArrayList<Integer>();
    private Bundle extras;
    private int dadosOk = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(Constantes.TITULO_LISTA_ITENS);
        setContentView(R.layout.activity_my);

        listViewTitulos = (ListView) findViewById(R.id.list_view_titulos);

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                dadosOk = 0;
            } else {
                dadosOk = extras.getInt("ok");
            }
        }

        if (dadosOk == 1) {
            Log.i("dados inseridos", "sucesso");
            carregaDados();

        } else {
            Log.i("dados NAO inseridos", "ERRO");//mas existem outros dados ?
            carregaDados();
        }
    }

    public void carregaDados() {
        this.banco = new DataBaseHelper(this);
        listaIdExterno = banco.obterListaIdExterno();
        listaItemChecagem = banco.obterListaItemChecagem();

        final ListaItensAdapter adapter = new ListaItensAdapter(ListaItens.this, listaItemChecagem);

        listViewTitulos.setAdapter(adapter);
        listViewTitulos.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                Intent irParaChecagem = new Intent(getApplicationContext(), Coletor.class);
                irParaChecagem.putExtra(Constantes.ID_EXTERNO, listaIdExterno.get(position));
                irParaChecagem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(irParaChecagem);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.action_refresh:
                Intent irParaSincronia = new Intent(ListaItens.this, Sincronizar.class);
                startActivity(irParaSincronia);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
