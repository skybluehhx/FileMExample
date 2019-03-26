package web.service;

import com.lin.FileListener;
import com.lin.FileMonitor;
import com.lin.util.ResourceUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jianglinzou
 * @date 2019/3/22 下午10:17
 */
@Service
public class IniService {


    //本地缓存变量，存放配置表中的所有键值对
    private static final ConcurrentHashMap<String, String> iniMap = new ConcurrentHashMap<String, String>();

    private static final Logger logger = LoggerFactory.getLogger(IniService.class);

    static { //开始监视
        minor();
    }

    public static int refresh() {
        logger.info("start to load config");
        int count = 0;
        try {
            refresh(ResourceUtils.getResourceAsStream("jdbc.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("fail to refresh because of:{}", e);
        }
        logger.info("refresh config count:{}", count);
        return count;
    }


    private static int refresh(InputStream in) {
        logger.info("start to load config");
        int count = 0;
        Properties props = new Properties();
        try {
            props.load(in);
            for (Map.Entry entry : props.entrySet()) {
                iniMap.put(entry.getKey().toString(), entry.getValue().toString());
                count++;
            }

        } catch (IOException e) {
            logger.info("fail to load because :{}", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {

            }
        }
        logger.info("refresh config count:{}", count);
        return count;

    }


    /**
     * @param name  配置项的名，不支持空值
     * @return 配置项的值
     */
    public String getIniValue(String name) {
        if (Strings.isBlank(name)) return null;
        return iniMap.get(name);
    }


    public static void minor() {

        //需要监听的配置文件
        String dir = System.getProperty("user.dir") + "/src/main/resources/jdbc.properties";

        FileMonitor fileMonitor = new FileMonitor(dir, 10);
        fileMonitor.registerFileListener(dir, new FileListener() {
            //当文件改变时 将触发此方法,重新刷新配置
            @Override
            public void onFileChange(File file) {
                try {
                    InputStream inputStream = new FileInputStream(file);
                    refresh(inputStream);
                } catch (Exception e) {
                    logger.info("fail to execute file change because of:{}", e);
                }

            }

            @Override
            public void onFileCreate(File file) {

            }

            @Override
            public void onFileDelete(File file) {

            }

            @Override
            public String name() {
                return "fileListener";
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public boolean accept() {
                return true;
            }
        });
        fileMonitor.start();

    }


}
