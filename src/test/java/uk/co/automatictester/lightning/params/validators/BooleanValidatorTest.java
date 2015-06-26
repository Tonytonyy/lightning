package uk.co.automatictester.lightning.params.validators;

import com.beust.jcommander.ParameterException;
import org.testng.annotations.Test;

public class BooleanValidatorTest {

    private static final String SKIP_SCHEMA_VALIDATION = "-skipSchemaValidation";

    @Test
    public void testValidateTrue() {
        new BooleanValidator().validate(SKIP_SCHEMA_VALIDATION, "true");
    }

    @Test
    public void testValidateFalse() {
        new BooleanValidator().validate(SKIP_SCHEMA_VALIDATION, "false");
    }

    @Test(expectedExceptions = ParameterException.class, expectedExceptionsMessageRegExp = "Parameter '" + SKIP_SCHEMA_VALIDATION + "' should be boolean \\(true or false\\)")
    public void testValidateOther() {
        new BooleanValidator().validate(SKIP_SCHEMA_VALIDATION, "True");
    }
}