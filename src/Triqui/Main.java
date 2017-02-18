package Triqui;

import processing.core.PApplet;

public class Main extends PApplet{

	public static void main(String[] args) {
		PApplet.main("Triqui.Main");
	}
	
	public void settings() {
		size(500,500);
	}
	
	Logica log;
	
	public void setup() {
	log = new Logica(this);
	}
	
	public void draw() {
	log.ejecutar();
	}
	
	public void mousePressed() {
	log.pressed();
	}

}
