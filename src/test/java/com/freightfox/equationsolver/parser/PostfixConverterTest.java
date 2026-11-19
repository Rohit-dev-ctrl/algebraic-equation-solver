
package com.freightfox.equationsolver.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostfixConverterTest {

    private final Tokenizer tokenizer = new Tokenizer();

    private final PostfixConverter converter = new PostfixConverter();

    @Test
    void shouldConvertEquationToPostfix() {

        String expression = "3x + 2y - z";

        List<String> tokens = tokenizer.tokenize(expression);

        List<String> postfix = converter.convert(tokens);

        List<String> expected = List.of(
                "3", "x", "*", "2", "y", "*", "+", "z", "-"
        );

        assertEquals(expected, postfix);
    }

    @Test
    void shouldRespectOperatorPrecedence() {

        List<String> tokens = tokenizer.tokenize("3 + 4 * 2");

        List<String> postfix = converter.convert(tokens);

        assertEquals(
                List.of("3", "4", "2", "*", "+"),
                postfix
        );
    }

    @Test
    void shouldHandleParentheses() {

        List<String> tokens = tokenizer.tokenize("(3 + 4) * 2");

        List<String> postfix = converter.convert(tokens);

        assertEquals(
                List.of("3", "4", "+", "2", "*"),
                postfix
        );
    }

    @Test
    void shouldHandleUnaryMinusBeforeExponentiation() {

        List<String> tokens = tokenizer.tokenize("-2^2");

        List<String> postfix = converter.convert(tokens);

        assertEquals(
                List.of("2", "2", "^", "u-"),
                postfix
        );
    }

    @Test
    void shouldHandleNegativeExponent() {

        List<String> tokens = tokenizer.tokenize("2^-3");

        List<String> postfix = converter.convert(tokens);

        assertEquals(
                List.of("2", "3", "u-", "^"),
                postfix
        );
    }

    @Test
    void shouldHandleRightAssociativeExponentiation() {

        List<String> tokens = tokenizer.tokenize("2^3^2");

        List<String> postfix = converter.convert(tokens);

        assertEquals(
                List.of("2", "3", "2", "^", "^"),
                postfix
        );
    }

    @Test
    void shouldHandleImplicitMultiplicationWithParentheses() {

        List<String> tokens = tokenizer.tokenize("3(x+2)");

        List<String> postfix = converter.convert(tokens);

        assertEquals(
                List.of("3", "x", "2", "+", "*"),
                postfix
        );
    }

    @Test
    void shouldRejectExpressionEndingWithOperator() {
        List<String> tokens = tokenizer.tokenize("3 +");

        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(tokens)
        );
    }

    @Test
    void shouldRejectConsecutiveBinaryOperators() {
        List<String> tokens = tokenizer.tokenize("2 + * 3");

        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(tokens)
        );
    }

    @Test
    void shouldRejectUnmatchedOpeningParenthesis() {
        List<String> tokens = tokenizer.tokenize("(2 + 3");

        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(tokens)
        );
    }

    @Test
    void shouldRejectUnmatchedClosingParenthesis() {
        List<String> tokens = tokenizer.tokenize("2 + 3)");

        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(tokens)
        );
    }

    @Test
    void shouldRejectEmptyParentheses() {
        List<String> tokens = tokenizer.tokenize("()");

        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(tokens)
        );
    }

    @Test
    void shouldHandleMultipleUnaryMinusOperators() {
        List<String> tokens = tokenizer.tokenize("--2");

        assertEquals(
                List.of("2", "u-", "u-"),
                converter.convert(tokens)
        );
    }

    @Test
    void shouldHandleUnaryMinusAfterSubtraction() {
        List<String> tokens = tokenizer.tokenize("5 - -3");

        assertEquals(
                List.of("5", "3", "u-", "-"),
                converter.convert(tokens)
        );
    }
}
