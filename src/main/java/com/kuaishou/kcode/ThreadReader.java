package com.kuaishou.kcode;

import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveAction;

public class ThreadReader extends RecursiveAction {


    boolean running = false;

    public void start() {
        this.running = true;
        this.fork();
    }

    public ByteBuffer stop() {
        if (this.running) {
            this.join();
            this.running = false;
        }
        this.reinitialize();
        return this.buffer;
    }

    ByteBuffer buffer = ByteBuffer.allocateDirect(KcodeRpcMonitorImpl.READ_SIZE);
    StringBuilder string_builder_ip = new StringBuilder();
    char[] ch = new char[200];
    byte b;
    int func_a_b;
    int func_a;
    String ip_a_b;
    int func_b;

    int is_right = 0;
    int time_use = 0;
    int minute = 0;
    int index;

    public void compute() {
        int i = 0, end = this.buffer.limit();
        while (i < end) {

            // func_a
            index = 0;
            while ((ch[index] = (char) buffer.get(i)) != ',') {
                index += 1;
                i += 1;
            }
            i += 1;
            func_a = Hash.get_hashcode_char(ch, index);

            // ip_a-----------------------
            index = 0;
            while ((ch[index] = (char) buffer.get(i)) != ',') {
                index += 1;
                i += 1;
            }
            i += 1;
            string_builder_ip.append(ch, 0, index).append(',');

            // func_b---------------------------
            index = 0;
            while ((ch[index] = (char) buffer.get(i)) != ',') {
                index += 1;
                i += 1;
            }
            i += 1;
            func_b = Hash.get_hashcode_char(ch, index);


            // ip_b------ -----------------
            index = 0;
            while ((ch[index] = (char) buffer.get(i)) != ',') {
                index += 1;
                i += 1;
            }
            i += 1;
            string_builder_ip.append(ch, 0, index);
            ip_a_b = string_builder_ip.toString();
            string_builder_ip.delete(0, string_builder_ip.length());


            // 开始读 true false
            if (buffer.get(i) == 't') {
                is_right = 1;
                i += 5;
            } else {
                is_right = 0;
                i += 6;
            }
            // 开始读 time use
            time_use = 0;
            while ((b = buffer.get(i)) != ',') {
                time_use = time_use * 10 + b - '0';
                i += 1;
            }
            i += 1; // 逗号下一个
            // 开始读 timestamp 秒级
            minute = 0;
            for (int j = 0; j < 10; j++) {
                b = buffer.get(i + j);
                minute = minute * 10 + b - '0';
            }
            minute /= 60;
            i += 14;

            func_a_b = (func_b * 1481 + func_a) % KcodeRpcMonitorImpl.HASH_SIZE;
            // 通过分钟数算出偏差
            if (KcodeRpcMonitorImpl.begin_minute == -1) {
//                System.out.println("begin_minute: " + minute);
//                System.out.println(func_a + " " + func_b + " " + ip_a + " " + ip_b + " " + func_a_b + " " + ip_a_b + " " + minute);
                KcodeRpcMonitorImpl.begin_minute = minute;
            }
//            System.out.println(func_a + " " + func_b + " " + ip_a + " " + ip_b + " " + func_a_b + " " + ip_a_b + " " + minute);

            int minute_index = minute - KcodeRpcMonitorImpl.begin_minute;
//            System.out.println(minute + " " + minute_index);
            KcodeRpcMonitorImpl.thread_computer[minute_index].add(func_a_b, func_b, ip_a_b, is_right, time_use, minute_index);

        }
        this.running = false;
    }
}
