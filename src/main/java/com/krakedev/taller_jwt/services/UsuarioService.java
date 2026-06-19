package com.krakedev.taller_jwt.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.taller_jwt.entidades.Usuario;
import com.krakedev.taller_jwt.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
    	
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario guardar(Usuario usuario) {
    	String passEncrip= BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
    	usuario.setPassword(passEncrip);
        return usuarioRepository.save(usuario);
    }

    public boolean autenticar(String userName, String password) {

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUserName(userName);

        if (usuarioEncontrado.isPresent()) {

            Usuario usuario = usuarioEncontrado.get();

            if (BCrypt.checkpw(password, usuario.getPassword())) {
                return true;
            }
        }

        return false;
    }
}