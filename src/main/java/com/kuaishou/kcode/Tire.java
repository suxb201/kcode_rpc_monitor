//package com.kuaishou.kcode;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
//class TireNode {
//    int[] next = new int[97];
//    int value = -1;
//}
//
//public class Tire {
//    public static final AtomicInteger hash_number = new AtomicInteger(1);
//    public static final TireNode[] list = new TireNode[2000];
//    static ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
//
//    public static int find(String s1, String s2) {
//        int length1 = s1.length();
//        int length2 = s2.length();
//        char[] ch = {s1.charAt(0), s1.charAt(length1 - 1), s1.charAt(length1 - 2), s1.charAt(length1 - 3), s1.charAt(length1 - 4), s1.charAt(length1 - 5)
//                , s2.charAt(0), s2.charAt(length2 - 1), s2.charAt(length2 - 3)};
//        //        if (!map.containsKey(value)) map.put(value, s1 + ',' + s2);
////        if (!map.get(value).equals(s1 + "," + s2)) System.out.println(map.get(value) + "       " + s1 + "," + s2);
//        return find(ch);
//    }
//
//    static int find(char[] ch) {
//        int now = 0;
//        while (now < 9) {
//            int num = ch[0] - '0';
//            if (list[now].next[num] == 0) list[now].next[num] = hash_number.getAndIncrement();
//            now = list[now].next[num];
//        }
//
//        if (list[now].value == -1) list[now].value = hash_number.getAndIncrement();
//        return list[now].value;
//
//    }
//}
