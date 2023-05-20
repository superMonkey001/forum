package cn.edu.hncj.forum.utils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
 
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Service;
 
import lombok.extern.slf4j.Slf4j;
 
/**
 * 
 * @author fenglibin
 *
 */
@Service
@Slf4j
public class ZKCuratorUtil {
 
	// ZK客户端
	@Resource(name = "curatorFramework")
	private CuratorFramework curatorFramework;

	public ZKCuratorUtil(CuratorFramework curatorFramework) {
		this.curatorFramework = curatorFramework;
	}
 
	/**
	 * 初始化操作
	 */
	@PostConstruct
	public void init() {
		log.info("Use zookeeper as the zookeeper's name space.");
		curatorFramework.start();
		// 使用命名空间
		curatorFramework = curatorFramework.usingNamespace("zookeeper");
	}
 
	/**
	 * 获取Zookeeper客户端连接
	 * 
	 * @return
	 */
	public CuratorFramework getCuratorFramework() {
		return curatorFramework;
	}
	
	public ZooKeeper getZooKeeper() throws Exception {
		return getCuratorFramework().getZookeeperClient().getZooKeeper();
	}
 
	/**
	 * 判断ZK是否连接
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return curatorFramework != null 
				&& (curatorFramework.getState() == CuratorFrameworkState.STARTED);
	}
 
	/**
	 * 判断是否已经停止
	 * 
	 * @return
	 */
	public boolean isStoped() {
		return curatorFramework == null 
				|| (curatorFramework.getState() == CuratorFrameworkState.STOPPED);
	}
}