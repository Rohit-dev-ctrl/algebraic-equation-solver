
package com.freightfox.equationsolver.repository;

import com.freightfox.equationsolver.model.Equation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EquationRepository {

    private final ConcurrentHashMap<UUID, Equation> equations =
            new ConcurrentHashMap<>();

    public Equation save(Equation equation) {

        equations.put(equation.getId(), equation);

        return equation;
    }

    public Optional<Equation> findById(UUID id) {

        return Optional.ofNullable(equations.get(id));
    }

    public List<Equation> findAll() {

        return List.copyOf(equations.values());
    }

    public boolean deleteById(UUID id) {

        return equations.remove(id) != null;
    }

    public boolean existsById(UUID id) {

        return equations.containsKey(id);
    }
}
