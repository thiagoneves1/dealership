package br.tecsinapse.checklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.tecsinapse.checklist.entidades.Categoria;
import br.tecsinapse.checklist.entidades.Condicao;
import br.tecsinapse.checklist.entidades.ItemChecagem;
import br.tecsinapse.checklist.entidades.ItemChecagemDaCategoria;
import br.tecsinapse.checklist.entidades.Opcao;
import br.tecsinapse.checklist.entidades.Resposta;
import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int VERSAO_BANCO = 1;
    private static final String NOME_BANCO = "bancoModulo";

    public DataBaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlItem = "CREATE TABLE IF NOT EXISTS itemChecagem " +
                "(id INTEGER PRIMARY KEY, " +
                "idExterno INTEGER, tituloItem TEXT, app TEXT, status INTEGER, data TEXT, progresso INTEGER)";

        String sqlCategoria = "CREATE TABLE IF NOT EXISTS categoria" +
                "(id INTEGER PRIMARY KEY, " +
                "idItem INTEGER, nomeCategoria TEXT, " +
                "FOREIGN KEY(idItem) REFERENCES itemChecagem(id))";

        String sqlItemCategoria = "CREATE TABLE IF NOT EXISTS itemDaCategoria " +
                "(id INTEGER PRIMARY KEY, idCategoria INTEGER, itemTitulo TEXT, " +
                "idExternoItemDaCategoria INTEGER, " +
                "FOREIGN KEY(idCategoria) REFERENCES categoria(id))";

        String sqlPergunta = "CREATE TABLE IF NOT EXISTS valorResposta " +
                "(id INTEGER PRIMARY KEY, idExterno INTEGER, idItemDaCategoria " +
                "INTEGER, tipo TEXT, opcional INTEGER, condicional INTEGER, " +
                "respondida INTEGER, valorResposta TEXT, " +
                "FOREIGN KEY(idItemDaCategoria) REFERENCES itemDaCategoria(id))";

        String sqlOpcao = "CREATE TABLE IF NOT EXISTS opcao " +
                "(id INTEGER PRIMARY KEY,idResposta INTEGER, " +
                "valorTexto TEXT, valorResposta TEXT,FOREIGN KEY(idResposta)  " +
                "REFERENCES valorResposta(id))";

        String sqlCondicao = "CREATE TABLE IF NOT EXISTS condicao " +
                "(id INTEGER PRIMARY KEY, idResposta INTEGER, " +
                "idRespostaEmCondicional INTEGER, valorResposta TEXT, " +
                "FOREIGN KEY(idResposta) REFERENCES valorResposta(id))";

        String sqlFotos = "CREATE TABLE IF NOT EXISTS foto " +
                "(id INTEGER PRIMARY KEY, idItemChecagem INTEGER," +
                "caminhoArquivo TEXT, FOREIGN KEY(idItemChecagem) REFERENCES itemChecagem(id))";


        db.execSQL(sqlItem);
        db.execSQL(sqlCategoria);
        db.execSQL(sqlItemCategoria);
        db.execSQL(sqlPergunta);
        db.execSQL(sqlOpcao);
        db.execSQL(sqlCondicao);
        db.execSQL(sqlFotos);

        Log.i("BANCO", "BANCO CRIADO COM SUCESSO");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public String obterCaminhoFoto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT caminhoArquivo FROM foto WHERE id =" + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        db.close();
        return null;
    }

    public List<Integer> obterListaIdFotos(int idItemChecagem) {
        List<Integer> listaIdFotos = new ArrayList<Integer>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id FROM foto WHERE idItemChecagem =" + idItemChecagem;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            listaIdFotos.add(cursor.getInt(0));
        }
        db.close();
        return listaIdFotos;

    }

    public void insereIdECaminhoFoto(int idRespostaParaFoto, String caminhoFoto) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idItemChecagem", idRespostaParaFoto);
        values.put("caminhoArquivo", caminhoFoto);
        db.insert("foto", null, values);
        db.close();

    }

    public long insereItemChecagem(int idExterno, String tituloChecagem, String app, String data) {
        Log.i("BANCO", String.valueOf(idExterno) + " - " + tituloChecagem);
        long retorno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idExterno", idExterno);
        values.put("tituloItem", tituloChecagem);
        values.put("app", app);
        values.put("status", Constantes.VALOR_INICIAL_STATUS);//sempre com status 0
        Log.i("DataBaseHelper", data);
        values.put("data", data);
        values.put("progresso", Constantes.VALOR_INICIAL_PROGRESSO_ITEM);
        retorno = db.insert("itemChecagem", null, values);
        db.close();
        return retorno;
    }


    public long insereCatetoria(String nomeCategoria, long idItemChecagem) {
        long retorno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idItem", idItemChecagem);
        values.put("nomeCategoria", nomeCategoria);
        retorno = db.insert("categoria", null, values);
        db.close();
        return retorno;
    }

    public long insereItemDaCategoria(String tituloItemDaCategoria, long idCategoria, long idExternoItemDaCategoria) {
        long retorno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCategoria", idCategoria);
        values.put("itemTitulo", tituloItemDaCategoria);
        values.put("idExternoItemDaCategoria", idExternoItemDaCategoria);
        retorno = db.insert("itemDaCategoria", null, values);
        db.close();
        return retorno;
    }

    public List<Integer> obterListaIdExterno() {
        List<Integer> valores = new ArrayList<Integer>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT idExterno FROM itemChecagem";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            valores.add(cursor.getInt(0));
        }
        db.close();
        return valores;
    }

    public List<String> obterListaTituloItemChecagem() {
        List<String> valores = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT tituloItem FROM itemChecagem";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            valores.add(cursor.getString(0));
        }
        db.close();
        return valores;
    }

    public int obetStatus(int idExterno) {
        int retorno = 2;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT status FROM itemChecagem WHERE idExterno =" + idExterno;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            retorno = cursor.getInt(0);
        }
        db.close();
        return retorno;
    }

    public ItemChecagem obterItemChecagem(int idExterno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM itemChecagem WHERE idExterno =" + idExterno;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null)
            cursor.moveToFirst();
        ItemChecagem itemChecagem = new ItemChecagem();
        itemChecagem.setId(cursor.getInt(0));
        itemChecagem.setIdExterno(cursor.getInt(1));
        itemChecagem.setTituloItem(cursor.getString(2));
        itemChecagem.setApp(cursor.getString(3));
        itemChecagem.setStatus(cursor.getInt(4));
        itemChecagem.setData(cursor.getString(5));//salvar banco
        itemChecagem.setProgresso(cursor.getInt(6));
        db.close();
        return itemChecagem;
    }

    public Condicao obterCondicao(int id) {
        Condicao condicao = new Condicao();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM condicao WHERE idResposta =" + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            condicao.setId(cursor.getInt(0));
            condicao.setIdResposta(cursor.getInt(1));
            condicao.setIdRespostaEmCondicional(cursor.getInt(2));
            condicao.setValorResposta(cursor.getString(3));
        }
        db.close();
        return condicao;
    }

    public List<Categoria> obterListaCategorias(int idItemChecagem) {
        List<Categoria> listaCategorias = new ArrayList<Categoria>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM categoria WHERE idItem =" + idItemChecagem;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Categoria categoria = new Categoria();
            categoria.setId(cursor.getInt(0));
            categoria.setIdItem(cursor.getInt(1));
            categoria.setNome(cursor.getString(2));
            listaCategorias.add(categoria);
        }
        db.close();
        return listaCategorias;
    }

    public List<ItemChecagemDaCategoria> obterListaItemDaCategoria(int idCategoria) {
        List<ItemChecagemDaCategoria> listaChecagem = new ArrayList<ItemChecagemDaCategoria>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM itemDaCategoria WHERE idCategoria =" + idCategoria;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ItemChecagemDaCategoria ic = new ItemChecagemDaCategoria();
            ic.setId(cursor.getInt(0));
            ic.setIdCategoria(cursor.getInt(1));
            ic.setTitulo(cursor.getString(2));
            ic.setIdExternoItemDaCategoria(cursor.getInt(3));
            listaChecagem.add(ic);
        }
        db.close();
        return listaChecagem;
    }

    public List<Resposta> obterListaRespostasDoItem(int id) {
        List<Resposta> listaResposta = new ArrayList<Resposta>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM valorResposta WHERE idItemDaCategoria =" + id;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Resposta resposta = new Resposta();
            resposta.setId(cursor.getInt(0));
            resposta.setIdExterno(cursor.getInt(1));
            resposta.setIdItemDaCategoria(cursor.getInt(2));
            resposta.setTipo(cursor.getString(3));
            resposta.setOpcional(cursor.getInt(4));
            resposta.setCondicional(cursor.getInt(5));
            resposta.setRespondida(cursor.getInt(6));
            resposta.setValorResposta(cursor.getString(7));
            listaResposta.add(resposta);
        }
        db.close();
        return listaResposta;
    }

    public List<ItemChecagem> obterListaItemChecagem() {
        List<ItemChecagem> listaItemChecagem = new ArrayList<ItemChecagem>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM itemChecagem";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ItemChecagem itemChecagem = new ItemChecagem();
            itemChecagem.setId(cursor.getInt(0));
            itemChecagem.setIdExterno(cursor.getInt(1));
            itemChecagem.setTituloItem(cursor.getString(2));
            itemChecagem.setApp(cursor.getString(3));
            itemChecagem.setStatus(cursor.getInt(4));
            itemChecagem.setData(cursor.getString(5));//salvar banco
            itemChecagem.setProgresso(cursor.getInt(6));
            listaItemChecagem.add(itemChecagem);
        }
        return listaItemChecagem;
    }

    public List<Opcao> obterListaOpcoesDaResposta(int id) {
        List<Opcao> listaOpcao = new ArrayList<Opcao>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM opcao WHERE idResposta =" + id;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Opcao opcao = new Opcao();
            opcao.setId(cursor.getInt(0));
            opcao.setIdResposta(cursor.getInt(1));
            opcao.setValorTexto(cursor.getString(2));
            opcao.setValorResposta(cursor.getString(3));
            listaOpcao.add(opcao);
        }
        return listaOpcao;
    }

    public long insereRespostaDoItemDaCategoria(long idItemDaCategoria, String tipo, int idExterno, int valorOpcional, int valorCondicional) {
        long retorno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idItemDaCategoria", idItemDaCategoria);
        values.put("idExterno", idExterno);
        values.put("tipo", tipo);
        values.put("opcional", valorOpcional);
        values.put("condicional", valorCondicional);
        values.put("respondida", 0);//default
        retorno = db.insert("valorResposta", null, values);
        db.close();
        return retorno;
    }

    public long insereOpcao(long idPerguntaDoItem, String texto, String resposta) {
        long retorno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idResposta", idPerguntaDoItem);
        values.put("valorTexto", texto);
        values.put("valorResposta", resposta);
        retorno = db.insert("opcao", null, values);
        db.close();
        return retorno;
    }

    public void insereCondicao(long idRespostaDoItemNaoVisivel, long idRespostaQueDependo, String valorResposta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idResposta", idRespostaDoItemNaoVisivel);
        values.put("idRespostaEmCondicional", idRespostaQueDependo);
        values.put("valorResposta", valorResposta);
        db.insert("condicao", null, values);
        db.close();

    }

    public void atualizaRespostas(Resposta resposta) {
        String filtro = "id=" + resposta.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("respondida", resposta.getRespondida());
        values.put("valorResposta", resposta.getValorResposta());
        db.update("valorResposta", values, filtro, null);
        db.close();
    }

    public void atualizaStatusDoitem(int idExterno) {
        String filtro = "idExterno=" + idExterno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", 1);
        db.update("itemChecagem", values, filtro, null);
        db.close();
    }

    public void atualizaValorProgressoDoitem(int idExterno, int valorProgresso) {
        String filtro = "idExterno=" + idExterno;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("progresso", valorProgresso);
        db.update("itemChecagem", values, filtro, null);
        db.close();
    }


    public List<Categoria> obterCategoriasRespondidasDoApp(int idItemChecagem) {
        List<Categoria> listaCategorias = new ArrayList<Categoria>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM categoria WHERE idItem =" + idItemChecagem;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Categoria categoria = new Categoria();
            categoria.setId(cursor.getInt(0));
            categoria.setIdItem(cursor.getInt(1));
            categoria.setNome(cursor.getString(2));
            listaCategorias.add(categoria);
        }
        db.close();
        return listaCategorias;
    }

    public int obterIdItemChecagem(String nomeApp) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id FROM itemChecagem WHERE app LIKE '" + nomeApp + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        db.close();
        return 0;
    }

    public String obterNomeApp(int idExterno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT app FROM itemChecagem WHERE idExterno = " + idExterno;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        db.close();
        return null;
    }


}