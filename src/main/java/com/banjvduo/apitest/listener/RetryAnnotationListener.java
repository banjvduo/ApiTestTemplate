package com.banjvduo.apitest.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 重跑加载监听器
 * <p>
 * User: stagry@gmail.com
 * Date: 18/4/8
 * Time: 15:59
 * Created by IntelliJ IDEA.
 */
public class RetryAnnotationListener implements IAnnotationTransformer {

    private static final Logger logger = LoggerFactory.getLogger(RetryAnnotationListener.class);

    @Override
    public void transform(ITestAnnotation iTestAnnotation, Class aClass, Constructor constructor, Method method) {
        IRetryAnalyzer retryAnalyzer = iTestAnnotation.getRetryAnalyzer();
        if (retryAnalyzer == null) {
            logger.info("Set RetryAnalyzer : TestNGRetry");
            iTestAnnotation.setRetryAnalyzer(TestNGRetry.class);
        }
    }
}
