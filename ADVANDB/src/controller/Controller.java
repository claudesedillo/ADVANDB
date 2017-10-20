package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Database;
import view.View;

public class Controller {
	
	public Database database;
	public View view;
	
	public void start(){
		init();
	}
	
	public void init(){
		View view = new View(this);
	}
	
	public class queryAL implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			database.executeQuery();
		}
	}
}
