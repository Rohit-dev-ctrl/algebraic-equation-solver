
package com.freightfox.equationsolver.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void shouldInsertImplicitMultiplication() {

        assertEquals(
                List.of("3", "*", "x", "+", "2", "*", "y"),
                tokenizer.tokenize("3x + 2y")
        );
    }

    @Test
    void shouldHandleParentheses() {

        assertEquals(
                List.of("2", "*", "(", "x", "+", "3", ")"),
                tokenizer.tokenize("2(x + 3)")
        );
    }

    @Test
    void shouldHandleDecimalNumbers() {

        assertEquals(
                List.of("2.5", "*", "x", "+", "1.5"),
                tokenizer.tokenize("2.5x + 1.5")
        );
    }

    @Test
    void shouldRejectInvalidCharacters() {

        assertThrows(
                IllegalArgumentException.class,
                () -> tokenizer.tokenize("3x @ 2")
        );
    }

    @Test
    void shouldRejectInvalidNumbers() {

        assertThrows(
                IllegalArgumentException.class,
                () -> tokenizer.tokenize("2..5 + x")
        );
    }

    @Test
    void shouldRejectEmptyExpression() {
        assertThrows(
                IllegalArgumentException.class,
                () -> tokenizer.tokenize("")
        );
    }

    @Test
    void shouldRejectBlankExpression() {
        assertThrows(
                IllegalArgumentException.class,
                () -> tokenizer.tokenize("   ")
        );
    }

    @Test
    void shouldRejectNullExpression() {
        assertThrows(
                IllegalArgumentException.class,
                () -> tokenizer.tokenize(null)
        );
    }

    @Test
    void shouldRejectInvalidDecimal() {
        assertThrows(
                IllegalArgumentException.class,
                () -> tokenizer.tokenize(".")
        );
    }
}
