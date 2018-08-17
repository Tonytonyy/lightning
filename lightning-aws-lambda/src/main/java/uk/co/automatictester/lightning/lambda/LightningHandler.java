package uk.co.automatictester.lightning.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.automatictester.lightning.core.facade.LightningCoreS3Facade;

public class LightningHandler implements RequestHandler<LightningRequest, LightningResponse> {

    private static final Logger log = LogManager.getLogger(LightningHandler.class);
    private LightningCoreS3Facade core = new LightningCoreS3Facade();

    private String bucket;
    private String region;
    private String mode;
    private String xml;
    private String jmeterCsv;
    private String perfmonCsv;

    private LightningResponse response = new LightningResponse();

    public LightningResponse handleRequest(LightningRequest lightningRequest, Context context) {
        parseRequestParams(lightningRequest);
        core.loadTestDataFromS3();

        if (mode.equals("verify")) {
            runTests();
            String junitReportS3Path = core.saveJunitReportToS3();
            response.setJunitReport(junitReportS3Path);
        } else if (mode.equals("report")) {
            runReport();
        }
        notifyCIServer();

        return response;
    }

    private void parseRequestParams(LightningRequest request) {
        LightningRequestValidator.validate(request);
        log.info(response.getExitCode()); // TODO

        mode = request.getMode();
        bucket = request.getBucket();
        region = request.getRegion();
        xml = request.getXml();
        jmeterCsv = request.getJmeterCsv();
        perfmonCsv = request.getPerfmonCsv();

        core.setRegionAndBucket(region, bucket);
        core.setLightningXml(xml);
        core.setJmeterCsv(jmeterCsv);
        core.setPerfMonCsv(perfmonCsv);
    }

    private void notifyCIServer() {
        if (mode.equals("verify")) {
            String teamCityReport = core.getTeamCityVerifyStatistics();
            log.info(response.getExitCode()); // TODO
            log.info(teamCityReport);
            String teamCityReportS3Path = core.putS3Object("output/teamcity.log", teamCityReport);
            response.setTeamCityReport(teamCityReportS3Path);

            String jenkinsReportS3Path = core.storeJenkinsBuildNameForVerifyInS3();
            response.setJenkinsReport(jenkinsReportS3Path);

        } else if (mode.equals("report")) {
            String teamCityBuildStatusText = core.getTeamCityBuildReportSummary();
            String teamCityReportStatistics = core.getTeamCityReportStatistics();
            String combinedTeamCityReport = String.format("\n%s\n%s", teamCityBuildStatusText, teamCityReportStatistics);
            log.info(response.getExitCode()); // TODO
            log.info(combinedTeamCityReport);
            String combinedTeamCityReportS3Path = core.putS3Object("output/teamcity.log", combinedTeamCityReport);
            response.setTeamCityReport(combinedTeamCityReportS3Path);

            String jenkinsReportS3Path = core.storeJenkinsBuildNameForReportInS3();
            response.setJenkinsReport(jenkinsReportS3Path);
        }
    }

    private void runTests() {
        long testSetExecStart = System.currentTimeMillis();

        core.setLightningXml(xml);
        core.loadConfigFromS3();

        String testExecutionReport = core.executeTests();
        String testSetExecutionSummaryReport = core.getTestSetExecutionSummaryReport();

        String combinedTestReport = String.format("\n%s%s\n", testExecutionReport, testSetExecutionSummaryReport);
        log.info(response.getExitCode()); // TODO
        log.info(combinedTestReport);
        String combinedTestReportS3Path = core.putS3Object("output/verify.log", combinedTestReport);
        response.setCombinedTestReport(combinedTestReportS3Path);

        long testSetExecEnd = System.currentTimeMillis();
        long testExecTime = testSetExecEnd - testSetExecStart;
        String message = String.format("Execution time:    %dms", testExecTime);
        log.info(response.getExitCode()); // TODO
        log.info(message);

        if (core.hasExecutionFailed()) {
            response.setExitCode(1);
        }
    }

    private void runReport() {
        String report = core.runReport();
        log.info(response.getExitCode()); // TODO
        log.info(report);

        String jmeterReportS3Path = core.putS3Object("output/report.log", report);
        response.setJmeterReport(jmeterReportS3Path);

        if (core.hasFailedTransactions()) {
            response.setExitCode(1);
        }
    }
}
