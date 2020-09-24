package dmk.undertow;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperClient implements Watcher, Runnable, ChildrenCallback {

	private ZooKeeper zk;
	private boolean dead;
	private String znode;

	public ZookeeperClient(String hostPort, String znode, String localIp)
			throws KeeperException, IOException, InterruptedException {

		zk = new ZooKeeper(hostPort, 3000, this); // server 연결 및 watcher(this) 등록.

		this.znode = znode;

		zk.create(znode + "/client", localIp.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

		/*
		 * 다른 client 의 연결상태를 모니터링 하기 위해 Watch event 를 설정한다. - 비동기함수로 콜백함수인
		 * processResult()가 실행됨.
		 */
		zk.getChildren(znode, true, this, null);
	}

	/**
	 * 서버로부터의 event 발생시 실행된다. - 새로운 client 가 추가되거나 삭제되면 NodeChildrenChanged 이벤트가
	 * 발생된다. - 이벤트 수신후 새로운 Watch event 를 설정한다. (참고로 event는 한번만 수신 받기때문에 재등록한다.)
	 */
	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		String eventType = event.getType().name();
		String eventState = event.getState().name();
		System.out.println("## process: path : " + path + ", eventType : " + eventType + ", eventState: " + eventState);
		if (event.getType() == Event.EventType.NodeChildrenChanged) {
			zk.getChildren(znode, true, this, null);
		} else if (event.getType() == Event.EventType.NodeDataChanged) {

			byte[] data = null;
			try {
				data = zk.getData(path, this, null); // client ip 정보 가져오기.
			} catch (KeeperException e) {
				// We don't need to worry about recovering now. The watch
				// callbacks will kick off any exception handling
				e.printStackTrace();
			} catch (InterruptedException e) {
				return;
			}
			System.out.println("## process: path=" + path + ", data = " + new String(data));
		}

	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				while (!dead) {
					wait();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children) {
		// TODO Auto-generated method stub
		for (String child : children) {

			byte[] data = null;
			try {
				data = zk.getData(znode + "/" + child, this, null); // client ip 정보 가져오기.
			} catch (KeeperException e) {
				// We don't need to worry about recovering now. The watch
				// callbacks will kick off any exception handling
				e.printStackTrace();
			} catch (InterruptedException e) {
				return;
			}
			System.out.println("## processResult: rc=" + rc + ", path=" + path + ", child= " + child + ", data = "
					+ new String(data));
		}
	}

	public static void main(String[] args) {
		String hostPort = "192.168.119.230:2181,192.168.119.231:2181,192.168.119.232:2181";
		// zookeeper server IP:Port
		String znode = "/zk_test";
		String localIp = "192.168.255.11";
		// //client ip

		try {
			new ZookeeperClient(hostPort, znode, localIp).run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
