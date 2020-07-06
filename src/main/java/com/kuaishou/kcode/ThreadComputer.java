package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

class DataStruct {
    public AtomicInteger[] bucket = new AtomicInteger[KcodeRpcMonitorImpl.BUCKET_SIZE];
    public AtomicInteger cnt = new AtomicInteger(0);
    public AtomicInteger right_cnt = new AtomicInteger(0);

    public DataStruct() {
        for (int i = 0; i < KcodeRpcMonitorImpl.BUCKET_SIZE; i++)
            bucket[i] = new AtomicInteger(0);
    }
}

public class ThreadComputer extends RecursiveAction {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, DataStruct>> inner_map = new ConcurrentHashMap<>(5000, 0.5f, 8);
    private final int minute_index;

    public ThreadComputer(int minute_index_) {

        minute_index = minute_index_;
        for (int i = 0; i < KcodeRpcMonitorImpl.HASH_SIZE; i += 1) {
            inner_map.put(i, new ConcurrentHashMap<>(1000, 0.5f, 8));
        }
    }

    public void add(int func_a_b, int func_b, String ip_a_b, int is_right, int time_use, int minute_index) {

        DataStruct data = inner_map
                .get(func_a_b)
                .computeIfAbsent(ip_a_b, k -> new DataStruct());
        data.cnt.getAndIncrement();
        data.right_cnt.getAndAdd(is_right);
        data.bucket[time_use].getAndIncrement();

        Problem2Struct tmp = KcodeRpcMonitorImpl.map_problem2[func_b][minute_index];
        tmp.cnt.getAndIncrement();
        tmp.right_cnt.getAndAdd(is_right);
    }

    public void compute() {
        ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> main_map = KcodeRpcMonitorImpl.map_problem1.get(minute_index);
        for (Map.Entry<Integer, ConcurrentHashMap<String, DataStruct>> entry_func : inner_map.entrySet()) {
            ConcurrentLinkedQueue<String> main_map2 = main_map.get(entry_func.getKey());
            for (Map.Entry<String, DataStruct> entry_ip : entry_func.getValue().entrySet()) {
                String ip = entry_ip.getKey();
                DataStruct tmp_data = entry_ip.getValue();
                if (tmp_data.cnt.get() == 0) continue;

                double rate = (double) tmp_data.right_cnt.get() / tmp_data.cnt.get();
                int index_cnt = (int) (tmp_data.cnt.get() - Math.ceil(0.99 * tmp_data.cnt.get()) + 1);

                int p99 = 0;
                for (int i = KcodeRpcMonitorImpl.BUCKET_SIZE - 1; i >= 0; i--) {
                    index_cnt -= tmp_data.bucket[i].get();
                    if (index_cnt <= 0) {
                        p99 = i;
                        break;
                    }
                }
                main_map2.offer(String.format("%s,%s,%d", ip, KcodeRpcMonitorImpl.decimal_format.format(rate), p99));
            }
        }
    }
}
