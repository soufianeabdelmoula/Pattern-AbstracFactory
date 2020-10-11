package ex2;

public class TelFact implements FormulaireFactory {

	@Override
	public telBouton getBouton() {
		// TODO Auto-generated method stub
		return new telBouton();
	}

	@Override
	public telListeChoix getListeChoix() {
		// TODO Auto-generated method stub
		return new telListeChoix();
	}

	@Override
	public  telZoneText getZoneText() {
		// TODO Auto-generated method stub
		return new telZoneText();
	}

}
