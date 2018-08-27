package question3;

import question2.*;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;
import java.util.Date;
/**
 * 
 *
 */
public class SouscriptionDurable extends Souscription implements MessageListener {
    private Context         contexte;
    private TopicConnection connexion;
    /*
   private TopicSession    session;
    private Topic           topic;
    */
    private TopicSubscriber  subscriberDurable;
    
    public SouscriptionDurable(String topicName, String identifier) throws NamingException, JMSException {

        super(topicName);
        /*
        J'ai d'abord pensé à passé les variable en protected mais ce n'est pas une bonne idée 
        car évidemment ça impacte la sessoin pour souscripteur aussi.
        donc j'avais tout réécris …
      
        Hashtable<String,String> props = new Hashtable<String,String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
        //creation de la connection
        contexte = new InitialContext(props); 
        //Connection for a TOPIC
        TopicConnectionFactory factory = (TopicConnectionFactory) contexte.lookup("JmsTopicConnectionFactory");
        connexion = factory.createTopicConnection(); 
        session = connexion.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); 

        // On va chercher le topic et on cree un subscriber
        topic = (Topic) contexte.lookup(topicName); 
        */
       super.connexion.setClientID(identifier);
        super.subscriber = super.session.createDurableSubscriber(topic, identifier);
        super.connexion.start();

    }

    public void onMessage(Message message) {
        try {
            RendezVous rdv = (RendezVous)((ObjectMessage)message).getObject();
            System.out.println("rdv reçu : " + rdv);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}

