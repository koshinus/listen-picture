package com.listen_picture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.listen_picture.GlobalVariables.bytesQueue;

/**
 * Created by vadim on 19.12.16.
 */
public class Worker extends Thread {
    ExecutorService executor;

    public Worker(){
        this.executor = Executors.newSingleThreadExecutor();
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
