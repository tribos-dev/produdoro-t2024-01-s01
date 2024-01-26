package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }
    @Test
    void editaTarefa(){
        Usuario usuarioMock = DataHelper.createUsuario();
        Tarefa tarefaMock = DataHelper.createTarefa();
        EditaTarefaRequest tarefaReq = DataHelper.getEditaTarefaRequest();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuarioMock);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefaMock));
        tarefaApplicationService.alteraTarefa(usuarioMock.getEmail(), tarefaMock.getIdTarefa(), tarefaReq);

        verify (usuarioRepository, times(1)).buscaUsuarioPorEmail(usuarioMock.getEmail());
        verify (tarefaRepository, times(1)).buscaTarefaPorId(tarefaMock.getIdTarefa());
        assertEquals(tarefaReq.getDescricao(), tarefaMock.getDescricao());
    }

    void naoEditaTarefa(){
        UUID idTarefaInvalido = UUID.randomUUID();
        String usuario = "usuarioTeste";
        EditaTarefaRequest editaTarefaRequest = new EditaTarefaRequest("TESTE");

        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
        assertThrows(APIException.class, () -> tarefaApplicationService.alteraTarefa(usuario, idTarefaInvalido, editaTarefaRequest));

        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefaInvalido);
        verifyNoMoreInteractions(tarefaRepository);
    }

}
