
package com.freightfox.equationsolver.parser;

import com.freightfox.equationsolver.model.ExpressionNode;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class ExpressionTreeBuilder {

    public ExpressionNode build(List<String> postfix) {

        if (postfix == null || postfix.isEmpty()) {
            throw new IllegalArgumentException(
                    "Postfix expression cannot be empty"
            );
        }

        Deque<ExpressionNode> stack = new ArrayDeque<>();

        for (String token : postfix) {

            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid token in postfix expression"
                );
            }

            // Handle number or variable
            if (isOperand(token)) {

                stack.push(new ExpressionNode(token));
            }

            // Handle unary minus
            else if (token.equals("u-")) {

                if (stack.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Insufficient operands for unary minus"
                    );
                }

                ExpressionNode operand = stack.pop();

                ExpressionNode node = new ExpressionNode(
                        token,
                        operand,
                        null
                );

                stack.push(node);
            }

            // Handle binary operators
            else if (isBinaryOperator(token)) {

                if (stack.size() < 2) {
                    throw new IllegalArgumentException(
                            "Insufficient operands for operator: " + token
                    );
                }

                ExpressionNode right = stack.pop();
                ExpressionNode left = stack.pop();

                ExpressionNode node = new ExpressionNode(
                        token,
                        left,
                        right
                );

                stack.push(node);
            }

            else {
                throw new IllegalArgumentException(
                        "Invalid postfix token: " + token
                );
            }
        }

        // A valid expression must produce exactly one root node
        if (stack.size() != 1) {
            throw new IllegalArgumentException(
                    "Invalid postfix expression"
            );
        }

        return stack.pop();
    }

    private boolean isOperand(String token) {

        return token.matches("\\d+(\\.\\d+)?|\\.\\d+")
                || token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private boolean isBinaryOperator(String token) {

        return token.equals("+")
                || token.equals("-")
                || token.equals("*")
                || token.equals("/")
                || token.equals("^");
    }
}
