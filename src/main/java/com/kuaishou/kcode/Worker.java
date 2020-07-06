//package com.kuaishou.kcode;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.math.RoundingMode;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channels;
//import java.nio.channels.ReadableByteChannel;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.RecursiveAction;
//
////class DataStruct {
////    public int[] bucket = new int[KcodeRpcMonitorImpl.BUCKET_SIZE];
////    public int cnt = 0;
////    public int right_cnt = 0;
////    public byte[] ip = new byte[31];
////
////    public void clear() {
////        cnt = 0;
////        right_cnt = 0;
////        for (int i = 0; i < KcodeRpcMonitorImpl.BUCKET_SIZE; i++)
////            bucket[i] = 0;
////    }
////}
//
//public class Worker extends RecursiveAction {
//    private boolean running = false;
//
//    // func_a+func_b, ip_a+ip_b, DataStruct
//    private final ConcurrentHashMap<String, ConcurrentHashMap<String, DataStruct>> inner_map = new ConcurrentHashMap<>();
//    private int minute_index = 0;
//    private long chunk_size = 0;
//    ReadableByteChannel chan = null;
//
//    public void start(String path, long skip_byte, long chunk_size_, int minute) throws IOException {
//        // 这个分片能读多少
//        chunk_size = chunk_size_;
//        minute_index = minute - KcodeRpcMonitorImpl.begin_minute;
//
////        System.out.println("开始处理 minute: " + minute
////                + " index: " + minute_index
////                + " skip_byte: " + skip_byte
////                + " chunk_size: " + chunk_size_);
//
//        FileInputStream input_stream = new FileInputStream(path);
//        long skipped = input_stream.skip(skip_byte);
//        if (skipped != skip_byte) System.out.println("跳过字节数不对");
//        chan = Channels.newChannel(input_stream);
//
//        this.running = true;
//        this.fork();
//    }
//
//    public void stop() {
//        if (this.running) {
//            this.join();
//            this.reinitialize();
//            this.running = false;
//        }
//    }
//
//    public void add(String func_a_b, String func_b, String ip_a_b, int is_right, int time_use, int minute_index) {
//
////        DataStruct data = inner_map
////                .computeIfAbsent(func_a_b, k -> new ConcurrentHashMap<>())
////                .computeIfAbsent(ip_a_b, k -> new DataStruct());
////
////        data.cnt += 1;
////        data.right_cnt += is_right;
////        data.bucket[time_use] += 1;
////
////        KcodeRpcMonitorImpl.map_problem2[Hash.get_hashcode(func_b)][minute_index].cnt += 1;
////        KcodeRpcMonitorImpl.map_problem2[Hash.get_hashcode(func_b)][minute_index].right_cnt += is_right;
//    }
//
//    public void compute() {
//
//        ByteBuffer buffer1 = ByteBuffer.allocateDirect(KcodeRpcMonitorImpl.READ_SIZE);
//        ByteBuffer buffer2 = ByteBuffer.allocateDirect(KcodeRpcMonitorImpl.READ_SIZE);
//        ByteBuffer buf = buffer2;
//        ByteBuffer tmp_buffer = ByteBuffer.allocate(256);
//        tmp_buffer.limit(0);
//        int r = 0;
//        while (r != -1) {
//            buf = buf == buffer1 ? buffer2 : buffer1;
//
//            buf.clear();
//
//            buf.put(tmp_buffer);
//
//            while (buf.hasRemaining() && r != -1) {
//                try {
//                    r = chan.read(buf);
//                    chunk_size -= r;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            int end = buf.position();
//            //noinspection StatementWithEmptyBody
//            while (buf.get(--end) != '\n') ;
//
//            int old_end = buf.position();
//
//            tmp_buffer.clear();
//            for (int i = end + 1; i < old_end; i++) {
//                tmp_buffer.put(buf.get(i));
//            }
//            tmp_buffer.flip();
//
//            buf.position(0);
//            buf.limit(end + 1);
//
//            compute_buff(buf);
//
//            if (chunk_size <= 0) break;
//        }
//        compute_3();
//    }
//
//    public void compute_buff(ByteBuffer buffer) {
//        int i = 0, end = buffer.limit();
//        while (i < end) {
//            byte[] byte_array = new byte[KcodeRpcMonitorImpl.LINE_SIZE];
//            int byte_cnt = 0;
//            do {
//                byte_array[byte_cnt] = buffer.get(i);
//                byte_cnt += 1;
//                i += 1;
//            } while (byte_array[byte_cnt - 1] != '\n');
//
//            String line = new String(byte_array, 0, byte_cnt - 1);
//
//            String[] splits = line.split(",");
//            String func_a = splits[0];
//            String ip_a = splits[1];
//            String func_b = splits[2];
//            String ip_b = splits[3];
//            int is_right = splits[4].equals("true") ? 1 : 0;
//            int time_use = Integer.parseInt(splits[5]);
//
//            // 通过分钟数算出偏差
//            int minute = (int) (Long.parseLong(splits[6]) / 1000 / 60);
////            System.out.println(line + " " + minute_index + " " + (minute - KcodeRpcMonitorImpl.begin_minute));
//            if (minute != KcodeRpcMonitorImpl.begin_minute + minute_index) break;
//
//            add(func_a + func_b, func_b, ip_a + "," + ip_b, is_right, time_use, minute_index);
//        }
//    }
//
//    public void compute_3() {
////        System.out.println("compute_3 index: " + minute_index);
////        System.out.println(data_map.containsKey("agentService31openService545"));
////        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> main_map = KcodeRpcMonitorImpl.map_problem1.get(minute_index);
////        for (Map.Entry<String, ConcurrentHashMap<String, DataStruct>> entry_func : inner_map.entrySet()) {
////            ConcurrentLinkedQueue<String> main_map2 = main_map.computeIfAbsent(entry_func.getKey(), k -> new ConcurrentLinkedQueue<>());
////            for (Map.Entry<String, DataStruct> entry_ip : entry_func.getValue().entrySet()) {
////                String ip = entry_ip.getKey();
////                DataStruct tmp_data = entry_ip.getValue();
////                if (tmp_data.cnt == 0) continue;
////
////                double rate = (double) tmp_data.right_cnt / tmp_data.cnt;
////                int index_cnt = (int) (tmp_data.cnt - Math.ceil(0.99 * tmp_data.cnt) + 1);
////
////                int p99 = 0;
////                for (int i = KcodeRpcMonitorImpl.BUCKET_SIZE - 1; i >= 0; i--) {
////                    index_cnt -= tmp_data.bucket[i];
////                    if (index_cnt <= 0) {
////                        p99 = i;
////                        break;
////                    }
////                }
////                String s = String.format("%s,%s,%d", ip, KcodeRpcMonitorImpl.decimal_format.format(rate), p99);
////                main_map2.offer(s);
////                tmp_data.clear();
////            }
////        }
//    }
//}
