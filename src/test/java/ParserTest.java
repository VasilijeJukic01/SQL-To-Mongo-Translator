import bp.parser.Parser;
import bp.parser.Query;
import bp.parser.SQLParser;
import bp.parser.clauses.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParserTest {

    // Interface Test
    @Test
    public void interface_parser_test() {
        // Act
        Parser<Integer> parser = Integer::parseInt;

        // Assert
        Assertions.assertEquals(Integer.valueOf(42), parser.parse("42"));
        Assertions.assertThrows(NumberFormatException.class, () -> parser.parse("NotNumber"));
    }

    // SQLParser Test
    @ParameterizedTest
    @MethodSource("sqlValidQueries")
    public void sql_parser_valid_test(String sqlQuery, Class<?>[] expectedClauses) {
        // Arrange
        Parser<Query<Clause>> parser = new SQLParser();

        // Act
        Query<Clause> query = parser.parse(sqlQuery);

        // Assert
        IntStream.range(0, expectedClauses.length)
                .forEach(i -> Assertions.assertTrue(expectedClauses[i].isInstance(query.getClauses().get(i))));

        Assertions.assertEquals(expectedClauses.length, query.getClauses().size());
    }

    public static Stream<Arguments> sqlValidQueries() {
        return Stream.of(
                Arguments.of("SELECT street_address, postal_code, city, country_id FROM locations WHERE country_id LIKE 'US'",
                        new Class<?>[]{SelectClause.class, FromClause.class, WhereClause.class}),
                Arguments.of("SELECT last_name, first_name, salary FROM employees WHERE salary > 5000 AND salary < 10000 OR salary = 24000 OR salary BETWEEN 16000 AND 18000",
                        new Class<?>[]{SelectClause.class, FromClause.class, WhereClause.class}),
                Arguments.of("SELECT last_name, phone_number, email FROM employees JOIN departments USING department_id WHERE department_id IN [60,70,80,90]",
                        new Class<?>[]{SelectClause.class, FromClause.class, JoinClause.class, UsingClause.class, WhereClause.class}),
                Arguments.of("SELECT city, COUNT(employee_id), AVG(salary) FROM employees JOIN departments USING department_id JOIN locations USING location_id GROUP BY city HAVING AVG(salary) > 5000",
                        new Class<?>[]{SelectClause.class, FromClause.class, JoinClause.class, UsingClause.class, JoinClause.class, UsingClause.class, GroupClause.class, HavingClause.class})
        );
    }

    @ParameterizedTest
    @MethodSource("sqlInvalidQueries")
    public void sql_parser_invalid_test(String sqlQuery, Class<?>[] expectedClauses) {
        // Arrange
        Parser<Query<Clause>> parser = new SQLParser();

        // Act
        Query<Clause> query = parser.parse(sqlQuery);

        // Assert
        IntStream.range(0, expectedClauses.length)
                .forEach(i -> Assertions.assertTrue(expectedClauses[i].isInstance(query.getClauses().get(i))));

    }

    public static Stream<Arguments> sqlInvalidQueries() {
        return Stream.of(
                Arguments.of("", new Class<?>[]{}),
                Arguments.of("Random string", new Class<?>[]{}),
                Arguments.of("Select select select", new Class<?>[]{SelectClause.class, SelectClause.class, SelectClause.class})
        );
    }

    @Test
    public void null_input_parser_test() {
        // Arrange
        Parser<Query<Clause>> parser = new SQLParser();

        // Act & Assert
        Assertions.assertThrows(NullPointerException.class, () -> parser.parse(null));
        Query<Clause> query = parser.parse("");

        // Assert
        Assertions.assertNull(query);
    }

}
