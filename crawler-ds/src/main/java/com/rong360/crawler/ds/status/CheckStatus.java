package com.rong360.crawler.ds.status;

import com.rong360.crawler.bean.Job;
import com.rong360.crawler.ds.page.generator.JDCookieTaskGenerator;
import com.rong360.crawler.ds.page.generator.KeyGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @ClassName: CheckStatus
 * @Description:爬虫抓取状态记录
 * @author xiongwei
 * @date 2015-4-20 上午11:34:06
 * 
 */
public class CheckStatus {
	@Autowired
	private JDCookieTaskGenerator jdCookieTaskGenerator;
	/*****抓取状态记录表(key:userId, value:UserBean) *****/
	private Map<String, UserBean> checkStatusMap = new ConcurrentHashMap<String, UserBean>();


	/***** 日志记录 *****/
	private static Logger log = Logger.getLogger(CheckStatus.class);
	class UserBean {
		/*****所属于网站*****/
		private Job job;
		
		/*****已经抓取次数*****/
		private AtomicInteger crawlerTimes;
		
		/*****总共需要抓取次数*****/
		private AtomicInteger totalCrawlerTimes;
		
		private long startTime;
		
		private UserBean(Job job, AtomicInteger crawlerTimes,
				AtomicInteger totalCrawlerTimes, long startTime) {
			super();
			this.job = job;
			this.crawlerTimes = crawlerTimes;
			this.totalCrawlerTimes = totalCrawlerTimes;
			this.startTime = startTime;
		}
		public Job getJob() {
			return job;
		}
		public void setJob(Job job) {
			this.job = job;
		}
		public AtomicInteger getCrawlerTimes() {
			return crawlerTimes;
		}
		public void setCrawlerTimes(AtomicInteger crawlerTimes) {
			this.crawlerTimes = crawlerTimes;
		}
		public AtomicInteger getTotalCrawlerTimes() {
			return totalCrawlerTimes;
		}
		public void setTotalCrawlerTimes(AtomicInteger totalCrawlerTimes) {
			this.totalCrawlerTimes = totalCrawlerTimes;
		}

		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
	}
	/**
	 * 根据网站获取该userId数据最多需要抓取几次
	 * @param job
	 * @return
	 */
	public int getTotalCrawlerTimes(Job job) {
		int totalCrawlerTimes = 1;
		switch (job) {
		case TAOBAO:
			totalCrawlerTimes = 1;
			break;
		case JD:
			totalCrawlerTimes = 5;
			break;
		default:
			break;
		}
		return totalCrawlerTimes;
	}
	
	/**
	 * 更新抓取状态
	 * @param userId
	 * @return
	 */
	public void updateCheckStatus(Job job, String userId, String merchantId) {
		/*****不存在该用户的key*****/
		String key = KeyGenerator.generateKey(job.name(), userId, merchantId);
		if (!checkStatusMap.containsKey(key)) {
			checkStatusMap.put(key, new UserBean(job, new AtomicInteger(1), new AtomicInteger(getTotalCrawlerTimes(job)), System.currentTimeMillis()));
		} else {
			UserBean uerBean = checkStatusMap.get(key);
			/*****如果当前的任务已经抓取完毕,则是前一次抓取操作所引起的,故将它设置为0*****/
			if (uerBean.getCrawlerTimes().get() == uerBean.getTotalCrawlerTimes().get()) {
				uerBean.getCrawlerTimes().set(1);
			} else {
				uerBean.getCrawlerTimes().addAndGet(1);
			}
		}
	}
	
	/**
	 * 判断抓取是否完成
	 * @param userId
	 * @return
	 */
	public boolean finished(Job job, String userId, String merchantId) {
		String key = KeyGenerator.generateKey(job.name(), userId, merchantId);

		if (!checkStatusMap.containsKey(key)) {
			return false;
		}
		log.info("key:" + key + ", count:" + checkStatusMap.get(key).getCrawlerTimes());


		if (checkStatusMap.get(key).getCrawlerTimes().get() < checkStatusMap.get(key).getTotalCrawlerTimes().get()) {
			return false;
		}
		
		if (checkStatusMap.get(key).getStartTime() - System.currentTimeMillis() > 7200 * 1000) {
			checkStatusMap.remove(key);
		}
		jdCookieTaskGenerator.clearUser(key);
		return true;
	}

    public boolean finished(String userId, String merchantId) {
        boolean tb = finished(Job.JD, userId, merchantId);
        boolean jd = finished(Job.TAOBAO, userId, merchantId);
        return tb || jd;
    }

	public boolean isProcessing(Job job, String userId, String merchantId) {
		return checkStatusMap.containsKey(KeyGenerator.generateKey(job.name(), userId, merchantId));
	}
}
