package top.codemc.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Passport {

    public Passport() {
    }

    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String jiami(String string, String miyao) {
        String str1 = string;
        try {
            str1 = BASE64.encryptBASE64(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Passport().passport_encrypt(str1, miyao);
    }

    public static String jiemi(String string, String miyao) {
        String str1 = string;
        try {
            str1 = new Passport().passport_decrypt(str1, miyao);
            return new String(BASE64.decryptBASE64(str1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str1;
    }

    public static void main(String[] args) {
        System.out
                .println(new Passport().jiemi("Aw4JHQU7D3xSH1E8VENUNgIfAwMBWw8+AAQEDg==", "mc123456"));
    }

    public String md5(String x) {

        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(x.getBytes("UTF8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (m == null) {
            return null;
        }
        byte s[] = m.digest();
        String result = "";
        for (int i = 0; i < s.length; i++) {
            result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
        }

        return result;

    }

    public String base64_decode(String txt) {
        BASE64Decoder base64_decode = new BASE64Decoder();

        String str = "";
        try {
            str = new String(base64_decode.decodeBuffer(txt));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String base64_encode(String txt) {
        BASE64Encoder base64_encode = new BASE64Encoder();
        return base64_encode.encode(txt.getBytes());
    }

    public String passport_encrypt(String txt, String key) {
        Random random = new Random();
        String rad = String.valueOf(random.nextInt(32000));
        String encrypt_key = md5(rad);

        int ctr = 0;
        String tmp = "";

        char encrypt_key_char[] = encrypt_key.toCharArray();
        char txt_char[] = txt.toCharArray();

        for (int i = 0; i < txt.length(); i++) {
            ctr = ctr == encrypt_key_char.length ? 0 : ctr;
            char tmp1 = txt_char[i];
            char tmp4 = encrypt_key_char[ctr];
            char tmp2 = encrypt_key_char[ctr++];
            char tmp3 = (char) (tmp1 ^ tmp2);
            tmp += tmp4 + "" + tmp3;
        }
        return base64_encode(passport_key(tmp, key));

    }

    public String passport_decrypt(String txt, String key) {

        txt = passport_key(base64_decode(txt), key);

        String tmp = "";

        char txt_char[] = txt.toCharArray();
        for (int i = 0; i < txt.length(); i++) {
            tmp += (char) (txt_char[i] ^ txt_char[++i]);
        }

        return tmp;

    }

    String passport_key(String txt, String encrypt_key) {

        encrypt_key = md5(encrypt_key);
        int ctr = 0;
        String tmp = "";

        char encrypt_key_char[] = encrypt_key.toCharArray();
        char txt_char[] = txt.toCharArray();
        for (int i = 0; i < txt.length(); i++) {
            ctr = ctr == encrypt_key.length() ? 0 : ctr;
            char c = (char) (txt_char[i] ^ encrypt_key_char[ctr++]);
            tmp = tmp + c;
        }

        return tmp;

    }

    String passport_encode(String array[]) {

        String arrayenc[] = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            String val = array[i];
            arrayenc[i] = i + "=" + URLEncoder.encode(val);
        }
        return implode("&", arrayenc);

    }

    public String implode(String str, String array[]) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1) {
                result += array[i] + str;
            } else {
                result += array[i];
            }
        }
        return result;
    }
}
