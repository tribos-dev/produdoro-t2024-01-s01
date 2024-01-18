package dev.wakandaacademy.produdoro.tarefa.application.api;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Getter
@AllArgsConstructor (access = AccessLevel.PUBLIC)
@NoArgsConstructor (access = AccessLevel.PRIVATE, force = true)
public class EditaTarefaRequest {
    @NotBlank
    @Size(message = "O campo não pode estar vazio", max = 255, min = 3)
    private String descricao;
}
