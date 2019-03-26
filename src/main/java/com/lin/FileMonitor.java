package com.lin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件监视器
 *
 * @author jianglinzou
 * @date 2019/3/21 下午4:00
 */
public class FileMonitor {

    private static Logger logger = LoggerFactory.getLogger(FileMonitor.class);

    private final FileListnerRegister fileListnerRegister;
    private final FileLoader fileLoader;

    private final String[] dirs;


    public FileMonitor(String[] dirs) {
        this.fileListnerRegister = new FileListnerRegister();
        this.dirs = dirs;
        fileLoader = new FileLoader(dirs, fileListnerRegister);
    }

    public FileMonitor(String[] dirs, int loaderingIntervalSeconds) {
        this.fileListnerRegister = new FileListnerRegister();
        this.dirs = dirs;
        fileLoader = new FileLoader(dirs, fileListnerRegister);

    }


    public FileMonitor(String dir) {
        this.fileListnerRegister = new FileListnerRegister();
        String[] dirs = new String[]{dir};
        this.dirs = dirs;
        fileLoader = new FileLoader(dirs, fileListnerRegister);
    }

    public FileMonitor(String dir, int loaderingIntervalSeconds) {
        this.fileListnerRegister = new FileListnerRegister();
        String[] dirs = new String[]{dir};
        this.dirs = dirs;
        fileLoader = new FileLoader(dirs, loaderingIntervalSeconds, fileListnerRegister);
    }


    public void registerFileListener(String path, FileListener fileListener) {
        logger.info("register for path:{} FileListener:{}", path, fileListener.name());
        fileListnerRegister.register(path, fileListener);
    }

    public void start() {
        fileLoader.startLoader();
    }


    public void stop() {
        fileLoader.stopLoader();
    }

    public int getLoaderingIntervalSeconds() {
        return this.fileLoader.getLoaderingIntervalSeconds();
    }

    public void setLoaderingIntervalSeconds(int loaderingIntervalSeconds) {
        this.fileLoader.setLoaderingIntervalSeconds(loaderingIntervalSeconds);
    }
}
