package com.krakedev.taller_jwt.util;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    // Contraseña secreta que se usa para firmar el token.
    // Debe ser privada y no compartirse.
    private static final String CLAVE_SECRETA ="EstaEsUnaClaveEsSuperSecretayMuyLarga12346789!";

    // Nombre de la aplicación que genera el token.
    private static final String EMISOR = "KrakeDevBackend";

    // El token durará 1 hora.
    // 1000 ms = 1 segundo
    // 1000 * 60 * 60 = 3600000 ms = 1 hora
    private static final long TIEMPO_EXPIRACION = 3600000;

    public static String generarToken(String userName, String rol) {

        // Crea el algoritmo de encriptación usando la clave secreta.
        Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

        // Obtiene la fecha y hora actual.
        long tiempoActual = System.currentTimeMillis();

        // Calcula cuándo expirará el token.
        Date fechaExpiracion =new Date(tiempoActual + TIEMPO_EXPIRACION);

        // Comienza a construir el token JWT.
        String tokenGenerado = JWT.create()

                // Guarda quién creó el token.
                .withIssuer(EMISOR)

                // Guarda el nombre del usuario.
                .withSubject(userName)

                // Guarda la fecha de creación.
                .withIssuedAt(new Date(tiempoActual))

                // Guarda la fecha de expiración.
                .withExpiresAt(fechaExpiracion)

                // Guarda información adicional.
                // En este caso el rol del usuario.
                .withClaim("rol", rol)

                // Firma el token usando la clave secreta.
                .sign(algoritmo);

        // Devuelve el token generado.
        return tokenGenerado;
    }
    
    //validar token
    
    public static DecodedJWT validarToker(String token) {

        try {

            // Se crea el mismo algoritmo usado para firmar el token.
            // La clave secreta debe ser exactamente la misma que se usó
            // cuando se generó el JWT.
            Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

            // Se crea un verificador del token.
            // También se verifica que el emisor (issuer)
            // sea el esperado.
            JWTVerifier verificador = JWT.require(algoritmo)
                                         .withIssuer(EMISOR)
                                         .build();

            // Verifica que el token sea válido:
            // - Que no haya sido modificado.
            // - Que la firma sea correcta.
            // - Que no haya expirado.
            // - Que el emisor sea correcto.
            DecodedJWT tokenDecodificado = verificador.verify(token);

            // Si todo está correcto, devuelve el token decodificado.
            return tokenDecodificado;

        } catch (Exception e) {

            // Si ocurre cualquier error significa que el token
            // no es válido o ya expiró.

            // Se devuelve null para indicar que la validación falló.
            return null;
        }
    }
}