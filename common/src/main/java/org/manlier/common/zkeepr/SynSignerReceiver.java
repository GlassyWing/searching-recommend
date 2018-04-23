package org.manlier.common.zkeepr;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.function.Consumer;

public class SynSignerReceiver implements Watcher {

    private String path;

    private ActiveKeyValueStore store = new ActiveKeyValueStore();

    private Consumer<String> consumer;

    public SynSignerReceiver(String hosts, String path, Consumer<String> consumer) throws IOException, InterruptedException {
        this.path = path;
        this.consumer = consumer;
        store.connect(hosts);
    }

    public void process() throws KeeperException, InterruptedException {
        String value = store.read(path, this);
        System.out.printf("Read %s as %s\n", path, value);
        consumer.accept(value);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            try {
                this.process();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        SynSignerReceiver receiver = new SynSignerReceiver("localhost:2181", "/test", System.out::println);
        receiver.process();
        Thread.sleep(Long.MAX_VALUE);
    }
}
