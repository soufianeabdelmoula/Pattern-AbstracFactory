package ex2;

public class Client {

	public static void main(String[] args) {
		// affichage pour ordinateur
		OrdiFact  Ordifact=new OrdiFact();
		
		Bouton orb=Ordifact.getBouton();
		textArea orzt=Ordifact.getZoneText();
		ListeChoix orlc=Ordifact.getListeChoix();
		
		// affichage pour tablette
		TabFac  tabfact=new TabFac();
		
		Bouton tabb=tabfact.getBouton();
		textArea tabzt=tabfact.getZoneText();
		ListeChoix tabdlc=tabfact.getListeChoix();
		
		// affichage pour telephone
		TelFact  telfact=new TelFact();
		
		Bouton telb=telfact.getBouton();
		textArea telzt=telfact.getZoneText();
		ListeChoix tellc=telfact.getListeChoix();
		
	}

}
