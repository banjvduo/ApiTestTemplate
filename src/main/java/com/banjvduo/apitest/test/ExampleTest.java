package com.banjvduo.apitest.test;

import com.banjvduo.apitest.common.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.banjvduo.apitest.common.Constant.ADDRESS;
import static com.banjvduo.apitest.common.Constant.SUCCESS;

/**
 * User: stagry@gmail.com
 * Date: 18/4/9
 * Time: 14:09
 * Created by IntelliJ IDEA.
 */
public class ExampleTest {

    private static final Logger logger = LoggerFactory.getLogger(ExampleTest.class);

    @Test
    public void test() throws IOException {
        logger.info("test方法");
        int code = HttpUtil.getForCode(ADDRESS + "");
        Assert.assertEquals(code,SUCCESS);
    }

}
