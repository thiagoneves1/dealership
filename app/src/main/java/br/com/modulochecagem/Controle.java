package br.com.modulochecagem;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import br.com.checklist.testes.R;
import java.util.ArrayList;
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
        JSONArray jsonPrimeiroNo = jsonCompleto.optJSONArray(Constantes.CHECKLISTS);//separa os itens de Checagem
        int tamanhoJsonArray = jsonPrimeiroNo.length();

        for (int i = 0; i < tamanhoJsonArray; i++) {

            JSONObject jsonObjetosUnicos = jsonPrimeiroNo.getJSONObject(i);
            String tituloChecagem = jsonObjetosUnicos.getString(Constantes.NOME);
            String nomeApp = jsonObjetosUnicos.getString(Constantes.APP);
            int idExterno = jsonObjetosUnicos.getInt(Constantes.ID);

            if (listaIdExterno.contains(idExterno)) {
                salvaItem = false;
            }

            if (salvaItem) {
                SalvarItem(jsonObjetosUnicos, tituloChecagem, idExterno, nomeApp);
                retorno = true;
            }
        }
        return retorno;
    }

    private void SalvarItem(JSONObject jsonObjetosUnicos, String tituloChecagem, int idExterno, String nomeApp) throws JSONException {

        long idItemChecagem;
        idItemChecagem = banco.insereItemChecagem(idExterno, tituloChecagem, nomeApp);//status 0 ja no metodo no banco
        JSONArray jsonCategorias = jsonObjetosUnicos.optJSONArray(Constantes.CATEGORIAS);
        int qtdCategoriaNoObjeto = jsonCategorias.length();

        salvaCategorias(idItemChecagem, jsonCategorias, qtdCategoriaNoObjeto);
    }

    private void salvaCategorias(long idItemChecagem, JSONArray jsonCategorias, int qtdCategoriaNoObjeto) throws JSONException {
        long idCategoria;

        for (int a = 0; a < qtdCategoriaNoObjeto; a++) {
            JSONObject objetCategoria = jsonCategorias.getJSONObject(a);
            String nomeCategoria = objetCategoria.getString(Constantes.NOME);
            JSONArray jsonItensChecagem = objetCategoria.optJSONArray(Constantes.ITENS);
            idCategoria = banco.insereCatetoria(nomeCategoria, idItemChecagem);
            int qtdItensNaCategoria = jsonItensChecagem.length();
            salvaItensDaCategoria(idCategoria, jsonItensChecagem, qtdItensNaCategoria);
        }
    }

    private void salvaItensDaCategoria(long idCategoria, JSONArray jsonItensChecagem, int qtdItensNaCategoria) throws JSONException {
        long idItemDaCategoria;
        for (int b = 0; b < qtdItensNaCategoria; b++) {
            JSONObject objectItemNaCategoria = jsonItensChecagem.getJSONObject(b);
            int idExternoTituloItemDaCategoria = objectItemNaCategoria.getInt(Constantes.ID);
            String tituloItemDaCategoria = objectItemNaCategoria.getString(Constantes.TEXTO);
            idItemDaCategoria = banco.insereItemDaCategoria(tituloItemDaCategoria, idCategoria, idExternoTituloItemDaCategoria);
            JSONArray jsonRespostasDoItemDaCategoria = objectItemNaCategoria.optJSONArray(Constantes.RESPOSTAS);
            int qtdRespostasNoItem = jsonRespostasDoItemDaCategoria.length();
            salvaRespostasDoItem(idItemDaCategoria, jsonRespostasDoItemDaCategoria, qtdRespostasNoItem);
        }
    }

    private void salvaRespostasDoItem(long idItemDaCategoria, JSONArray jsonPerguntasDoItemDaCategoria, int qtdPerguntasNoItem) throws JSONException {
        long idRespostaDoItem;
        HashMap<Integer,Long> mapaIdExternoEIdInterno = new HashMap<Integer, Long>();
        for (int c = 0; c < qtdPerguntasNoItem; c++) {
            JSONObject objectPerguntaDoItem = jsonPerguntasDoItemDaCategoria.getJSONObject(c);
            int idExternoResposta = objectPerguntaDoItem.getInt(Constantes.ID);
            String tipo = objectPerguntaDoItem.getString(Constantes.TIPO);
            String opcional;
            opcional = objectPerguntaDoItem.optString(Constantes.OPCIONAL);

            int valorOpcional = Constantes.valorOpcionalFalse;

                if (opcional.equals("true")) {
                    valorOpcional = Constantes.valorOpcionalTrue;
                }

            String condicional;
            condicional = objectPerguntaDoItem.optString(Constantes.VISIVEL_SE);

            int valorCondicional = Constantes.valorCondicionalFalse;

                if (condicional.contains(Constantes.PERGUNTA)) {
                    valorCondicional = Constantes.valorCondicionalTrue;
                    valorOpcional =Constantes.valorOpcionalTrue; //toda condicional é opcional
                }
            idRespostaDoItem = banco.insereRespostaDoItemDaCategoria(idItemDaCategoria, tipo, idExternoResposta, valorOpcional, valorCondicional);

            mapaIdExternoEIdInterno.put(idExternoResposta,idRespostaDoItem);

                if (tipo.contains(Constantes.ALTERNATIVAS)) {
                salvaAlternativas(idRespostaDoItem, objectPerguntaDoItem);

            }

                if (valorCondicional == Constantes.valorCondicionalTrue) {
                    salvaCondicionais(idRespostaDoItem, objectPerguntaDoItem, mapaIdExternoEIdInterno);
                }
        }
    }

    private void salvaAlternativas(long idPerguntaDoItem, JSONObject objectPerguntaDoItem) throws JSONException {
        JSONArray jsonOpcoes = objectPerguntaDoItem.optJSONArray(Constantes.OPCOES);
        int qtdOpcoes = jsonOpcoes.length();

            for (int d = 0; d < qtdOpcoes; d++) {
                JSONObject objectOpcao = jsonOpcoes.getJSONObject(d);
                String valorTexto = objectOpcao.getString(Constantes.TEXTO);
                String valorResposta = objectOpcao.getString(Constantes.RESPOSTA);
               banco.insereOpcao(idPerguntaDoItem, valorTexto, valorResposta);
            }
    }

    private void salvaCondicionais(long idRespostaDoItem, JSONObject objectPerguntaDoItem, HashMap<Integer, Long> mapaIdExternoEIdInterno) throws JSONException {
        JSONObject objectCondicional = objectPerguntaDoItem.getJSONObject(Constantes.VISIVEL_SE);

        int idExterno = objectCondicional.getInt(Constantes.PERGUNTA);

        long idRespostaQueDependo = mapaIdExternoEIdInterno.get(idExterno);

        String valorResposta = objectCondicional.getString(Constantes.VALOR);
        banco.insereCondicao(idRespostaDoItem, idRespostaQueDependo, valorResposta);
    }

    public LinearLayout montaLayout() {

        LinearLayout linearLayoutPrincipal = new LinearLayout(this.context);
        linearLayoutPrincipal.setPadding(15,15,15,15);
        ListView listViewTitulos = new ListView(this.context);
        listaIdExterno = banco.obterListaIdExterno();
        listaTituloItemChecagem = banco.obterListaTituloItemChecagem();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context.getApplicationContext(), R.layout.simple_list_item_1, listaTituloItemChecagem);
        listViewTitulos.setAdapter(adapter);
        listViewTitulos.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                Intent irParaChecagem = new Intent(Controle.this.context.getApplicationContext(), PreencimentoChecklist.class);
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
                jsonValoresItemChecagem.put(Constantes.APP_ID,String.valueOf(idItemChecagem));
                jsonValoresItemChecagem.put(Constantes.APP_NOME,nomeApp);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        arrayPrincipal.put(jsonValoresItemChecagem);

        for(Categoria categoria:listaCategoria){
            arrayItensDaCategoria = new JSONArray();
            jsonItensDaCategoria = new JSONObject();
            List<ItemChecagemDaCategoria> listaItensDaCategoria = banco.obterListaItemDaCategoria(categoria.getId());

            for(ItemChecagemDaCategoria itemChecagemDaCategoria:listaItensDaCategoria){

                JSONObject jsonValoresItemChecagemDaCategoria = new JSONObject();
                try {
                    jsonValoresItemChecagemDaCategoria.put("id", itemChecagemDaCategoria.getIdExternoItemDaCategoria());
                    arrayItensDaCategoria.put(jsonValoresItemChecagemDaCategoria);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                        List<Resposta> listaPerguntasDoItem = banco.obterListaRespostasDoItem(itemChecagemDaCategoria.getId());
                        arrayRespostas = new JSONArray();

                                            for(Resposta resposta : listaPerguntasDoItem){

                                                    if(resposta.getValorResposta()!=null) {
                                                        jsonRespostas = new JSONObject();
                                                        JSONObject jsonValoresRespostas = new JSONObject();
                                                           try {
                                                               jsonValoresRespostas.put(Constantes.ID, String.valueOf(resposta.getIdExterno()));
                                                               jsonValoresRespostas.put(Constantes.RESPOSTA, resposta.getValorResposta());
                                                               arrayRespostas.put(jsonValoresRespostas);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                    }
                                            }
                try {

                    jsonRespostas.put(Constantes.VALORES, arrayRespostas);
                    arrayItensDaCategoria.put(jsonRespostas);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                jsonItensDaCategoria.put(Constantes.RESPOSTAS, arrayItensDaCategoria);
                arrayPrincipal.put(jsonItensDaCategoria);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            jsonPrincipal.put(Constantes.CHECKLISTS_RESPOSTAS,arrayPrincipal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonPrincipal;
    }
}

