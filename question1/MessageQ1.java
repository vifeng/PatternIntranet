package question1;

import java.util.Date;
import java.io.Serializable;

public class MessageQ1 implements Serializable {
    private String texte;
    private Date estampille;
    public MessageQ1(String texte){
        this.texte = texte;
        this.estampille = null;
    }

    public String getTexte(){
        return this.texte;
    }

    public Date getEstampille(){
        return this.estampille;
    }

    public void setEstampille(Date d){
        this.estampille = d;
    }

    public String toString(){
        return "<" + texte + ":" + estampille + ">";
    }
}
