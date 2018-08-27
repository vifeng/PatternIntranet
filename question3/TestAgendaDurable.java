package question3;

import question2.*;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;
import java.util.concurrent.*;
import java.util.Date;

public class TestAgendaDurable extends junit.framework.TestCase{

    private SynchronousQueue<RendezVous> queue1,queue2,queue3;
    public void test_souscripteur_durable(){
        Souscription s1=null,s2=null,s3=null;
        PriseDeRDV priseDeRDV=null;
        queue1 = new SynchronousQueue<RendezVous>();
        try{
            priseDeRDV = new PriseDeRDV("agenda_tests");
            RendezVous r1 = new RendezVous(new Date(40000L),"test");
            priseDeRDV.publier(r1);
            System.out.println("envoi !!!" + r1);

            s1 = new SouscriptionDurable("agenda_tests","durable3"){
                public void onMessage(Message message){
                    try{
                        RendezVous rdv = (RendezVous)((ObjectMessage)message).getObject();
                        System.out.println("recu !!!" + rdv);
                        queue1.put(rdv);
                    }catch(Exception e) {
                    }
                }
            };

            RendezVous r = queue1.poll(2L, TimeUnit.SECONDS);
            assertNotNull(" le rendezVous n'est pas reçu, en moins de 2 sec ?",r);
            System.out.println("recu !!!" + r);
            assertTrue(" le rendezVous n'est pas le bon ?",r.equals(r1));
        }catch(Exception e){
            fail("exception inattendue : " + e.getClass().getName());
        }finally{
            try{s1.close();}catch(Exception e){}
            try{priseDeRDV.close();}catch(Exception e){}
        }
    }

}