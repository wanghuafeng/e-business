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
public class TaobaoRSAKeyPair {
	public static Logger log = Logger.getLogger(TaobaoRSAKeyPair.class);

	/***
	 * 加密算法
	 * @param exponent
	 * @param module
	 * @param password
	 * @return
	 */
	public  String encryptedString(String exponent, String module,
			String password) {

		/***** 1.获取JavaScript引擎管理器*****/
		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = engineManager.getEngineByName("javascript");
		String encrytedPassword = "";
		
		/***** 2.初始化JavaScript脚本*****/
		try {
			scriptEngine.eval(readRSAFromFile("taobaomobile-rsa.js"));
			if (scriptEngine instanceof Invocable) {
				Invocable invoke = (Invocable) scriptEngine; // 调用merge方法，并传入两个参数
				encrytedPassword = (String) invoke.invokeFunction("encryptedString", exponent, module, password);

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
	 * @return
	 * @throws IOException
	 */
	private  String readRSAFromFile(String fileName) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(TaobaoRSAKeyPair.class.getResourceAsStream(fileName)));
		StringBuilder sb = new StringBuilder();
		String s = "";
		while ((s = bufferedReader.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();

	}
	
	public static void main(String[] args) {
		TaobaoRSAKeyPair taobaoRSAKeyPair = new TaobaoRSAKeyPair();
		System.out.println(taobaoRSAKeyPair.encryptedString("3", "aec669d058d8b4e1486dc7741663701f8d3e447f51e1edf09c794f971e5a911d5485c4591f004d1c71eaa921c9ec3f89c5eee9d4fc99d1840318e2b92a1c9b74d696ca1bd0b1bb353d7d49f299d3883d794025cb807cf90d4f6d0c8a9db6c0f0dccc4d367e99024ba0a2e4aadba65f4a07f6ba42169a4393a31dba4c386f945b", "yjdcwf860504"));
	}
}
