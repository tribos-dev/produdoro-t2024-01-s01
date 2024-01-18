package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.Value;

@Value
public class TarefaListResponse {

	private UUID idTarefa;
	@NotBlank
	 private String descricao;
	
	public static List<TarefaListResponse> converte(List<Tarefa> tarefas) {
	return tarefas.stream()
				.map(TarefaListResponse::new)
				.collect(Collectors.toList());
		
	}

	private TarefaListResponse(Tarefa tarefa) {
		this.idTarefa = tarefa.getIdTarefa();
		this.descricao = tarefa.getDescricao();
	}
	
	
	
}
