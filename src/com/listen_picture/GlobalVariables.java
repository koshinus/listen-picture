package com.listen_picture;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by vadim on 19.12.16.
 */
public class GlobalVariables
{
    static ConcurrentLinkedQueue<QueueItem<byte[]>> bytesQueue = new ConcurrentLinkedQueue<>();
    static ConcurrentLinkedQueue<QueueItem<Color[]>> colorsQueue = new ConcurrentLinkedQueue<>();
    static int FRAME_LENGTH = 1500;
    static final int byteShift = 128;
    static int imageWidth = 2000;
    static int imageHeight = 500;
}
