package ex2;

public class OrdiFact implements FormulaireFactory{

	@Override
	public OrdiBouton getBouton() {
		// TODO Auto-generated method stub
		return new OrdiBouton();
	}

	@Override
	public OrdiListeChoix getListeChoix() {
		// TODO Auto-generated method stub
		return new OrdiListeChoix();
	}

	@Override
	public OrdiZoneText getZoneText() {
		// TODO Auto-generated method stub
		return new OrdiZoneText();
	}

}
