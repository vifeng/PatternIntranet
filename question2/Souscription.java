package question2;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;
import java.util.Date;

public class Souscription implements MessageListener {
    private Context         contexte;
    protected TopicConnection connexion;

    protected TopicSession    session;
    protected Topic           topic;
    protected TopicSubscriber  subscriber;

    public Souscription(String topicName) throws NamingException, JMSException {
        Hashtable<String,String> props = new Hashtable<String,String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
        //creation de la connection
        contexte = new InitialContext(props); 
        // TOPIC
        TopicConnectionFactory factory = (TopicConnectionFactory) contexte.lookup("JmsTopicConnectionFactory");
        connexion = factory.createTopicConnection(); 
        session = connexion.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); 

        // On va chercher le topic et on cree un subscriber
        topic = (Topic) contexte.lookup(topicName); 
        subscriber = session.createSubscriber(topic);
        // MESSAGE ASYNCHRONE : on enregistre le listener
        subscriber.setMessageListener(this);

        connexion.start();

    }
    public void onMessage(Message message) {
        try {
            RendezVous rdv = null;
            if (message instanceof ObjectMessage) {
                Object object = ((ObjectMessage) message).getObject();
                rdv = (RendezVous) object;
            }
            System.out.println("rdv reçu : " + rdv);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    public void close() throws NamingException, JMSException {
        // fermeture du contexte et de la connexion
        contexte.close();
        connexion.close();
    }

    public static void main(String[] args) throws Exception{
        Souscription souscription=null;
        try{
            souscription = new Souscription("agenda");
            Thread.sleep(Long.MAX_VALUE); // ...
        }finally{
            souscription.close();
        }
    }

}

