package question2;

import java.util.Date;
import java.io.Serializable;

public class RendezVous implements Serializable{
	private Date   date;
	private String motif;
	
	public static final RendezVous MAINTENANT = new RendezVous(new Date(System.currentTimeMillis()),"urgent ...");
	
	public RendezVous(Date date, String motif){
	  this.date = date;
	  this.motif = motif;
	 }
	 
	 public Date getDate(){
	   return this.date;
	 }
	 
	 public String getMotif(){
	   return this.motif;
	 }	 

	 public String toString(){
	   return "[" + getDate() + ":" + getMotif() + "]";
	 }
	   
	 public boolean equals(Object obj){
	   RendezVous r = (RendezVous) obj;
	   return getDate().equals(r.getDate());
	 }
}
