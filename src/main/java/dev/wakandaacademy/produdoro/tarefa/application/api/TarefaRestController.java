package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void editaTarefa(String token, UUID idTarefa, EditaTarefaRequest tarefaRequestEditada) {
		log.info("[inicia] TarefaRestController - editaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.alteraTarefa(usuario, idTarefa, tarefaRequestEditada);
		log.info("[finaliza] TarefaRestController - editaTarefa");
	}

	public void concluiTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - concluiTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.concluiTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - concluiTarefa");
	}

	@Override
	public void definirTarefaComoAtiva(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - definirTarefaComoAtiva");
		String usuario = getUsuarioByToken(token);
		tarefaService.definirTarefaComoAtiva(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - definirTarefaComoAtiva");
	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void incrementaPomodoro(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaPomodoro");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.incrementaPomodoro(idTarefa, usuarioEmail);
		log.info("[finaliza] TarefaRestController - incrementaPomodoro");
	}

	public List<TarefaListResponse> buscaTarefasPorUsuario(String token, UUID idUsuario) {
		log.info("[incia] TarefaInfraRepository getTodasTarefas");
		log.info("[idUsuario] {}", idUsuario);
		String usuario = getUsuarioByToken(token);
		List<TarefaListResponse> tarefas = tarefaService.buscaTarefasPorUsuario(usuario, idUsuario);
		log.info("[finaliza] TarefaInfraRepository getTodasTarefas");
		return tarefas;
	}

	public void deletaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - DeletaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.deletaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - DeletaTarefa");

	}

}
