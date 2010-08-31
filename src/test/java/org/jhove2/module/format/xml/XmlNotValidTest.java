package org.jhove2.module.format.xml;

import static org.junit.Assert.assertEquals;

import org.jhove2.module.format.Validator.Validity;
import org.junit.Before;
import org.junit.Test;

public class XmlNotValidTest extends XmlModuleTestBase {

    @Before 
    public void initialize() {
        if (! initialized) {
            parse("w3cTestSuite/xmlconf/sun/invalid/dtd03.xml");
        }
    }

    /**
     * Test method for Validation Information
     */   
    @Test
    public void testValidationInfo() {
        ValidationResults vr = testXmlModule.getValidationResults();
        ValidationMessageList warnings = vr.getParserWarnings();
        assertEquals(0, warnings.getValidationMessageCount());
        ValidationMessageList errors = vr.getParserErrors();
        assertEquals(0, errors.getValidationMessageCount());
        ValidationMessageList fatals = vr.getFatalParserErrors();
        assertEquals(1, fatals.getValidationMessageCount());
        String msg = fatals.getValidationMessages().get(0).getMessage();
        assertEquals("A pseudo attribute name is expected. ", msg);
    }

    /**
     * Test method for Validity
     */   
    @Test
    public void testValidatity() {
        assertEquals(Validity.False,  testXmlModule.isWellFormed());
        assertEquals(Validity.False, testXmlModule.isValid());
    }

}
