
package com.freightfox.equationsolver.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Tokenizer {

    public List<String> tokenize(String expression) {

        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }

        List<String> tokens = new ArrayList<>();

        int i = 0;

        while (i < expression.length()) {

            char current = expression.charAt(i);

            // Ignore whitespace
            if (Character.isWhitespace(current)) {
                i++;
                continue;
            }

            // Read numbers
            if (Character.isDigit(current) || current == '.') {

                int start = i;
                boolean decimalFound = false;

                while (i < expression.length()) {

                    char ch = expression.charAt(i);

                    if (Character.isDigit(ch)) {
                        i++;
                    } else if (ch == '.') {

                        if (decimalFound) {
                            throw new IllegalArgumentException(
                                    "Invalid number"
                            );
                        }

                        decimalFound = true;
                        i++;

                    } else {
                        break;
                    }
                }

                String number = expression.substring(start, i);

                if (!isNumber(number)) {
                    throw new IllegalArgumentException(
                            "Invalid number: " + number
                    );
                }

                tokens.add(number);
                continue;
            }

            // Read variable names
            if (Character.isLetter(current) || current == '_') {

                int start = i;

                while (i < expression.length()) {

                    char ch = expression.charAt(i);

                    if (Character.isLetterOrDigit(ch) || ch == '_') {
                        i++;
                    } else {
                        break;
                    }
                }

                tokens.add(expression.substring(start, i));
                continue;
            }

            // Mathematical operators and parentheses
            if ("+-*/^()".indexOf(current) != -1) {

                tokens.add(String.valueOf(current));
                i++;
                continue;
            }

            throw new IllegalArgumentException(
                    "Invalid character: " + current
            );
        }

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }

        return insertImplicitMultiplication(tokens);
    }

    private List<String> insertImplicitMultiplication(
            List<String> tokens) {

        List<String> result = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {

            String current = tokens.get(i);

            result.add(current);

            if (i == tokens.size() - 1) {
                continue;
            }

            String next = tokens.get(i + 1);

            boolean currentEndsOperand =
                    isNumber(current)
                            || isVariable(current)
                            || current.equals(")");

            boolean nextStartsOperand =
                    isNumber(next)
                            || isVariable(next)
                            || next.equals("(");

            if (currentEndsOperand && nextStartsOperand) {
                result.add("*");
            }
        }

        return result;
    }

    private boolean isNumber(String token) {
        return token.matches("\\d+(\\.\\d+)?|\\.\\d+");
    }

    private boolean isVariable(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
}
