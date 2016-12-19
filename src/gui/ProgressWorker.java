package gui;

import com.listen_picture.Converter;
import com.listen_picture.Main;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by iddqd on 19/12/2016.
 */
public class ProgressWorker extends Thread {
    ExecutorService executor;
    private ConcurrentLinkedQueue<String> urlsQueue = new ConcurrentLinkedQueue<>();
    private ProgressBar[] progressBars;
    private Label[] labels;
    private double[][] canvasSizes;
    private boolean[] progressFree = { true, true, true, true };
    private int lastUsedIndex;
    private Semaphore mutex = new Semaphore(1, true);

    public ProgressWorker(ProgressBar[] progressBars, Label[] labels, double[][] canvasSizes, List<String> urls){
        this.executor = Executors.newFixedThreadPool(4);
        this.progressBars = progressBars;
        this.labels = labels;
        this.canvasSizes = canvasSizes;
        for (String url : urls) {
            urlsQueue.add(url);
        }
        start();
    }

    public void run() {
        while( true ) {
            if (urlsQueue.isEmpty()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            } else {
                System.out.println("before loop");
                boolean f = true;
                while (f) {
                    try {
                        mutex.acquire();
                        System.out.println("mutex captured");

                        if (progressFree[0] || progressFree[1] || progressFree[2] || progressFree[3]) {
                            f = false;
                            int i = 0;
                            while (!progressFree[i]) {
                                i++;
                            }
                            System.out.println(i);
                            progressFree[i] = false;
                            lastUsedIndex = i;
                            final int capturedIndex = i;
                            final String current = urlsQueue.poll();
                            System.out.println(current);
                            executor.execute(new ProgressTask(progressBars[i], labels[i], canvasSizes[i][0], canvasSizes[i][1], current, new Callable<Integer>() {
                                public Integer call() {
                                    try {
                                        mutex.acquire();
                                        System.out.println("mutex captured");
                                        progressFree[capturedIndex] = true;
                                        System.out.println("mutex released");
                                        mutex.release();
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return -1;
                                    }
                                    return lastUsedIndex;
                                }
                            }));
                        } else {
                            Thread.sleep(500);
                        }
                        System.out.println("mutex released");
                        mutex.release();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }
}
