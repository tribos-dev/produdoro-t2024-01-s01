package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

	//@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //@MockBean
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
