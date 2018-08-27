package question2;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;

public class PriseDeRDV {
    private Context         contexte;
    private TopicConnection connexion;
    private TopicSession    session;
    private Topic           topic;
    private TopicPublisher  sender;

    public PriseDeRDV(String topicName) throws NamingException, JMSException {
        Hashtable<String,String> props = new Hashtable<String,String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
        //creation de la connection
        contexte = new InitialContext(props); 
        TopicConnectionFactory factory = (TopicConnectionFactory) contexte.lookup("JmsTopicConnectionFactory");
        connexion = factory.createTopicConnection(); 
        session = connexion.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); 

        // On va chercher le topic et on cree un publisher
        topic = (Topic) contexte.lookup(topicName); 
        sender = session.createPublisher(topic);

        connexion.start();
    }

    public void publier(RendezVous rdv)  throws JMSException {
        ObjectMessage message = session.createObjectMessage(rdv);
        sender.publish(message);
    }

    
    public void close() throws NamingException, JMSException {
        contexte.close();
        connexion.close();
    }

    public static void main(String[] args) throws Exception{

        PriseDeRDV priseDeRDV=null;
        try{
            priseDeRDV = new PriseDeRDV("agenda");
            priseDeRDV.publier(RendezVous.MAINTENANT);
        }finally{
            priseDeRDV.close();
        }
    }

}
