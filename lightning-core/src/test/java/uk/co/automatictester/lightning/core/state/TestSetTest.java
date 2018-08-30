package uk.co.automatictester.lightning.core.state;

import org.testng.annotations.Test;
import uk.co.automatictester.lightning.ConsoleOutputTest;
import uk.co.automatictester.lightning.core.data.JMeterTransactions;
import uk.co.automatictester.lightning.core.data.PerfMonEntries;
import uk.co.automatictester.lightning.core.structures.LightningTests;
import uk.co.automatictester.lightning.core.structures.TestData;
import uk.co.automatictester.lightning.core.tests.PassedTransactionsAbsoluteTest;
import uk.co.automatictester.lightning.core.tests.RespTimeAvgTest;
import uk.co.automatictester.lightning.core.tests.ServerSideGreaterThanTest;
import uk.co.automatictester.lightning.core.tests.ServerSideLessThanTest;
import uk.co.automatictester.lightning.core.tests.base.LightningTest;
import uk.co.automatictester.lightning.shared.LegacyTestData;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestSetTest extends ConsoleOutputTest {

    @Test
    public void verifyExecuteServerMethod_2_1_1() {
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestA = new PassedTransactionsAbsoluteTest.Builder("Test #1", 0).withDescription("Verify number of passed tests").withTransactionName("Login").build();
        List<LightningTest> tests = new ArrayList<>();
        tests.add(passedTransactionsAbsoluteTestA);

        List<String[]> clientSideTestData = new ArrayList<>();
        clientSideTestData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        clientSideTestData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JMeterTransactions jmeterTransactions = JMeterTransactions.fromList(clientSideTestData);
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

        TestSet testSet = new TestSet();
        LightningTests lightningTests = LightningTests.getInstance();
        lightningTests.flush();
        lightningTests.addAll(tests);
        configureStream();
        testSet.executeTests();
        revertStream();

        assertThat(testSet.getTestCount(), is(4));
        assertThat(testSet.getPassCount(), is(2));
        assertThat(testSet.getFailCount(), is(1));
        assertThat(testSet.getErrorCount(), is(1));
    }

    @Test
    public void verifyExecuteClientMethod_2_0_0() {
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestA = new PassedTransactionsAbsoluteTest.Builder("Test #1", 0).withDescription("Verify number of passed tests").withTransactionName("Login").build();
        PassedTransactionsAbsoluteTest passedTransactionsAbsoluteTestB = new PassedTransactionsAbsoluteTest.Builder("Test #2", 0).withDescription("Verify number of passed tests").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JMeterTransactions jmeterTransactions = JMeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<LightningTest> tests = new ArrayList<>();
        tests.add(passedTransactionsAbsoluteTestA);
        tests.add(passedTransactionsAbsoluteTestB);

        TestSet testSet = new TestSet();
        LightningTests lightningTests = LightningTests.getInstance();
        lightningTests.flush();
        lightningTests.addAll(tests);
        configureStream();
        testSet.executeTests();
        revertStream();

        assertThat(testSet.getTestCount(), is(2));
        assertThat(testSet.getPassCount(), is(2));
        assertThat(testSet.getFailCount(), is(0));
        assertThat(testSet.getErrorCount(), is(0));
    }

    @Test
    public void verifyExecuteClientMethod_1_1_1() {
        RespTimeAvgTest respTimeAvgTestA = new RespTimeAvgTest.Builder("Test #1", 4000).withTransactionName("Login").build();
        RespTimeAvgTest respTimeAvgTestB = new RespTimeAvgTest.Builder("Test #2", 5000).withTransactionName("Search").build();
        RespTimeAvgTest respTimeAvgTestC = new RespTimeAvgTest.Builder("Test #3", 1000).withTransactionName("Sear").build();

        List<String[]> testData = new ArrayList<>();
        testData.add(LegacyTestData.LOGIN_3514_SUCCESS);
        testData.add(LegacyTestData.SEARCH_11221_SUCCESS);
        JMeterTransactions jmeterTransactions = JMeterTransactions.fromList(testData);
        TestData.getInstance().addClientSideTestData(jmeterTransactions);

        List<LightningTest> tests = new ArrayList<>();
        tests.add(respTimeAvgTestA);
        tests.add(respTimeAvgTestB);
        tests.add(respTimeAvgTestC);

        TestSet testSet = new TestSet();
        LightningTests lightningTests = LightningTests.getInstance();
        lightningTests.flush();
        lightningTests.addAll(tests);
        configureStream();
        testSet.executeTests();
        revertStream();

        assertThat(testSet.getTestCount(), is(3));
        assertThat(testSet.getPassCount(), is(1));
        assertThat(testSet.getFailCount(), is(1));
        assertThat(testSet.getErrorCount(), is(1));
    }
}