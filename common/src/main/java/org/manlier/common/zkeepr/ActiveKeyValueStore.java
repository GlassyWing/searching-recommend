package org.manlier.common.zkeepr;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ActiveKeyValueStore extends ConnectionWatcher {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_PERIOD_SECONDS = 2;

    public void write(String path, String value) throws InterruptedException,
            KeeperException {
        int retries = 0;
        while (true) {
            try {
                Stat stat = zk.exists(path, false);
                if (stat == null) {
                    zk.create(path, value.getBytes(CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                } else {
                    zk.setData(path, value.getBytes(CHARSET), stat.getVersion());
                }
                return;
            } catch (KeeperException.SessionExpiredException e) {
                throw e;
            } catch (KeeperException e) {
                if (retries++ == MAX_RETRIES) {
                    throw e;
                }
                // sleep then retry
                TimeUnit.SECONDS.sleep(RETRY_PERIOD_SECONDS);
            }
        }
    }

    public String read(String path, Watcher watcher) throws InterruptedException,
            KeeperException {
        byte[] data = zk.getData(path, watcher, null);
        return new String(data, CHARSET);
    }
}
