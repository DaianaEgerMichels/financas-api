package com.github.daianaegermichels.financas.service;

import com.github.daianaegermichels.financas.exception.ErroAutenticacao;
import com.github.daianaegermichels.financas.exception.RegraNegocioException;
import com.github.daianaegermichels.financas.model.Usuario;
import com.github.daianaegermichels.financas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    private final UsuarioRepository usuarioRepository;
    private PasswordEncoder encoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        var usuario = usuarioRepository.findByEmail(email);

        if(!usuario.isPresent()){
            throw new ErroAutenticacao("Usuário não encontrado para o email informado!");
        }

        if(!usuario.get().getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha inválida!");
        }

        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        criptografarSenha(usuario);
        return usuarioRepository.save(usuario);
    }

    private void criptografarSenha(Usuario usuario) {
        String  senha = usuario.getSenha();
        String senhaCriptografada = encoder.encode(senha);
        usuario.setSenha(senhaCriptografada);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = usuarioRepository.existsByEmail(email);
        if(existe){
            throw new RegraNegocioException("Já existe um usuário com este email!");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return usuarioRepository.findById(id);
    }


}
