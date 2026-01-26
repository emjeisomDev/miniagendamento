package dev.emjeisom.miniagendamento.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AgendamentoUpdadeRequest(
        @Size(max = 120) String titulo,
        @Size(max = 4000) String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {
}
