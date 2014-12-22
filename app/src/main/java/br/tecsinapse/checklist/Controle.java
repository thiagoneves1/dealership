package br.tecsinapse.checklist;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.entidades.Categoria;
import br.tecsinapse.checklist.entidades.ItemChecagemDaCategoria;
import br.tecsinapse.checklist.entidades.Resposta;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Controle {

    public Context context;
    DataBaseHelper banco;
    private boolean salvaItem = true;
    JSONObject jsonCompleto = null;
    List<Integer> listaIdExterno = new ArrayList<Integer>();
    List<String> listaTituloItemChecagem = new ArrayList<String>();

    public Controle(Context context) {
        this.context = context;
        this.banco = new DataBaseHelper(this.context);
    }

    public boolean insereJson(String json) {
        boolean retorno;
        listaIdExterno = banco.obterListaIdExterno(); //lista somente pra ver se tem o id dentro do banco

        try {
            retorno = verificaSeItemExisteESalva(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return retorno;
    }

    private boolean verificaSeItemExisteESalva(String json) throws JSONException {
        boolean retorno = false;
        jsonCompleto = new JSONObject(json);
        JSONArray jsonPrimeiroNo = jsonCompleto.optJSONArray(JsonConstantes.JSON_CHECKLISTS);//separa os itens de Checagem
        int tamanhoJsonArray = jsonPrimeiroNo.length();

        for (int i = 0; i < tamanhoJsonArray; i++) {

            JSONObject jsonObjetosUnicos = jsonPrimeiroNo.getJSONObject(i);
            String tituloChecagem = jsonObjetosUnicos.getString(JsonConstantes.JSON_NOME);
            String nomeApp = jsonObjetosUnicos.getString(JsonConstantes.JSON_APP);
            int idExterno = jsonObjetosUnicos.getInt(JsonConstantes.JSON_ID);

            if (listaIdExterno.contains(idExterno)) {
                salvaItem = false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String data = sdf.format(new Date());

            if (salvaItem) {
                SalvarItem(jsonObjetosUnicos, tituloChecagem, idExterno, nomeApp, data);
                retorno = true;
            }
        }
        return retorno;
    }

    private void SalvarItem(JSONObject jsonObjetosUnicos, String tituloChecagem, int idExterno, String nomeApp, String data) throws JSONException {

        long idItemChecagem;
        idItemChecagem = banco.insereItemChecagem(idExterno, tituloChecagem, nomeApp, data);//status 0 ja no metodo no banco
        JSONArray jsonCategorias = jsonObjetosUnicos.optJSONArray(JsonConstantes.JSON_CATEGORIAS);
        int qtdCategoriaNoObjeto = jsonCategorias.length();

        salvaCategorias(idItemChecagem, jsonCategorias, qtdCategoriaNoObjeto);
    }

    private void salvaCategorias(long idItemChecagem, JSONArray jsonCategorias, int qtdCategoriaNoObjeto) throws JSONException {
        long idCategoria;

        for (int a = 0; a < qtdCategoriaNoObjeto; a++) {
            JSONObject objetCategoria = jsonCategorias.getJSONObject(a);
            String nomeCategoria = objetCategoria.getString(JsonConstantes.JSON_NOME);
            JSONArray jsonItensChecagem = objetCategoria.optJSONArray(JsonConstantes.JSON_ITENS);
            idCategoria = banco.insereCatetoria(nomeCategoria, idItemChecagem);
            int qtdItensNaCategoria = jsonItensChecagem.length();
            salvaItensDaCategoria(idCategoria, jsonItensChecagem, qtdItensNaCategoria);
        }
    }

    private void salvaItensDaCategoria(long idCategoria, JSONArray jsonItensChecagem, int qtdItensNaCategoria) throws JSONException {
        long idItemDaCategoria;
        for (int b = 0; b < qtdItensNaCategoria; b++) {
            JSONObject objectItemNaCategoria = jsonItensChecagem.getJSONObject(b);
            int idExternoTituloItemDaCategoria = objectItemNaCategoria.getInt(JsonConstantes.JSON_ID);
            String tituloItemDaCategoria = objectItemNaCategoria.getString(JsonConstantes.JSON_TEXTO);
            idItemDaCategoria = banco.insereItemDaCategoria(tituloItemDaCategoria, idCategoria, idExternoTituloItemDaCategoria);
            JSONArray jsonRespostasDoItemDaCategoria = objectItemNaCategoria.optJSONArray(JsonConstantes.JSON_RESPOSTAS);
            int qtdRespostasNoItem = jsonRespostasDoItemDaCategoria.length();
            salvaRespostasDoItem(idItemDaCategoria, jsonRespostasDoItemDaCategoria, qtdRespostasNoItem);
        }
    }

    private void salvaRespostasDoItem(long idItemDaCategoria, JSONArray jsonPerguntasDoItemDaCategoria, int qtdPerguntasNoItem) throws JSONException {
        long idRespostaDoItem;
        HashMap<Integer, Long> mapaIdExternoEIdInterno = new HashMap<Integer, Long>();
        for (int c = 0; c < qtdPerguntasNoItem; c++) {
            JSONObject objectPerguntaDoItem = jsonPerguntasDoItemDaCategoria.getJSONObject(c);
            int idExternoResposta = objectPerguntaDoItem.getInt(JsonConstantes.JSON_ID);
            String tipo = objectPerguntaDoItem.getString(JsonConstantes.JSON_TIPO);
            String opcional;
            opcional = objectPerguntaDoItem.optString(JsonConstantes.JSON_OPCIONAL);

            int valorOpcional = Constantes.VALOR_OPCIONAL_FALSE;

            if (opcional.equals("true")) {
                valorOpcional = Constantes.VALOR_OPCIONAL_TRUE;
            }

            String condicional;
            condicional = objectPerguntaDoItem.optString(JsonConstantes.JSON_VISIVEL_SE);

            int valorCondicional = Constantes.VALOR_CONDICIONAL_FALSE;

            if (condicional.contains(JsonConstantes.JSON_PERGUNTA)) {
                valorCondicional = Constantes.VALOR_CONDICIONAL_TRUE;
                valorOpcional = Constantes.VALOR_OPCIONAL_TRUE; //toda condicional é opcional
            }
            idRespostaDoItem = banco.insereRespostaDoItemDaCategoria(idItemDaCategoria, tipo, idExternoResposta, valorOpcional, valorCondicional);

            mapaIdExternoEIdInterno.put(idExternoResposta, idRespostaDoItem);

            if (tipo.contains(JsonConstantes.JSON_ALTERNATIVAS)) {
                salvaAlternativas(idRespostaDoItem, objectPerguntaDoItem);

            }

            if (valorCondicional == Constantes.VALOR_CONDICIONAL_TRUE) {
                salvaCondicionais(idRespostaDoItem, objectPerguntaDoItem, mapaIdExternoEIdInterno);
            }
        }
    }

    private void salvaAlternativas(long idPerguntaDoItem, JSONObject objectPerguntaDoItem) throws JSONException {
        JSONArray jsonOpcoes = objectPerguntaDoItem.optJSONArray(JsonConstantes.JSON_OPCOES);
        int qtdOpcoes = jsonOpcoes.length();

        for (int d = 0; d < qtdOpcoes; d++) {
            JSONObject objectOpcao = jsonOpcoes.getJSONObject(d);
            String valorTexto = objectOpcao.getString(JsonConstantes.JSON_TEXTO);
            String valorResposta = objectOpcao.getString(JsonConstantes.JSON_RESPOSTA);
            banco.insereOpcao(idPerguntaDoItem, valorTexto, valorResposta);
        }
    }

    private void salvaCondicionais(long idRespostaDoItem, JSONObject objectPerguntaDoItem, HashMap<Integer, Long> mapaIdExternoEIdInterno) throws JSONException {
        JSONObject objectCondicional = objectPerguntaDoItem.getJSONObject(JsonConstantes.JSON_VISIVEL_SE);

        int idExterno = objectCondicional.getInt(JsonConstantes.JSON_PERGUNTA);

        long idRespostaQueDependo = mapaIdExternoEIdInterno.get(idExterno);

        String valorResposta = objectCondicional.getString(JsonConstantes.JSON_VALOR);
        banco.insereCondicao(idRespostaDoItem, idRespostaQueDependo, valorResposta);
    }

    public LinearLayout montaLayout() {


        Intent apresentaSplash = new Intent(Controle.this.context.getApplicationContext(), SplashScreenActivity.class);
        apresentaSplash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Controle.this.context.startActivity(apresentaSplash);

        //levar abaixo para splash screen ?
        LinearLayout linearLayoutPrincipal = new LinearLayout(this.context);
        linearLayoutPrincipal.setPadding(15, 15, 15, 15);
        ListView listViewTitulos = new ListView(this.context);

        listaIdExterno = banco.obterListaIdExterno();
        listaTituloItemChecagem = banco.obterListaTituloItemChecagem();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context.getApplicationContext(), R.layout.simple_list_item_1, listaTituloItemChecagem);
        listViewTitulos.setAdapter(adapter);
        listViewTitulos.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                Intent irParaChecagem = new Intent(Controle.this.context.getApplicationContext(), Coletor.class);
                irParaChecagem.putExtra(Constantes.ID_EXTERNO, listaIdExterno.get(position));
                irParaChecagem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Controle.this.context.startActivity(irParaChecagem);
            }
        });
        linearLayoutPrincipal.addView(listViewTitulos, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        return linearLayoutPrincipal;
    }

    public JSONObject obterJsonRespostas(String nomeApp) {

        JSONObject jsonPrincipal = new JSONObject();
        JSONObject jsonItensDaCategoria = null;
        JSONObject jsonRespostas = null;
        JSONArray arrayPrincipal = new JSONArray();
        JSONArray arrayRespostas;
        JSONArray arrayItensDaCategoria;

        int idItemChecagem = banco.obterIdItemChecagem(nomeApp);
        List<Categoria> listaCategoria = banco.obterCategoriasRespondidasDoApp(idItemChecagem);

        JSONObject jsonValoresItemChecagem = new JSONObject();

        try {
            jsonValoresItemChecagem.put(JsonConstantes.JSON_APP_ID, String.valueOf(idItemChecagem));
            jsonValoresItemChecagem.put(JsonConstantes.JSON_APP_NOME, nomeApp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayPrincipal.put(jsonValoresItemChecagem);

        for (Categoria categoria : listaCategoria) {
            arrayItensDaCategoria = new JSONArray();
            jsonItensDaCategoria = new JSONObject();
            List<ItemChecagemDaCategoria> listaItensDaCategoria = banco.obterListaItemDaCategoria(categoria.getId());

            for (ItemChecagemDaCategoria itemChecagemDaCategoria : listaItensDaCategoria) {

                JSONObject jsonValoresItemChecagemDaCategoria = new JSONObject();
                try {
                    jsonValoresItemChecagemDaCategoria.put("id", itemChecagemDaCategoria.getIdExternoItemDaCategoria());
                    arrayItensDaCategoria.put(jsonValoresItemChecagemDaCategoria);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<Resposta> listaPerguntasDoItem = banco.obterListaRespostasDoItem(itemChecagemDaCategoria.getId());
                arrayRespostas = new JSONArray();

                for (Resposta resposta : listaPerguntasDoItem) {

                    if (resposta.getValorResposta() != null) {
                        jsonRespostas = new JSONObject();
                        JSONObject jsonValoresRespostas = new JSONObject();
                        try {
                            jsonValoresRespostas.put(JsonConstantes.JSON_ID, String.valueOf(resposta.getIdExterno()));
                            jsonValoresRespostas.put(JsonConstantes.JSON_RESPOSTA, resposta.getValorResposta());
                            arrayRespostas.put(jsonValoresRespostas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {

                    jsonRespostas.put(JsonConstantes.JSON_VALORES, arrayRespostas);
                    arrayItensDaCategoria.put(jsonRespostas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonItensDaCategoria.put(JsonConstantes.JSON_RESPOSTAS, arrayItensDaCategoria);
                arrayPrincipal.put(jsonItensDaCategoria);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            jsonPrincipal.put(JsonConstantes.JSON_CHECKLISTS_RESPOSTAS, arrayPrincipal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonPrincipal;
    }
}

