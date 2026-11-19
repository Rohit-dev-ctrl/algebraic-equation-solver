
package com.freightfox.equationsolver.parser;

import com.freightfox.equationsolver.model.ExpressionNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTreeBuilderTest {

    private final ExpressionTreeBuilder builder =
            new ExpressionTreeBuilder();

    @Test
    void shouldBuildExpressionTree() {

        List<String> postfix = List.of(
                "3", "x", "*", "2", "y", "*", "+", "z", "-"
        );

        ExpressionNode root = builder.build(postfix);

        assertEquals("-", root.getValue());

        assertEquals("+", root.getLeft().getValue());

        assertEquals("z", root.getRight().getValue());

        assertEquals("*", root.getLeft().getLeft().getValue());

        assertEquals("*", root.getLeft().getRight().getValue());
    }

    @Test
    void shouldBuildNumberNode() {

        ExpressionNode root = builder.build(
                List.of("10")
        );

        assertEquals("10", root.getValue());
        assertTrue(root.isLeaf());
    }

    @Test
    void shouldBuildUnaryMinusNode() {

        ExpressionNode root = builder.build(
                List.of("5", "u-")
        );

        assertEquals("u-", root.getValue());
        assertEquals("5", root.getLeft().getValue());
        assertNull(root.getRight());
    }

    @Test
    void shouldRejectInsufficientOperands() {

        assertThrows(
                IllegalArgumentException.class,
                () -> builder.build(List.of("3", "+"))
        );
    }

    @Test
    void shouldRejectInvalidPostfixExpression() {

        assertThrows(
                IllegalArgumentException.class,
                () -> builder.build(List.of("3", "4"))
        );
    }
}
