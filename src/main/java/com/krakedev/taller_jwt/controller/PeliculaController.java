package com.krakedev.taller_jwt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.taller_jwt.entidades.Pelicula;
import com.krakedev.taller_jwt.repository.PeliculaRepository;

@RestController
@RequestMapping("/auth/peliculas")
public class PeliculaController {
	
	private final PeliculaRepository peliculaRepository;

	public PeliculaController(PeliculaRepository peliculaRepository) {
		super();
		this.peliculaRepository = peliculaRepository;
	}
	
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Pelicula> peliculas=peliculaRepository.findAll();
		return ResponseEntity.ok(peliculas);
	}
}
