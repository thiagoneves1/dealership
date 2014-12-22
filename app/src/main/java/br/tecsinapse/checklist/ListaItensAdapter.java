package br.tecsinapse.checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.com.dealer.dealerships.R;
import br.tecsinapse.checklist.entidades.ItemChecagem;
import java.util.List;


public class ListaItensAdapter extends ArrayAdapter<ItemChecagem> {

    private Context context;
    private List<ItemChecagem> listaItens = null;

    public ListaItensAdapter(Context context, List<ItemChecagem> listaItens) {
        super(context, 0, listaItens);
        this.listaItens = listaItens;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemChecagem itemChecagem = listaItens.get(position);

        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.list_row, null);

        TextView textViewNomeItem = (TextView) view.findViewById(R.id.text_view_titulo_item);
        textViewNomeItem.setText(itemChecagem.getTituloItem());

        TextView textViewData = (TextView) view.findViewById(R.id.text_view_data);
        textViewData.setText(itemChecagem.getData());

        TextView textViewProgresso = (TextView) view.findViewById(R.id.text_view_progresso);
        if (itemChecagem.getProgresso() < 50) {
            textViewProgresso.setTextColor(context.getResources().getColor(Constantes.COR_VERMELHO));
        } else if (itemChecagem.getProgresso() > 49) {
            textViewProgresso.setTextColor(context.getResources().getColor(Constantes.COR_AMARELO));
        }

        if (itemChecagem.getStatus() == Constantes.VALOR_ITEM_JA_VERIFICADO_TRUE) {
            textViewProgresso.setTextColor(context.getResources().getColor(Constantes.COR_AZUL_CLARO));
        }

        textViewProgresso.setText(String.valueOf(itemChecagem.getProgresso() + " %"));

        return view;
    }
}