package com.central.common.utils;

import java.util.UUID;

/**
 * 短8位UUID思想其实借鉴微博短域名的生成方式，但是其重复概率过高，而且每次生成4个，需要随即选取一个。
 * 
 * 本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，所以将UUID分成8组，每4个为一组，然后通过模62操作，
 * 结果作为索引取出字符， 这样重复率大大降低。
 * 
 * 经测试，在生成一千万个数据也没有出现重复，完全满足大部分需求。
 * 
 * @author wangfan
 * @date 2017-3-28 下午1:11:31
 */
public class UUIDUtil {
	private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };

	/**
	 * 生成8位uuid
	 * @return
	 * @author wangfan
	 */
	public static String randomUUID8() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}
	
	/**
	 * 生成32位uuid
	 * @return
	 * @author wangfan
	 */
	public static String randomUUID32(){
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}

}
