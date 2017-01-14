package gui;

import com.listen_picture.Converter;
import com.listen_picture.Main;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.Collections;
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
    ArrayWrapper progressFree = new ArrayWrapper();
    private Callable onDone;

    private int urlsCount = 0;
    private int urlsDoneCount = 0;

    class ArrayWrapper {
        private boolean[] array = new boolean[]{true, true, true, true};

        public void setValue(int index, boolean value) {
            synchronized (array) {
                array[index] = value;
            }
        }

        public boolean getValue(int index) {
            synchronized (array) {
                return array[index];
            }
        }
    }

    public ProgressWorker(ProgressBar[] progressBars, Label[] labels, double[][] canvasSizes, List<String> urls, Callable onDone) {
        this.executor = Executors.newFixedThreadPool(4);
        this.progressBars = progressBars;
        this.labels = labels;
        this.canvasSizes = canvasSizes;
        this.onDone = onDone;

        this.urlsCount = urls.size();
        for (String url : urls) {
            urlsQueue.add(url);
        }
        start();
    }

    public void run() {
        while (true) {
            if (urlsQueue.isEmpty()) {
                executor.shutdown();
                System.out.println("DONE");
                return;
            } else {
                if (progressFree.getValue(0) || progressFree.getValue(1) || progressFree.getValue(2) || progressFree.getValue(3)) {

                    int i = 0;
                    while (!progressFree.getValue(i)) {
                        i++;
                    }

                    final int capturedIndex = i;
                    progressFree.setValue(capturedIndex, false);

                    final String current = urlsQueue.poll();
                    System.out.println(current);
                    executor.execute(new ProgressTask(progressBars[i], labels[i], canvasSizes[i][0], canvasSizes[i][1], current, () -> {
                        progressFree.setValue(capturedIndex, true);
                        urlsDoneCount += 1;
                        checkDone();
                        return 1;
                    }));
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void checkDone(){
        if (urlsDoneCount == urlsCount){
            try {
                onDone.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
