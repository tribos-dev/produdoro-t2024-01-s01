package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaService {
	TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);

	Tarefa detalhaTarefa(String usuario, UUID idTarefa);

	void alteraTarefa(String usuario, UUID idTarefa, EditaTarefaRequest tarefaRequestEditada);

	void incrementaPomodoro(UUID idTarefa, String usuarioEmail);

	void concluiTarefa(String usuario, UUID idTarefa);

	List<TarefaListResponse> buscaTarefasPorUsuario(String usuario, UUID idUsuario);

	void deletaTarefa(String usuario, UUID idTarefa);

	void definirTarefaComoAtiva(String usuario, UUID idTarefa);
}
