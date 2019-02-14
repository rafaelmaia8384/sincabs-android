package br.com.sincabs.appsincabs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class FileHash {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {

            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static String getHashSHA1(File file) {

        byte[] digest = null;

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            InputStream is = new FileInputStream(file);

            int res;

            while ((res = is.read()) != -1) {

                messageDigest.update((byte)res);
            }

            digest = messageDigest.digest();
        }
        catch (Exception e) {

            return null;
        }

        return bytesToHex(digest);
    }

    public static void copyFile(File src, File dst) throws IOException {

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();

        try {

            inChannel.transferTo(0, inChannel.size(), outChannel);

        }
        finally {

            if (inChannel != null) {

                inChannel.close();
            }

            outChannel.close();
        }
    }
}
