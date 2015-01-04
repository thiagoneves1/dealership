package br.tecsinapse.checklist;


import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import br.com.dealer.dealerships.R;

public class LayoutDinamico {
    Context context;

    public LayoutDinamico(Context context){
        this.context = context;
    }

    public LinearLayout gerarLayoutBase(){

        LinearLayout linearLayoutBase =  new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linearLayoutBase.setLayoutParams(layoutParams);
        linearLayoutBase.setPadding(10, 0, 10, 10);
        linearLayoutBase.setBackgroundColor(context.getResources().getColor(R.color.pardo));
        linearLayoutBase.setOrientation(LinearLayout.VERTICAL);

        return  linearLayoutBase;
    }

    public LinearLayout gerarLayoutBaseItem(){

        LinearLayout linearLayoutBase =  new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,20);
        linearLayoutBase.setLayoutParams(layoutParams);
        linearLayoutBase.setPadding(3, 3, 3, 3);
        linearLayoutBase.setBackgroundColor(context.getResources().getColor(R.color.cinza));
        linearLayoutBase.setOrientation(LinearLayout.VERTICAL);
        return  linearLayoutBase;
    }

    public TextView gerarTextViewCategorias() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        TextView textViewCategorias = new TextView(context);
        textViewCategorias.setBackgroundColor(context.getResources().getColor(R.color.cinza));
        textViewCategorias.setTextColor(context.getResources().getColor(R.color.ghostwhite));
        textViewCategorias.setPadding(10, 10, 10, 10);
        textViewCategorias.setTextSize(19);
        textViewCategorias.setLayoutParams(layoutParams);
        textViewCategorias.setGravity(Gravity.CENTER);

        return textViewCategorias;
    }
    public Button gerarButton() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,10,10,0);
        layoutParams.weight=1;
        Button button = new Button(context);
        button.setTextSize(19);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(Constantes.COR_CINZA_ESCURO);
        button.setTextColor(context.getResources().getColor(R.color.ghostwhite));
        button.setGravity(Gravity.CENTER);

        return button;
    }


    public LinearLayout gerarLayoutItemChegagemDaCategoria() {

        LinearLayout linearLayoutItensTextEFoto = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(1,1,1,0);
        linearLayoutItensTextEFoto.setLayoutParams(layoutParams);
        linearLayoutItensTextEFoto.setBackgroundColor(context.getResources().getColor(R.color.ghostwhite));
        linearLayoutItensTextEFoto.setPadding(5,5,5,5);
        linearLayoutItensTextEFoto.setGravity(Gravity.CENTER_VERTICAL);
        linearLayoutItensTextEFoto.setOrientation(LinearLayout.HORIZONTAL);

        return linearLayoutItensTextEFoto;
    }

    public TextView gerarTextViewNumeroDoItem() {

        TextView textViewNumeroDoItem = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(50,LayoutParams.MATCH_PARENT);
        textViewNumeroDoItem.setLayoutParams(layoutParams);
        textViewNumeroDoItem.setGravity(Gravity.CENTER);
        textViewNumeroDoItem.setTextSize(15);
        textViewNumeroDoItem.setTextColor(context.getResources().getColor(R.color.preto));

        return  textViewNumeroDoItem;
    }

    public TextView gerarTextViewItemDaCategoria() {

        TextView textViewItemDaCategoria = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;
        textViewItemDaCategoria.setLayoutParams(layoutParams);
        textViewItemDaCategoria.setPadding(5,5,5,5);
        textViewItemDaCategoria.setTextSize(19);
        textViewItemDaCategoria.setTypeface(null, Typeface.BOLD);
        textViewItemDaCategoria.setGravity(Gravity.CENTER_VERTICAL);
        textViewItemDaCategoria.setTextColor(context.getResources().getColor(R.color.preto));

        return textViewItemDaCategoria;
    }

    public ImageView gerarImageView() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,30,0);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }

    public LinearLayout gerarLayoutRadio() {

        LinearLayout layoutRadio = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;
        layoutRadio.setLayoutParams(layoutParams);
        layoutRadio.setOrientation(LinearLayout.VERTICAL);

        return layoutRadio;
    }

    public LinearLayout gerarLayoutSpinnerIndividual() {

        float density = context.getResources().getDisplayMetrics().density;
        LinearLayout layoutSpinner = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(38*density));
        layoutSpinner.setOrientation(LinearLayout.HORIZONTAL);
        int margin = (int)(2*density);
        layoutParams.setMargins(0,margin,margin,margin);
        layoutSpinner.setLayoutParams(layoutParams);
        layoutSpinner.setBackgroundColor(context.getResources().getColor(R.color.ghostwhite));

        return  layoutSpinner;
    }

    public LinearLayout gerarLayoutSpinner() {

        LinearLayout layoutSpinner = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;
        layoutSpinner.setLayoutParams(layoutParams);
        layoutSpinner.setOrientation(LinearLayout.VERTICAL);

        return layoutSpinner;
    }
    public LinearLayout gerarLayoutRadioESpinner() {

        LinearLayout layoutRadioESpinner = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,1,0,0);
        layoutRadioESpinner.setBackgroundColor(context.getResources().getColor(R.color.cinza));
        layoutRadioESpinner.setLayoutParams(layoutParams);
        layoutRadioESpinner.setOrientation(LinearLayout.HORIZONTAL);

        return layoutRadioESpinner;
    }

    public RadioGroup gerarRadioGrupo() {


        RadioGroup radioGroup = new RadioGroup(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;
        radioGroup.setLayoutParams(layoutParams);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.setGravity(Gravity.LEFT);

        return radioGroup;
    }

    public Spinner gerarSpinner() {

        Spinner spinner = new Spinner(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;
        spinner.setLayoutParams(layoutParams);

        return spinner;
    }

    public LinearLayout gerarLayoutBotoes() {

        LinearLayout layoutBotoes = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,3,0,0);
        layoutBotoes.setPadding(5,0,5,0);
        layoutBotoes.setOrientation(LinearLayout.HORIZONTAL);
        layoutBotoes.setLayoutParams(layoutParams);

        return layoutBotoes;
    }

    public RadioGroup.LayoutParams gerarParametroParaRadio() {

        float density = context.getResources().getDisplayMetrics().density;
        RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
                (int)(38*density));
        int margin = (int)(2*density);
        params_rb.setMargins(margin, margin, 0, margin);
        return params_rb;
    }

    public LinearLayout gerarLayoutSpinnerIndividualEsquerda() {

        float density = context.getResources().getDisplayMetrics().density;
        LinearLayout layoutSpinner = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(38*density));
        layoutSpinner.setOrientation(LinearLayout.HORIZONTAL);
        int margin = (int)(2*density);
        layoutParams.setMargins(margin,margin,0,margin);
        layoutSpinner.setLayoutParams(layoutParams);
        layoutSpinner.setBackgroundColor(context.getResources().getColor(R.color.ghostwhite));

        return  layoutSpinner;
    }
}
