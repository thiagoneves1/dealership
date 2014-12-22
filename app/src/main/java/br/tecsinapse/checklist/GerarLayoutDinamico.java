package br.tecsinapse.checklist;


import android.content.Context;
import android.graphics.Color;
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

public class GerarLayoutDinamico {
    Context context;

    public GerarLayoutDinamico(Context context){
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
        textViewCategorias.setTextSize(17);
        textViewCategorias.setLayoutParams(layoutParams);
        textViewCategorias.setGravity(Gravity.CENTER);

        return textViewCategorias;
    }
    public Button gerarButton() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,10,0,0);
        Button button = new Button(context);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(Constantes.COR_CINZA_ESCURO);
        button.setTextColor(context.getResources().getColor(R.color.ghostwhite));
        button.setGravity(Gravity.CENTER);
        return button;
    }


    public LinearLayout gerarLayoutItemChegagemDaCategoria() {



        LinearLayout linearLayoutItensTextEFoto = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,3,0,0);
        linearLayoutItensTextEFoto.setLayoutParams(layoutParams);
        linearLayoutItensTextEFoto.setBackgroundColor(Color.WHITE);
        linearLayoutItensTextEFoto.setPadding(5,10,5,5);
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
        textViewItemDaCategoria.setTextSize(15);
        textViewItemDaCategoria.setTypeface(null, Typeface.BOLD);
        textViewItemDaCategoria.setGravity(Gravity.CENTER_VERTICAL);
        textViewItemDaCategoria.setTextColor(context.getResources().getColor(R.color.preto));
        return textViewItemDaCategoria;
    }

    public ImageView gerarImageView() {



        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        ImageView imageViewText = new ImageView(context);
        imageViewText.setLayoutParams(layoutParams);


        return imageViewText;
    }

    public LinearLayout gerarLayoutRadioESpinner() {

        LinearLayout layoutRadioESpinner = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,3,0,0);
        layoutRadioESpinner.setLayoutParams(layoutParams);
        layoutRadioESpinner.setOrientation(LinearLayout.HORIZONTAL);
        layoutRadioESpinner.setBackgroundColor(Color.WHITE);

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


}
