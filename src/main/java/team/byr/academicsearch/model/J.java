package team.byr.academicsearch.model;

//Journal 
public class J {
	private String JN; //Journal name
	private long JId;  //Journal ID
	public String getJN() {
		return JN;
	}
	public void setJN(String jN) {
		JN = jN;
	}
	public long getJId() {
		return JId;
	}
	public void setJId(long jId) {
		JId = jId;
	}
	@Override
	public String toString() {
		return "J [JN=" + JN + ", JId=" + JId + "]";
	}

}
