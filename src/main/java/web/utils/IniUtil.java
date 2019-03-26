package web.utils;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.service.IniService;

/**
 * @author jianglinzou
 * @date 2019/3/22 下午10:20
 */
public class IniUtil {


    private static IniService iniService = (IniService) SpringBeanUtils.getBean(IniService.class);

    private static Logger logger = LoggerFactory.getLogger(IniUtil.class);


    static {

        //在系统刚启动，容器未初始化完成的时候，有可能拿到空的bean。但一般来讲，我们在业务层面使用该类，在该类加载时spring已经初始化完成，否则业务上的各种bean都会有问题。
        //严谨起见，若拿不到实例，延时1秒后在去获取。
        //if代码块执行的概率极低，若不是spring基础配置出现问题，一般不会执行。
        if (iniService == null) {
            logger.error("暂未拿到iniService实例，延时1后再次尝试");
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
            iniService = (IniService) SpringBeanUtils.getBean(IniService.class);
        }
        init();

    }


    public static int init() {
        if (iniService != null) {
            return iniService.refresh();
        } else {
            logger.error("初始化配置失败，未拿到iniService实例！");
            return 0;
        }
    }


    /**
     * 获取配置项的值
     *
     * @param name 配置项名
     * @return 值
     */
    public static String getIniValue(String name) {

        if (iniService == null) {
            iniService = (IniService) SpringBeanUtils.getBean(IniService.class);
        }

        if (iniService != null) {
            return iniService.getIniValue(name);
        } else {
            logger.error("初始化配置失败，未拿到iniService实例！");
            return null;
        }

    }


    /**
     * 获取配置项的值
     *
     * @param name        配置项名
     * @param defultValue 如果配置项为空，返回传人的默认值
     * @return 值
     */
    public static String getIniStringValue(String name, String defultValue) {

        String value = getIniValue(name);
        if (value == null) {
            return defultValue;
        }
        return value;

    }

    /**
     * 获取配置项的值
     *
     * @param name        配置项名
     * @param defultValue 如果配置项为空，返回传人的默认值
     * @return 值
     */
    public static int getIniIntValue(String name, int defultValue) {
        int ret;
        String value = getIniValue(name);
        if (value == null) {
            return defultValue;
        }

        try {
            ret = Integer.parseInt(value.trim());
        } catch (Exception ex) {
            logger.error("配置项" + name + "：" + value + "转成整型异常");
            return defultValue;
        }
        return ret;

    }

}



