package com.gfx6.tracker;

public class Posicion {
	private double _latitud, _longitud;
	private double velocidad;
	public double getLatitud()
	{
		return _latitud;
	}
	public void setLatitud(double l)
	{
		_latitud=l;
	}
	public double getLongitud()
	{
		return _longitud;
	}

	public void setLongitud(double l)
	{
		_longitud=l;
	}
	public double getVelocidad()
	{
		return velocidad;
	}
	public void setVelocidad(double v)
	{
		velocidad=v;
	}
}
