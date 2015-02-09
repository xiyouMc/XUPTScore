package com.mc.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * <p>
 * Title: Passport
 * </p>
 * <p>
 * Description: 带有私钥的可逆加密算法（生成类似Base64的ASCII字符串序列）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: ChinaSoft International Ltd.
 * </p>
 * 
 * @author etc
 * @version 1.0
 */
public class Passport {

	public Passport() {
		// TODO 自动生成构造函数存根
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args) {
	// // TODO 自动生成方法存根
	// Passport passport = new Passport();
	// String txt = "中文文本";
	// String key = "1a";
	// String jia_str = passport.passport_encrypt(txt, key);
	// String jie_str = passport.passport_decrypt("XIFrBJTAB5TBCujL", key);
	// System.out.println("加密函数测试：" + jia_str);
	// System.out.println("解密函数测试：" + jie_str);
	//
	// }

	/**
	 * Md5加密
	 * 
	 * @param x
	 * @return
	 * @throws Exception
	 */
	public String md5(String x) {

		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(x.getBytes("UTF8")); // 更新被文搞描述的位元组
		} catch (NoSuchAlgorithmException e) {
			// 创建一个MD5消息文搞 的时候出错
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// 更新被文搞描述的位元组 的时候出错
			e.printStackTrace();
		}
		byte s[] = m.digest(); // 最后更新使用位元组的被叙述的排列,然后完成文摘计算
		// System.out.println(s); // 输出加密后的位元组
		String result = "";
		for (int i = 0; i < s.length; i++) {
			result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00)
					.substring(6);
			// 进行十六进制转换
		}

		return result;

	}

	/**
	 * 本函数将字符串以 MIME BASE64 编码。此编码方式可以让中文字或者图片也能在网络上顺利传输。在 BASE64
	 * 编码后的字符串只包含英文字母大小写、阿拉伯数字、加号与反斜线，共 64 个基本字符，不包含其它特殊的字符， 因而才取名
	 * BASE64。编码后的字符串比原来的字符串长度再加 1/3 左右。更多的 BASE64 编码信息可以参考 RFC2045 文件之 6.8 节
	 * 
	 * @param txt
	 *            等待编码的原字串
	 * @return
	 */
	public String base64_decode(String txt) {
		BASE64Decoder base64_decode = new BASE64Decoder();

		String str = "";
		try {
			str = new String(base64_decode.decodeBuffer(txt));
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return str;
	}

	public String base64_encode(String txt) {
		BASE64Encoder base64_encode = new BASE64Encoder();
		return base64_encode.encode(txt.getBytes());
	}

	/**
	 * Passport 加密函数
	 * 
	 * @param string
	 *            等待加密的原字串
	 * @param string
	 *            私有密匙(用于解密和加密)
	 * 
	 * @return string 原字串经过私有密匙加密后的结果
	 */
	public String passport_encrypt(String txt, String key) {
		Random random = new Random();
		String rad = String.valueOf(random.nextInt(32000));
		// 使用随机数发生器产生 0~32000 的值并 MD5()
		// srand((double)microtime() * 1000000);
		String encrypt_key = md5(rad);

		// 变量初始化
		int ctr = 0;
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();

		for (int i = 0; i < txt.length(); i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encrypt_key_char.length ? 0 : ctr;
			// $tmp 字串在末尾增加两位，其第一位内容为 $encrypt_key 的第 $ctr 位，
			// 第二位内容为 $txt 的第 $i 位与 $encrypt_key 的 $ctr 位取异或。然后 $ctr = $ctr + 1
			char tmp1 = txt_char[i];
			char tmp4 = encrypt_key_char[ctr];
			char tmp2 = encrypt_key_char[ctr++];
			char tmp3 = (char) (tmp1 ^ tmp2);
			tmp += tmp4 + "" + tmp3;
		}
		// 返回结果，结果为 passport_key() 函数返回值的 base65 编码结果
		return base64_encode(passport_key(tmp, key));

	}

	/**
	 * Passport 解密函数
	 * 
	 * @param string
	 *            加密后的字串
	 * @param string
	 *            私有密匙(用于解密和加密)
	 * 
	 * @return string 字串经过私有密匙解密后的结果
	 */
	public String passport_decrypt(String txt, String key) {

		// $txt 的结果为加密后的字串经过 base64 解码，然后与私有密匙一起，
		// 经过 passport_key() 函数处理后的返回值

		txt = passport_key(base64_decode(txt), key);

		// 变量初始化
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char txt_char[] = txt.toCharArray();
		for (int i = 0; i < txt.length(); i++) {
			// $tmp 字串在末尾增加一位，其内容为 $txt 的第 $i 位，
			// 与 $txt 的第 $i + 1 位取异或。然后 $i = $i + 1
			tmp += (char) (txt_char[i] ^ txt_char[++i]);
		}

		// 返回 $tmp 的值作为结果
		return tmp;

	}

	/**
	 * Passport 密匙处理函数
	 * 
	 * @param string
	 *            待加密或待解密的字串
	 * @param string
	 *            私有密匙(用于解密和加密)
	 * 
	 * @return string 处理后的密匙
	 */
	String passport_key(String txt, String encrypt_key) {

		// 将 $encrypt_key 赋为 $encrypt_key 经 md5() 后的值
		encrypt_key = md5(encrypt_key);
		// 变量初始化
		int ctr = 0;
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();
		for (int i = 0; i < txt.length(); i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encrypt_key.length() ? 0 : ctr;
			// $tmp 字串在末尾增加一位，其内容为 $txt 的第 $i 位，
			// 与 $encrypt_key 的第 $ctr + 1 位取异或。然后 $ctr = $ctr + 1
			char c = (char) (txt_char[i] ^ encrypt_key_char[ctr++]);
			tmp = tmp + c;
		}

		// 返回 $tmp 的值作为结果
		return tmp;

	}

	/**
	 * Passport 信息(数组)编码函数
	 * 
	 * @param array
	 *            待编码的数组
	 * 
	 * @return string 数组经编码后的字串
	 */
	String passport_encode(String array[]) {

		// 数组变量初始化
		String arrayenc[] = new String[array.length];

		// 遍历数组 $array，其中 $key 为当前元素的下标，$val 为其对应的值
		for (int i = 0; i < array.length; i++) {
			String val = array[i];
			// $arrayenc 数组增加一个元素，其内容为 "$key=经过 urlencode() 后的 $val 值"
			arrayenc[i] = i + "=" + URLEncoder.encode(val);
		}

		// 返回以 "&" 连接的 $arrayenc 的值(implode)，例如 $arrayenc = array('aa', 'bb',
		// 'cc', 'dd')，
		// 则 implode('&', $arrayenc) 后的结果为 ”aa&bb&cc&dd"
		return implode("&", arrayenc);

	}

	/**
	 * 返回以 "&" 连接的 $arrayenc 的值(implode)，例如 $arrayenc = array('aa', 'bb','cc',
	 * 'dd')，则 implode('&', $arrayenc) 后的结果为 ”aa&bb&cc&dd"
	 * 
	 * @param array
	 * @return
	 */
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


	/**
	 * 加密
	 * @param string 原始字符串
	 * @param miyao  密钥
	 * @return
	 */
	public static String jiami(String string,String miyao)
	{
		// 先进行base64加密 然后进行加密
				String str1 = string;
				try {
					str1 = BASE64.encryptBASE64(string.getBytes());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new Passport().passport_encrypt(str1, miyao);
	}
}
