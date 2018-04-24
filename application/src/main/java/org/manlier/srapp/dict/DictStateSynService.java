package org.manlier.srapp.dict;

import org.apache.zookeeper.KeeperException;
import org.manlier.srapp.component.ComponentService;
import org.manlier.srapp.constraints.SynSignal;
import org.manlier.common.zkeepr.SynSignerReceiver;
import org.manlier.common.zkeepr.SynSignerSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class DictStateSynService implements Consumer<String> {

    private SynSignerSender sender;

    private ComponentService service;

    public DictStateSynService(ComponentService service) {
        this.service = service;
    }

    public void init(String zkHosts, String zkPath) {
        try {
            this.sender = new SynSignerSender(zkHosts, zkPath);
            new SynSignerReceiver(zkHosts, zkPath, this).process();
        } catch (IOException | InterruptedException | KeeperException e) {
            throw new DictSynException("Can't init dict synchronize service", e);
        }
    }

    public void requestSync() {
        try {
            sender.sendSynSignal(SynSignal.DICT_SYN_REQ.name());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runIndexSyncJob() {
        service.rebuild();
        try {
            sender.sendSynSignal(SynSignal.SYN_DONE.name());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void accept(String s) {
        switch (SynSignal.valueOf(s)) {
            case DICT_SYN_DONE:
                CompletableFuture.runAsync(this::runIndexSyncJob);
        }
    }

    public static void main(String[] args) throws InterruptedException {
    }
}
