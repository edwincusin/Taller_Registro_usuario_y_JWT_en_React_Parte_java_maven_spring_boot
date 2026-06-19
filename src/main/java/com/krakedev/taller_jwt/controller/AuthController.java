package com.krakedev.taller_jwt.controller;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.taller_jwt.entidades.Usuario;
import com.krakedev.taller_jwt.repository.UsuarioRepository;
import com.krakedev.taller_jwt.services.TokenBlackListService;
import com.krakedev.taller_jwt.services.UsuarioService;
import com.krakedev.taller_jwt.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepositorio;
	private final TokenBlackListService blackListService;

	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepositorio, TokenBlackListService blackListService) {
		this.usuarioService = usuarioService;
		this.usuarioRepositorio = usuarioRepositorio;
		this.blackListService = blackListService;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

		try {
			Usuario usuarioGuardado = usuarioService.guardar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);

		} catch (DataIntegrityViolationException e) {

			return ResponseEntity.badRequest()
					.body("Los datos enviados violan una restricción de la base de datos: " + e.getMessage());

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno del servidor al guardar: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {

		try {
			String username = credenciales.get("username");
			String password = credenciales.get("password");

			boolean usuarioAutenticado = usuarioService.autenticar(username, password);

			if (usuarioAutenticado) {// true
				Usuario usuario = usuarioRepositorio.findByUserName(username).get();

				String token = JwtUtil.generarToken(usuario.getUserName(), usuario.getRol());// genera el token

				return ResponseEntity.ok(Map.of("token", token));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
			}

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al logear");
		}
	}

	// para probar un token

	@GetMapping("/perfil") // 
	// Endpoint protegido que muestra información del usuario autenticado
	public ResponseEntity<?> verPerfil() {

	    // Obtiene el objeto Authentication almacenado por Spring Security
	    // dentro del SecurityContext después de validar el JWT
	    Authentication auth = SecurityContextHolder
	            .getContext()
	            .getAuthentication();

	    // Obtiene el nombre del usuario autenticado
	    // Normalmente corresponde al username guardado en el JWT
	    String usuario = auth.getName();

	    // Obtiene el primer rol o autoridad asignada al usuario
	    // Ejemplo: ROLE_ADMIN o ROLE_USER
	    String rol = auth.getAuthorities()
	            .iterator()
	            .next()
	            .getAuthority();

	    // Devuelve una respuesta HTTP 200 con información
	    // extraída del contexto de seguridad
	    return ResponseEntity.status(HttpStatus.OK)
	            .body(Map.of(
	                    "Mensaje", "Bienvenido al sistema protegido por Spring Security",
	                    "Usuario", usuario,
	                    "Rol_detectado", rol,
	                    "Status", "Autenticado exitosamente"
	            ));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
	        @RequestHeader(value = "Authorization", required = false) String authHeader) {

	    // Verifica que el encabezado Authorization exista
	    // y que tenga el formato: "Bearer <token>"
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {

	        // Extrae únicamente el JWT eliminando el prefijo "Bearer "
	        String token = authHeader.substring(7);
	        
	        //validar si la session ya fue cerrada  para no volver a invalidar algo cerrrado
	        if (blackListService.estaInvalidado(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("La sesión ya fue cerrada");
	        }

	        // Agrega el token a la blacklist para impedir que vuelva a utilizarse
	        blackListService.invalidarToken(token);

	        // Retorna una respuesta indicando que el cierre de sesión fue exitoso
	        return ResponseEntity.status(HttpStatus.OK)
	                .body(Map.of("mensaje",
	                        "Sesión cerrada exitosamente. Token invalidado."));
	    } else {

	        // Retorna un error si no se recibió el token o el formato es incorrecto
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Token no proporcionado");
	    }
	}
	
	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> adminDashBoard(){
		
		String usuario=SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(Map.of("Mensaje: ","Bienvenido al panel secreto de administradores","Admin: ",usuario));
		
	}
	
}