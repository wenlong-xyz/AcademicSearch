package team.byr.academicsearch.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class Entity {
	private long Id;    //Entity ID
	private String Ti;  //Paper title
	private int Y;	    //Paper year
	private Date D;	    //Paper date
	private int CC;     //Citation count
	private List<AA> AA;//Author
	private List<F> F;  //Field of study
	private J J;  //Journal 
	private C C;        //Conference series
	private List<Long> RId;   //Reference ID
	private String W;   //Words from paper title/abstract for full text search
	private Set<F> fSet;
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
	public String getTi() {
		return Ti;
	}
	public void setTi(String ti) {
		Ti = ti;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public Date getD() {
		return D;
	}
	public void setD(Date d) {
		D = d;
	}
	public int getCC() {
		return CC;
	}
	public void setCC(int cC) {
		CC = cC;
	}
	public List<AA> getAA() {
		return AA;
	}
	public void setAA(List<AA> aA) {
		AA = aA;
	}
	public List<F> getF() {
		return F;
	}
	public void setF(List<F> f) {
		F = f;
	}
	public J getJ() {
		return J;
	}
	public void setJ(J j) {
		J = j;
	}
	public C getC() {
		return C;
	}
	public void setC(C c) {
		C = c;
	}
	public List<Long> getRId() {
		return RId;
	}
	public void setRId(List<Long> rId) {
		RId = rId;
	}
	public String getW() {
		return W;
	}
	public void setW(String w) {
		W = w;
	}
	public Set<F> getfSet() {
		return fSet;
	}
	public void setfSet(Set<F> fSet) {
		this.fSet = fSet;
	}
	@Override
	public String toString() {
		return "Entity [Id=" + Id + ", Ti=" + Ti + ", Y=" + Y + ", D=" + D
				+ ", CC=" + CC + ", AA=" + AA + ", F=" + F + ", J=" + J
				+ ", C=" + C + ", RId=" + RId + ", W=" + W + ", fSet=" + fSet
				+ "]";
	}
	
	
}
