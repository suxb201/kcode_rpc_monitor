package com.kuaishou.kcode;

public class Hash {
    private static final String[] cached = new String[KcodeRpcMonitorImpl.HASH_SIZE];
    private static final String[] cached2 = new String[KcodeRpcMonitorImpl.HASH_SIZE];

    public static int get_hashcode_char(char[] ch, int len) {
        int length = len - 1;
        length -= ch[length] == 'r' ? 2 : 0;

        int code = ch[length - 3] * 97000 - 5172913
                + ch[length - 2] * 9700
                + ch[length - 1] * 970
                + ch[length] * 97 + ch[0];

        code %= KcodeRpcMonitorImpl.HASH_SIZE;

        return code;
    }

    public static int get_hashcode(String s) {
        int length = s.length() - 1;
        length -= s.charAt(length) == 'r' ? 2 : 0;

        int code = s.charAt(length - 3) * 97000 - 5172913
                + s.charAt(length - 2) * 9700
                + s.charAt(length - 1) * 970
                + s.charAt(length) * 97 + s.charAt(0);

        code %= KcodeRpcMonitorImpl.HASH_SIZE;

        return code;
    }

    public static int get_hashcode2(String s1, String s2) {
        int length1 = s1.length() - 1;
        length1 -= s1.charAt(length1) == 'r' ? 2 : 0;

        int code1 = s1.charAt(length1 - 3) * 97000 - 5172913
                + s1.charAt(length1 - 2) * 9700
                + s1.charAt(length1 - 1) * 970
                + s1.charAt(length1) * 97 + s1.charAt(0);

        code1 %= KcodeRpcMonitorImpl.HASH_SIZE;

        int length2 = s2.length() - 1;
        length2 -= s2.charAt(length2) == 'r' ? 2 : 0;

        int code2 = s2.charAt(length2 - 3) * 97000 - 5172913
                + s2.charAt(length2 - 2) * 9700
                + s2.charAt(length2 - 1) * 970
                + s2.charAt(length2) * 97 + s2.charAt(0);

        code2 %= KcodeRpcMonitorImpl.HASH_SIZE;

        return (code2 * 1481 + code1) % KcodeRpcMonitorImpl.HASH_SIZE;
    }
}
