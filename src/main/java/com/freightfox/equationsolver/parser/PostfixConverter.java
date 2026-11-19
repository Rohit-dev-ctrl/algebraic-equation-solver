
package com.freightfox.equationsolver.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
public class PostfixConverter {

    public List<String> convert(List<String> tokens) {

        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }

        List<String> output = new ArrayList<>();
        Deque<String> operators = new ArrayDeque<>();

        boolean expectOperand = true;

        for (String token : tokens) {

            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid token in expression"
                );
            }

            // Handle numbers and variables
            if (isOperand(token)) {

                if (!expectOperand) {
                    throw new IllegalArgumentException(
                            "Missing operator before: " + token
                    );
                }

                output.add(token);
                expectOperand = false;
            }

            // Handle opening parenthesis
            else if (token.equals("(")) {

                if (!expectOperand) {
                    throw new IllegalArgumentException(
                            "Missing operator before opening parenthesis"
                    );
                }

                operators.push(token);
            }

            // Handle closing parenthesis
            else if (token.equals(")")) {

                if (expectOperand) {
                    throw new IllegalArgumentException(
                            "Invalid expression near closing parenthesis"
                    );
                }

                while (!operators.isEmpty()
                        && !operators.peek().equals("(")) {

                    output.add(operators.pop());
                }

                if (operators.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Mismatched parentheses"
                    );
                }

                operators.pop();
                expectOperand = false;
            }

            // Handle mathematical operators
            else if (isOperator(token)) {

                // Handle unary minus separately
                if (expectOperand) {

                    if (token.equals("-")) {

                        operators.push("u-");
                        expectOperand = true;
                        continue;

                    } else {

                        throw new IllegalArgumentException(
                                "Unexpected operator: " + token
                        );
                    }
                }

                String currentOperator = token;

                // Handle operator precedence and associativity
                while (!operators.isEmpty()
                        && isOperator(operators.peek())) {

                    String topOperator = operators.peek();

                    boolean shouldPop =
                            isRightAssociative(currentOperator)
                                    ? precedence(currentOperator)
                                    < precedence(topOperator)
                                    : precedence(currentOperator)
                                    <= precedence(topOperator);

                    if (!shouldPop) {
                        break;
                    }

                    output.add(operators.pop());
                }

                operators.push(currentOperator);
                expectOperand = true;
            }

            else {

                throw new IllegalArgumentException(
                        "Invalid token: " + token
                );
            }
        }

        // Expression cannot end with an operator
        if (expectOperand) {
            throw new IllegalArgumentException(
                    "Expression cannot end with an operator"
            );
        }

        // Pop remaining operators
        while (!operators.isEmpty()) {

            String operator = operators.pop();

            if (operator.equals("(")) {
                throw new IllegalArgumentException(
                        "Mismatched parentheses"
                );
            }

            output.add(operator);
        }

        return output;
    }

    private boolean isOperand(String token) {

        return token.matches("\\d+(\\.\\d+)?|\\.\\d+")
                || token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private boolean isOperator(String token) {

        return token.equals("+")
                || token.equals("-")
                || token.equals("*")
                || token.equals("/")
                || token.equals("^")
                || token.equals("u-");
    }

    private int precedence(String operator) {

        return switch (operator) {
            case "^" -> 4;
            case "u-" -> 3;
            case "*", "/" -> 2;
            case "+", "-" -> 1;
            default -> 0;
        };
    }

    private boolean isRightAssociative(String operator) {

        return operator.equals("^")
                || operator.equals("u-");
    }
}
