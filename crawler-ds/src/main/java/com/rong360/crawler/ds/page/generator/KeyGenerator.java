package com.rong360.crawler.ds.page.generator;

public class KeyGenerator {
    /**
     * @param job 任务名
     * @param uid userId
     * @param merchantId 商户id
     * @return 生成的key
     */
    public static String generateKey(String job, String uid, String merchantId) {
        return job + "_" + uid + "_" + merchantId;
    }
}
