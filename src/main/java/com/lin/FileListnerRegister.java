package com.lin;

import org.apache.commons.collections.CollectionUtils;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * listner 注册中心
 *
 * @author jianglinzou
 * @date 2019/3/21 上午11:08
 */
public class FileListnerRegister {


    static FileListnerRegister INSTANCE = new FileListnerRegister();

    public static FileListnerRegister getInstance() {
        return INSTANCE;
    }

    private ConcurrentHashMap<String /*文件的绝对路径*/, List<FileListener>> fileListenerMap =
            new ConcurrentHashMap<String, List<FileListener>>();


    public boolean register(String path, FileListener fileListener) {
        List<FileListener> listeners = fileListenerMap.get(path);
        if (listeners == null) { //解决注册并发添加问题
            synchronized (path) {
                listeners = fileListenerMap.get(path);
                if (listeners == null) { //二次判断可能，为并发添加，此时别的线程已添加成功，此时listener不为空
                    listeners = new ArrayList<FileListener>();
                    listeners.add(fileListener);
                    fileListenerMap.put(path, listeners);
                    return true;
                } else {
                    synchronized (listeners) {
                        listeners.add(fileListener);
                        return true;
                    }
                }
            }
        } else {
            synchronized (listeners) {
                listeners.add(fileListener);
                return true;
            }
        }
    }

    /**
     * 返回现有监听器的副本
     *
     * @param path
     * @return
     */
    public List<FileListener> getListnerCopy(String path) {
        List<FileListener> retureList = new ArrayList();
        List<FileListener> listeners = fileListenerMap.get(path);
        if (CollectionUtils.isEmpty(listeners)) { //为空直接返回
            return retureList;
        }
        synchronized (listeners) {
            retureList.addAll(listeners);
        }
        retureList.sort(new Comparator<FileListener>() {
            @Override
            public int compare(FileListener o1, FileListener o2) {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                }
                return -1;
            }
        });
        return retureList;
    }


    public ConcurrentHashMap<String /*文件的绝对路径*/, List<FileListener>> getFileListenerMap() {
        return this.fileListenerMap;
    }

}



