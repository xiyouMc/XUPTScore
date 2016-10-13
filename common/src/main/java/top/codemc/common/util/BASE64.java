package top.codemc.common.util;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * BASE64���ܽ���
 */
public class BASE64 {

    /**
     * BASE64����
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64����
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    public static void main(String[] args) throws Exception {
        String data = BASE64.encryptBASE64("http://aub.iteye.com/".getBytes());
        System.out.println("����ǰ��" + data);

        byte[] byteArray = BASE64.decryptBASE64(data);
        System.out.println("���ܺ�" + new String(byteArray));
    }
}
