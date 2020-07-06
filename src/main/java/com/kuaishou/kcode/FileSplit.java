//package com.kuaishou.kcode;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channels;
//import java.nio.channels.ReadableByteChannel;
//import java.nio.channels.WritableByteChannel;
//
//public class FileSplit {
//    FileDispatcher file_dispatcher = null;
//
//    public void start(String path) throws IOException {
//        long time_start_all = System.currentTimeMillis();
//
//        file_dispatcher = new FileDispatcher();
//        // in
//        FileInputStream input_stream = new FileInputStream(path);
//        ReadableByteChannel chan_in = Channels.newChannel(input_stream);
//
//        ByteBuffer buffer1 = ByteBuffer.allocateDirect(KcodeRpcMonitorImpl.READ_SIZE);
//        ByteBuffer buffer2 = ByteBuffer.allocateDirect(KcodeRpcMonitorImpl.READ_SIZE);
//        ByteBuffer buf = buffer2;
//        ByteBuffer tmp_buffer = ByteBuffer.allocate(256);
//        tmp_buffer.limit(0);
//
//        int r = 0;
//        long time_sum = 0;
//        while (r != -1) {
//            buf = buf == buffer1 ? buffer2 : buffer1;
//            buf.clear();
//            buf.put(tmp_buffer);
//
//            while (buf.hasRemaining() && r != -1) {
//                r = chan_in.read(buf);
//            }
//
//            int end = buf.position();
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
//            long time_start = System.currentTimeMillis();
//            file_dispatcher.stop();
//            file_dispatcher.start(buf);
//            long time_end = System.currentTimeMillis();
//            time_sum += time_end - time_start;
//        }
//        file_dispatcher.stop();
//        long time_end_all = System.currentTimeMillis();
//
////        System.out.println("找边界 thread 用时: " + time_sum);
////        System.out.println("找边界 all 用时: " + (time_end_all - time_start_all));
//    }
//}
