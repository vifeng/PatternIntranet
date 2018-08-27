package question2;

public class UnScenario{
    public static void main(String[] args) throws Exception{
        Souscription s1=null,s2=null,s3=null;
        PriseDeRDV priseDeRDV=null;
        try{
            s1 = new Souscription("agenda");
            s2 = new Souscription("agenda");
            s3 = new Souscription("agenda");

            priseDeRDV = new PriseDeRDV("agenda");
            priseDeRDV.publier(RendezVous.MAINTENANT);
            //Thread.sleep(1000000);
        }finally{
            s1.close();s2.close();s3.close();
            priseDeRDV.close();
        }
    }
}
