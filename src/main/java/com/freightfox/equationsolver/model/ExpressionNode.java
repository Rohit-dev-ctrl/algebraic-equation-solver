package com.freightfox.equationsolver.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExpressionNode {

    private final String value;

    private final ExpressionNode left;

    private final ExpressionNode right;

    public ExpressionNode(String value){
        this(value,null,null);
    }

    public boolean isLeaf(){
        return left == null && right == null;
    }
}
