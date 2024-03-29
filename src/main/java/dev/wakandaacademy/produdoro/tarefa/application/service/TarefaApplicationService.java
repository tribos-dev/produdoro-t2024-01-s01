package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
	private final TarefaRepository tarefaRepository;
	private final UsuarioRepository usuarioRepository;

	@Override
	public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
		log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
		return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
	}

	@Override
	public void alteraTarefa(String usuario, UUID idTarefa, EditaTarefaRequest tarefaRequestEditada) {
		log.info("[inicia] TarefaApplicationService - alteraTarefa");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefa.editaDescricao(tarefaRequestEditada);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - alteraTarefa");

	}

	@Override
	public void incrementaPomodoro(UUID idTarefa, String usuarioEmail) {
		log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
		Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
		tarefa.incrementaContagemPomodoro();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
	}

	@Override
	public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - detalhaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
		return tarefa;
	}

	@Override
	public void concluiTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - concluiTarefa");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefa.concluiTarefa();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - concluiTarefa");
	}

	@Override
	public List<TarefaListResponse> buscaTarefasPorUsuario(String usuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - buscaTodasTarefas");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuarioPorEmail.validaUsuario(idUsuario);
		List<Tarefa> tarefas = tarefaRepository.buscaTarefasPorUsuario(idUsuario);
		log.info("[Finaliza] TarefaApplicationService - buscaTodasTarefas");
		return TarefaListResponse.converte(tarefas);
	}

	@Override
	public void deletaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - deletaTarefa");
		tarefaRepository.deletaTarefaPorId(detalhaTarefa(usuario, idTarefa));
		log.info("[finaliza] TarefaApplicationService - deletaTarefa");
	}

	@Override
	public void definirTarefaComoAtiva(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - definirTarefaComoAtiva");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = validaTarefa(idTarefa, usuarioPorEmail);
		tarefa.inativarOutraTarefa(tarefaRepository);
		tarefa.definirComoAtiva();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - definirTarefaComoAtiva");
	}

	private Tarefa validaTarefa(UUID idTarefa, Usuario usuario) {
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Id da tarefa invalido"));
		tarefa.pertenceAoUsuario(usuario);
		return tarefa;
	}
}
