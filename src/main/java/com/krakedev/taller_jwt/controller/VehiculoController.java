package com.krakedev.taller_jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.krakedev.taller_jwt.entidades.Vehiculo;
import com.krakedev.taller_jwt.repository.VehiculoRepository;

@RestController
@RequestMapping("/auth/vehiculo")
@CrossOrigin(origins="http://localhost:5173") // esta permiido que se consuma desde la 
public class VehiculoController {
	
	private final VehiculoRepository vehiculoRepository;

	public VehiculoController(VehiculoRepository vehiculoRepository) {
		super();
		this.vehiculoRepository = vehiculoRepository;
	}
	
	//
	@PostMapping("/registrar")
	public ResponseEntity<?> resgistrarVehiculo(
			@RequestParam("file") MultipartFile file,
			@RequestParam("marca") String marca,
			@RequestParam("modelo") String modelo
			){
		try {
			Vehiculo vehiculo = new Vehiculo();
			vehiculo.setMarca(marca);
			vehiculo.setModelo(modelo);
			vehiculo.setMimeType(file.getContentType());
			vehiculo.setFoto(file.getBytes());
			
			vehiculoRepository.save(vehiculo);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("Mensaje","Vehiculo registrado correctamente"));
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Mensaje","error al procesar el archivo => "+e.getMessage()));
		}
	}
	@GetMapping("/listar")
	public ResponseEntity<?> listarVehiculos(){
		List<Vehiculo> listaVehiculos=vehiculoRepository.findAll();
		
		//recorrer la lista para removr los bytes
		for(Vehiculo vehiculo:listaVehiculos) {
			vehiculo.setFoto(null);
		}
		return ResponseEntity.ok(listaVehiculos);
	}
	
	// ═══════════════════════════════════════════
	// ENDPOINT PARA OBTENER LA FOTO DE UN VEHÍCULO
	// ═══════════════════════════════════════════
	@GetMapping("/{id}/foto")
	public ResponseEntity<byte[]> obtenerFoto(@PathVariable Integer id){

	    // Busca el vehículo por su ID.
	    // Si no existe, lanza una excepción.
	    Vehiculo vehiculo = vehiculoRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado"));

	    // Obtiene el tipo MIME almacenado en la base de datos.
	    // Ejemplos: image/jpeg, image/png
	    String mimeType = vehiculo.getMimeType();

	    // Crea los encabezados HTTP de la respuesta.
	    HttpHeaders headers = new HttpHeaders();

	    // Configura el Content-Type para que el navegador
	    // sepa qué tipo de imagen está recibiendo.
	    headers.setContentType(MediaType.parseMediaType(mimeType));

	    // Devuelve:
	    // 1. Los bytes de la imagen
	    // 2. Los encabezados HTTP
	    // 3. El estado 200 OK
	    return new ResponseEntity<>(
	            vehiculo.getFoto(),
	            headers,
	            HttpStatus.OK
	    );
	}
	
}
