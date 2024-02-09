import bp.parser.Parser;
import bp.parser.Query;
import bp.parser.SQLParser;
import bp.parser.clauses.Clause;
import bp.validator.rules.GroupRule;
import bp.validator.rules.MandatoryRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidationRulesTest {

    private Parser<Query<Clause>> parser;
    private MandatoryRule mandatoryRule;
    private GroupRule groupRule;

    @BeforeEach
    public void setup() {
        parser = new SQLParser();
        mandatoryRule = new MandatoryRule("Mandatory");
        groupRule = new GroupRule("Group");
    }

    @Test
    public void mandatoryRule_nullQuery() {
        // Arrange
        Query<Clause> query = null;

        // Act
        boolean result = mandatoryRule.check(query);

        // Assert
        Assertions.assertFalse(result);
        Assertions.assertEquals("Query is empty", mandatoryRule.getMessage());
    }

    @Test
    public void mandatoryRule_emptyQuery() {
        // Arrange
        Query<Clause> query = parser.parse("country_id");

        // Act
        boolean result = mandatoryRule.check(query);

        // Assert
        Assertions.assertFalse(result);
        Assertions.assertEquals("Query is empty", mandatoryRule.getMessage());
    }

    @Test
    public void mandatoryRule_oneFalseQuery() {
        // Arrange
        Query<Clause> query = parser.parse("SELECT street_address, postal_code, city, country_id");

        // Act
        boolean result = mandatoryRule.check(query);

        // Assert
        Assertions.assertFalse(result);
        Assertions.assertEquals("FROM doesn't exists in query!", mandatoryRule.getMessage());
    }

    @Test
    public void mandatoryRule_bothFalseQuery() {
        // Arrange
        Query<Clause> query = parser.parse("WHERE locations LIKE 'US'");

        // Act
        boolean result = mandatoryRule.check(query);

        // Assert
        Assertions.assertFalse(result);
        Assertions.assertEquals("SELECT and FROM don't exist in query!", mandatoryRule.getMessage());
    }

    @Test
    public void mandatoryRule_bothTrueQuery() {
        // Arrange
        Query<Clause> query = parser.parse("SELECT street_address, postal_code, city, country_id FROM locations");

        // Act
        boolean result = mandatoryRule.check(query);

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertEquals("", mandatoryRule.getMessage());
    }

    @Test
    public void groupRule_nullQuery() {
        // Arrange
        Query<Clause> query = null;

        // Act
        boolean result = groupRule.check(query);

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertEquals("Query is empty", groupRule.getMessage());
    }

    @Test
    public void groupRule_emptyQuery() {
        // Arrange
        Query<Clause> query = parser.parse("country_id");

        // Act
        boolean result = groupRule.check(query);

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertEquals("Query is empty", groupRule.getMessage());
    }

    @Test
    public void groupRule_trueQuery() {
        // Arrange
        Query<Clause> query = parser.parse("SELECT student_id, AVG(salary) FROM students GROUP BY student_id");

        // Act
        boolean result = groupRule.check(query);

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertEquals("", groupRule.getMessage());
    }

}
