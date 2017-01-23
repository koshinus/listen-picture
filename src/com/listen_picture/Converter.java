package com.listen_picture;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class Converter {
    private ConcurrentLinkedQueue<Main.QueueItem<byte[]>> bytesQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Main.QueueItem<Color[]>> colorsQueue = new ConcurrentLinkedQueue<>();

    static public final int byteShift = 128;
    static int FRAME_LENGTH = 1500;
    static Random rand = new Random();

    public class MyTask implements Runnable {
        Main.QueueItem<byte[]> item;
        public MyTask(Main.QueueItem<byte[]> i) {
            this.item = i;
        }

        public void run() {
            byte[] bytes = item.items;
            Color[] result = new Color[FRAME_LENGTH / 3];
            for (int i = 0; i < FRAME_LENGTH; i += 3) {
                result[i / 3] = new Color(bytes[i] + byteShift, bytes[i + 1] + byteShift, bytes[i + 2] + byteShift);
            }
//            try {
//                Thread.sleep(rand.nextInt(150) + 1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            colorsQueue.add(new Main.QueueItem<>(item.order, result));
        }
    }

    public class Worker extends Thread {
        ExecutorService executor;

        public Worker(){
            this.executor = Executors.newFixedThreadPool(4);
            start();
        }

        public void run() {
            while( true ) {
                if (bytesQueue.isEmpty()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    executor.execute(new MyTask(bytesQueue.poll()));
                }
            }
        }
    }



    public Main.MyThread encode(String audioPath, Graphics graphics) {
        final Worker w = new Worker();
        final File file = new File(audioPath);

        try (final AudioInputStream in = getAudioInputStream(file)) {
            int width = (int)file.length() / FRAME_LENGTH + 10;
            BufferedImage bufferedImage = new BufferedImage(width, 500, BufferedImage.TYPE_INT_RGB);
            Graphics imageG = bufferedImage.getGraphics();

            int columns = 0;
            int nSum = 0;

            int n = 0;
            while(n != -1){
                final byte[] buffer = new byte[FRAME_LENGTH];
                n = in.read(buffer, 0, buffer.length);
                nSum += n;
                bytesQueue.add(new Main.QueueItem<>(columns, buffer));
                columns += 1;
            }

            final int cl = columns;

            Main.MyThread t;
            t = new Main.MyThread(() -> {
                int columnsDrawed = 0;
                while( columnsDrawed != cl ) {
                    if (colorsQueue.isEmpty()){
                        try {
                            Thread.sleep(500);
                            System.out.println("wait2 " + columnsDrawed + "/" + cl);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        Main.QueueItem<Color[]> item = colorsQueue.poll();
                        int x = item.order;
                        for (int y = 0; y < 500; y+=1){
                            graphics.setColor(item.items[y]);
                            imageG.setColor(item.items[y]);

                            graphics.drawLine(x, y, x, y);
                            imageG.drawLine(x, y, x, y);
                        }
                        columnsDrawed += 1;
                    }
                }
                w.interrupt();
            });

            t.length = nSum;
            t.image = bufferedImage;
            t.start();

            return t;
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }


}
