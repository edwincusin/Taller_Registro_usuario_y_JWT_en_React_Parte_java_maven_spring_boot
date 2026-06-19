package com.krakedev.taller_jwt.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.taller_jwt.services.TokenBlackListService;
import com.krakedev.taller_jwt.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // componente especial
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final TokenBlackListService blackList;
	
	
	public JwtAuthenticationFilter(TokenBlackListService blackList) {
		super();
		this.blackList = blackList;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    // Obtiene el valor de la cabecera Authorization enviada por el cliente
	    String authHeader = request.getHeader("Authorization");

	    // Verifica si la cabecera no existe o no comienza con "Bearer "
	    if(authHeader == null || !authHeader.startsWith("Bearer ")) {

	        // Permite que la petición continúe sin autenticación JWT
	        filterChain.doFilter(request, response);
	        return;
	    }

	    // Extrae el token eliminando el prefijo "Bearer "
	    String token = authHeader.substring(7);

	    // Verifica si el token se encuentra en la blacklist
	    if (blackList.estaInvalidado(token)) {

	        // Devuelve el código HTTP 401 Unauthorized
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

	        // Escribe un mensaje en la respuesta indicando que la sesión fue cerrada
	        response.getWriter().write("Acceso denegado: sesion cerrada");

	        // Finaliza la ejecución del filtro
	        return;
	    }

	    // Valida el token JWT y obtiene su contenido decodificado
	    DecodedJWT datosToken = JwtUtil.validarToker(token);

	    // Verifica que el token sea válido
	    if(datosToken != null) {

	        // Obtiene el username almacenado en el Subject del JWT
	        String username = datosToken.getSubject();

	        // Obtiene el rol almacenado en el claim "rol"
	        String rolOriginal = datosToken.getClaim("rol").asString();

	        // Agrega el prefijo ROLE_ requerido por Spring Security
	        String rolSpring = "ROLE_" + rolOriginal;

	        // Crea una autoridad de Spring Security basada en el rol
	        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpring);

	        // Crea un objeto Authentication con:
	        // - usuario autenticado
	        // - contraseña nula (porque ya se validó el JWT)
	        // - lista de autoridades del usuario
	        UsernamePasswordAuthenticationToken authentication =
	                new UsernamePasswordAuthenticationToken(
	                        username,
	                        null,
	                        Collections.singleton(authority));

	        // Guarda la autenticación en el contexto de seguridad de Spring
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	    }

	    // Continúa con el siguiente filtro o controlador de la cadena
	    filterChain.doFilter(request, response);
	}
}
