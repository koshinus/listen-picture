package com.listen_picture;

import java.awt.Color;

import static com.listen_picture.GlobalVariables.FRAME_LENGTH;
import static com.listen_picture.GlobalVariables.byteShift;
import static com.listen_picture.GlobalVariables.colorsQueue;

/**
 * Created by vadim on 19.12.16.
 */
public class MyTask implements Runnable {
    QueueItem<byte[]> item;
    public MyTask(QueueItem<byte[]> i) {
        this.item = i;
    }

    public void run() {
        byte[] bytes = item.items;
        Color[] result = new Color[FRAME_LENGTH / 3];
        for (int i = 0; i < FRAME_LENGTH; i += 3) {
            result[i / 3] = new Color(bytes[i] + byteShift, bytes[i + 1] + byteShift, bytes[i + 2] + byteShift);
        }
        System.out.println("Done " + item.order);
        colorsQueue.add(new QueueItem<>(item.order, result));
    }
}
