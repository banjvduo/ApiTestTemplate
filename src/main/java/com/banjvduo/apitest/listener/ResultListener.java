package com.banjvduo.apitest.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TestNG 测试结果监听器
 * 自动排除重复的 重跑成功的 等多余的测试结果,保证测试结果是符合直觉的
 * <p>
 * User: stagry@gmail.com
 * Date: 18/4/8
 * Time: 16:05
 * Created by IntelliJ IDEA.
 */
public class ResultListener implements ITestListener {

    public static final Logger logger = LoggerFactory.getLogger(ResultListener.class);

    @Override
    public void onTestStart(ITestResult iTestResult) {
        logger.info(iTestResult.getName() + " Start");
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        logger.info(iTestResult.getName() + " Success");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        logger.info(iTestResult.getName() + " Failure");
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        logger.info(iTestResult.getName() + " Skiped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        logger.info(iTestResult.getName() + " FailedButWithinSuccessPercentage");
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        logger.info(iTestContext.getName() + " Test Start");
    }

    @Override
    public void onFinish(ITestContext iTestContext) {

        //将要移除的测试结果
        List<ITestResult> testsToBeRemoved = new ArrayList<>();

        //构造通过的测试结果
        Set<Integer> passedTestIds = new HashSet<>();
        for (ITestResult passedTest : iTestContext.getPassedTests().getAllResults()) {
            logger.info("PassedTest: " + passedTest.getName());
            passedTestIds.add(getId(passedTest));
        }

        //构造跳过的测试结果
        Set<Integer> skippedTestIds = new HashSet<>();
        for (ITestResult skippedTest : iTestContext.getSkippedTests().getAllResults()) {
            logger.info("SkippedTest: " + skippedTest.getName());
            int skippedTestId = getId(skippedTest);
            //排除重复的跳过的测试结果 和 已成功(包含重跑后)的测试结果
            if (skippedTestIds.contains(skippedTestId) || passedTestIds.contains(skippedTestId)) {
                testsToBeRemoved.add(skippedTest);
            } else {
                skippedTestIds.add(skippedTestId);
            }
        }

        //构造失败的结果
        Set<Integer> failedTestIds = new HashSet<>();
        for (ITestResult failedTest : iTestContext.getFailedTests().getAllResults()) {
            logger.info("FailedTest: " + failedTest.getName());
            int failedTestId = getId(failedTest);
            //排除重复的失败测试的结果 已成功(包含重跑后成功)的测试结果 和 跳过的测试结果
            if (failedTestIds.contains(failedTestId)
                    || passedTestIds.contains(failedTestId)
                    || skippedTestIds.contains(failedTestId)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedTestIds.add(failedTestId);
            }
        }

        Iterator<ITestResult> iterator = iTestContext.getFailedTests().getAllResults().iterator();
        while (iterator.hasNext()) {
            ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
                logger.info("Remove redundant Test: " + testResult.getName());
                iterator.remove();
            }
        }
    }

    /**
     * id = class + method + dataProvider
     *
     * @param result
     * @return
     */
    private int getId(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id += result.getMethod().getMethodName().hashCode();
        id += result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0;
        return id;
    }
}
