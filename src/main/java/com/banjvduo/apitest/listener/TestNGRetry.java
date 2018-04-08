package com.banjvduo.apitest.listener;

import com.banjvduo.apitest.common.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG Retry 配置
 * 支持数据驱动测试 DataProvider 的重跑
 * <p>
 * User: stagry@gmail.com
 * Date: 18/4/8
 * Time: 15:48
 * Created by IntelliJ IDEA.
 */
public class TestNGRetry implements IRetryAnalyzer {

    private int retryCount = 0;

    private static final int MAX_RETRY_COUNT = ResourceUtil.getMaxRetryCount();

    private static final Logger logger = LoggerFactory.getLogger(TestNGRetry.class);

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            logger.info("Retrying test " + result.getName() + " with status "
                    + getResultStatusName(result.getStatus()) + " for the " + (retryCount + 1) + " time(s).");
            retryCount++;
            return true;
        }
        // 支持 TestNG DataProvider Retry
        retryCount = 0;
        return false;
    }

    private String getResultStatusName(int status) {
        String statusName;
        switch (status) {
            case 1:
                statusName = "SUCCESS";
                break;
            case 2:
                statusName = "FAILURE";
                break;
            case 3:
                statusName = "SKIP";
                break;
            default:
                statusName = "";
        }
        return statusName;
    }
}
