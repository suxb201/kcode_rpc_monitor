//package com.kuaishou.kcode;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channels;
//import java.nio.channels.WritableByteChannel;
//import java.util.concurrent.RecursiveAction;
//
//public class FileDispatcher extends RecursiveAction {
//    private ByteBuffer buffer;
//    private boolean running;
//
//    public void start(ByteBuffer buffer) {
//        this.buffer = buffer;
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
//    public static int last_minute = 0;
//    long byte_count = 0;
//
//    public void compute() {
//        int i = 0, end = buffer.limit();
//        int end_minute = get_minute(buffer, end - 1);
//        while (i < end) {
//            int length = calc_length(buffer, i);
//            int minute = get_minute(buffer, i + length - 1);
//
//            if (minute != last_minute) {
//                last_minute = minute;
//                KcodeRpcMonitorImpl.skip_bytes.add(byte_count);
//                KcodeRpcMonitorImpl.minutes.add(minute);
//            }
//            if (minute == end_minute) {
//                byte_count += end - i;
//                break;
//            }
//            i += length;
//            byte_count += length;
//        }
//    }
//
//    // 句子长度
//    int calc_length(ByteBuffer buffer, int index) {
//        int end = index;
//        while (buffer.get(end) != '\n') end += 1;
//        return end - index + 1;
//    }
//
//    // 输入 index 为句子换行符所在位置
//    int get_minute(ByteBuffer buffer, int index) {
//        long number = 0;
//        index -= 13;
//        for (int i = 0; i < 10; i++) {
//            number = number * 10 + buffer.get(index + i) - '0';
//        }
//        number = number / 60;
//        return (int) number;
//    }
//
//
//}
