package com.krakedev.taller_jwt.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	@PostMapping("/registro")
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
			
			return ResponseEntity.status(HttpStatus.CREATED).body("Vehiculo registrado correctamente");
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Mensaje","error al procesar el archivo => "+e.getMessage()));
		}
	}
	
	public ResponseEntity<?> listarVehiculos(){
		List<Vehiculo> listaVehiculos=vehiculoRepository.findAll();
		
		//recorrer la lista para removr los bytes
		for(Vehiculo vehiculo:listaVehiculos) {
			vehiculo.setFoto(null);
		}
		return ResponseEntity.ok(listaVehiculos);
	}
	
}
