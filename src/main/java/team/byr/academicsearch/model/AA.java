package team.byr.academicsearch.model;

public class AA {
	
	private String AuN; //Author name
	private long AuId;  //Author ID
	private String SfN; //Author affiliation name
	private long AfId;  //Author affiliation ID
	public String getAuN() {
		return AuN;
	}
	public void setAuN(String auN) {
		AuN = auN;
	}

	public long getAuId() {
		return AuId;
	}
	public void setAuId(long auId) {
		AuId = auId;
	}
	public String getSfN() {
		return SfN;
	}
	public void setSfN(String sfN) {
		SfN = sfN;
	}
	public long getAfId() {
		return AfId;
	}
	public void setAfId(long afId) {
		AfId = afId;
	}
	@Override
	public String toString() {
		return "AA [AuN=" + AuN + ", AuId=" + AuId + ", SfN=" + SfN + ", AfId="
				+ AfId + "]";
	}
	

}
