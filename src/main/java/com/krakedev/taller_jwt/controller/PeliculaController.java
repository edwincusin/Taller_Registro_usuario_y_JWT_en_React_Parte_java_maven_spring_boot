package com.krakedev.taller_jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	
	//METODO LISTAR
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Pelicula> peliculas=peliculaRepository.findAll();
		return ResponseEntity.ok(peliculas);
	}
	
	//METODO CREAR 
	@PostMapping
	public ResponseEntity<?> crear(
			@RequestParam("file") MultipartFile file,
			@RequestParam("titulo") String titulo,
			@RequestParam("genero") String genero,
			@RequestParam("sinopsis") String sinopsis
			){
		try {
			Pelicula peliculaNueva=new Pelicula();
			peliculaNueva.setTitulo(titulo);
			peliculaNueva.setGenero(genero);
			peliculaNueva.setSinopis(sinopsis);
			peliculaNueva.setMimeType(file.getContentType());
			peliculaNueva.setFoto(file.getBytes());
			
			peliculaRepository.save(peliculaNueva);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("Mensaje","Registro existoso"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Mensaje","Error interno del sistema al crear pelicula"));
		}
	}
}
