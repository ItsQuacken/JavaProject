package hr.java.projektnizadatak.entities;

public sealed interface Recordable permits User {
	String displayShort();
	
	String displayFull();
}
