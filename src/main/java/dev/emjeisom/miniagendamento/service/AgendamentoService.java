package dev.emjeisom.miniagendamento.service;

import dev.emjeisom.miniagendamento.dto.AgendamentoCreateRequest;
import dev.emjeisom.miniagendamento.dto.AgendamentoResponse;
import dev.emjeisom.miniagendamento.dto.AgendamentoUpdateRequest;
import dev.emjeisom.miniagendamento.mapper.AgendamentoMapper;
import dev.emjeisom.miniagendamento.model.Agendamento;
import dev.emjeisom.miniagendamento.model.StatusAgendamento;
import dev.emjeisom.miniagendamento.repository.AgendamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repo;

    public AgendamentoService(AgendamentoRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AgendamentoResponse criar(@Valid AgendamentoCreateRequest req){
        validarIntervalo(req.dataInicio(), req.dataFim());
        checarConflito(req.usuario(), req.dataInicio(), req.dataFim(), null);
        Agendamento entity = AgendamentoMapper.toEntity(req);
        entity = repo.save(entity);
        return AgendamentoMapper.toResonse(entity);
    }

    @Transactional
    public AgendamentoResponse atualizar(Long id, @Valid AgendamentoUpdateRequest req){
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado."));
        AgendamentoMapper.merge(entity, req);
        validarIntervalo(req.dataInicio(), req.dataFim());
        checarConflito(entity.getUsuario(), req.dataInicio(), req.dataFim(), entity.getId());
        entity = repo.save(entity);
        return AgendamentoMapper.toResonse(entity);
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id){
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado."));
        entity.setStatus(StatusAgendamento.CANCELADO);
        entity = repo.save(entity);
        return AgendamentoMapper.toResonse(entity);
    }

    @Transactional
    public AgendamentoResponse concluir(Long id){
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado."));
        entity.setStatus(StatusAgendamento.CONCLUIDO);
        entity = repo.save(entity);
        return AgendamentoMapper.toResonse(entity);
    }

    @Transactional
    public AgendamentoResponse buscarPorId(Long id){
        Agendamento entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado."));
        return AgendamentoMapper.toResonse(entity);
    }

    private void validarIntervalo(LocalDateTime inicio, LocalDateTime fim){
        if(inicio == null || fim == null || !inicio.isBefore(fim)){
            throw new IllegalArgumentException("Intervalo inválido: dataInicio deve ser anterior a dataFim");
        }
    }

    private void checarConflito(String usuario, LocalDateTime inicio, LocalDateTime fim, Long id){
        if(repo.existsConflito(usuario, inicio, fim,id)){
            throw new IllegalArgumentException("Conflito na agenda: já existe agendamento nesse período.");
        }
    }



}
