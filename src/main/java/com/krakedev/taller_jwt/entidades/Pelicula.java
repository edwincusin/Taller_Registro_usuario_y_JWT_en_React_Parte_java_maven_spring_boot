package com.krakedev.taller_jwt.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "peliculas")
public class Pelicula {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "genero", nullable = false)
	private String genero;

	@Column(name = "sinopis", nullable = false)
	private String sinopis;

	@Column(name = "mime_Type")
	private String mimeType;

	@Column(name = "foto", columnDefinition = "bytea")
	private byte[] foto;

	public Pelicula() {
	}

	public Pelicula(String titulo, String genero, String sinopis, String mimeType, byte[] foto) {
		super();
		this.titulo = titulo;
		this.genero = genero;
		this.sinopis = sinopis;
		this.mimeType = mimeType;
		this.foto = foto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getSinopis() {
		return sinopis;
	}

	public void setSinopis(String sinopis) {
		this.sinopis = sinopis;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

}
