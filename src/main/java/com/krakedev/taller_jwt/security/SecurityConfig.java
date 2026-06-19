package com.krakedev.taller_jwt.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	// Variable que almacenará una instancia de tu filtro JWT
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	// Constructor de la clase SecurityConfig
	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	// Indica a Spring que el objeto retornado será un Bean administrado
	@Bean
	// Método que configura toda la seguridad de la aplicación
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {

		// Retorna la configuración completa de seguridad
		return http.cors(Customizer.withDefaults()) // habilitamos para que puedan usar la api

				// Desactiva la protección CSRF
				.csrf(csrf -> csrf.disable())

				// Configura la gestión de sesiones
				.sessionManagement(session ->

				// Indica que la aplicación no utilizará sesiones HTTP
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Configura las reglas de autorización
				.authorizeHttpRequests(auth -> auth

						// Permite acceso sin autenticación a estas rutas
						.requestMatchers("/auth/login", "/auth/registrar")

						// Permite que cualquier usuario acceda a esas rutas
						.permitAll()

						// Cualquier otra ruta requiere autenticación
						.anyRequest()

						// Exige que el usuario esté autenticado
						.authenticated())

				// Agrega tu filtro JWT antes del filtro de autenticación de Spring Security
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

				// Construye y devuelve la cadena de filtros de seguridad
				.build();
	}

	@Bean // le dice a Spring: "crea y administra este objeto automáticamente"
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration(); // crea las reglas de quién puede llamar a tu API

		configuration.setAllowedOrigins(List.of("http://localhost:5173")); // solo React (Vite) puede hacer peticiones

		configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS", "PUT")); // métodos HTTP permitidos

		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // headers permitidos: el token JWT y
																					// el tipo de dato (JSON)

		configuration.setAllowCredentials(true); // permite enviar credenciales (como el token) en las peticiones

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // contenedor que aplica estas
																						// reglas

		source.registerCorsConfiguration("/**", configuration); // aplica las reglas a TODAS las rutas de tu API ("/**"
																// = todo)

		return source; // devuelve la configuración para que Spring Security la use
	}
}
