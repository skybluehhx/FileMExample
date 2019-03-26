package com.lin;

import com.lin.Event.EventType;
import com.lin.util.ConcurrentHashSet;
import com.lin.util.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责加载文件
 *
 * @author jianglinzou
 * @date 2019/3/21 下午2:29
 */
public final class FileLoader {

    private final static int DEFAULT_INTERVAL_SECONDS = 5;
    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    ListenerActuator actuator = ListenerActuator.getInstance();

    private final ConcurrentHashMap<String, Long> fileLastModified = new ConcurrentHashMap<>();

    //存放上次已有的文件 考虑用hashSet?
    private volatile ConcurrentHashSet<String> lastFilesInfo = new ConcurrentHashSet<>();

    //存放当前已有的文件信息 考虑用hashSet
    private volatile ConcurrentHashSet<String> currentFilesInfo = new ConcurrentHashSet<>();

    private final FileListnerRegister fileListnerRegister;


    Thread loadThread;

    private final String[] dirs;

    int loaderingIntervalSeconds;

    volatile boolean running = true;


    public FileLoader(String[] dirs, int loaderingIntervalSeconds, FileListnerRegister fileListnerRegister) {

        this.loaderingIntervalSeconds = loaderingIntervalSeconds;
        this.dirs = dirs;
        //加载文件
        this.fileListnerRegister = fileListnerRegister;
        init(dirs);

    }

    public FileLoader(String[] dirs, FileListnerRegister fileListnerRegister) {
        this(dirs, DEFAULT_INTERVAL_SECONDS, fileListnerRegister);
    }


    public FileLoader(String dir, FileListnerRegister fileListnerRegister) {
        this(dir, DEFAULT_INTERVAL_SECONDS, fileListnerRegister);
    }

    public FileLoader(String dir, int loaderingIntervalSeconds, FileListnerRegister fileListnerRegister) {
        String[] dirs = new String[]{dir};
        this.dirs = dirs;
        this.loaderingIntervalSeconds = loaderingIntervalSeconds;
        this.fileListnerRegister = fileListnerRegister;
        init(dirs);
    }

    /**
     * 初始化
     */
    public void init(String[] dir) {
        if (Objects.isNull(dir) || dir.length == 0) {
            return;
        }
        for (int i = 0; i < dir.length; i++) {
            List<File> files = FileUtils.getAllFile(dir[i]);
            setLastModifiedAndCurrentFileInfoForCurrentFiles(files);
        }
    }


    private void setLastModifiedAndCurrentFileInfoForCurrentFiles(List<File> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (File file : files) {
            fileLastModified.put(file.getAbsolutePath(), file.lastModified());
            currentFilesInfo.add(file.getAbsolutePath());
        }
    }


    public void stopLoader() {
        running = false;
    }

    public void startLoader() {
        loadThread = new Thread("FileLoaderThread") {
            public void run() {
                while (running) {
                    try {
                        sleep(FileLoader.this.loaderingIntervalSeconds * 1000);
                        loaderFile();
                        notifyLister();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.loadThread.setDaemon(true);
        loadThread.start();
    }


    public void loaderFile() {
        ConcurrentHashSet<String> fileInfo = new ConcurrentHashSet<>();
        for (int i = 0; i < dirs.length; i++) {
            List<File> files = FileUtils.getAllFile(dirs[i]);
            LoadcurrentFilesInfo(files, fileInfo);
        }
        lastFilesInfo = currentFilesInfo;
        currentFilesInfo = fileInfo;
    }


    public void LoadcurrentFilesInfo(List<File> files, ConcurrentHashSet set) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (File file : files) {
            set.add(file.getAbsolutePath());
        }

    }


    public void notifyLister() {
        ConcurrentHashMap<String /*文件的绝对路径*/, List<FileListener>> fileListenersMap = fileListnerRegister.getFileListenerMap();
        Set<String> keysets = fileListenersMap.keySet();
        for (Iterator<String> iterator = keysets.iterator(); iterator.hasNext(); ) {
            String path = iterator.next();
            List<FileListener> listeners = fileListnerRegister.getListnerCopy(path);
            File file = new File(path);
            if (isChange(file)) {
                logger.info("file :{} has changed", file.getAbsolutePath());
                actuator.run(listeners, new Event(file, EventType.CHANGE));
            }
            if (isCreate(file)) {
                logger.info("file :{} has been created", file.getAbsolutePath());
                actuator.run(listeners, new Event(file, EventType.CREATE));
            }
            if (isDelete(file)) {
                logger.info("file :{} has been deleted", file.getAbsolutePath());
                actuator.run(listeners, new Event(file, EventType.DELETE));
            }
        }


    }


    /**
     * 如果该方法返回true 代表该文件
     *
     * @param file
     * @return
     */
    public boolean isChange(File file) {
        String fileAbsolutePath = file.getAbsolutePath();
        if ((fileLastModified.get(fileAbsolutePath) != null) &&
                (file.lastModified() != fileLastModified.get(fileAbsolutePath))) {//文件已改变
            logger.debug("file :{} has been changed", file.getAbsoluteFile());
            fileLastModified.put(fileAbsolutePath, file.lastModified()); //更新文件的本次改变时间
            return true;
        }
        return false;


    }

    /**
     * 该方法会存在一种"幻觉"现象，如果文件A被创建，
     * 然后又被删除，即使文件被创建过，该方法也会返回false
     *
     * @param file
     * @return
     */
    public boolean isCreate(File file) {
        String path = file.getAbsolutePath();
        if (!lastFilesInfo.contains(path) && currentFilesInfo.contains(path) && file.exists()) {
            fileLastModified.putIfAbsent(file.getAbsolutePath(), file.lastModified());//添加该文件本次改变的时间
            return true;
        } else {
            return false;
        }
    }


    /**
     * 该方法会存在一种幻觉现象，如果文件A被删除后，
     * 然后又被创建，即使文件被删除过，该方法也会返回false
     *
     * @param file
     * @return
     */
    public boolean isDelete(File file) {
        String path = file.getAbsolutePath();
        if (!currentFilesInfo.contains(path) && lastFilesInfo.contains(path) && !file.exists()) {
            fileLastModified.remove(file.getAbsolutePath()); //移除文件的改变时间
            return true;
        }
        return false;
    }


    public int getLoaderingIntervalSeconds() {
        return loaderingIntervalSeconds;
    }

    public void setLoaderingIntervalSeconds(int loaderingIntervalSeconds) {
        this.loaderingIntervalSeconds = loaderingIntervalSeconds;
    }

    public static void main(String[] args) {
        File file = new File(System.getProperty("user.home") + File.separator + "exquisitemq_recover");
        System.out.println(FileUtils.getAllFile(file));
    }


}
