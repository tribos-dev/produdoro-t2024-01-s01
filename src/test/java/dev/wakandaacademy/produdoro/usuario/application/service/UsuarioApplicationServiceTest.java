package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

class UsuarioApplicationServiceTest {
	
	@InjectMocks
	UsuarioApplicationService usuarioApplicationService;
	
	@Mock
	UsuarioRepository usuarioRepository;
	
	@Test
	void UsuarioMudaStatusPausaLongaSucesso() {
		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
		verify(usuarioRepository, times(1)).salva(any());
	}
	
	@Test
	void UsuarioMudaStatusPausaLongaFalha() {
		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		APIException ex = assertThrows(APIException.class,
				() -> usuarioApplicationService.mudaStatusPausaLonga(usuario.getEmail(), UUID.randomUUID()));
		assertEquals(APIException.class, ex.getClass());
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
		assertEquals("credencial de autenticação não e valida!",ex.getMessage());
	}
				
	
}