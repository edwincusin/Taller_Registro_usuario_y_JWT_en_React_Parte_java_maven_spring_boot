package com.krakedev.taller_jwt.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.krakedev.taller_jwt.entidades.Pelicula;
import com.krakedev.taller_jwt.repository.PeliculaRepository;

@RestController
@RequestMapping("/auth/peliculas")
@CrossOrigin(origins="http://localhost:5173") // esta permiido que se consuma desde la 
public class PeliculaController {

	private final PeliculaRepository peliculaRepository;

	public PeliculaController(PeliculaRepository peliculaRepository) {
		super();
		this.peliculaRepository = peliculaRepository;
	}

	// METODO LISTAR
	@GetMapping
	public ResponseEntity<?> listar() {
		List<Pelicula> peliculas = peliculaRepository.findAll();
		return ResponseEntity.ok(peliculas);
	}

	// METODO CREAR
	@PostMapping
	public ResponseEntity<?> crear(@RequestParam("file") MultipartFile file, @RequestParam("titulo") String titulo,
			@RequestParam("genero") String genero, @RequestParam("sinopsis") String sinopsis) {
		try {
			Pelicula peliculaNueva = new Pelicula();
			peliculaNueva.setTitulo(titulo);
			peliculaNueva.setGenero(genero);
			peliculaNueva.setSinopis(sinopsis);
			peliculaNueva.setMimeType(file.getContentType());
			peliculaNueva.setFoto(file.getBytes());

			peliculaRepository.save(peliculaNueva);

			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("Mensaje", "Registro existoso"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("Mensaje", "Error interno del sistema al crear pelicula"));
		}
	}

	// METODO EDITAR PELICULA
	@PutMapping("/{id}")
	public ResponseEntity<?> editar(
			@PathVariable("id") Integer id,
			@RequestParam("titulo") String titulo,
			@RequestParam("genero") String genero,
			@RequestParam("sinopsis") String sinopsis,
			@RequestParam(value="file", required=false) MultipartFile file
			){
		
		try {
			Optional<Pelicula> peliculaOptionalRecuperadaBDD=peliculaRepository.findById(id);
			
			if (peliculaOptionalRecuperadaBDD.isPresent()) {
				Pelicula pelicula=peliculaOptionalRecuperadaBDD.get(); // aqui sacamos del envoltorio de optional el obejeto recuperado
				//seteamos en el mismo objeto lo que nos viene del front
				
				pelicula.setTitulo(titulo);
				pelicula.setGenero(genero);
				pelicula.setSinopis(sinopsis);
				
				if(file!=null && !file.isEmpty()) {
					pelicula.setFoto(file.getBytes());
					pelicula.setMimeType(file.getContentType());
				}
				
				peliculaRepository.save(pelicula);
				return ResponseEntity.status(HttpStatus.OK).body(Map.of("Mensaje","Edision exitosa"));
				
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensjae","Pelicula no encontrada en la BDD"));
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Mensaje","Error al realizar la edicion"));
		}
	}

	// METODO ELIMINAR PELICULA
	@DeleteMapping("/{id}")
		public ResponseEntity<?> eliminar(@PathVariable("id") Integer id	){
			
			try {
				boolean existePelicula=peliculaRepository.existsById(id);
				
				if (existePelicula) {
										
					peliculaRepository.deleteById(id);
					return ResponseEntity.status(HttpStatus.OK).body(Map.of("Mensaje","Se elimino con exito de la BDD"));
					
				}else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensjae","Pelicula no encontrada en la BDD"));
				}
				
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Mensaje","Error al eliminar."));
			}
		}
	
	// ═══════════════════════════════════════════
	// ENDPOINT PARA OBTENER LA FOTO DE UNA PELICULA
	// ═══════════════════════════════════════════
	@GetMapping("/{id}/foto")
	public ResponseEntity<byte[]> obtenerFoto(@PathVariable Integer id){

	    // Busca la pelicula por su ID.
	    // Si no existe, lanza una excepción.
	    Pelicula pelicula = peliculaRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));

	    // Obtiene el tipo MIME almacenado en la base de datos.
	    String mimeType = pelicula.getMimeType();

	    // Crea los encabezados HTTP de la respuesta.
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(mimeType));

	    // Devuelve los bytes de la imagen + headers + 200 OK
	    return new ResponseEntity<>(
	            pelicula.getFoto(),
	            headers,
	            HttpStatus.OK
	    );
	}
}
