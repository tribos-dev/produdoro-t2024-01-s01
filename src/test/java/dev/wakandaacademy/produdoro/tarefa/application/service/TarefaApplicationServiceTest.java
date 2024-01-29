package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

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
	void deveAtivarTarefaEInativarAAnterior() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();
		Tarefa tarefaAtiva = getTarefaAtiva(usuario);
		UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");

		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(eq(idTarefa))).thenReturn(Optional.of(tarefa));
		when(tarefaRepository.buscarTarefaAtiva()).thenReturn(tarefaAtiva);

		tarefaApplicationService.definirTarefaComoAtiva(String.valueOf(usuario), idTarefa);

		verify(tarefaRepository, times(1)).salva(tarefa);
		verify(tarefaRepository, times(1)).buscarTarefaAtiva();
		verify(tarefaRepository, times(1)).salva(tarefa);
	}

	@Test
	void deveRetornarBadRequestENaoAtivarTarefa_QuandoPassarIdTarefaInvalido() {
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
				.idUsuario(usuario.getIdUsuario()).descricao("descricao tarefa")
				.statusAtivacao(StatusAtivacaoTarefa.ATIVA).build();
	}

	public TarefaRequest getTarefaRequest() {
		TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
		return request;
	}

	@Test
	void deveIncrementaUmPomodoroAUmaTarefa() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
		tarefaApplicationService.incrementaPomodoro(tarefa.getIdTarefa(), usuario.getEmail());

		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
		verify(tarefaRepository, times(1)).buscaTarefaPorId(tarefa.getIdTarefa());
		assertEquals(2, tarefa.getContagemPomodoro());
	}

	@Test
	void naoDeveIncrementarUmPomodoroUmaTarefa() {
		Tarefa tarefa = DataHelper.createTarefa();
		UUID idTarefa = tarefa.getIdTarefa();
		Usuario usuario2 = DataHelper.createUsuarioDiferente();
		String usuarioEmail = usuario2.getEmail();

		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario2);
		when(tarefaRepository.buscaTarefaPorId(any(UUID.class))).thenReturn(Optional.of(tarefa));
		APIException ex = assertThrows(APIException.class, () -> {
			tarefaApplicationService.incrementaPomodoro(idTarefa, usuarioEmail);
		});
		assertEquals("Usuário não é dono da Tarefa solicitada!", ex.getMessage());
	}

	@Test
	@DisplayName("Teste Conclui Tarefa")
	public void deveConcluirTarefa() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
		tarefaApplicationService.concluiTarefa(usuario.getEmail(), tarefa.getIdTarefa());
		assertEquals(tarefa.getStatus(), StatusTarefa.CONCLUIDA);
	}

	@Test
	@DisplayName("Teste Não Conclui Tarefa")
	public void naoConcluiTarefa() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenThrow(APIException.class);
		assertThrows(APIException.class,
				() -> tarefaApplicationService.concluiTarefa("emailInvalido@gmail.com", tarefa.getIdTarefa()));
	}

	@Test
	public void testBuscaTodasTarefasPorUsuario() {
		Usuario usuario = DataHelper.createUsuario();
		List<Tarefa> tarefas = DataHelper.createListTarefa();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefasPorUsuario(any())).thenReturn(tarefas);

		List<TarefaListResponse> resultado = tarefaApplicationService.buscaTarefasPorUsuario(usuario.getEmail(),
				usuario.getIdUsuario());

		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
		verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
		verify(tarefaRepository, times(1)).buscaTarefasPorUsuario(usuario.getIdUsuario());

		assertEquals(resultado.size(), 8);
	}

	@Test
	public void testNaoDeveBuscaTodasTarefasPorUsuario() {
		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(any()))
				.thenThrow(APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"));

		APIException e = assertThrows(APIException.class, () -> tarefaApplicationService
				.buscaTarefasPorUsuario("emailinvalido@gmail.com", usuario.getIdUsuario()));

		assertEquals(HttpStatus.BAD_REQUEST, e.getStatusException());
		assertEquals("Usuario não encontrado!", e.getMessage());
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
