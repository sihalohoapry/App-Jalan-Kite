package com.development.jalankite.ui.home;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AllLokasiResponse{
	@SerializedName("msg")
	private String msg;

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("status")
	private boolean status;

	public String getMsg(){
		return msg;
	}

	public List<DataItem> getData(){
		return data;
	}

	public boolean isStatus(){
		return status;
	}
}