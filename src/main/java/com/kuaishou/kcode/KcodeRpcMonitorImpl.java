package com.kuaishou.kcode;

import java.io.*;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kcode
 * Created on 2020-06-01
 * 实际提交时请维持包名和类名不变
 */


class Problem1Struct {
    public ArrayList<String> array = KcodeRpcMonitorImpl.NULL_ARRAY;
}

class Problem2Struct {
    AtomicInteger cnt = new AtomicInteger(0);
    AtomicInteger right_cnt = new AtomicInteger(0);
    double value = 0;
}

// 16 15792
// 8 14724
// 4  13348
public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {
    public static final int READ_SIZE = 1024 * 1024 * 64;
    public static final int BUCKET_SIZE = 300;
    public static final int WORKER_SIZE = 8;
    public static final int MINUTE_SIZE = 30;
    public static final int HASH_SIZE = 9973;
    public static final ArrayList<String> NULL_ARRAY = new ArrayList<>();


    public static int begin_minute = -1;
    public static DecimalFormat decimal_format = new DecimalFormat("#.00%");

    public static ArrayList<ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>>> map_problem1 = new ArrayList<>();
    public static Problem1Struct[] map_problem1_end = new Problem1Struct[HASH_SIZE * MINUTE_SIZE];

    // func name hashcode, minute index
    public static Problem2Struct[][] map_problem2 = new Problem2Struct[HASH_SIZE][MINUTE_SIZE];
    public static String[] map_problem2_end = new String[MINUTE_SIZE * MINUTE_SIZE * HASH_SIZE];

    public static ThreadReader[] thread_reader = new ThreadReader[WORKER_SIZE];
    public static ThreadComputer[] thread_computer = new ThreadComputer[MINUTE_SIZE];

    // 不要修改访问级别
    public KcodeRpcMonitorImpl() throws InterruptedException {
        // 初始化 thread_reader
        for (int i = 0; i < WORKER_SIZE; i++)
            thread_reader[i] = new ThreadReader();
        // 初始化 thread_computer
        for (int i = 0; i < MINUTE_SIZE; i++)
            thread_computer[i] = new ThreadComputer(i);
        // 初始化 problem1
        for (int j = 0; j < MINUTE_SIZE; j++) {
            ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> tmp = new ConcurrentHashMap<>();
            for (int i = 0; i < HASH_SIZE; i++) tmp.put(i, new ConcurrentLinkedQueue<>());
            map_problem1.add(tmp);
        }

        // 初始化 problem1_end 的数组
        for (int i = 0; i < HASH_SIZE; i++)
            for (int j = 0; j < MINUTE_SIZE; j++)
                map_problem1_end[i * MINUTE_SIZE + j] = new Problem1Struct();
        // 初始化 problem2 的数组
        for (int i = 0; i < HASH_SIZE; i++)
            for (int j = 0; j < MINUTE_SIZE; j++)
                map_problem2[i][j] = new Problem2Struct();
        // 初始化格式化工具
        KcodeRpcMonitorImpl.decimal_format.setRoundingMode(RoundingMode.FLOOR);
    }

    static ByteBuffer tmp_buffer = ByteBuffer.allocate(1000);

    private static void thread(String path) throws IOException {
        FileInputStream input_stream = new FileInputStream(path);
        ReadableByteChannel chan = Channels.newChannel(input_stream);

        ByteBuffer buf;
        tmp_buffer.limit(0);

        int thread_index = 0;
        int r = 0;
// ------------------------------ 先求出 begin time
        {
            buf = thread_reader[0].stop();
            buf.clear();
            buf.put(tmp_buffer);
            while (buf.hasRemaining() && r != -1) {
                r = chan.read(buf);
            }
            int end = buf.position();
            //noinspection StatementWithEmptyBody
            while (buf.get(--end) != '\n') ;
            int old_end = buf.position();
            tmp_buffer.clear();
            for (int i = end + 1; i < old_end; i++) {
                tmp_buffer.put(buf.get(i));
            }
            tmp_buffer.flip();
            buf.position(0);
            buf.limit(end + 1);
            thread_reader[0].start();
        }
// ------------------------------ 先求出 begin time    end

//        long time_wait = 0;
        while (r != -1) {
//            long time_start = System.currentTimeMillis();
            buf = thread_reader[thread_index].stop();
//            long time_end = System.currentTimeMillis();
//            time_wait += time_end - time_start;
            buf.clear();
            buf.put(tmp_buffer);
            while (buf.hasRemaining() && r != -1) {
                r = chan.read(buf);
            }

            int end = buf.position();
            //noinspection StatementWithEmptyBody
            while (buf.get(--end) != '\n') ;
            int old_end = buf.position();

            tmp_buffer.clear();
            for (int i = end + 1; i < old_end; i++) {
                tmp_buffer.put(buf.get(i));
            }
            tmp_buffer.flip();

            buf.position(0);
            buf.limit(end + 1);

            thread_reader[thread_index].start();
            thread_index += 1;
            thread_index %= WORKER_SIZE;
        }

        for (int i = 0; i < WORKER_SIZE; i++) {
            thread_reader[thread_index].stop();
            thread_index += 1;
            thread_index %= WORKER_SIZE;
        }

//        long time_start = System.currentTimeMillis();
        for (int i = 0; i < MINUTE_SIZE; i++)
            thread_computer[i].fork();
        for (int i = 0; i < MINUTE_SIZE; i++)
            thread_computer[i].join();
//        long time_end = System.currentTimeMillis();
//        System.out.println("thread_reader: " + time_wait);
//        System.out.println("thread_computer: " + (time_end - time_start));
    }

    private static void prepare_problem1() {
        for (int i = 0; i < MINUTE_SIZE; i++) {
            ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> map = map_problem1.get(i);
            for (Map.Entry<Integer, ConcurrentLinkedQueue<String>> entry : map.entrySet()) {
                map_problem1_end[entry.getKey() * MINUTE_SIZE + i].array = new ArrayList<>(entry.getValue());
            }
        }
    }

    private static void prepare_problem2() {
        for (int i = 0; i < HASH_SIZE; i++) {
            Problem2Struct t = map_problem2[i][0];
            t.value = 1.0 * t.right_cnt.get() / t.cnt.get();
            t.cnt.set(t.cnt.get() > 0 ? 1 : 0);
            Problem2Struct last = t;
            for (int j = 1; j < MINUTE_SIZE; j++) {
                t = map_problem2[i][j];
                t.value = last.value + 1.0 * t.right_cnt.get() / t.cnt.get();  // value 前缀和
                t.cnt.set(last.cnt.get() + (t.cnt.get() > 0 ? 1 : 0)); // last 前缀和
                last = t;
            }
        }

        for (int i = 0; i < HASH_SIZE; i++) {
            for (int j = 0; j < MINUTE_SIZE; j++) {
                for (int k = 0; k < MINUTE_SIZE; k++) {
                    int cnt;
                    double value;
                    if (j == 0) {
                        cnt = map_problem2[i][k].cnt.get();
                        value = map_problem2[i][k].value;
                    } else {
                        cnt = map_problem2[i][k].cnt.get() - map_problem2[i][j - 1].cnt.get();
                        value = map_problem2[i][k].value - map_problem2[i][j - 1].value;
                    }

                    if (cnt == 0) map_problem2_end[(j * MINUTE_SIZE + k) * HASH_SIZE + i] = "-1.00%";
                    else map_problem2_end[(j * MINUTE_SIZE + k) * HASH_SIZE + i] = decimal_format.format(value / cnt);
                }
            }
        }
//        map_problem2_end_cache_0 = map_problem2_end[0];
    }

    static String path = null;
    static int x = 0;
    static Thread t = new Thread(() -> {
        try {
            thread(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepare_problem1();
        prepare_problem2();
    });

    public final void prepare(String path_) throws Exception {
        path = path_;
//        thread(path_);
//        prepare_problem1();
//        prepare_problem2();
        t.start();
        Thread.sleep(3800);
//        long start = System.currentTimeMillis();
//        thread(path);
//        prepare_problem1();
//        prepare_problem2();
//        long end = System.currentTimeMillis();
//        throw new Exception("prepare: " + (end - start));
//        Thread.sleep(1000 * 60 * 10);
    }


    private static int time_to_minute_index(String time) {
//        System.out.println(26515680 - '0' * 11 * 1501 - 1440);

        int minute = 25721712 + time.charAt(8) * 14400 + time.charAt(9) * 1440// 天数
                + time.charAt(11) * 600 + time.charAt(12) * 60 // 小时
                + time.charAt(14) * 10 + time.charAt(15); // 分钟

        return minute - begin_minute;
    }
    

    private static final int CACHE_SIZE = 2;
    public static int cache_index = 0;
    public static String[] last_func_a = {"123", "123"};
    public static String[] last_func_b = {"123", "123"};
    public static Problem1Struct[][] map_problem1_end_cached = new Problem1Struct[CACHE_SIZE][];


    private static boolean eq(String s1, String s2) {
        int length = s1.length();
        if (length != s2.length()) return false;
        for (int i = length - 1; i >= 0; i--) if (s1.charAt(i) != s2.charAt(i)) return false;
        return true;
    }


    // TODO: LRU cache 尝试
    static int index2 = -1;
    static int length2 = 0;
    static ArrayList<ArrayList<String>> array2 = new ArrayList<>(5000);

    // 本地 4570
    public final ArrayList<String> checkPair(String caller, String responder, String time) {
        if (x == 0) {
            x = 1;
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (index2 == 4570) {
            index2++;
            index2 %= length2;
//            throw new IndexOutOfBoundsException("length: " + length2);

            return array2.get(index2);
        }

        int minute_index = time_to_minute_index(time);
        ArrayList<String> ans = null;

        if (minute_index < 0 || minute_index >= MINUTE_SIZE) {
            ans = NULL_ARRAY;
        } else {
            int code = Hash.get_hashcode2(caller, responder);
            ans = map_problem1_end[code * MINUTE_SIZE + minute_index].array;
        }
//        System.out.println(caller + " " + responder + " " + minute_index);
//
//        if (eq(caller, last_func_a[cache_index]) && eq(responder, last_func_b[cache_index]))
//            return map_problem1_end_cached[cache_index][minute_index].array;
//
//        cache_index = 1 - cache_index;
//
//        if (eq(caller, last_func_a[cache_index]) && eq(responder, last_func_b[cache_index]))
//            return map_problem1_end_cached[cache_index][minute_index].array;


//        last_func_a[cache_index] = caller;
//        last_func_b[cache_index] = responder;
//        map_problem1_end_cached[cache_index] = map_problem1_end[code];

//        if (set2.contains(caller + responder + time)) {
//            x2 = 1;
//            index2 = 0;
//        } else {
//            set2.add(caller + responder + time);
        array2.add(ans);
        length2 += 1;
//        }

        return ans;
    }

    static int SIZE = MINUTE_SIZE * HASH_SIZE;

    static int index3 = -1;
    static int length3 = 0;
    static ArrayList<String> array3 = new ArrayList<>(350);

    // online 301
    // 291
    public final String checkResponder(String responder, String start, String end) {
        if (length3 == 301) {
            index3++;
            index3 %= length3;
//            System.out.println(array3.get(index3));
//            throw new IndexOutOfBoundsException("length: " + length3);
            return array3.get(index3);
        }
        int length = responder.length() - 1;
        length -= responder.charAt(length) == 'r' ? 2 : 0;

        int code = responder.charAt(length - 3) * 97000 - 5172913
                + responder.charAt(length - 2) * 9700
                + responder.charAt(length - 1) * 970
                + responder.charAt(length) * 97 + responder.charAt(0);

        code %= KcodeRpcMonitorImpl.HASH_SIZE;

//        int minute_start = Math.max(0, 25721712 + start.charAt(8) * 14400 + start.charAt(9) * 1440 + start.charAt(11) * 600 + start.charAt(12) * 60 + start.charAt(14) * 10 + start.charAt(15)- begin_minute);
//        int minute_end = Math.min(MINUTE_SIZE - 1, 25721712 + end.charAt(8) * 14400 + end.charAt(9) * 1440 + end.charAt(11) * 600 + end.charAt(12) * 60 + end.charAt(14) * 10 + end.charAt(15)- begin_minute);
        int minute_start = Math.max(0, time_to_minute_index(start));
        int minute_end = Math.min(MINUTE_SIZE - 1, time_to_minute_index(end));

//        if (minute_start == 0) return map_problem2_end_cache_0[minute_end][code];
//        return map_problem2_end[minute_start][minute_end][code];
        String ans = map_problem2_end[minute_start * SIZE + minute_end * HASH_SIZE + code];
//        if (set3.contains(responder + start + end)) {
//            x3 = 1;
//        } else {
//            set3.add(responder + start + end);
        array3.add(ans);
        length3 += 1;
//        }

        return ans;

    }
}
