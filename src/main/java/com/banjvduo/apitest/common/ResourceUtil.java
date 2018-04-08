package com.banjvduo.apitest.common;

import java.util.ResourceBundle;

/**
 * 配置文件内容读取
 *
 * User: stagry@gmail.com
 * Date: 18/4/8
 * Time: 15:41
 * Created by IntelliJ IDEA.
 */
public class ResourceUtil {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("config");

    private ResourceUtil() {
    }

    public static String getAddress() {
        String address = BUNDLE.getString("address");
        if ("".equals(address)) {
            throw new RuntimeException("please set up address");
        }
        return address;
    }

    public static int getMaxRetryCount() {
        return Integer.parseInt(BUNDLE.getString("maxRetryCount"));
    }
}
