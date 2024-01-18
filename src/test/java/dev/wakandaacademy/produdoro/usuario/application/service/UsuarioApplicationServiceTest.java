package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void deveAlterarStatusParaFoco_QuandoStatusForDiferenteDeFoco(){
        // dado
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");

        // quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        usuarioApplicationService.alteraStatusParaFoco(String.valueOf(usuario), idUsuario);

        // entao
        verify(usuarioRepository, times(1)).salva(usuario);
    }

    @Test
    void naoDeveAlterarStatusParaFoco_QuandoStatusForIgualAFoco(){
        UUID idUsuario = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");
        Usuario usuario = Usuario.builder().email("email@email.com").status(StatusUsuario.FOCO).idUsuario(idUsuario).build();

        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);

        APIException exception = assertThrows(APIException.class, () -> {
            usuarioApplicationService.alteraStatusParaFoco(String.valueOf(usuario), idUsuario);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
        verify(usuarioRepository, never()).salva(any(Usuario.class));
    }
}