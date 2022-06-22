package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.model.Usuario;


public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);


}
