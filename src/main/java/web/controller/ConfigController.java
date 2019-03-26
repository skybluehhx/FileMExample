package web.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import web.utils.IniUtil;

/**
 * @author jianglinzou
 * @date 2019/3/22 下午10:11
 */
@Controller
public class ConfigController {


    /**
     * 注意观察：
     * 第一次请求test后，修改配置文件值
     * 10s过后，再次请求，将会看到打印
     * 出最新值
     */
    @RequestMapping("/test")
    @ResponseBody
    public void test() {
        System.out.println("---------" + IniUtil.getIniValue("jdbc") + "-------");

    }

}
