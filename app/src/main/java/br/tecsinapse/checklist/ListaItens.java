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
import java.util.ArrayList;
import java.util.List;


public class ListaItens extends ActionBarActivity {

   // String json = "{\"checklists\":[{\"App\":\"X\",\"id\":\"1\",\"nome\":\"Entrega do Carro do José\",\"categorias\":[{\"nome\":\"Manuais e Serviços\",\"itens\":[{\"id\":\"1\",\"texto\":\"Entrega do Manual do Proprietário\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"foto\"},{\"id\":\"3\",\"tipo\":\"texto-livre\",\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"1\",\"valor\":\"OK\"}}]},{\"id\":\"2\",\"texto\":\"Entrega da Chave reserva\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"},{\"id\":\"3\",\"tipo\":\"foto\"},{\"id\":\"4\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"0\",\"valor\":\"N-A\"}}]}]},{\"nome\":\"Documentos\",\"itens\":[{\"id\":\"3\",\"texto\":\"Informação sobre a Autorização Provisória para circulação\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\",\"opcional\":\"true\"},{\"id\":\"3\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"2\",\"valor\":\"N-OK\"}}]}]}]},{\"App\":\"Y\",\"id\":\"2\",\"nome\":\"Entrega do Carro do João\",\"categorias\":[{\"nome\":\"Manuais e Serviços\",\"itens\":[{\"id\":\"1\",\"texto\":\"Entrega do Manual do Proprietário \",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"}]},{\"id\":\"2\",\"texto\":\"Entrega da Chave reserva\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"},{\"id\":\"3\",\"tipo\":\"foto\"}]}]},{\"nome\":\"Documentos\",\"itens\":[{\"id\":\"3\",\"texto\":\"Informação sobre a Autorização Provisória para circulaçãoY\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcional\":\"true\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"},{\"id\":\"3\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"2\",\"valor\":\"V1\"}}]}]}]},{\"App\":\"Z\",\"id\":\"3\",\"nome\":\"Entrega do Carro do Luiz\",\"categorias\":[{\"nome\":\"Manuais e Serviços \",\"itens\":[{\"id\":\"1\",\"texto\":\"Entrega do Manual do Proprietário \",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\",\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"2\",\"valor\":\"N-OK\"}}]},{\"id\":\"3\",\"texto\":\"Entrega da Chave reserva \",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"4\",\"tipo\":\"texto-livre\"},{\"id\":\"5\",\"tipo\":\"foto\"}]}]},{\"nome\":\"Documentos\",\"itens\":[{\"id\":\"3\",\"texto\":\"Informação sobre a Autorização Provisória para circulação Z\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\",\"opcional\":\"true\"},{\"id\":\"3\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"0\",\"valor\":\"N-A\"}}]}]}]}]}";

   // String json = "{\"checklists\":[{\"App\":\"X\",\"id\":\"1\",\"nome\":\"Entrega do Carro do José\",\"categorias\":[{\"nome\":\"Manuais e Serviços\",\"itens\":[{\"id\":\"1\",\"texto\":\"Entrega do Manual do Proprietário\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"foto\"},{\"id\":\"3\",\"tipo\":\"texto-livre\",\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"1\",\"valor\":\"OK\"}}]},{\"id\":\"2\",\"texto\":\"Entrega da Chave reserva\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"},{\"id\":\"3\",\"tipo\":\"foto\"},{\"id\":\"4\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}]}]}]},{\"nome\":\"Documentos\",\"itens\":[{\"id\":\"3\",\"texto\":\"Informação sobre a Autorização Provisória para circulação\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\",\"opcional\":\"true\"},{\"id\":\"3\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"2\",\"valor\":\"N-OK\"}}]}]}]},{\"App\":\"Y\",\"id\":\"2\",\"nome\":\"Entrega do Carro do João\",\"categorias\":[{\"nome\":\"Manuais e Serviços\",\"itens\":[{\"id\":\"1\",\"texto\":\"Entrega do Manual do Proprietário \",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"}]},{\"id\":\"2\",\"texto\":\"Entrega da Chave reserva\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"},{\"id\":\"3\",\"tipo\":\"foto\"}]}]},{\"nome\":\"Documentos\",\"itens\":[{\"id\":\"3\",\"texto\":\"Informação sobre a Autorização Provisória para circulaçãoY\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcional\":\"true\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\"},{\"id\":\"3\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"2\",\"valor\":\"V1\"}}]}]}]},{\"App\":\"Z\",\"id\":\"3\",\"nome\":\"Entrega do Carro do Luiz\",\"categorias\":[{\"nome\":\"Manuais e Serviços \",\"itens\":[{\"id\":\"1\",\"texto\":\"Entrega do Manual do Proprietário \",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\",\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"2\",\"valor\":\"N-OK\"}}]},{\"id\":\"3\",\"texto\":\"Entrega da Chave reserva \",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"4\",\"tipo\":\"texto-livre\"},{\"id\":\"5\",\"tipo\":\"foto\"}]}]},{\"nome\":\"Documentos\",\"itens\":[{\"id\":\"3\",\"texto\":\"Informação sobre a Autorização Provisória para circulação Z\",\"respostas\":[{\"id\":\"1\",\"tipo\":\"alternativas-radio\",\"opcoes\":[{\"texto\":\"OK\",\"resposta\":\"1\"},{\"texto\":\"N-OK\",\"resposta\":\"2\"},{\"texto\":\"N-A\",\"resposta\":\"0\"}]},{\"id\":\"2\",\"tipo\":\"texto-livre\",\"opcional\":\"true\"},{\"id\":\"3\",\"tipo\":\"alternativas-lista\",\"opcoes\":[{\"texto\":\"V1+\",\"resposta\":\"1\"},{\"texto\":\"V1\",\"resposta\":\"2\"},{\"texto\":\"V2\",\"resposta\":\"3\"},{\"texto\":\"V3\",\"resposta\":\"4\"}],\"visivel-se\":{\"pergunta\":\"1\",\"resposta\":\"0\",\"valor\":\"N-A\"}}]}]}]}]}";


    List<ItemChecagem> listaItemChecagem = new ArrayList<ItemChecagem>();

    DataBaseHelper banco;

    ListView listViewTitulos;

    List<Integer> listaIdExterno = new ArrayList<Integer>();
    private Bundle extras;
    private int dadosOk=0;


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

        ab.setTitle(Constantes.TITULO_LISTA_ITENS);

        if (dadosOk == 1) {
            Log.i("dados inseridos", "sucesso");
        carregaDados();

        } else {
            Log.i("dados NAO inseridos", "ERRO");//mas existem outros dados ?
           carregaDados();
        }

    }

    public void carregaDados(){

        this.banco = new DataBaseHelper(this);

        listaIdExterno = banco.obterListaIdExterno();
        for(int id :listaIdExterno){
            Log.i("ids",String.valueOf(id));
        }

        listaItemChecagem = banco.obterListaItemChecagem();

        for(ItemChecagem item :listaItemChecagem){
            Log.i("ids", item.getTituloItem());
        }


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.i("ListaItens", "action_settings");
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
