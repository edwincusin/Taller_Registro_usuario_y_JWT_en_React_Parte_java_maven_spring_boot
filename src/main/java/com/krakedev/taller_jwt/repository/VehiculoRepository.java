package com.krakedev.taller_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krakedev.taller_jwt.entidades.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

}
