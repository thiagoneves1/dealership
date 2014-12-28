package br.tecsinapse.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dealer.dealerships.R;

public class Configuracao extends ActionBarActivity {
    private static final String TAG = "Configuracao";
    private TextView textViewIdDevice;
    private TextView textViewUsuario;
    private Spinner spinnerUsuarios;
    private Button buttonOk;
    private DataBaseHelper banco;
    private boolean existeUsuario;
    private String deviceId;
    private String usuario;

    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_configuracao);

        banco = new DataBaseHelper(Configuracao.this);
        utils = new Utils(Configuracao.this);

        textViewIdDevice = (TextView) findViewById(R.id.text_view_id_device);
        textViewUsuario = (TextView) findViewById(R.id.text_view_usuario);
        spinnerUsuarios = (Spinner) findViewById(R.id.spinner_usuarios);
        buttonOk = (Button) findViewById(R.id.button_ok_usuarios);

        deviceId= utils.getUniquePsuedoID();
        textViewIdDevice.append(" " + deviceId);

        existeUsuario = banco.existeUsuario();

        if(existeUsuario){
            Log.i(TAG, "existeUsuario");
            usuario = banco.obterUsuario();
            textViewUsuario.append(" " + usuario);
            spinnerUsuarios.setVisibility(View.INVISIBLE);
            buttonOk.setVisibility(View.INVISIBLE);
        }
        else{
            //temporario
            String nomesArquivo = utils.lerArquivo("arquivo_nomes");
            String[] array = nomesArquivo.split(",");
            //
            Log.i(TAG, "nao existeUsuario");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,array);

            spinnerUsuarios = (Spinner) findViewById(R.id.spinner_usuarios);

            spinnerUsuarios.setAdapter(adapter);

            buttonOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    usuario = spinnerUsuarios.getSelectedItem().toString();
                    try {
                        banco.insereDeviceIdEUsuario(deviceId, usuario);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    textViewUsuario.append(" " + usuario);
                    Toast.makeText(Configuracao.this, Constantes.USUÁRIO_SALVO_COM_SUCESSO,Toast.LENGTH_LONG).show();
                    Configuracao.this.finish();
                }
            });
        }













    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configuracao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:

                return true;

            case R.id.action_refresh:
                Intent irParaSincronia = new Intent(Configuracao.this, Sincronizar.class);
                startActivity(irParaSincronia);
                return true;

            case android.R.id.home:
             Configuracao.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
