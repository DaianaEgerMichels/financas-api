package com.github.daianaegermichels.financas.repository;

import com.github.daianaegermichels.financas.enuns.TipoLancamento;
import com.github.daianaegermichels.financas.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = " select sum(l.valor) from Lancamento l join l.usuario u where u.id = :idUsuario and l.tipo = :tipo group by u ")
    BigDecimal obterSaldoPorTipoDeLancamentoEUsuario(@Param("isUsuario") Long isUsuario, @Param("tipo") TipoLancamento tipo);
}
