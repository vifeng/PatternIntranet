package question1;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;

public class Consumer {
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
    public Consumer(String requestChannel, String replyChannel) throws NamingException, JMSException {
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

        dest = (Destination) contexte.lookup(requestChannel); 
        destBack = (Destination) contexte.lookup(replyChannel); 
        // on va chercher la queue passée en paramètre. Le patron impose une queue 
        // mais je laisse la classe mère au cas où le code change…
        sender = session.createProducer(dest);  //creation d'un sender
        receiver = session.createConsumer(destBack);  //creation d'un Receiver

        connexion.start();
    }

    public void send(String s)  throws JMSException {
        MessageQ1 mq1 = new MessageQ1(s);
        ObjectMessage message = session.createObjectMessage(mq1);
        sender.send(message);  
    }

    public MessageQ1 receive()  throws JMSException {
        MessageQ1 msg = null; 
        Message message = receiver.receive();
        if (message instanceof ObjectMessage) {
            Object object = ((ObjectMessage) message).getObject();
            msg = (MessageQ1)object;
        }else{
            // Faudrait lancer une belle exception
        }          
        return msg; 
    }

    public void close() throws NamingException, JMSException {
        // fermeture du contexte et de la connexion
        contexte.close();
        connexion.close();
    }

    public static void main(String[] args) throws Exception{
        Consumer consumer=null;
        try{
            consumer = new Consumer("request","reply");
            consumer.send("test_envoi");
            System.out.print("message recu : ");
            System.out.println(consumer.receive());
        }finally{
            consumer.close();
        }
    }

}
