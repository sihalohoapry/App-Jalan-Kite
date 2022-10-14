package com.development.jalankite.ui.home;

import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("nama_lokasi")
	private String namaLokasi;

	@SerializedName("alamat_lokasi")
	private String alamatLokasi;

	@SerializedName("foto")
	private String foto;

	@SerializedName("deskripsi_lokasi")
	private String deskripsiLokasi;

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("id")
	private int id;

	@SerializedName("longitude")
	private double longitude;

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

	public int getId(){
		return id;
	}

	public double getLongitude(){
		return longitude;
	}
}