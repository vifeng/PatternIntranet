package question1;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;
import java.util.Date;

public class Provider implements MessageListener {

    private Context    contexte;
    private Connection connexion;
    Destination dest;
    Destination destBack;
    Session session;
    MessageProducer sender;
    MessageConsumer receiver;

    /** Création du Consumer
     * @param requestChannel le nom de la file pour l'envoi
     * @param replyChannel le nom de la file pour la réception
     */
    public Provider(String requestChannel, String replyChannel) throws NamingException, JMSException {
        // nsy102 lignes extraites de http://openjms.sourceforge.net/usersguide/using.html
        // Creating a JNDI InitialContext -> un annuaire OpenJMS
        Hashtable<String,String> props = new Hashtable<String,String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); // adresse de la fabrique
        props.put(Context.PROVIDER_URL, "tcp://localhost:3035/"); // adresse de la base de donnée - seul truc à changer.
        
        contexte = new InitialContext(props); // initialisation du contexte
        ConnectionFactory factory = (ConnectionFactory) contexte.lookup("ConnectionFactory"); // Looking up a ConnectionFactory
        connexion = factory.createConnection(); // creation de la connection
        session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE); // creation d'une session :
        // false pour transaction, auto_ACK… pour acquiteement par le courtier

        dest = (Destination) contexte.lookup(requestChannel);  // receive thru this channel
        destBack = (Destination) contexte.lookup(replyChannel); // send thru this channel
        receiver = session.createConsumer(dest);
        sender = session.createProducer(destBack); 
        // on va chercher la queue passée en paramètre. Le patron impose une queue 
        // mais je laisse la classe mère au cas où le code change…

        // Méthode asynchrone : on se sert d'un listener qu'il faut inscrire
        receiver.setMessageListener(this);

        connexion.start();
    }

    public void onMessage(Message message) {
        try {
            //réception du message
            ObjectMessage text = (ObjectMessage) message; 
            MessageQ1 msg = (MessageQ1) text.getObject();
            // enregistrement de l'estampille
            Date date = new Date( message.getJMSTimestamp());
            msg.setEstampille(date);
            // creation et definition d'un objet de session
            ObjectMessage messageEnv = session.createObjectMessage();
            messageEnv.setObject(msg);
            // envoi d'un msg
            sender.send(messageEnv); 

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
        Provider provider=null;
        try{
            provider = new Provider("request","reply");
            Thread.sleep(Long.MAX_VALUE); // ...
        }finally{
            provider.close();
        }
    }

}
