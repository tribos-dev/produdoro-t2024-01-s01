package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TarefaReponse {

	private UUID idTarefa;
	
}
