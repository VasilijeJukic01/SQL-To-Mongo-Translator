import bp.parser.Query;
import bp.parser.clauses.Clause;
import bp.parser.SQLParser;
import bp.validator.SQLValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    private SQLValidator sqlValidator;
    private SQLParser sqlParser;

    @BeforeEach
    public void setup() {
        sqlValidator = new SQLValidator();
        sqlParser = new SQLParser();
    }

    @ParameterizedTest
    @MethodSource("provideQueriesForValidation")
    public void testValidate(String sqlQuery, boolean expectedResult) {
        // Arrange
        Query<Clause> query = sqlParser.parse(sqlQuery);

        // Act
        boolean result = sqlValidator.validate(query);

        // Assert
        assertEquals(expectedResult, result);
    }

    // First 4 queries need to fail
    private static Stream<Arguments> provideQueriesForValidation() {
        return Stream.of(
                Arguments.of("SELECT street_address, postal_code, city, country_id FROM locations WHERE country_id LIKE 'US'", true),
                Arguments.of("SELECT last_name, first_name, salary FROM employees WHERE salary > 5000 AND salary < 10000 OR salary = 24000 OR salary BETWEEN 16000 AND 18000", true),
                Arguments.of("SELECT last_name, phone_number, email FROM employees JOIN departments USING department_id WHERE department_id IN [60,70,80,90]", true),
                Arguments.of("SELECT city, COUNT(employee_id), AVG(salary) FROM employees JOIN departments USING department_id JOIN locations USING location_id GROUP BY city HAVING AVG(salary) > 5000", true),
                Arguments.of("", false),
                Arguments.of("Random string", false),
                Arguments.of("Select select select", false)
        );
    }
}