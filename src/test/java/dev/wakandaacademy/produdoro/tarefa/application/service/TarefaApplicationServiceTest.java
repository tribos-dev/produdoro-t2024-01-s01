package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {


    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    
    @Mock
    TarefaRepository tarefaRepository;

    @Mock
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
    
    @Test
    void deveAtivarTarefaEInativarAAnterior(){
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        Tarefa tarefaAtiva = getTarefaAtiva(usuario);
        UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");

        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(eq(idTarefa))).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.buscarTarefaAtiva()).thenReturn(tarefaAtiva);

        tarefaApplicationService.definirTarefaComoAtiva(String.valueOf(usuario) , idTarefa);

        verify(tarefaRepository, times(1)).salva(tarefa);
        verify(tarefaRepository, times(1)).buscarTarefaAtiva();
        verify(tarefaRepository, times(1)).salva(tarefa);
    }

    @Test
    void deveRetornarBadRequestENaoAtivarTarefa_QuandoPassarIdTarefaInvalido(){
        Usuario usuario = DataHelper.createUsuario();
        UUID idTarefaInvalido = UUID.fromString("e504cc82-20ba-4716-bcf8-b890548db749");

        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(eq(idTarefaInvalido)))
                .thenThrow(APIException.build(HttpStatus.BAD_REQUEST, "Id da tarefa invalido"));

        APIException exception = assertThrows(APIException.class, () -> {
            tarefaApplicationService.definirTarefaComoAtiva(String.valueOf(usuario), idTarefaInvalido);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        verify(tarefaRepository, never()).buscarTarefaAtiva();
        verify(tarefaRepository, never()).salva(any(Tarefa.class));
    }

    private static Tarefa getTarefaAtiva(Usuario usuario) {
        return Tarefa.builder().contagemPomodoro(1).idTarefa(UUID.fromString("4c70c27a-446c-4506-b666-1067085d8d85"))
                .idUsuario(usuario.getIdUsuario()).descricao("descricao tarefa").statusAtivacao(StatusAtivacaoTarefa.ATIVA).build();
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    void testDeletaTarefa() {
    	UUID idTarefa = UUID.randomUUID();
    	String usuario = "exemplo@usuario.com";
    	Usuario usuarioMock = DataHelper.createUsuario();
    	Tarefa tarefaMock = DataHelper.createTarefa();
    	
    	when(usuarioRepository.buscaUsuarioPorEmail(usuario)).thenReturn(usuarioMock);
    	when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefaMock));
    	tarefaApplicationService.deletaTarefa(usuario, idTarefa);
    	
    	verify(tarefaRepository, times(1)).deletaTarefaPorId(tarefaMock);
    }
    
    @Test
    void naoDeveDeletarTarefaQuandoIdForInvalido() {
    	UUID idTarefa = UUID.fromString("385c48f2-49ab-485b-87b1-02d5de2f7710");
    	String usuarioEmail = "churupita@gmail.com";
    	Usuario usuario = DataHelper.createUsuario();
    	Tarefa tarefa = DataHelper.createTarefa();
    	
    	APIException ex = assertThrows(APIException.class,
    			() -> tarefaApplicationService.deletaTarefa(usuario.getEmail(), tarefa.getIdTarefa()));
    	
    	assertNotEquals(idTarefa, tarefa.getIdTarefa());
    	assertNotEquals(usuarioEmail, usuario.getEmail());
    	assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    }
}
