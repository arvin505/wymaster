package com.miqtech.master.client.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PingYinUtil {
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		if (inputString == null) {
			return "@";
		}
		char[] input = inputString.trim().toCharArray();
		String output = "";
		if (input != null) {
			if (((int) input[0] > 97 && (int) input[0] < 122) || ((int) input[0] > 65 && (int) input[0] < 90)) {
				output = String.valueOf(input[0]).toLowerCase();
			} else {
				try {
					for (int i = 0; i < input.length; i++) {
						if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
							String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
							output += temp[0];
						} else
							output += Character.toString(input[i]);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			}

		}
		return output;
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		if (chines == null) {
			return "@";
		}
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	/**
	 * 判断是不是汉字
	 * 
	 * @param chines汉字
	 * @return 拼音
	 */
	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		if ((ch[0] >= 0x4e00) && (ch[0] <= 0x9fbb)) {
			return true;
		}
		return false;
	}

	public static String substring(String text, int length, String encode) throws UnsupportedEncodingException {
		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int currentLength = 0;
		for (char c : text.toCharArray()) {
			currentLength += String.valueOf(c).getBytes(encode).length;
			if (currentLength <= length) {
				sb.append(c);
			} else {
				break;
			}
		}
		return sb.toString();

	}
	
	/**
	 * 将list转换成string，加','
	 * @param mList
	 * @return
	 */
	public static String listToString(List<String> mList){
		if(mList!=null && mList.size()>0){
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<mList.size();i++){
				sb.append(mList.get(i));
				if(i<mList.size()-1){
					sb.append(",");
				}
			}
			return sb.toString();
		}
		return null ;
	}

	public static String toJP(String c) {

		char[] chars = c.toCharArray();

		StringBuffer sb = new StringBuffer("");

		for (int i = 0; i < chars.length; i++) {
			sb.append(getJP(chars[i]));
		}

		return sb.toString().toUpperCase();
	}

	public static String getJP(char c)
	      {
	              byte[] array = new byte[2];
	              try {
	      array = String.valueOf(c).getBytes("gbk");
	     } catch (UnsupportedEncodingException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	     }
	              if(array.length<2)return String.valueOf(c);
	              int i = (short)(array[0] - '\0' + 256) * 256 + ((short)(array[1] - '\0' + 256));
	              if ( i < 0xB0A1) return String.valueOf(c);
	              if ( i < 0xB0C5) return "a";
	              if ( i < 0xB2C1) return "b";
	              if ( i < 0xB4EE) return "c";
	              if ( i < 0xB6EA) return "d";
	              if ( i < 0xB7A2) return "e";
	              if ( i < 0xB8C1) return "f";
	              if ( i < 0xB9FE) return "g";
	              if ( i < 0xBBF7) return "h";
	              if ( i < 0xBFA6) return "j";
	              if ( i < 0xC0AC) return "k";
	              if ( i < 0xC2E8) return "l";
	              if ( i < 0xC4C3) return "m";
	              if ( i < 0xC5B6) return "n";
	              if ( i < 0xC5BE) return "o";
	              if ( i < 0xC6DA) return "p";
	              if ( i < 0xC8BB) return "q";
	              if ( i < 0xC8F6) return "r";
	              if ( i < 0xCBFA) return "s";
	              if ( i < 0xCDDA) return "t";
	              if ( i < 0xCEF4) return "w";
	              if ( i < 0xD1B9) return "x";
	              if ( i < 0xD4D1) return "y";
	              if ( i < 0xD7FA) return "z";
	              return String.valueOf(c);
	      }
}
