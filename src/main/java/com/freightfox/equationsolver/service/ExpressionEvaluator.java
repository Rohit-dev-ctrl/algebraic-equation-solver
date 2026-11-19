
package com.freightfox.equationsolver.service;

import com.freightfox.equationsolver.model.ExpressionNode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExpressionEvaluator {

    public double evaluate(ExpressionNode root, Map<String, Double> variables) {

        if (variables == null) {
            throw new IllegalArgumentException(
                    "Variables cannot be null"
            );
        }

        // Handle numbers and variables.
        if (root.isLeaf()) {

            String value = root.getValue();

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
                // If it is not a number, treat it as a variable.
            }

            Double variableValue = variables.get(value);

            if (variableValue == null) {
                throw new IllegalArgumentException(
                        "Missing value for variable: " + value
                );
            }

            if (!Double.isFinite(variableValue)) {
                throw new IllegalArgumentException(
                        "Variable value must be finite: " + value
                );
            }

            return variableValue;
        }

        // Handle unary minus.
        if ("u-".equals(root.getValue())) {

            // ExpressionTreeBuilder stores unary operand in LEFT child.
            double operand = evaluate(root.getLeft(), variables);

            double result = -operand;

            if (!Double.isFinite(result)) {
                throw new ArithmeticException(
                        "Equation result is not a finite number"
                );
            }

            return result;
        }

        // Evaluate left and right subtrees.
        double left = evaluate(root.getLeft(), variables);
        double right = evaluate(root.getRight(), variables);

        // Perform the operation.
        double result = switch (root.getValue()) {

            case "+" -> left + right;

            case "-" -> left - right;

            case "*" -> left * right;

            case "/" -> {
                if (right == 0) {
                    throw new ArithmeticException(
                            "Division by zero is not allowed"
                    );
                }
                yield left / right;
            }

            case "^" -> Math.pow(left, right);

            default -> throw new IllegalArgumentException(
                    "Unsupported operator: " + root.getValue()
            );
        };

        // Prevent NaN and Infinity results.
        if (!Double.isFinite(result)) {
            throw new ArithmeticException(
                    "Equation result is not a finite number"
            );
        }

        return result;
    }
}
