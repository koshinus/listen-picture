package com.listen_picture;

/**
 * Created by vadim on 19.12.16.
 */
public class QueueItem <T> {
    public int order;
    public T items;
    public QueueItem(int o, T i){
        this.order = o;
        this.items = i;
    }
}
