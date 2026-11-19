
package com.freightfox.equationsolver.service;

import com.freightfox.equationsolver.model.ExpressionNode;
import com.freightfox.equationsolver.parser.ExpressionTreeBuilder;
import com.freightfox.equationsolver.parser.PostfixConverter;
import com.freightfox.equationsolver.parser.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    private final Tokenizer tokenizer = new Tokenizer();
    private final PostfixConverter converter = new PostfixConverter();
    private final ExpressionTreeBuilder builder =
            new ExpressionTreeBuilder();
    private final ExpressionEvaluator evaluator =
            new ExpressionEvaluator();

    private ExpressionNode createTree(String expression) {

        return builder.build(
                converter.convert(
                        tokenizer.tokenize(expression)
                )
        );
    }

    @Test
    void shouldEvaluateExpressionWithVariables() {

        ExpressionNode root = createTree("3x + 2y - z");

        Map<String, Double> variables = Map.of(
                "x", 2.0,
                "y", 3.0,
                "z", 1.0
        );

        double result = evaluator.evaluate(root, variables);

        assertEquals(11.0, result, 0.0001);
    }

    @Test
    void shouldEvaluateSimpleExpression() {

        ExpressionNode root = createTree("10 + 5 * 2");

        double result = evaluator.evaluate(root, Map.of());

        assertEquals(20.0, result, 0.0001);
    }

    @Test
    void shouldEvaluateParentheses() {

        ExpressionNode root = createTree("(10 + 5) * 2");

        double result = evaluator.evaluate(root, Map.of());

        assertEquals(30.0, result, 0.0001);
    }

    @Test
    void shouldEvaluateNegativeNumbers() {

        ExpressionNode root = createTree("-5 + 10");

        double result = evaluator.evaluate(root, Map.of());

        assertEquals(5.0, result, 0.0001);
    }

    @Test
    void shouldThrowExceptionForMissingVariable() {

        ExpressionNode root = createTree("3x + 2");

        assertThrows(
                IllegalArgumentException.class,
                () -> evaluator.evaluate(root, Map.of())
        );
    }

    @Test
    void shouldThrowExceptionForDivisionByZero() {

        ExpressionNode root = createTree("10 / 0");

        assertThrows(
                ArithmeticException.class,
                () -> evaluator.evaluate(root, Map.of())
        );
    }


    @Test
    void shouldEvaluateUnaryMinusBeforeExponentiation() {

        ExpressionNode root = createTree("-2^2");

        double result = evaluator.evaluate(root, Map.of());

        assertEquals(-4.0, result, 0.0001);
    }

    @Test
    void shouldEvaluateNegativeExponent() {

        ExpressionNode root = createTree("2^-3");

        double result = evaluator.evaluate(root, Map.of());

        assertEquals(0.125, result, 0.0001);
    }

    @Test
    void shouldEvaluateRightAssociativeExponentiation() {

        ExpressionNode root = createTree("2^3^2");

        double result = evaluator.evaluate(root, Map.of());

        assertEquals(512.0, result, 0.0001);
    }

    @Test
    void shouldEvaluateImplicitMultiplicationWithParentheses() {

        ExpressionNode root = createTree("3(x+2)");

        double result = evaluator.evaluate(
                root,
                Map.of("x", 4.0)
        );

        assertEquals(18.0, result, 0.0001);
    }

    @Test
    void shouldEvaluateMultipleVariablesWithImplicitMultiplication() {

        ExpressionNode root = createTree("2(x+y)");

        double result = evaluator.evaluate(
                root,
                Map.of(
                        "x", 3.0,
                        "y", 5.0
                )
        );

        assertEquals(16.0, result, 0.0001);
    }

}
