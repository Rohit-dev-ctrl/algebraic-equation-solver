
package com.freightfox.equationsolver.parser;

import com.freightfox.equationsolver.model.ExpressionNode;
import org.springframework.stereotype.Component;

@Component
public class ExpressionTreeFormatter {

    public String format(ExpressionNode root) {

        if (root == null) {
            throw new IllegalArgumentException(
                    "Expression tree cannot be null"
            );
        }

        return formatNode(root);
    }

    private String formatNode(ExpressionNode node) {

        // Leaf node: number or variable
        if (node.isLeaf()) {
            return node.getValue();
        }

        // Unary minus
        if (node.getValue().equals("u-")) {
            return "(-" + formatNode(node.getLeft()) + ")";
        }

        // Binary operators
        String left = formatNode(node.getLeft());
        String right = formatNode(node.getRight());

        return "(" + left + " "
                + node.getValue() + " "
                + right + ")";
    }
}
