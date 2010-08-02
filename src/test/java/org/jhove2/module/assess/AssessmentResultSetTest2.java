package org.jhove2.module.assess;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.jhove2.core.JHOVE2Exception;
import org.jhove2.module.format.Validator.Validity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**/mock-module-ruleset-2-config.xml",
        "classpath*:**/mock-module-object-config.xml"})
public class AssessmentResultSetTest2 {
    private static String name = "MockRuleSet2";
    private static String description  = "RuleSet2 for testing Mock Module";
    private static String objectFilter  = "org.jhove2.module.assess.MockModule";
   
    /* The AssessmentResultSet whose  being examined */
    private AssessmentResultSet resultSet = new AssessmentResultSet();

    /* Construct a RuleSet object using Spring */
    @Resource(name = "MockModuleRuleSet2")
    public void setRuleSet(RuleSet ruleSet)  {
        resultSet.setRuleSet(ruleSet);
    }
   
    /* Construct a Module object using Spring */
    @Resource(name = "MockModule")
    public void setAssessedObject(MockModule assessedObject)  {
        resultSet.setAssessedObject(assessedObject);
    }

    @Test
    public void testGetRuleSetName() {
        assertEquals(name, resultSet.getRuleSetName());
    }

    @Test
    public void testGetDescription() {
        assertEquals(description, resultSet.getRuleSetDescription());
    }

    @Test
    public void testGetObjectFilter() {
        assertEquals(objectFilter, resultSet.getObjectFilter());
    }

    @Test
    public void testGetResultSet() {
        List<AssessmentResult> results = resultSet.getAssessmentResults();
        assertTrue(results.size() == 2);
    }

    @Test
    public void testGetAssessedObject() {
        Object assessedObject = resultSet.getAssessedObject();
        assertTrue(assessedObject instanceof MockModule);
    }
    
    @Test
    public void testFireAllRules() {
        try {
            resultSet.fireAllRules();
            assertEquals("MockModuleRule20",resultSet.assessmentResults.get(0).getRule().getName());
            assertEquals(Validity.True,resultSet.assessmentResults.get(0).getBooleanResult());
            assertEquals("MockModuleRule22",resultSet.assessmentResults.get(1).getRule().getName());
            assertEquals(Validity.True,resultSet.assessmentResults.get(1).getBooleanResult());
            /*
            assertEquals(Validity.True,resultSet.assessmentResults.get(2).getBooleanResult());
            assertEquals(Validity.True,resultSet.assessmentResults.get(3).getBooleanResult());
            assertEquals(Validity.True,resultSet.assessmentResults.get(4).getBooleanResult());
            assertEquals(Validity.True,resultSet.assessmentResults.get(5).getBooleanResult());
            assertEquals(Validity.True,resultSet.assessmentResults.get(6).getBooleanResult());        
            assertEquals(Validity.True,resultSet.assessmentResults.get(7).getBooleanResult());        
            assertEquals(Validity.Undetermined,resultSet.assessmentResults.get(8).getBooleanResult());        
            assertEquals(Validity.Undetermined,resultSet.getBooleanResult());
            assertTrue(Validity.Undetermined.toString().equals(resultSet.getNarrativeResult()));
            */
        }
        catch (JHOVE2Exception e) {
            fail(e.getMessage());
            // e.printStackTrace();
        }
    }
}
