package com.fxmind.repository;

import com.fxmind.domain.Symbol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {

    Optional<Symbol> findByName(String name);

    List<Symbol> findByDisabledFalseAndTrackSentimentTrue();
}
