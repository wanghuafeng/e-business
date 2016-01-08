package com.rong360.crawler.ds.encrypt;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * 
 * @ClassName: TaobaoRSAKeyPair
 * @Description:淘宝密码RSA加密算法
 * @author xiongwei
 * @date 2015-4-28 上午11:34:06
 * 
 */
public class TaobaoRSAPC {

	public static Logger log = Logger.getLogger(TaobaoRSAPC.class);
	/***
	 * 加密算法
	 * @param exponent
	 * @param pbk
	 * @param password
	 * @return
	 */
	public  String encryptedString(String exponent, String pbk,
			String password) {

		/***** 1.获取JavaScript引擎管理器*****/
		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = engineManager.getEngineByName("javascript");
		String encrytedPassword = "";
		
		/***** 2.初始化JavaScript脚本*****/
		try {
			scriptEngine.eval(readRSAFromFile("taobaopc-rsa.js"));
			if (scriptEngine instanceof Invocable) {
				Invocable invoke = (Invocable) scriptEngine; // 调用merge方法，并传入两个参数
				encrytedPassword = (String) invoke.invokeFunction("encryptedString", exponent, pbk, password);

				//System.out.println("encrytedPassword = " + encrytedPassword);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			e.printStackTrace();
		} 
		return encrytedPassword;
	}

	/***
	 * 从文件中读取内容
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private  String readRSAFromFile(String fileName) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(TaobaoRSAPC.class.getResourceAsStream(fileName)));
		StringBuilder sb = new StringBuilder();
		String s = "";
		while ((s = bufferedReader.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();

	}
	
	public static void main(String[] args) {
		TaobaoRSAPC taobaoRSAKeyPair = new TaobaoRSAPC();
		System.out.println(taobaoRSAKeyPair.encryptedString("10001", "9a39c3fefeadf3d194850ef3a1d707dfa7bec0609a60bfcc7fe4ce2c615908b9599c8911e800aff684f804413324dc6d9f982f437e95ad60327d221a00a2575324263477e4f6a15e3b56a315e0434266e092b2dd5a496d109cb15875256c73a2f0237c5332de28388693c643c8764f137e28e8220437f05b7659f58c4df94685", "15801864527"));
	}
}
