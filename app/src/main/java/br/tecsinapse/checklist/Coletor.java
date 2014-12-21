package br.tecsinapse.checklist;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;import br.com.dealer.dealerships.R;
import br.tecsinapse.dealer.dealerships.JsonResposta;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coletor extends ActionBarActivity {

    private DataBaseHelper banco;
    private Bundle extras;
    private ItemChecagem itemChecagem;
    private List<Categoria> listaCategoria;
    private List<ItemChecagemDaCategoria> listaItemDaCategoria;
    private List<Resposta> listaRespostasDoItem;
    private final List<Resposta> listaRespostasTotais = new ArrayList<Resposta>();
    private final Map<Integer,Integer> mapaPerguntasCondicionais = new HashMap<Integer,Integer>();
    private int idExterno;
    private int idItemChecagem;
    private Uri fileUri;
    private int idRespostaParaFoto=0;
    private String caminhoFoto="";
    private  GeraLayoutDinamico geraLayoutDinamico;
    private int quantidadeDeRespostasRequeridas = 0;
    private  int porcentagem =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        banco = new DataBaseHelper(getApplicationContext());
        idExterno = 0;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if(extras == null) {
                idExterno= 0;
            } else {
                idExterno= extras.getInt(Constantes.ID_EXTERNO);
            }
        }

        int statusItemJaVerificado = banco.obetStatus(idExterno);
        if(statusItemJaVerificado== Constantes.VALOR_ITEM_JA_VERIFICADO_TRUE){ //somente teste

            Intent irParaResposta = new Intent(Coletor.this, JsonResposta.class);
            irParaResposta.putExtra(Constantes.ID_EXTERNO, idExterno);
            startActivity(irParaResposta);
            Coletor.this.finish();
        }
        else{

            carregaListas();
            ScrollView sv = montaLayout();

            this.setContentView(sv);
            verificaQuantidadeDeRespostasRequeridas();
            verificaCondicionaisSalvas();
        }
    }

    private ScrollView montaLayout() {

        geraLayoutDinamico = new GeraLayoutDinamico(getApplicationContext());
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        LinearLayout linearLayoutBase =  geraLayoutDinamico.gerarLayoutBase();
        scrollView.addView(linearLayoutBase);

        LinearLayout linearLayoutBaseItem = geraLayoutDinamico.gerarLayoutBaseItem();

        linearLayoutBase.addView(linearLayoutBaseItem);

        montaLabelsCategorias(linearLayoutBase);

        Button buttonOkPerguntasRespondidas = geraLayoutDinamico.gerarButton(); //new Button(this);
        buttonOkPerguntasRespondidas.setText(Constantes.OK);
        linearLayoutBase.addView(buttonOkPerguntasRespondidas);

        buttonOkPerguntasRespondidas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                salvaESaiDaActivity();
                }

        });

        Button buttonFinalizarQuestionario = geraLayoutDinamico.gerarButton();
        buttonFinalizarQuestionario.setText(Constantes.FINALIZAR);
        linearLayoutBase.addView(buttonFinalizarQuestionario);

        buttonFinalizarQuestionario.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizaProgresso();
                Boolean perguntaSemResposta = false;
                for (Resposta resposta : listaRespostasTotais) {
                    if (resposta.getOpcional() == Constantes.VALOR_OPCIONAL_FALSE) {
                        if (resposta.getRespondida() == Constantes.VALOR_RESPONDIDA_FALSE) {
                            perguntaSemResposta = true;
                        }
                    }
                }

                if (perguntaSemResposta) {
                    Toast.makeText(getApplicationContext(), Constantes.RESPONDA_TODOS_ITENS, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        salvaPerguntas();
                        atualizaValorProgresso(idExterno);
                        atualizaStatuDoItem(idExterno);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), Constantes.ERRO_AO_SALVAR_ITENS, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), Constantes.ITENS_RESPONDIDOS_SALVANDO, Toast.LENGTH_SHORT).show();
                    Intent irParaListaItens = new Intent(Coletor.this, ListaItens.class);
                    startActivity(irParaListaItens);
                }
            }
        });

        return scrollView;
    }

    private void salvaPerguntas() {

        for (Resposta resposta : listaRespostasTotais) {
            banco.atualizaRespostas(resposta);
        }
    }

    private void montaLabelsCategorias( LinearLayout linearLayout) {
        for(final Categoria categoria:itemChecagem.getListaCategorias()) {

            LinearLayout linearLayoutBaseItem = geraLayoutDinamico.gerarLayoutBaseItem();
            linearLayout.addView(linearLayoutBaseItem);

            TextView textViewCategorias = geraLayoutDinamico.gerarTextViewCategorias();// new LinearLayout(this);
            textViewCategorias.setText(categoria.getNome().toUpperCase());
            linearLayoutBaseItem.addView(textViewCategorias);

            montaItensDaCategoria(linearLayoutBaseItem, categoria);
        }
    }

    private void montaItensDaCategoria( LinearLayout linearLayout, Categoria categoria) {
        int index = 0;
        for(final ItemChecagemDaCategoria itemChecagemDaCategoria :categoria.getListaItemChecagemDaCategoria())
        {
            index++;
            LinearLayout linearLayoutItensTextEFoto = geraLayoutDinamico.gerarLayoutItemChegagemDaCategoria();// new LinearLayout(this);

            TextView textViewNumeroDoItem = geraLayoutDinamico.gerarTextViewNumeroDoItem();
            textViewNumeroDoItem.setText(String.valueOf(index));
            TextView textViewItemDaCategoria = geraLayoutDinamico.gerarTextViewItemDaCategoria(); //new TextView(this);
            textViewItemDaCategoria.setText(itemChecagemDaCategoria.getTitulo());

            linearLayoutItensTextEFoto.addView(textViewNumeroDoItem);
            linearLayoutItensTextEFoto.addView(textViewItemDaCategoria);
            linearLayout.addView(linearLayoutItensTextEFoto);
            LinearLayout linearLayoutRadioESpinner = geraLayoutDinamico.gerarLayoutRadioESpinner();
            linearLayout.addView(linearLayoutRadioESpinner);
            listaRespostasDoItem = banco.obterListaRespostasDoItem(itemChecagemDaCategoria.getId());
            montaViewsDasRespostas(itemChecagemDaCategoria, linearLayoutItensTextEFoto,linearLayoutRadioESpinner,linearLayout);
        }
    }

    private void verificaCondicionaisSalvas() {

        for(Resposta resposta:listaRespostasDoItem){
          //  if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_RADIO) || (resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_LISTA))) {
                if(resposta.getRespondida()==Constantes.VALOR_RESPONDIDA_TRUE) {
                    for(Opcao opcao : resposta.getListaOpcoes()){
                        if(opcao.getValorResposta().equals(resposta.getValorResposta())){
                            verificaCondicionais(resposta.getId(), opcao.getValorTexto());
                        }
                    }
                }
          //  }
        }
    }

    private void montaViewsDasRespostas(final ItemChecagemDaCategoria itemChecagemDaCategoria, LinearLayout linearLayoutItensTextEFoto, LinearLayout linearLayoutRadioESpinner, LinearLayout linearLayout) {

        List<Opcao> listaOpcoes = null;
        for(final Resposta resposta : listaRespostasDoItem){

            if(resposta.getCondicional()==Constantes.VALOR_CONDICIONAL_TRUE){
                    resposta.setCondicao(banco.obterCondicao(resposta.getId()));
                    mapaPerguntasCondicionais.put(resposta.getCondicao().getIdRespostaEmCondicional(), resposta.getId());
                }

            listaRespostasTotais.add(resposta);

            if(resposta.getTipo().contains(Constantes.ALTERNATIVAS)){
                listaOpcoes = banco.obterListaOpcoesDaResposta(resposta.getId());
                resposta.setListaOpcoes(listaOpcoes);
            }

            if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_RADIO)) {

                final RadioGroup radioGroup = geraLayoutDinamico.gerarRadioGrupo();
                radioGroup.setId(resposta.getId());

                for(Opcao opcao:listaOpcoes){
                       RadioButton rb1 = new RadioButton(this);
                        rb1.setText(opcao.getValorTexto());
                        rb1.setTextSize(15);
                    radioGroup.addView(rb1);
                    }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup rg, int checkedId) {
                        for (int i = 0; i < rg.getChildCount(); i++) {
                            RadioButton btn = (RadioButton) rg.getChildAt(i);

                            if (btn.getId() == checkedId) {
                                String text = (String) btn.getText();//valor
                                if(radioGroup.isShown()) {
                                    marcaResposta(radioGroup.getId(),text);
                                    verificaCondicionais(radioGroup.getId(),text);
                                }
                            }
                        }
                    }
                });
                if(resposta.getCondicional()==Constantes.VALOR_CONDICIONAL_TRUE){
                    radioGroup.setVisibility(View.INVISIBLE);
                }
                linearLayoutRadioESpinner.addView(radioGroup);
                if(resposta.getRespondida()==Constantes.VALOR_RESPONDIDA_TRUE){

                    String valorTexto="";
                    for(Opcao opcao : resposta.getListaOpcoes()){
                        if(opcao.getValorResposta().equals(resposta.getValorResposta())){
                            valorTexto = opcao.getValorTexto();

                        }
                    }
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        RadioButton btn = (RadioButton) radioGroup.getChildAt(i);

                        if(btn.getText().equals(valorTexto)){

                            btn.setChecked(true);
                        }
                    }
                }
            }
            else if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_LISTA)){
                final Spinner spinner = geraLayoutDinamico.gerarSpinner();
                spinner.setId(resposta.getId());
                List<String> listaParaAdapter = new ArrayList<String>();
                for(Opcao opcao:listaOpcoes) {
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

                String text="";
                if(resposta.getRespondida()==Constantes.VALOR_RESPONDIDA_TRUE){
                    for(Opcao opcao : resposta.getListaOpcoes()){
                        if(opcao.getValorResposta().equals(resposta.getValorResposta())){
                            text=opcao.getValorTexto();
                            for(int i=0; i < adapter.getCount(); i++) {
                                if(text.trim().equals(adapter.getItem(i).toString())){
                                   spinner.setSelection(i);
                                 break;
                               }
                            }

                        }
                    }

                }
                if(resposta.getCondicional()==Constantes.VALOR_CONDICIONAL_TRUE){
                    spinner.setVisibility(View.INVISIBLE);
                }
                linearLayoutRadioESpinner.addView(spinner);

            }

            else if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_TEXTO_LIVRE)){

              final  ImageView imageViewText = geraLayoutDinamico.gerarImageView();

                imageViewText.setId(resposta.getId());
                if(resposta.getOpcional()== Constantes.VALOR_OPCIONAL_TRUE){
                    imageViewText.setBackgroundResource(R.drawable.ic_texto_opcional);
                }
                else{
                    imageViewText.setBackgroundResource(R.drawable.ic_texto_requerido);
                }

                imageViewText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String valorJaRespondido = verificaValorJaRespondido(imageViewText.getId());
                        if(valorJaRespondido==null){
                            valorJaRespondido="";
                        }

                        final Dialog dialogText = new Dialog(Coletor.this);
                        dialogText.setContentView(R.layout.dialog_text);
                        final TextView textViewTitulo = (TextView)dialogText.findViewById(R.id.text_view_titulo_dialog);
                        textViewTitulo.setText(itemChecagemDaCategoria.getTitulo());
                        final EditText input = (EditText)dialogText.findViewById(R.id.edit_text_dialog);
                        input.setText(valorJaRespondido);
                        final Button buttonOk =  (Button)dialogText.findViewById(R.id.button_ok_dialog);
                        buttonOk.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String text = input.getEditableText().toString();
                                Log.i("Button ok", text);
                                if(text.length()>1){
                                    marcaResposta(imageViewText.getId(),text);
                                }
                                else{
                                    desmarcaResposta(imageViewText.getId());
                                }
                                dialogText.cancel();
                            }
                        });
                        final Button buttonCancela = (Button)dialogText.findViewById(R.id.button_cancelar_dialog);
                        buttonCancela.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogText.cancel();
                            }
                        });
                        dialogText.show();
                    }
                });
                if(resposta.getCondicional()==Constantes.VALOR_CONDICIONAL_TRUE && resposta.getRespondida()==Constantes.VALOR_RESPONDIDA_FALSE){
                    imageViewText.setVisibility(View.INVISIBLE);
                }
                linearLayoutItensTextEFoto.addView(imageViewText);

                if(resposta.getRespondida()==Constantes.VALOR_RESPONDIDA_TRUE) {
                    imageViewText.setBackgroundResource(R.drawable.ic_texto_respondido);
                }

            }


            else if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_FOTO)){

              final  ImageView imageViewFoto = geraLayoutDinamico.gerarImageView();
                imageViewFoto.setId(resposta.getId());
                if(resposta.getOpcional()== Constantes.VALOR_OPCIONAL_TRUE){
                    imageViewFoto.setBackgroundResource(R.drawable.ic_camera_opcional);
                }
                else{
                    imageViewFoto.setBackgroundResource(R.drawable.ic_camera_requerido);
                }

                imageViewFoto.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        idRespostaParaFoto = imageViewFoto.getId();
                        String valorJaRespondido = verificaValorJaRespondido(imageViewFoto.getId());

                      if (valorJaRespondido != null) {
                         mostraFoto(valorJaRespondido,imageViewFoto.getId(), itemChecagemDaCategoria.getTitulo());
                   } else {
                          caminhoFoto = null;
                          String text = tirarFoto(idRespostaParaFoto);
                          caminhoFoto = text;
                          marcaResposta(idRespostaParaFoto, caminhoFoto);

                        }
                    }
                });

                if(resposta.getCondicional()==Constantes.VALOR_CONDICIONAL_TRUE){
                    imageViewFoto.setVisibility(View.INVISIBLE);
                }
                linearLayoutItensTextEFoto.addView(imageViewFoto);

                if(resposta.getRespondida()==Constantes.VALOR_RESPONDIDA_TRUE) {
                    imageViewFoto.setBackgroundResource(R.drawable.ic_camera_respondido);
                }
            }
        }

    }

    private void verificaCondicionais(int id, String text) {
        if(mapaPerguntasCondicionais.containsKey(id)){
            int idDependente = mapaPerguntasCondicionais.get(id);
            percorrePerguntas:for (Resposta resposta : listaRespostasTotais){
                if(idDependente== resposta.getId()){
                    if(resposta.getCondicao().getValorResposta().equals(text)){

                        View view = findViewById(idDependente);
                        view.setVisibility(View.VISIBLE);
                        break percorrePerguntas;
                    }
                    else{
                        View view = findViewById(idDependente);
                        view.setVisibility(View.INVISIBLE);
                        desmarcaResposta(idDependente);
                    }
                }
            }
        }
    }


    private void marcaResposta(int id, String text) {
        for(Resposta resposta : listaRespostasTotais){
            if(resposta.getId()==id){
                if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_LISTA)||resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_ALTERNATIVAS_RADIO)){//pega valor resposta, quando tem opções
                    for(Opcao opcao : resposta.getListaOpcoes()){
                        if(opcao.getValorTexto().equals(text)){
                            resposta.setValorResposta(opcao.getValorResposta());
                            resposta.setRespondida(1);
                            atualizaStatus(id);
                        }
                    }
                }
                else {
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

    private void mostraFoto(final String caminhoFoto, int id, String titulo) {
    final int idInterno = id;
        ImageView imageViewFoto;
        Button buttonManterFoto, buttonTrocarFoto;
        TextView textViewTituloFoto;

        final Dialog dialog = new Dialog(Coletor.this);
        dialog.setContentView(R.layout.dialog_foto);
        imageViewFoto=(ImageView)dialog.findViewById(R.id.image_view_foto);
        imageViewFoto.setImageURI(Uri.parse(caminhoFoto));

        textViewTituloFoto=(TextView)dialog.findViewById(R.id.text_view_titulo_foto);
        textViewTituloFoto.setText(titulo);

        buttonManterFoto=(Button)dialog.findViewById(R.id.button_manter_foto);
        buttonManterFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        buttonTrocarFoto=(Button)dialog.findViewById(R.id.button_trocar_foto);
        buttonTrocarFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = tirarFoto(idInterno);
                marcaResposta(idInterno, text);
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

    private void desmarcaResposta(int id){

        View view;
        for(Resposta resposta : listaRespostasTotais) {
            if (resposta.getId() == id) {
                resposta.setValorResposta(null);
                resposta.setRespondida(0);
                if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_FOTO)){
                    if(resposta.getOpcional()==Constantes.VALOR_OPCIONAL_TRUE){
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_camera_opcional);
                    }
                    else{
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_camera_requerido);
                    }

                }
                else if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_TEXTO_LIVRE)){
                    if(resposta.getOpcional()==Constantes.VALOR_OPCIONAL_TRUE){
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_texto_opcional);
                    }
                    else{
                        view = findViewById(id);
                        view.setBackgroundResource(R.drawable.ic_texto_requerido);
                    }
                }
            }
        }
    }


    private void atualizaStatus(int id) {
        View view;
        for(Resposta resposta : listaRespostasTotais) {
            if (resposta.getId() == id) {
                if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_FOTO)){
                    view = findViewById(id);
                    view.setBackgroundResource(R.drawable.ic_camera_respondido);
                }
                else if(resposta.getTipo().equals(Constantes.RESPOSTA_TIPO_TEXTO_LIVRE)){
                    view = findViewById(id);
                    view.setBackgroundResource(R.drawable.ic_texto_respondido);
                }
            }
        }
    }



    private File caminhoArquivoDaFoto(int id) {
        File diretorio =  Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!diretorio.exists()) {
            if (!diretorio.mkdirs()) {
                return null;
            }
        }
        String montaString = Constantes.FOTOGRAFIA_PERGUNTA_ID + String.valueOf(idExterno) + String.valueOf(id)  ;
        salvaCaminhoFotoBanco(idItemChecagem, montaString );
        return new File(diretorio.getPath() + File.separator
                + montaString.trim() + ".jpg");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constantes.CODIGO_IMAGEM_CAPTURA_FOTO) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(this, Constantes.IMAGEM_SALVA_COM_SUCESSO,
                            Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(this, Constantes.IMAGEM_SALVA_COM_SUCESSO_EM + data.getData(),
                            Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, Constantes.CANCELADO, Toast.LENGTH_SHORT).show();
                desmarcaResposta(idRespostaParaFoto);
            } else {
                Toast.makeText(this, Constantes.ERRO_AO_SALVAR_IMAGEM,
                        Toast.LENGTH_LONG).show();
                desmarcaResposta(idRespostaParaFoto);
            }
        }
    }

    private void carregaListas() {
        itemChecagem = banco.obterItemChecagem(idExterno);
        idItemChecagem = itemChecagem.getId();
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(itemChecagem.getTituloItem().toUpperCase());
        listaCategoria = banco.obterListaCategorias(itemChecagem.getId());
        itemChecagem.setListaCategorias(listaCategoria);
        for(Categoria categoria:listaCategoria){
           listaItemDaCategoria = banco.obterListaItemDaCategoria(categoria.getId());
            categoria.setListaItemChecagemDaCategoria(listaItemDaCategoria);
        }

    }
    private void verificaQuantidadeDeRespostasRequeridas() {
        for(Resposta resposta : listaRespostasTotais){
            if (resposta.getOpcional()== Constantes.VALOR_OPCIONAL_FALSE){
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
        if(quantidadeResposdida>0) {
            porcentagem = (100 * quantidadeResposdida) / quantidadeDeRespostasRequeridas;
        }
    }

    public void onBackPressed(){
        salvaESaiDaActivity();
    }

    private void salvaESaiDaActivity() {
        atualizaProgresso();
        try {
            salvaPerguntas();
            atualizaValorProgresso(idExterno);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), Constantes.ERRO_AO_SALVAR_ITENS, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), Constantes.ALGUNS_ITENS_RESPONDIDOS_SALVANDO, Toast.LENGTH_SHORT).show();
        Intent irParaListaItens = new Intent(Coletor.this, ListaItens.class);
        startActivity(irParaListaItens);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings:

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