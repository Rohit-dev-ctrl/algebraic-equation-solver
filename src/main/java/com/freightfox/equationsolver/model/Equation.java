
package com.freightfox.equationsolver.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Equation {

    private final UUID id;

    private final String expression;

    private final ExpressionNode root;

    private final Instant createdAt;
}
