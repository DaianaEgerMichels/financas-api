package com.github.daianaegermichels.financas.model;

import com.github.daianaegermichels.financas.enuns.StatusLancamento;
import com.github.daianaegermichels.financas.enuns.TipoLancamento;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.convert.Jsr310Converters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Entity
@NoArgsConstructor
@Table(name = "lancamento", schema = "financas")
public class Lancamento {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "ano")
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "data_cadastro")
    @Convert(converter = Jsr310Converters.LocalDateToDateConverter.class)
    private LocalDate dataCadastro;

    @Column(name= "tipo")
    @Enumerated(value = EnumType.STRING)
    private TipoLancamento tipo;

    @Column(name= "status")
    @Enumerated(value = EnumType.STRING)
    private StatusLancamento status;
}
