package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioCriadoResponse;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;

import java.util.UUID;

public interface UsuarioService {
	UsuarioCriadoResponse criaNovoUsuario(UsuarioNovoRequest usuarioNovo);
    UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario);
	void mudaStatusPausaLonga(String usuarioEmail, UUID idUsuario);

    void mudaStatusPausaCurta(String usuario, UUID idUsuario);

    void alteraStatusParaFoco(String usuario, UUID idUsuario);
}
