package uk.co.automatictester.lightning.params;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import uk.co.automatictester.lightning.params.validators.BooleanValidator;
import uk.co.automatictester.lightning.params.validators.CIServerValidator;
import uk.co.automatictester.lightning.params.validators.FileValidator;

@Parameters(separators = "=")
public class CmdLineParams {

    @Parameter(names = "-xml", description = "Lightning XML config file", required = true, validateWith = FileValidator.class)
    private String xmlFile;

    @Parameter(names = "-csv", description = "JMeter CSV result file", required = true, validateWith = FileValidator.class)
    private String csvFile;

    @Parameter(names = "-skipSchemaValidation", description = "Skip XML schema validation", required = false, validateWith = BooleanValidator.class)
    private String skipSchemaValidation;

    @Parameter(names = "-ci", description = "CI server (jenkins or teamcity)", required = false, validateWith = CIServerValidator.class)
    private String ci;

    @Parameter(names = {"-h", "--help"}, help = true, hidden = true)
    private boolean help;

    public String getXmlFile() {
        return xmlFile;
    }

    public String getCSVFile() {
        return csvFile;
    }

    public boolean skipSchemaValidation() {
        return Boolean.parseBoolean(skipSchemaValidation);
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isCIEqualToTeamCity() {
        if (ci == null) {
            return false;
        } else {
            return (ci.toLowerCase().equals("teamcity"));
        }
    }

    public void setCI(String ci) {
        this.ci = ci;
    }

}
