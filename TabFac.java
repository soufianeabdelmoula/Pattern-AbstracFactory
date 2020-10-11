package ex2;

public class TabFac implements FormulaireFactory {

	@Override
	public tabBouton getBouton() {
		// TODO Auto-generated method stub
		return new tabBouton();
	}

	@Override
	public tabListeChoix getListeChoix() {
		// TODO Auto-generated method stub
		return new tabListeChoix();
	}

	@Override
	public tabZoneText getZoneText() {
		// TODO Auto-generated method stub
		return new tabZoneText();
	}


}
