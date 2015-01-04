package br.tecsinapse.checklist;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.entidades.Categoria;
import br.tecsinapse.checklist.entidades.ItemChecagem;
import br.tecsinapse.checklist.entidades.ItemChecagemDaCategoria;
import br.tecsinapse.checklist.entidades.Opcao;
import br.tecsinapse.checklist.entidades.Resposta;
import br.tecsinapse.dealer.dealerships.JsonResposta;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coletor extends ActionBarActivity {

    public static final String TAG = "Coletor";
    private static final int ACTIVITY_SELECT_IMAGE = 1;
    private DataBaseHelper banco;
    private Bundle extras;
    private ItemChecagem itemChecagem;
    private List<Categoria> listaCategoria;
    private List<ItemChecagemDaCategoria> listaItemDaCategoria;
    private List<Resposta> listaRespostasDoItem;
    private final List<Resposta> listaRespostasTotais = new ArrayList<Resposta>();
    private final Map<Integer, Integer> mapaPerguntasCondicionais = new HashMap<Integer, Integer>();
    private int idExterno;
    private int idItemChecagem;
    private Uri fileUri;
    private int idRespostaParaFoto = 0;
    private String caminhoFoto = "";
    private LayoutDinamico layoutDinamico;
    private int quantidadeDeRespostasRequeridas = 0;
    private int porcentagem = 0;
    private int quantidadeLayoutParaMontarNoSpinner =0;
    private List<LinearLayout> listaLinearLayoutParaInserirSpinner = new ArrayList<LinearLayout>();
    private Map<Integer,List> mapaIdPerguntaEIdViewParaSetarSpinner =  new HashMap<Integer, List>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        banco = new DataBaseHelper(getApplicationContext());
        idExterno = 0;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                idExterno = 0;
            } else {
                idExterno = extras.getInt(Constantes.ID_EXTERNO);
            }
        }

        int statusItemJaVerificado = banco.obetStatus(idExterno);
        if (statusItemJaVerificado == Constantes.VALOR_ITEM_JA_VERIFICADO_TRUE) { //somente teste

            Intent irParaResposta = new Intent(Coletor.this, JsonResposta.class);
            irParaResposta.putExtra(Constantes.ID_EXTERNO, idExterno);
            startActivity(irParaResposta);
            Coletor.this.finish();
            
        } else {

            carregaListas();
            ScrollView sv = montaLayout();
            Coletor.this.setContentView(sv);
            verificaQuantidadeDeRespostasRequeridas();
            pergundasCondicionaisSalvas();
        }
    }

    private void pergundasCondicionaisSalvas() {
        
        for (final Resposta resposta :  listaRespostasTotais) { 
            if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {
                Log.i(String.valueOf(resposta.getId()), resposta.getValorResposta());
                for (Opcao opcao : resposta.getListaOpcoes()) {

                    if (opcao.getValorResposta().equals(resposta.getValorResposta())) {
                        Log.i(String.valueOf(resposta.getId()), opcao.getValorTexto());
                        verificaCondicionais(resposta.getId(), opcao.getValorTexto());
                    }
                }
            }
        }
    }

    private ScrollView montaLayout() {

        layoutDinamico = new LayoutDinamico(getApplicationContext());
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        LinearLayout linearLayoutBase = layoutDinamico.gerarLayoutBase();
        scrollView.addView(linearLayoutBase);

        LinearLayout linearLayoutBaseItem = layoutDinamico.gerarLayoutBaseItem();
        linearLayoutBase.addView(linearLayoutBaseItem);

        montaLabelsCategorias(linearLayoutBase);

        LinearLayout linearLayoutBotoes = layoutDinamico.gerarLayoutBotoes();

        linearLayoutBase.addView(linearLayoutBotoes);
        criaBotoesOkFinaliza(linearLayoutBotoes);

        return scrollView;
    }

    private void criaBotoesOkFinaliza(LinearLayout linearLayoutBotoes) {
        
        Button buttonOkQuestionario = layoutDinamico.gerarButton();
        buttonOkQuestionario.setText(Constantes.SALVAR);
        linearLayoutBotoes.addView(buttonOkQuestionario);

        buttonOkQuestionario.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                salvaESaiDaActivity();
            }

        });

        Button buttonFinalizarQuestionario = layoutDinamico.gerarButton();
        buttonFinalizarQuestionario.setText(Constantes.FINALIZAR);
        linearLayoutBotoes.addView(buttonFinalizarQuestionario);

        buttonFinalizarQuestionario.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizaProgresso();
                Boolean perguntaNaoRespondida = false;
                perguntaNaoRespondida = exitemPerguntasNaoRespondidas();

                if (perguntaNaoRespondida) {
                    Toast.makeText(getApplicationContext(), Constantes.RESPONDA_TODOS_ITENS, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        salvaPerguntas();
                        atualizaValorProgresso(idExterno);
                        atualizaStatuDoItem(idExterno);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), Constantes.ERRO_AO_SALVAR_ITENS, Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getApplicationContext(), Constantes.ITENS_RESPONDIDOS_SALVANDO, Toast.LENGTH_LONG).show();
                    Intent irParaListaItens = new Intent(Coletor.this, ListaItens.class);
                    startActivity(irParaListaItens);
                }
            }
        });
    }

    private Boolean exitemPerguntasNaoRespondidas() {

        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_FALSE) {
                if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_FALSE) {
                    return true;
                }
            }
        }
        return false;
    }

    private void salvaPerguntas() {

        for (Resposta resposta : listaRespostasTotais) {
            banco.atualizaRespostas(resposta);
        }
    }

    private void montaLabelsCategorias(LinearLayout linearLayout) {

        for (final Categoria categoria : itemChecagem.getListaCategorias()) {

            LinearLayout linearLayoutBaseItem = layoutDinamico.gerarLayoutBaseItem();
            linearLayout.addView(linearLayoutBaseItem);

            TextView textViewCategorias = layoutDinamico.gerarTextViewCategorias();// new LinearLayout(this);
            textViewCategorias.setText(categoria.getNome().toUpperCase());
            linearLayoutBaseItem.addView(textViewCategorias);

            montaItensDaCategoria(linearLayoutBaseItem, categoria);
        }
    }

    private void montaItensDaCategoria(LinearLayout linearLayout, Categoria categoria) {

        int index = 0;

        for (final ItemChecagemDaCategoria itemChecagemDaCategoria : categoria.getListaItemChecagemDaCategoria()) {
            index++;
            LinearLayout linearLayoutItensTextEFoto = layoutDinamico.gerarLayoutItemChegagemDaCategoria();// new LinearLayout(this);

            TextView textViewNumeroDoItem = layoutDinamico.gerarTextViewNumeroDoItem();
            textViewNumeroDoItem.setText(String.valueOf(index));
            TextView textViewItemDaCategoria = layoutDinamico.gerarTextViewItemDaCategoria(); //new TextView(this);
            textViewItemDaCategoria.setText(itemChecagemDaCategoria.getTitulo());

            linearLayoutItensTextEFoto.addView(textViewNumeroDoItem);
            linearLayoutItensTextEFoto.addView(textViewItemDaCategoria);
            linearLayout.addView(linearLayoutItensTextEFoto);

            LinearLayout linearLayoutRadioESpinner = layoutDinamico.gerarLayoutRadioESpinner();
            LinearLayout linearLayoutRadio = layoutDinamico.gerarLayoutRadio();

            linearLayoutRadioESpinner.addView(linearLayoutRadio);
            LinearLayout linearLayoutSpinner = layoutDinamico.gerarLayoutSpinner();

            linearLayoutRadioESpinner.addView(linearLayoutSpinner);
            linearLayout.addView(linearLayoutRadioESpinner);
            listaRespostasDoItem = banco.obterListaRespostasDoItem(itemChecagemDaCategoria.getId());
            montaViewsDasRespostas(itemChecagemDaCategoria, linearLayoutItensTextEFoto, linearLayoutRadio, linearLayoutSpinner, linearLayout);
        }
    }




    private void montaViewsDasRespostas(final ItemChecagemDaCategoria itemChecagemDaCategoria, LinearLayout linearLayoutItensTextEFoto, LinearLayout linearLayoutRadio,LinearLayout linearLayoutSpinner, LinearLayout linearLayout) {

        for (final Resposta resposta :  listaRespostasDoItem) {
                if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {
                    Log.i(String.valueOf(resposta.getId()),resposta.getValorResposta());
                    for (Opcao opcao : resposta.getListaOpcoes()) {

                        if (opcao.getValorResposta().equals(resposta.getValorResposta())) {
                            Log.i(String.valueOf(resposta.getId()),opcao.getValorTexto());
                            verificaCondicionais(resposta.getId(), opcao.getValorTexto());
                        }
                    }
                }

            if (resposta.getCondicional() == Constantes.VALOR_CONDICIONAL_TRUE) {
                resposta.setCondicao(banco.obterCondicao(resposta.getId()));
                mapaPerguntasCondicionais.put(resposta.getCondicao().getIdRespostaEmCondicional(), resposta.getId());
            }

            listaRespostasTotais.add(resposta);

            List<Opcao> listaOpcoes = null;

            if (resposta.getTipo().contains(Constantes.ALTERNATIVAS)) {
                listaOpcoes = banco.obterListaOpcoesDaResposta(resposta.getId());
                resposta.setListaOpcoes(listaOpcoes);
            }


        if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_RADIO)) {

            montaRadioBox(linearLayoutRadio, listaOpcoes, resposta);

            montaLayoutParaSpinner(linearLayoutSpinner, quantidadeLayoutParaMontarNoSpinner, resposta.getId());

        }
        else if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_LISTA)) {

            montaSpinner(linearLayoutSpinner, linearLayoutRadio, listaOpcoes, resposta);

            }
        else if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_TEXTO_LIVRE)) {

            montaEditText(itemChecagemDaCategoria, linearLayoutItensTextEFoto, resposta);

            }
        else if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_FOTO)) {

            montaViewFoto(itemChecagemDaCategoria, linearLayoutItensTextEFoto, resposta);
            }
        }
    }

    private void montaLayoutParaSpinner(LinearLayout linearLayoutSpinner, int quantidadeLayoutParaMontarNoSpinner, int idResposta) {
        mapaIdPerguntaEIdViewParaSetarSpinner.clear();
        listaLinearLayoutParaInserirSpinner.clear();

        for(int i=0; i < quantidadeLayoutParaMontarNoSpinner; i++){
          final  LinearLayout layoutSpinnerIndiviual = layoutDinamico.gerarLayoutSpinnerIndividual();

            int idViewIndividualParaSpinner = Integer.valueOf(String.valueOf(idResposta) + String.valueOf(Constantes.DIFERENCA_ID_LAYOUT_SPINNER) + String.valueOf(i));
                layoutSpinnerIndiviual.setId(idViewIndividualParaSpinner);

            listaLinearLayoutParaInserirSpinner.add(layoutSpinnerIndiviual);
            linearLayoutSpinner.addView(layoutSpinnerIndiviual);

        }
        mapaIdPerguntaEIdViewParaSetarSpinner.put(idResposta, listaLinearLayoutParaInserirSpinner);

    }

    private void montaViewFoto(final ItemChecagemDaCategoria itemChecagemDaCategoria, LinearLayout linearLayoutItensTextEFoto, Resposta resposta) {

        final ImageView imageViewFoto = layoutDinamico.gerarImageView();

        imageViewFoto.setId(resposta.getId());
        if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_TRUE) {
            imageViewFoto.setBackgroundResource(R.drawable.ic_camera_opcional);
        } else {
            imageViewFoto.setBackgroundResource(R.drawable.ic_camera_requerido);
        }

        imageViewFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                idRespostaParaFoto = imageViewFoto.getId();
                String valorJaRespondido = verificaValorJaRespondido(imageViewFoto.getId());

                if (valorJaRespondido != null) {
                    mostraFoto(valorJaRespondido, imageViewFoto.getId(), itemChecagemDaCategoria.getTitulo());
                } else {
                    dialogEhParaFotograr(itemChecagemDaCategoria.getTitulo());
                }
            }
        });

        if (resposta.getCondicional() == Constantes.VALOR_CONDICIONAL_TRUE) {
            imageViewFoto.setVisibility(View.INVISIBLE);
        }
        linearLayoutItensTextEFoto.addView(imageViewFoto);

        if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {
            imageViewFoto.setBackgroundResource(R.drawable.ic_camera_respondido);
        }
    }

    private void montaEditText(final ItemChecagemDaCategoria itemChecagemDaCategoria, LinearLayout linearLayoutItensTextEFoto, Resposta resposta) {
        final ImageView imageViewText = layoutDinamico.gerarImageView();

        imageViewText.setId(resposta.getId());
        if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_TRUE) {
            imageViewText.setBackgroundResource(R.drawable.ic_texto_opcional);
        } else {
            imageViewText.setBackgroundResource(R.drawable.ic_texto_requerido);
        }

        imageViewText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String valorJaRespondido = verificaValorJaRespondido(imageViewText.getId());
                if (valorJaRespondido == null) {
                    valorJaRespondido = "";
                }

                final Dialog dialogText = new Dialog(Coletor.this);
                dialogText.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogText.setContentView(R.layout.dialog_text);
                final TextView textViewTitulo = (TextView) dialogText.findViewById(R.id.text_view_titulo_dialog);
                textViewTitulo.setText(itemChecagemDaCategoria.getTitulo());
                final EditText input = (EditText) dialogText.findViewById(R.id.edit_text_dialog);
                input.setText(valorJaRespondido);
                final Button buttonOk = (Button) dialogText.findViewById(R.id.button_ok_dialog);
                buttonOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = input.getEditableText().toString();
                        if (text.length() > 1) {
                            marcaResposta(imageViewText.getId(), text);
                        } else {
                            desmarcaResposta(imageViewText.getId());
                        }
                        dialogText.cancel();
                    }
                });
                final Button buttonCancela = (Button) dialogText.findViewById(R.id.button_cancelar_dialog);
                buttonCancela.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogText.cancel();
                    }
                });
                dialogText.show();
            }
        });
        if (resposta.getCondicional() == Constantes.VALOR_CONDICIONAL_TRUE && resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_FALSE) {
            imageViewText.setVisibility(View.INVISIBLE);
        }
        linearLayoutItensTextEFoto.addView(imageViewText);

        if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {
            imageViewText.setBackgroundResource(R.drawable.ic_texto_respondido);
        }
    }

    private void montaSpinner(LinearLayout linearLayoutSpinner, LinearLayout linearLayoutRadio, List<Opcao> listaOpcoes, Resposta resposta) {
        final Spinner spinner = layoutDinamico.gerarSpinner();
        spinner.setId(resposta.getId());
        List<String> listaParaAdapter = new ArrayList<String>();

            for (Opcao opcao : listaOpcoes) {
                listaParaAdapter.add(opcao.getValorTexto());
            }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, listaParaAdapter);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = spinner.getSelectedItem().toString(); //valor
                if (spinner.isShown()) {
                    marcaResposta(spinner.getId(), text);
                    verificaCondicionais(spinner.getId(), text);
                } else {
                    desmarcaResposta(spinner.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        String text = "";
        if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {
            for (Opcao opcao : resposta.getListaOpcoes()) {
                if (opcao.getValorResposta().equals(resposta.getValorResposta())) {
                    text = opcao.getValorTexto();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (text.trim().equals(adapter.getItem(i).toString())) {
                            spinner.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }

        if (resposta.getCondicional() == Constantes.VALOR_CONDICIONAL_TRUE) {

            if(listaLinearLayoutParaInserirSpinner.size()>0) {//existe um RadioGroup Criado
                int idRespostaQueEstaCondicionada = resposta.getCondicao().getIdRespostaEmCondicional();
                List<LinearLayout> listaLinearLayouts = mapaIdPerguntaEIdViewParaSetarSpinner.get(idRespostaQueEstaCondicionada);
                int indiceDaRespostaCondicionada = verificaIndiceCondicionais(resposta.getCondicao().getValorResposta(), idRespostaQueEstaCondicionada);
                listaLinearLayouts.get(indiceDaRespostaCondicionada).addView(spinner);//preciso ver o indice que esta condicionado
            }
            spinner.setVisibility(View.INVISIBLE);
            listaLinearLayoutParaInserirSpinner.clear();
        }
        else{ //preciso ver se nao tem layout montado ?
            if(listaLinearLayoutParaInserirSpinner.size()>0) {
                listaLinearLayoutParaInserirSpinner.get(0).addView(spinner);//no comeco (se for 3) da ultima lista montada
                listaLinearLayoutParaInserirSpinner.clear();
            }
            else{
                LinearLayout layoutSpinnerIndiviualEsquerda = layoutDinamico.gerarLayoutSpinnerIndividualEsquerda();
                linearLayoutRadio.addView(layoutSpinnerIndiviualEsquerda);

                LinearLayout layoutSpinnerIndiviualDireita = layoutDinamico.gerarLayoutSpinnerIndividual();
                layoutSpinnerIndiviualDireita.addView(spinner);
                linearLayoutSpinner.addView(layoutSpinnerIndiviualDireita);
            }
        }
    }

    private void montaRadioBox(LinearLayout linearLayoutRadio, List<Opcao> listaOpcoes, Resposta resposta) {

        final RadioGroup radioGroup = layoutDinamico.gerarRadioGrupo();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        radioGroup.setLayoutParams(lp);
        radioGroup.setId(resposta.getId());

        if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {

        }
        quantidadeLayoutParaMontarNoSpinner = 0;

        for (Opcao opcao : listaOpcoes) {
            quantidadeLayoutParaMontarNoSpinner++;
          final  RadioButton rb1 = new RadioButton(this);

            RadioGroup.LayoutParams params_rb = layoutDinamico.gerarParametroParaRadio();

            rb1.setText(opcao.getValorTexto());
            rb1.setTextSize(19);
            rb1.setBackgroundColor(getResources().getColor(R.color.ghostwhite));
            rb1.setId(Integer.parseInt(Integer.toString(resposta.getId()) + Constantes.DIFERENCA_ID_OPCOES_RADIO + Integer.toString(opcao.getId())));
            if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE) {
                if (opcao.getValorResposta().equals(resposta.getValorResposta())) {
                    rb1.setChecked(true);
                }
            }
            radioGroup.addView(rb1, params_rb);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {

                for (int i = 0; i < rg.getChildCount(); i++) {
                   RadioButton rbn = (RadioButton) rg.getChildAt(i);
                     Log.i(TAG, String.valueOf(checkedId) );
                    if (rbn.getId() == checkedId) {
                        String text = (String) rbn.getText();//valor
                            marcaResposta(radioGroup.getId(), text);
                            verificaCondicionais(radioGroup.getId(), text);
                    }
                }
            }
        });

        if (resposta.getCondicional() == Constantes.VALOR_CONDICIONAL_TRUE) {
            radioGroup.setVisibility(View.INVISIBLE);
        }
        linearLayoutRadio.addView(radioGroup);
    }

private  int verificaIndiceCondicionais( String text, int idRespostaQueEstaCondicionada) {
        int indice =0;

            percorrePerguntas:
            for (Resposta resposta : listaRespostasTotais) {
                if (idRespostaQueEstaCondicionada == resposta.getId()) {
                    for(Opcao opcao : resposta.getListaOpcoes()){
                        if(opcao.getValorTexto().equals(text)){
                            return indice;
                        }
                        indice++;
                    }
                }
            }

        return indice;
    }
    private void verificaCondicionais(int id, String text) {


        if (mapaPerguntasCondicionais.containsKey(id)) {
            int idDependente = mapaPerguntasCondicionais.get(id);
            percorrePerguntas:
            for (Resposta resposta : listaRespostasTotais) {
                if (idDependente == resposta.getId()) {
                    if (resposta.getCondicao().getValorResposta().equals(text)) {
                        View view = findViewById(idDependente);
                        view.setVisibility(View.VISIBLE);
                        break percorrePerguntas;
                    } else {
                        View view = findViewById(idDependente);
                        view.setVisibility(View.INVISIBLE);
                        desmarcaResposta(idDependente);
                    }
                }

            }
        }
    }

    private void marcaResposta(int id, String text) {
        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getId() == id) {
                if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_LISTA) || resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_RADIO)) {//pega valor resposta, quando tem opções
                    for (Opcao opcao : resposta.getListaOpcoes()) {
                        if (opcao.getValorTexto().equals(text)) {
                            resposta.setValorResposta(opcao.getValorResposta());
                            resposta.setRespondida(1);
                            atualizaStatus(id);
                        }
                    }
                } else {
                    resposta.setValorResposta(text);
                    resposta.setRespondida(1);
                    atualizaStatus(id);
                }
            }
        }
    }

    private void salvaCaminhoFotoBanco(int idItemChecagem, String caminhoFoto) {
        banco.insereIdECaminhoFoto(idItemChecagem, caminhoFoto);
    }

    private String tirarFoto(int id) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(caminhoArquivoDaFoto(id));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Constantes.CODIGO_IMAGEM_CAPTURA_FOTO);
        return String.valueOf(fileUri);
    }

    private void dialogEhParaFotograr(String titulo) {

        Button buttonSelecionar, buttonFotografar;
        TextView textViewTituloFoto;

        final Dialog dialog = new Dialog(Coletor.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_selecionar_galeria_foto);

        textViewTituloFoto = (TextView) dialog.findViewById(R.id.text_view_titulo_dialog_galeria_foto);
        textViewTituloFoto.setText(titulo);

        buttonSelecionar = (Button)dialog.findViewById(R.id.button_selecionar_dialog_galeria_foto);
        buttonSelecionar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            selecionarFotoDaGaleria();
                dialog.cancel();
            }
        });

        buttonFotografar = (Button)dialog.findViewById(R.id.button_fotografar_dialog_galeria_foto);
        buttonFotografar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                caminhoFoto = null;
                String text = tirarFoto(idRespostaParaFoto);
                caminhoFoto = text;
                marcaResposta(idRespostaParaFoto, caminhoFoto);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void selecionarFotoDaGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);

    }

    private void mostraFoto(final String caminhoFoto, int id, final String titulo) {

        ImageView imageViewFoto;
        Button buttonManterFoto, buttonTrocarFoto;
        TextView textViewTituloFoto;

        final Dialog dialog = new Dialog(Coletor.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_foto);
        imageViewFoto = (ImageView) dialog.findViewById(R.id.image_view_foto);
        imageViewFoto.setImageURI(Uri.parse(caminhoFoto));

        textViewTituloFoto = (TextView) dialog.findViewById(R.id.text_view_titulo_foto);
        textViewTituloFoto.setText(titulo);

        buttonManterFoto = (Button) dialog.findViewById(R.id.button_manter_foto);
        buttonManterFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        buttonTrocarFoto = (Button) dialog.findViewById(R.id.button_trocar_foto);
        buttonTrocarFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogEhParaFotograr(titulo);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private String verificaValorJaRespondido(int id) {

        String retorno = null;
        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getId() == id) {
                if (resposta.getRespondida() == 1) {
                    retorno = resposta.getValorResposta();
                }
            }
        }
        return retorno;
    }

    private void atualizaStatuDoItem(int idExterno) {
        banco.atualizaStatusDoitem(idExterno);
    }

    private void atualizaValorProgresso(int idExterno) {
        banco.atualizaValorProgressoDoitem(idExterno, porcentagem);
    }

    private void desmarcaResposta(int id) {

        View view;
        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getId() == id) {
                resposta.setValorResposta(null);
                resposta.setRespondida(0);
                if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_FOTO)) {
                    if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_TRUE) {
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_camera_opcional);
                    } else {
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_camera_requerido);
                    }

                } else if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_TEXTO_LIVRE)) {
                    if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_TRUE) {
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_texto_opcional);
                    } else {
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_texto_requerido);
                    }
                }
            }
        }
    }

    private void atualizaStatus(int id) {
        View view;
        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getId() == id) {
                if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_FOTO)) {
                    view = findViewById(id);
                    view.setBackgroundResource(R.drawable.ic_camera_respondido);
                } else if (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_TEXTO_LIVRE)) {
                    view = findViewById(id);
                    view.setBackgroundResource(R.drawable.ic_texto_respondido);
                }
            }
        }
    }

    private File caminhoArquivoDaFoto(int id) {

        File diretorio = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!diretorio.exists()) {
            if (!diretorio.mkdirs()) {
                return null;
            }
        }
        String montaString = Constantes.FOTOGRAFIA_PERGUNTA_ID + String.valueOf(idExterno) + String.valueOf(id);
        salvaCaminhoFotoBanco(idItemChecagem, montaString);
        return new File(diretorio.getPath() + File.separator
                + montaString.trim() + ".jpg");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case Constantes.CODIGO_IMAGEM_CAPTURA_FOTO:

            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(getApplicationContext(), Constantes.IMAGEM_SALVA_COM_SUCESSO,
                            Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(getApplicationContext(), Constantes.IMAGEM_SALVA_COM_SUCESSO_EM + data.getData(),
                            Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), Constantes.CANCELADO, Toast.LENGTH_SHORT).show();
                desmarcaResposta(idRespostaParaFoto);
            } else {
                Toast.makeText(getApplicationContext(), Constantes.ERRO_AO_SALVAR_IMAGEM,
                        Toast.LENGTH_LONG).show();
                desmarcaResposta(idRespostaParaFoto);
            }
             break;
            case ACTIVITY_SELECT_IMAGE:

                if(resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    marcaResposta(idRespostaParaFoto, filePath);
                }
                else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), Constantes.CANCELADO, Toast.LENGTH_SHORT).show();
                    desmarcaResposta(idRespostaParaFoto);
                } else {
                    Toast.makeText(getApplicationContext(), Constantes.ERRO_AO_SALVAR_IMAGEM,
                            Toast.LENGTH_LONG).show();
                    desmarcaResposta(idRespostaParaFoto);
                }
                break;
        }

    }

    private void carregaListas() {

        itemChecagem = banco.obterItemChecagem(idExterno);
        idItemChecagem = itemChecagem.getId();
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(itemChecagem.getTituloItem().toUpperCase());
        listaCategoria = banco.obterListaCategorias(itemChecagem.getId());
        itemChecagem.setListaCategorias(listaCategoria);
        for (Categoria categoria : listaCategoria) {
            listaItemDaCategoria = banco.obterListaItemDaCategoria(categoria.getId());
            categoria.setListaItemChecagemDaCategoria(listaItemDaCategoria);
        }
    }

    private void verificaQuantidadeDeRespostasRequeridas() {

        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_FALSE) {
                quantidadeDeRespostasRequeridas++;
            }
        }
    }

    private void atualizaProgresso() {

        int quantidadeResposdida = 0;
        for (Resposta resposta : listaRespostasTotais) {
            if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_TRUE && resposta.getOpcional() == Constantes.VALOR_OPCIONAL_FALSE) {
                quantidadeResposdida++;
            }
        }
        if (quantidadeResposdida > 0) {
            porcentagem = (100 * quantidadeResposdida) / quantidadeDeRespostasRequeridas;
        }
    }

    public void onBackPressed() {
        salvaESaiDaActivity();
    }

    private void salvaESaiDaActivity() {

        atualizaProgresso();
        try {
            salvaPerguntas();
            atualizaValorProgresso(idExterno);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), Constantes.ERRO_AO_SALVAR_ITENS, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), Constantes.ALGUNS_ITENS_RESPONDIDOS_SALVANDO, Toast.LENGTH_SHORT).show();
        Intent irParaListaItens = new Intent(Coletor.this, ListaItens.class);
        startActivity(irParaListaItens);
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
                Intent irParaConfiguracao = new Intent(Coletor.this, Configuracao.class);
                startActivity(irParaConfiguracao);
                return true;

            case R.id.action_refresh:
                Intent irParaSincronia = new Intent(Coletor.this, Sincronizar.class);
                startActivity(irParaSincronia);
                return true;

            case android.R.id.home:
                salvaESaiDaActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}