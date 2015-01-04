package br.tecsinapse.checklist;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import br.com.dealer.dealerships.R;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.UUID;


public class Utils {
    private static final String TAG = "Utils";
    Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public static String gerarSHA1(final File file) throws NoSuchAlgorithmException, IOException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

        InputStream is = new BufferedInputStream(new FileInputStream(file));
        final byte[] buffer = new byte[1024];
        for (int read = 0; (read = is.read(buffer)) != -1; ) {
            messageDigest.update(buffer, 0, read);
        }

        Formatter formatter = new Formatter();

        for (final byte b : messageDigest.digest()) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public String lerArquivo(String nomeArquivo) { //apenas teste, leitura arquivo json

        InputStream inputStream;
        if(nomeArquivo.equals("arquivo_json")) {
            inputStream = this.context.getResources().openRawResource(R.raw.arquivo_json);
        }
        else{
            inputStream = this.context.getResources().openRawResource(R.raw.arquivo_nomes);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return byteArrayOutputStream.toString();
    }

    public static String converteImagemParaString(File file) {
        String stringDadosImagem = null;
        try {

            FileInputStream arquivoImagem = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            arquivoImagem.read(imageData);

            stringDadosImagem = encodeImage(imageData);

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }
        return stringDadosImagem;
    }

    private static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    public static String getUniquePsuedoID() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            serial = "serial";
        }

        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

}



