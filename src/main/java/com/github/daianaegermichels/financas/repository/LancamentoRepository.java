package com.github.daianaegermichels.financas.repository;

import com.github.daianaegermichels.financas.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
}
