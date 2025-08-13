package com.mycompany.app.Cluster;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Collections;

public class ProcessGroup implements IProcessGroup {
    private static volatile ProcessGroup instance;

    private final ConcurrentMap<String, ProcessEntity> cluster = new ConcurrentHashMap<>();

    private ProcessGroup() {}

    public static ProcessGroup createProcessGroupInstance() {
        if (instance == null) {
            synchronized (ProcessGroup.class) {
                if (instance == null) {
                    instance = new ProcessGroup();
                }
            }
        }
        return instance;
    }

    // Helpers
    private static String keyOf(ProcessEntity p) {
        InetSocketAddress a = p.getCompleteSocketAddress();
        return a.getHostString() + ":" + a.getPort();
    }

    @Override
    public void addCorrectProcess(final ProcessEntity p) {
        cluster.putIfAbsent(keyOf(p), p);
    }

    @Override
    public boolean deleteCorrectProcess(final ProcessEntity p) {
        return cluster.remove(keyOf(p), p);
    }

    @Override
    public List<ProcessEntity> getProcessGroup() {
        return Collections.unmodifiableList(new ArrayList<>(cluster.values()));
    }

    @Override
    public boolean isEagerBroadcastCompatible(final ProcessEntity pe) {
        return !cluster.containsKey(keyOf(pe));
    }
}
