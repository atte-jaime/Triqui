package Triqui;

import java.util.Observable;
import java.util.Observer;

import processing.core.PApplet;

public class Logica implements Observer{

	private PApplet app;
	private Comunicacion com;
	public Logica(PApplet app) {
		this.app=app;
		com = new Comunicacion();
		Thread hilo = new Thread(com);
		hilo.start();
		com.addObserver(this);
	}
	
	public void ejecutar() {
		app.background(100,0,150);
		app.fill(255);
		app.textSize(25);
		app.textAlign(PApplet.CENTER, PApplet.CENTER);
		app.text(com.getIdentifier(), app.width/2, app.height/2);
	}
	
	public void pressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}
