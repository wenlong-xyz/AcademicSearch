package team.byr.academicsearch.model;

//Conference 
public class C {
	private String CN;  //Conference series name
	private long CId;   //Conference series ID
	public String getCN() {
		return CN;
	}
	public void setCN(String cN) {
		CN = cN;
	}
	public long getCId() {
		return CId;
	}
	public void setCId(long cId) {
		CId = cId;
	}
	@Override
	public String toString() {
		return "C [CN=" + CN + ", CId=" + CId + "]";
	}
	
	
}
