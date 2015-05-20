package com.suning.cus;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Http 网络请求测试
 * Created by 14110105 on 2015/4/24.
 */
public class HttpTest {

    public static Test suite() {
        TestSuite suite = new TestSuite("TaskSummaryTest");

        suite.addTestSuite(LoginTestCase.class);
        suite.addTestSuite(TaskSummaryTest.class);

        return suite;
    }

}
