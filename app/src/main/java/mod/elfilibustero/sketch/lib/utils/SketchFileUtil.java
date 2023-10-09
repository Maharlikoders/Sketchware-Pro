package mod.elfilibustero.sketch.lib.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import a.a.a.oB;

import com.sketchware.pro.R;

import mod.hey.studios.util.Helper;

public class SketchFileUtil {

    public static final String SKETCHWARE_SECURE = "sketchwaresecure";
    public static final String SKETCHWARE_DIRECTORY = ".sketchware-pro";
    public static final String SKETCHWARE_WORKSPACE_DIRECTORY = SKETCHWARE_DIRECTORY + File.separator + "workspace";

    private static final oB fileUtil = new oB();

    public static String decrypt(String path) throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get(path));
        return fileUtil.a(fileData);
    }

    public static String encrypt(String text) throws Exception {
        return new String(fileUtil.d(text));
    }

    public static boolean encrypt(String text, String path) throws Exception {
        new RandomAccessFile(path, "rw").write(getCipher(Cipher.ENCRYPT_MODE).doFinal(text.getBytes(StandardCharsets.UTF_8)));
        return true;
    }

    private static Cipher getCipher(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = SKETCHWARE_SECURE.getBytes();
        cipher.init(mode , new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(keyBytes));
        return cipher;
    }
}
