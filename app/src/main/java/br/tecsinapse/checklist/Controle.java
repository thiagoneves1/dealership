package br.tecsinapse.checklist;

import android.content.Context;
import android.util.Log;
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


    private static final String TAG = "Controle";
    private Context context;
    private DataBaseHelper banco;
    private boolean salvaItem = true;
    private JSONObject jsonCompleto = null;
    private List<Integer> listaIdExterno = new ArrayList<Integer>();

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
            Log.e(TAG, e.getMessage());
            return false;
        }
        return retorno;
    }

    private boolean verificaSeItemExisteESalva(String json) throws JSONException {
        boolean salvouItem = false;
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

            SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_DATA);
            String data = sdf.format(new Date());

            if (salvaItem) {
                try {
                    salvouItem = SalvarItem(jsonObjetosUnicos, tituloChecagem, idExterno, nomeApp, data);
                }
                catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    salvouItem = false;
                }
            }
        }
        return salvouItem;
    }

    private boolean SalvarItem(JSONObject jsonObjetosUnicos, String tituloChecagem, int idExterno, String nomeApp, String data) throws JSONException {
        boolean  salvouItem;
        long idItemChecagem;
        idItemChecagem = banco.insereItemChecagem(idExterno, tituloChecagem, nomeApp, data);//status 0 ja no metodo no banco
        JSONArray jsonCategorias = jsonObjetosUnicos.optJSONArray(JsonConstantes.JSON_CATEGORIAS);
        int qtdCategoriaNoObjeto = jsonCategorias.length();
            try {
                salvouItem = salvaCategorias(idItemChecagem, jsonCategorias, qtdCategoriaNoObjeto);
                return salvouItem;
            }
            catch (Exception e){
                Log.e(TAG, e.getMessage());
                salvouItem = false;
            }
        return salvouItem;
    }

    private boolean salvaCategorias(long idItemChecagem, JSONArray jsonCategorias, int qtdCategoriaNoObjeto) throws JSONException {
        long idCategoria;
        boolean salvouCategorias = false;
        try {
            for (int a = 0; a < qtdCategoriaNoObjeto; a++) {
                JSONObject objetCategoria = jsonCategorias.getJSONObject(a);
                String nomeCategoria = objetCategoria.getString(JsonConstantes.JSON_NOME);
                JSONArray jsonItensChecagem = objetCategoria.optJSONArray(JsonConstantes.JSON_ITENS);
                idCategoria = banco.insereCatetoria(nomeCategoria, idItemChecagem);
                int qtdItensNaCategoria = jsonItensChecagem.length();
                salvouCategorias = salvaItensDaCategoria(idCategoria, jsonItensChecagem, qtdItensNaCategoria);
            }

            return salvouCategorias;
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
            salvouCategorias = false;
        }
        return salvouCategorias;
    }

    private boolean salvaItensDaCategoria(long idCategoria, JSONArray jsonItensChecagem, int qtdItensNaCategoria) throws JSONException {
        long idItemDaCategoria;
        boolean salvouItensDaCategoria = false;
        try {
            for (int b = 0; b < qtdItensNaCategoria; b++) {
                JSONObject objectItemNaCategoria = jsonItensChecagem.getJSONObject(b);
                int idExternoTituloItemDaCategoria = objectItemNaCategoria.getInt(JsonConstantes.JSON_ID);
                String tituloItemDaCategoria = objectItemNaCategoria.getString(JsonConstantes.JSON_TEXTO);
                idItemDaCategoria = banco.insereItemDaCategoria(tituloItemDaCategoria, idCategoria, idExternoTituloItemDaCategoria);
                JSONArray jsonRespostasDoItemDaCategoria = objectItemNaCategoria.optJSONArray(JsonConstantes.JSON_RESPOSTAS);
                int qtdRespostasNoItem = jsonRespostasDoItemDaCategoria.length();
                salvouItensDaCategoria =  salvaRespostasDoItem(idItemDaCategoria, jsonRespostasDoItemDaCategoria, qtdRespostasNoItem);
            }
        }
        catch (JSONException jE){
            jE.printStackTrace();
            salvouItensDaCategoria = false;
        }
        return  salvouItensDaCategoria;
    }

    private boolean salvaRespostasDoItem(long idItemDaCategoria, JSONArray jsonPerguntasDoItemDaCategoria, int qtdPerguntasNoItem) throws JSONException {
        long idRespostaDoItem;
        boolean salvouRespostaDoItem;

        HashMap<Integer, Long> mapaIdExternoEIdInterno = new HashMap<Integer, Long>();
        try {
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
            salvouRespostaDoItem = true;
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
            salvouRespostaDoItem = false;
        }
        return  salvouRespostaDoItem;
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
            Log.e(TAG, e.getMessage());
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
                    Log.e(TAG, e.getMessage());
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
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
                try {

                    jsonRespostas.put(JsonConstantes.JSON_VALORES, arrayRespostas);
                    arrayItensDaCategoria.put(jsonRespostas);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            try {
                jsonItensDaCategoria.put(JsonConstantes.JSON_RESPOSTAS, arrayItensDaCategoria);
                arrayPrincipal.put(jsonItensDaCategoria);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        try {
            jsonPrincipal.put(JsonConstantes.JSON_CHECKLISTS_RESPOSTAS, arrayPrincipal);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonPrincipal;
    }
}

