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
import android.widget.TextView;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.entidades.ItemChecagem;
import java.util.ArrayList;
import java.util.List;


public class ListaItens extends ActionBarActivity {

    public static final String TAG = "ListaItens";
    private List<ItemChecagem> listaItemChecagem = new ArrayList<ItemChecagem>();
    private DataBaseHelper banco;
    private ListView listViewTitulos;
    private List<Integer> listaIdExterno = new ArrayList<Integer>();
    private Bundle extras;
    private int dadosOk = 0;
    private TextView textViewItensRespondidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(Constantes.TITULO_LISTA_ITENS);
        setContentView(R.layout.lista_itens);

        listViewTitulos = (ListView) findViewById(R.id.list_view_titulos);
        textViewItensRespondidos = (TextView) findViewById(R.id.text_view_quantidade_respondido);


        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                dadosOk = 0;
            } else {
                dadosOk = extras.getInt(Constantes.OK);
            }
        }

        if (dadosOk == 1) {
            Log.i("dados inseridos", "sucesso");
            carregaDados();

        } else {
            carregaDados(); //dados ja gravados
        }
    }

    public void carregaDados() {
        this.banco = new DataBaseHelper(this);
        listaIdExterno = banco.obterListaIdExterno();
        listaItemChecagem = banco.obterListaItemChecagem();
        Log.i(TAG, String.valueOf(listaItemChecagem.size()));

        final ListaItensAdapter adapter = new ListaItensAdapter(ListaItens.this, listaItemChecagem);

        int totalItens = listaItemChecagem.size();
        int totalRespondido =0;
        for (ItemChecagem ic : listaItemChecagem){
            if(ic.getStatus()==Constantes.VALOR_ITEM_JA_VERIFICADO_TRUE){
                totalRespondido++;
            }
        }

        textViewItensRespondidos.append(" " + String.valueOf(totalRespondido) + "/" + String.valueOf(totalItens));

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
        getMenuInflater().inflate(R.menu.coletor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent irParaConfiguracao = new Intent(ListaItens.this, Configuracao.class);
                startActivity(irParaConfiguracao);
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
