package ua.rokytne.bakery.orm;

public class ProductionHasIngridient {

	private int product_ID;
	private int ingridient_ID;
	private float count;
	
	
	public ProductionHasIngridient(int pID, int iID, float count){
		setProductionID(pID);
		setIngridientID(iID);
		setCount(count);
	}
	
	public void setProductionID(int value){
		this.product_ID = value;
	}

	public void setIngridientID(int value){
		this.ingridient_ID = value;
	}
	
	public void setCount(float value){
		this.count = value;
	}
	
	public int getProductionID(){
		return this.product_ID;
	}

	public int getIngridientID(){
		return this.ingridient_ID;
	}
	
	public float getCount(){
		return this.count;
	}
	
}
