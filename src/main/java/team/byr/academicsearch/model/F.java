package team.byr.academicsearch.model;

public class F {
	private String FN;  //Field of study name
	private long FId;   //Field of study ID
	public String getFN() {
		return FN;
	}
	public void setFN(String fN) {
		FN = fN;
	}
	public long getFId() {
		return FId;
	}
	public void setFId(long fId) {
		FId = fId;
	}
	@Override
	public String toString() {
		return "F [FN=" + FN + ", FId=" + FId + "]";
	}
	

}
