package br.tecsinapse.checklist;

import android.content.Context;
import android.os.Build;
import android.util.Base64;import br.com.dealer.dealerships.R;
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


public class Utilitario {
    Context context;

    public Utilitario(Context context){
        this.context = context;
    }

    public static String gerarSHA1(final File file) throws NoSuchAlgorithmException, IOException {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

        InputStream is = new BufferedInputStream(new FileInputStream(file));
        final byte[] buffer = new byte[1024];
        for (int read = 0; (read = is.read(buffer)) != -1;) {
            messageDigest.update(buffer, 0, read);
        }

        Formatter formatter = new Formatter();

        for (final byte b : messageDigest.digest()) {
            formatter.format("%02x", b);

        }
        return formatter.toString();
    }

    public String lerArquivo(){

        InputStream inputStream = this.context.getResources().openRawResource(R.raw.arquivo_json);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }
    public static String converteImagemParaString(File file) {
        String stringDadosImagem = null;
        try {

            FileInputStream arquivoImagem = new FileInputStream(file);
            byte imageData[] = new byte[(int)file.length()];
            arquivoImagem.read(imageData);

            stringDadosImagem = encodeImage(imageData);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return stringDadosImagem;
    }


    private static String encodeImage(byte[] imageByteArray){
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }



//http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
    public static String getUniquePsuedoID()
    {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a duplicate entry
        String serial = null;
        try
        {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        catch (Exception e)
        {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

}



