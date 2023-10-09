package mod.elfilibustero.sketch.lib.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sketchware.pro.R;
import mod.hey.studios.util.Helper;

public class SketchFileUtil {

    public static final String SKETCHWARE_SECURE = "sketchwaresecure";
    public static final String SKETCHWARE_DIRECTORY = ".sketchware-pro";
    public static final String SKETCHWARE_WORKSPACE_DIRECTORY = SKETCHWARE_DIRECTORY + File.separator + "workspace";

    public static String decrypt(String path) throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get(path));
        return new String(getCipher(true).doFinal(fileData));
    }

    public static String encrypt(String text) throws Exception {
        return new String(getCipher(false).doFinal(text.getBytes()));
    }

    public static boolean encrypt(String text, String path) throws Exception {
        new RandomAccessFile(path, "rw").write(getCipher(false).doFinal(text.getBytes()));
        return true;
    }

    private static Cipher getCipher(boolean isDecrypt) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = SKETCHWARE_SECURE.getBytes();
        SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec paramSpec = new IvParameterSpec(keyBytes);
        cipher.init(isDecrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, spec, paramSpec);
        return cipher;
    }
}
