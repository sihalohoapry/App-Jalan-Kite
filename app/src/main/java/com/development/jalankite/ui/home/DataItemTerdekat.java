package com.development.jalankite.ui.home;


public class DataItemTerdekat {
	private String namaLokasi;
	private String alamatLokasi;
	private String foto;
	private String deskripsiLokasi;
	private double latitude;
	private double longitude;
	private double jarak;

	public DataItemTerdekat(String namaLokasi, String alamatLokasi, String foto, String deskripsiLokasi, double latitude, double longitude, double jarak) {
		this.namaLokasi = namaLokasi;
		this.alamatLokasi = alamatLokasi;
		this.foto = foto;
		this.deskripsiLokasi = deskripsiLokasi;
		this.latitude = latitude;
		this.longitude = longitude;
		this.jarak = jarak;
	}

	public String getNamaLokasi(){
		return namaLokasi;
	}
	public String getAlamatLokasi(){
		return alamatLokasi;
	}
	public String getFoto(){
		return foto;
	}
	public String getDeskripsiLokasi(){
		return deskripsiLokasi;
	}
	public double getLatitude(){
		return latitude;
	}
	public double getLongitude(){
		return longitude;
	}
	public double getJarak(){
		return jarak;
	}
}