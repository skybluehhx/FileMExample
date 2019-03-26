package com.lin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jianglinzou
 * @date 2019/3/21 下午3:35
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static List<File> getAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return getAllFile(file);
    }

    /**
     * 返回指定下的所有的文件，和目录
     *
     * @param file
     * @return
     */
    public static List<File> getAllFile(File file) {
        List<File> list = new ArrayList<>();
        if (file.isDirectory()) {
            list.add(file);
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (int i = 0; i < files.length; i++) {
                    list.addAll(getAllFile(files[i]));
                }
            }
        } else {
            if (file.exists()) {
                list.add(file);
            }
        }
        return list;
    }


    /**
     * 返回指定目录下文件名(相对路径名file.getName)所有符合"regex"条件的文件
     *
     * @param file
     * @return
     */
    public static List<File> getAllFile(File file, String regex) {
        List<File> list = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File target) {
                    if (regex == null || regex.trim() == null) {
                        return true;
                    }
                    Matcher matcher = Pattern.compile(regex).matcher(target.getName());
                    if ((target.isFile() && matcher.find()) || target.isDirectory()) {
                        return true;
                    }
                    return false;
                }
            });
            if (files != null && files.length != 0) {
                for (int i = 0; i < files.length; i++) {
                    list.addAll(getAllFile(files[i], regex));
                }
            }
        } else {
            if (file.exists()) {
                list.add(file);
            }
        }
        return list;
    }


    public static File CreateDir(String path) {
        final File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }


    public static File createFile(File parent, String fileName) throws IOException {
        if (!parent.exists()) {
            parent.createNewFile();
        }
        final File file = new File(parent, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    public static File createFile(String parentPath, String fileName) throws IOException {
        File parent = new File(parentPath);
        if (!parent.exists()) {
            parent.mkdir();
        }
        final File file = new File(parent, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    public static boolean updateFileContent(File file, String content) {

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(content);
            pw.flush();
            return true;
        } catch (FileNotFoundException e) {
            logger.error("fail to update file content because of :{}", e);
            return false;
        } catch (IOException e) {
            logger.error("fail to update file content because of :{}", e);
            return false;
        } finally {
            close(fos, pw);
        }
    }

    public static void appendFileContent(File file, String content) {
        String oldContent = getFileContent(file);
        updateFileContent(file, oldContent + content);
    }

    public static String getFileContent(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        String lineContent = "";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            for (int i = 0; (lineContent = br.readLine()) != null; i++) {
                contentBuilder.append(lineContent).append(System.getProperty("line.separator"));
            }
            if (contentBuilder.length() > 1) {
                //出去最后一行的空格
                return contentBuilder.substring(0, contentBuilder.length() - 1);
            } else {
                return contentBuilder.toString();
            }
        } catch (Exception e) {
            logger.error("fail to get file content because of :{}", e);
            return null;
        } finally {
            close(fis, isr, br);
        }
    }

    public static void close(FileOutputStream fos, PrintWriter pw) {
        try {
            if (fos != null) {
                fos.close();
            }
            if (pw != null) {
                pw.close();
            }
        } catch (Exception e) {
            logger.warn("fail to colse file because of :{}", e);
        }

    }

    public static void close(FileInputStream fis, InputStreamReader isr, BufferedReader br) {

        try {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e) {
            logger.warn("fail to close file because of :{}", e);
        }

    }


}
