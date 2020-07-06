//package com.kuaishou.kcode;
//
//import java.nio.ByteBuffer;
//import java.util.concurrent.RecursiveAction;
//
//public class Dispatcher extends RecursiveAction {
//    private ByteBuffer buffer;
////    private final Worker[] workers;
//    private int worker_index;
//    private boolean running;
//    private int the_minute_index;
//
//    public Dispatcher(int worker_size) throws Exception {
////        workers = new Worker[worker_size];
////        for (int i = 0; i < worker_size; i++) {
////            workers[i] = new Worker();
////        }
//        the_minute_index = 0;
//        running = false;
//        worker_index = 0;
//    }
//
//    public void start(ByteBuffer buffer) {
//        this.buffer = buffer;
//        this.running = true;
//        this.fork();
//    }
//
//    public void stop() {
//        if (this.running) {
////            System.out.println(":waiting");
//            this.join();
//            this.reinitialize();
//            this.running = false;
//        }
//    }
//
//    public void compute() {
//
////        Worker worker = workers[worker_index];
//        int i = 0, end = buffer.limit();
//
//        while (i < end) {
//
//            byte[] byte_array = new byte[KcodeRpcMonitorImpl.LINE_SIZE];
//            int byte_cnt = 0;
//            do {
//                byte_array[byte_cnt] = buffer.get(i);
//                byte_cnt += 1;
//                i += 1;
//            } while (byte_array[byte_cnt - 1] != '\n');
//
//            String line = new String(byte_array, 0, byte_cnt - 1);
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
//            KcodeRpcMonitorImpl.begin_minute = Math.min(KcodeRpcMonitorImpl.begin_minute, minute);
//            int minute_index = minute - KcodeRpcMonitorImpl.begin_minute;
//
//            if (the_minute_index != minute_index) {
////                System.out.println(the_minute + " " + Long.parseLong(splits[6]));
////                worker.start(the_minute_index);
////
////                the_minute_index = minute_index;
////                worker_index += 1;
////                worker_index %= KcodeRpcMonitorImpl.WORKER_SIZE;
////                worker = workers[worker_index];
////
////                long time_start = System.currentTimeMillis();
////                worker.stop();
////                long time_end = System.currentTimeMillis();
////                System.out.println("等待 worker-" + worker_index + ": " + (time_end - time_start) + "ms");
//
//            }
////            worker.add(func_a, func_b, ip_a, ip_b, is_right, time_use, minute_index);
//        }
//    }
//
//
//    public void end() {
////        workers[worker_index].start(the_minute_index);
////
////        for (int i = 0; i < KcodeRpcMonitorImpl.WORKER_SIZE; i++) {
////            workers[i].stop();
////        }
//    }
//}
