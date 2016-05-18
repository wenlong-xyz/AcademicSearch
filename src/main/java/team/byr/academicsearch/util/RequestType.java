package team.byr.academicsearch.util;

public enum RequestType {
	ID("Id"),RID("RId"),FID("F.FId"),CID("C.CId"),JID("J.JId"),AUID("AA.AuId"),AFID("AA.AfId");
	private String name;
	private RequestType(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}

}
