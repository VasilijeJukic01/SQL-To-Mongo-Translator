import bp.parser.KeyWord;
import bp.parser.clauses.*;
import bp.parser.conditions.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class ClausesTest {

    // From Clause
    @Test
    public void from_clause_test() {
        // Arrange
        FromClause clause = new FromClause();

        // Act
        clause.setTable("managers");

        // Assertion
        Assertions.assertEquals("managers", clause.getTable());
        Assertions.assertEquals(KeyWord.FROM, clause.getKeyword());
    }

    // Select Clause
    @Test
    public void select_clause_no_aggregation_test() {
        // Arrange
        SelectClause clause = new SelectClause();
        clause.addColumn("last_name");
        clause.addColumn("first_name");
        clause.addColumn("salary");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(0, clause.getIndicators().size());
        Assertions.assertEquals(0, clause.getAggregations().size());
        Assertions.assertEquals(3, clause.getColumns().size());

        Assertions.assertEquals(KeyWord.SELECT, clause.getKeyword());
    }

    @Test
    public void select_clause_aggregation_test() {
        // Arrange
        SelectClause clause = new SelectClause();
        clause.addColumn("city");
        clause.addColumn("COUNT(employee_id)");
        clause.addColumn("AVG(salary)");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(2, clause.getIndicators().size());
        Assertions.assertEquals(2, clause.getAggregations().size());
        Assertions.assertEquals(3, clause.getColumns().size());

        Assertions.assertEquals(List.of("COUNT", "AVG"), clause.getIndicators());
        Assertions.assertEquals(List.of("employee_id", "salary"), clause.getAggregations());

        Assertions.assertEquals(KeyWord.SELECT, clause.getKeyword());
    }

    // Join Clause
    @Test
    public void join_clause_test() {
        // Arrange
        JoinClause clause = new JoinClause();

        // Act
        clause.setJoinTable("students");

        // Assert
        Assertions.assertEquals("students", clause.getJoinTable());
        Assertions.assertEquals(KeyWord.JOIN, clause.getKeyword());
    }

    // Group Clause
    @Test
    public void group_clause_test() {
        // Arrange
        GroupClause clause = new GroupClause();

        // Act
        clause.getColumns().addAll(List.of("students", "managers"));

        // Assert
        Assertions.assertEquals(List.of("students", "managers"), clause.getColumns());
        Assertions.assertEquals(KeyWord.GROUP, clause.getKeyword());
    }

    // Using Clause
    @Test
    public void using_clause_test() {
        // Arrange
        UsingClause clause = new UsingClause();

        // Act
        clause.setColumn("managers");

        // Assert
        Assertions.assertEquals("managers", clause.getColumn());
        Assertions.assertEquals(KeyWord.USING, clause.getKeyword());
    }

    // On Clause
    @Test
    public void on_clause_test() {
        // Arrange
        OnClause clause = new OnClause();
        clause.getArgs().add("student_id");
        clause.getArgs().add(">");
        clause.getArgs().add("100");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals("student_id", clause.getCondition().getLeftOperand());
        Assertions.assertEquals(">", clause.getCondition().getOperator());
        Assertions.assertEquals("100", clause.getCondition().getRightOperand());

        Assertions.assertEquals(KeyWord.ON, clause.getKeyword());
    }

    @Test
    public void on_clause_build_test() {
        // Arrange
        OnClause clause = new OnClause();
        clause.getArgs().add("student_id");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals("student_id", clause.getCondition().getLeftOperand());
        Assertions.assertEquals("", clause.getCondition().getOperator());
        Assertions.assertEquals("", clause.getCondition().getRightOperand());
    }

    // Order Clause
    @Test
    public void order_clause_test() {
        // Arrange
        OrderClause clause = new OrderClause();
        clause.getArgs().addAll(List.of("students_id", "ASC", "salary", "DESC"));

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(2, clause.getIndicators().size());
        Assertions.assertEquals(2, clause.getColumns().size());

        Assertions.assertEquals(List.of(1, -1), clause.getIndicators());
        Assertions.assertEquals(List.of("students_id", "salary"), clause.getColumns());

        Assertions.assertEquals(KeyWord.ORDER, clause.getKeyword());
    }

    @Test
    public void order_clause_build_test() {
        // Arrange
        OrderClause clause = new OrderClause();

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(0, clause.getIndicators().size());
        Assertions.assertEquals(0, clause.getColumns().size());
        Assertions.assertEquals(KeyWord.ORDER, clause.getKeyword());
    }

    // Where Clause
    @Test
    public void where_clause_test() {
        // Arrange
        WhereClause clause = new WhereClause();
        clause.getArgs().addAll(List.of("x", ">", "y", "AND", "name", "LIKE", "Test"));
        Condition c1 = new Condition("x", ">", "y");
        Condition c2 = new Condition("name", "LIKE", "Test");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(List.of(c1, c2), clause.getConditions());
        Assertions.assertEquals(List.of("AND"), clause.getOperators());

        Assertions.assertEquals(KeyWord.WHERE, clause.getKeyword());
    }

    @Test
    public void where_clause_build_test_empty() {
        // Arrange
        WhereClause clause = new WhereClause();

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(Collections.emptyList(), clause.getConditions());
        Assertions.assertEquals(Collections.emptyList(), clause.getOperators());
        Assertions.assertEquals(KeyWord.WHERE, clause.getKeyword());
    }

    @Test
    public void where_clause_build_test_no_operators() {
        // Arrange
        WhereClause clause = new WhereClause();
        clause.getArgs().addAll(List.of("x", ">", "y"));
        Condition c1 = new Condition("x", ">", "y");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(List.of(c1), clause.getConditions());
        Assertions.assertEquals(Collections.emptyList(), clause.getOperators());

        Assertions.assertEquals(KeyWord.WHERE, clause.getKeyword());
    }

    @Test
    public void where_clause_build_test_no_conditions() {
        // Arrange
        WhereClause clause = new WhereClause();
        clause.getArgs().addAll(List.of("AND", "OR"));
        Condition c1 = new Condition("", "", "");
        Condition c2 = new Condition("", "", "");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(List.of(c1, c2), clause.getConditions());
        Assertions.assertEquals(List.of("AND", "OR"), clause.getOperators());

        Assertions.assertEquals(KeyWord.WHERE, clause.getKeyword());
    }

    @Test
    public void where_clause_subquery_test() {
        // Arrange
        WhereClause clause = new WhereClause();
        clause.getArgs().addAll(List.of("{SELECT", "location_id", "FROM", "locations", "JOIN", "departments", "USING", "location_id",
                "JOIN", "employees", "USING", "department_id", "WHERE", "first_name", "LIKE", "'Steven'", "AND", "last_name", "LIKE", "'King'}"));

        // Act
        String query = clause.getSubQueryString();

        // Assert
        Assertions.assertEquals("JOIN locations JOIN departments USING location_id JOIN employees USING department_id WHERE first_name LIKE 'Steven' AND last_name LIKE 'King' ", query);

        Assertions.assertEquals(KeyWord.WHERE, clause.getKeyword());
    }

    // Having Clause
    @Test
    public void having_clause_test() {
        // Arrange
        HavingClause clause = new HavingClause();
        clause.getArgs().addAll(List.of("AVG(salary)", ">", "5000", "OR", "AVG(salary)", "<", "1000"));
        Condition c1 = new Condition("AVGsalary", ">", "5000");
        Condition c2 = new Condition("AVGsalary", "<", "1000");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(List.of(c1, c2), clause.getConditions());
        Assertions.assertEquals(1, clause.getOperators().size());

        Assertions.assertEquals(KeyWord.HAVING, clause.getKeyword());
    }

    @Test
    public void having_clause_build_test_empty() {
        // Arrange
        HavingClause clause = new HavingClause();

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(Collections.emptyList(), clause.getConditions());
        Assertions.assertEquals(Collections.emptyList(), clause.getOperators());
        Assertions.assertEquals(KeyWord.HAVING, clause.getKeyword());
    }

    @Test
    public void having_clause_build_test_no_operators() {
        // Arrange
        HavingClause clause = new HavingClause();
        clause.getArgs().addAll(List.of("AVG(salary)", ">", "5000"));
        Condition c1 = new Condition("AVGsalary", ">", "5000");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(List.of(c1), clause.getConditions());
        Assertions.assertEquals(Collections.emptyList(), clause.getOperators());

        Assertions.assertEquals(KeyWord.HAVING, clause.getKeyword());
    }

    @Test
    public void having_clause_build_test_no_conditions() {
        // Arrange
        HavingClause clause = new HavingClause();
        clause.getArgs().addAll(List.of("AND", "OR"));
        Condition c1 = new Condition("", "", "");
        Condition c2 = new Condition("", "", "");

        // Act
        clause.build();

        // Assert
        Assertions.assertEquals(List.of(c1, c2), clause.getConditions());
        Assertions.assertEquals(List.of("AND", "OR"), clause.getOperators());

        Assertions.assertEquals(KeyWord.HAVING, clause.getKeyword());
    }

}
