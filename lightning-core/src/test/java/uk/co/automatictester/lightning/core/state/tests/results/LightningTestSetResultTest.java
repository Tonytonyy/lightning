package uk.co.automatictester.lightning.core.state.tests.results;

import org.testng.annotations.Test;
import uk.co.automatictester.lightning.core.AbstractConsoleOutputTest;
import uk.co.automatictester.lightning.core.state.data.JmeterTransactions;
import uk.co.automatictester.lightning.core.state.data.PerfMonEntries;
import uk.co.automatictester.lightning.core.state.data.TestData;
import uk.co.automatictester.lightning.core.state.tests.LightningTestSet;
import uk.co.automatictester.lightning.core.tests.PassedTransactionsAbsoluteTest;
import uk.co.automatictester.lightning.core.tests.RespTimeAvgTest;
import uk.co.automatictester.lightning.core.tests.ServerSideGreaterThanTest;
import uk.co.automatictester.lightning.core.tests.ServerSideLessThanTest;
import uk.co.automatictester.lightning.core.tests.base.AbstractTest;
import uk.co.automatictester.lightning.shared.LegacyTestData;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class LightningTestSetResultTest extends AbstractConsoleOutputTest {

    @Test
    public void verifyExecuteServerMethod_2_1_1() {
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestA = new PassedTransactionsAbsoluteTest.Builder("Test #1", 0).withDescription("Verify number of passed tests").withTransactionName("Login").build();
        List<AbstractTest> tests = new ArrayList<>();
        tests.add(passedTransactionsAbsoluteTestA);

        List<String[]> clientSideTestData = new ArrayList<>();
        clientSideTestData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        clientSideTestData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JmeterTransactions jmeterTransactions = JmeterTransactions.fromList(clientSideTestData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        ServerSideLessThanTest testA = new ServerSideLessThanTest.Builder("Test #1", 10001).withDescription("Verify CPU utilisation").withHostAndMetric("192.168.0.12 CPU").build();
        ServerSideGreaterThanTest testB = new ServerSideGreaterThanTest.Builder("Test #2", 10001).withDescription("Verify CPU utilisation").withHostAndMetric("192.168.0.12 CPU").build();
        ServerSideGreaterThanTest testC = new ServerSideGreaterThanTest.Builder("Test #3", 10001).withDescription("Verify CPU utilisation").withHostAndMetric("192.168.0.240 CPU").build();

        List<String[]> serverSideTestData = new ArrayList<>();
        serverSideTestData.add(LegacyTestData.CPU_ENTRY_10000);
        serverSideTestData.add(LegacyTestData.CPU_ENTRY_10001);
        PerfMonEntries dataEntries = PerfMonEntries.fromList(serverSideTestData);
        TestData.getInstance().addServerSideTestData(dataEntries);

        tests.add(testA);
        tests.add(testB);
        tests.add(testC);

        LightningTestSetResult testSetResult = new LightningTestSetResult();
        LightningTestSet testSet = new LightningTestSet();
        testSet.addAll(tests);
        configureStream();
        testSetResult.executeTests(testSet);
        revertStream();

        assertThat(testSetResult.testCount(), is(4));
        assertThat(testSetResult.passCount(), is(2));
        assertThat(testSetResult.failCount(), is(1));
        assertThat(testSetResult.errorCount(), is(1));
    }

    @Test
    public void verifyExecuteClientMethod_2_0_0() {
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestA = new PassedTransactionsAbsoluteTest.Builder("Test #1", 0).withDescription("Verify number of passed tests").withTransactionName("Login").build();
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestB = new PassedTransactionsAbsoluteTest.Builder("Test #2", 0).withDescription("Verify number of passed tests").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JmeterTransactions jmeterTransactions = JmeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<AbstractTest> tests = new ArrayList<>();
        tests.add(passedTransactionsAbsoluteTestA);
        tests.add(passedTransactionsAbsoluteTestB);

        LightningTestSetResult testSetResult = new LightningTestSetResult();
        LightningTestSet testSet = new LightningTestSet();
        testSet.addAll(tests);
        configureStream();
        testSetResult.executeTests(testSet);
        revertStream();

        assertThat(testSetResult.testCount(), is(2));
        assertThat(testSetResult.passCount(), is(2));
        assertThat(testSetResult.failCount(), is(0));
        assertThat(testSetResult.errorCount(), is(0));
        assertThat(testSetResult.toString(), is(equalTo("Tests: 2, passed: 2, failed: 0, ignored: 0")));
    }

    @Test
    public void verifyExecuteClientMethod_1_1_1() {
        RespTimeAvgTest respTimeAvgTestA = new RespTimeAvgTest.Builder("Test #1", 4000).withTransactionName("Login").build();
        RespTimeAvgTest respTimeAvgTestB = new RespTimeAvgTest.Builder("Test #2", 5000).withTransactionName("Search").build();
        RespTimeAvgTest respTimeAvgTestC = new RespTimeAvgTest.Builder("Test #3", 1000).withTransactionName("Sear").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JmeterTransactions jmeterTransactions = JmeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<AbstractTest> tests = new ArrayList<>();
        tests.add(respTimeAvgTestA);
        tests.add(respTimeAvgTestB);
        tests.add(respTimeAvgTestC);

        LightningTestSetResult testSetResult = new LightningTestSetResult();
        LightningTestSet testSet = new LightningTestSet();
        testSet.addAll(tests);
        configureStream();
        testSetResult.executeTests(testSet);
        revertStream();

        assertThat(testSetResult.testCount(), is(3));
        assertThat(testSetResult.passCount(), is(1));
        assertThat(testSetResult.failCount(), is(1));
        assertThat(testSetResult.errorCount(), is(1));
        assertThat(testSetResult.toString(), is(equalTo("Tests: 3, passed: 1, failed: 1, ignored: 1")));
    }

    @Test
    public void testPrintTestSetExecutionSummaryReportForPass() {
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestA = new PassedTransactionsAbsoluteTest.Builder("Test #1", 0).withDescription("Verify number of passed tests").withTransactionName("Login").build();
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestB = new PassedTransactionsAbsoluteTest.Builder("Test #2", 0).withDescription("Verify number of passed tests").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JmeterTransactions jmeterTransactions = JmeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<AbstractTest> tests = new ArrayList<>();
        tests.add(passedTransactionsAbsoluteTestA);
        tests.add(passedTransactionsAbsoluteTestB);

        LightningTestSetResult testSetResult = new LightningTestSetResult();
        LightningTestSet testSet = new LightningTestSet();
        testSet.addAll(tests);
        configureStream();
        testSetResult.executeTests(testSet);
        revertStream();

        String expectedResult = String.format("============= EXECUTION SUMMARY =============%n" +
                "Tests executed:    2%n" +
                "Tests passed:      2%n" +
                "Tests failed:      0%n" +
                "Tests errors:      0%n" +
                "Test set status:   Pass");

        String output = testSetResult.testSetExecutionSummaryReport();
        assertThat(output, containsString(expectedResult));
    }

    @Test
    public void testPrintTestSetExecutionSummaryReportForFail() {
        RespTimeAvgTest respTimeAvgTestA = new RespTimeAvgTest.Builder("Test #1", 4000).withTransactionName("Login").build();
        RespTimeAvgTest respTimeAvgTestB = new RespTimeAvgTest.Builder("Test #2", 5000).withTransactionName("Search").build();
        RespTimeAvgTest respTimeAvgTestC = new RespTimeAvgTest.Builder("Test #3", 1000).withTransactionName("Search").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JmeterTransactions jmeterTransactions = JmeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<AbstractTest> tests = new ArrayList<>();
        tests.add(respTimeAvgTestA);
        tests.add(respTimeAvgTestB);
        tests.add(respTimeAvgTestC);

        LightningTestSetResult testSetResult = new LightningTestSetResult();
        LightningTestSet testSet = new LightningTestSet();
        testSet.addAll(tests);
        configureStream();
        testSetResult.executeTests(testSet);
        revertStream();

        String expectedResult = String.format("============= EXECUTION SUMMARY =============%n" +
                "Tests executed:    3%n" +
                "Tests passed:      1%n" +
                "Tests failed:      2%n" +
                "Tests errors:      0%n" +
                "Test set status:   FAIL");

        String output = testSetResult.testSetExecutionSummaryReport();
        assertThat(output, containsString(expectedResult));
    }

    @Test
    public void testPrintTestSetExecutionSummaryReportForIgnore() {
        RespTimeAvgTest respTimeAvgTestA = new RespTimeAvgTest.Builder("Test #1", 4000).withTransactionName("Login").build();
        RespTimeAvgTest respTimeAvgTestC = new RespTimeAvgTest.Builder("Test #3", 1000).withTransactionName("Sear").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JmeterTransactions jmeterTransactions = JmeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<AbstractTest> tests = new ArrayList<>();
        tests.add(respTimeAvgTestA);
        tests.add(respTimeAvgTestC);

        LightningTestSetResult testSetResult = new LightningTestSetResult();
        LightningTestSet testSet = new LightningTestSet();
        testSet.addAll(tests);
        configureStream();
        testSetResult.executeTests(testSet);
        revertStream();

        String expectedResult = String.format("============= EXECUTION SUMMARY =============%n" +
                "Tests executed:    2%n" +
                "Tests passed:      1%n" +
                "Tests failed:      0%n" +
                "Tests errors:      1%n" +
                "Test set status:   FAIL");

        String output = testSetResult.testSetExecutionSummaryReport();
        assertThat(output, containsString(expectedResult));
    }
}