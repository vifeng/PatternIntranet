package question4;

import javax.naming.*;
import javax.jms.*;
import java.util.Hashtable;

public class AvecEtSansTransaction{

    /** Envoi sans transaction sur 3 files,
     * la derniere file, n'existe pas, ce qui engendre une exception
     */
    public static void sansTransaction() throws NamingException, JMSException {
      Connection connexion = null;
      Context contexte = null;
      try{
        Hashtable<String,String> props = new Hashtable<String,String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
        contexte = new InitialContext(props);
        ConnectionFactory fabrique = (ConnectionFactory) contexte.lookup("ConnectionFactory");
        connexion = fabrique.createConnection();
        connexion.start();
        
        Session session = null;
        try{
        // envoi, d'un message aux 3 files ...
          session = connexion.createSession(false, Session.AUTO_ACKNOWLEDGE);
          
          Destination dest = (Destination) contexte.lookup("queue1");
          MessageProducer sender = session.createProducer(dest);
          TextMessage message = session.createTextMessage("envoi_queue1");
          sender.send(message);
          
          dest = (Destination) contexte.lookup("queue2");
          sender = session.createProducer(dest);
          message = session.createTextMessage("envoi_queue2");
          sender.send(message);
          
          dest = (Destination) contexte.lookup("queue_introuvable");

        }catch(javax.naming.NameNotFoundException e){
          System.out.println(e.getMessage() + " est effectivement introuvable");
        }catch(Exception e){
          e.printStackTrace();
        }
        
        Destination src1 = (Destination) contexte.lookup("queue1");
        MessageConsumer receiver1 = session.createConsumer(src1);
        Message msg1 = receiver1.receiveNoWait();
        if(msg1 !=null)
          System.out.println("msg1, lu depuis queue1 : " + ((TextMessage)msg1).getText());
        else
          System.out.println("msg1, queue1 est vide ");
        
        Destination src2 = (Destination) contexte.lookup("queue2");
        MessageConsumer receiver2 = session.createConsumer(src2);
        Message msg2 = receiver2.receiveNoWait();
        if(msg2 !=null)
          System.out.println("msg2, lu depuis queue2 : " + ((TextMessage)msg2).getText());
        else
          System.out.println("msg1, queue2 est vide ");


     }finally{
        try{contexte.close();}catch(Exception e){}
        try{connexion.close();}catch(Exception e){}
      }
    }

    
    /** Envoi sans transaction sur 3 files,
     * la derniere file, n'existe pas, ce qui engendre une exception
     */
    public static void avecTransaction() throws NamingException, JMSException {
      Connection connexion = null;
      Context contexte = null;
      try{
        Hashtable<String,String> props = new Hashtable<String,String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
        contexte = new InitialContext(props);
        ConnectionFactory fabrique = (ConnectionFactory) contexte.lookup("ConnectionFactory");
        connexion = fabrique.createConnection();
        connexion.start();
        
        Session session = null;
        try{
        // envoi, d'un message aux 3 files ...
          session = connexion.createSession(true, Session.AUTO_ACKNOWLEDGE);   // <----------------- true
          Destination dest = (Destination) contexte.lookup("queue1");
          MessageProducer sender = session.createProducer(dest);
          TextMessage message = session.createTextMessage("envoi_queue1");
          sender.send(message);
          
          dest = (Destination) contexte.lookup("queue2");
          sender = session.createProducer(dest);
          message = session.createTextMessage("envoi_queue2");
          sender.send(message);
          
          dest = (Destination) contexte.lookup("queue_introuvable");
          session.commit();                                                   // <----------------- commit
        }catch(javax.naming.NameNotFoundException e){
          System.out.println(e.getMessage() + " est effectivement introuvable, rollback");
          session.rollback();                                                 // <----------------- rollback
        }catch(Exception e){
          e.printStackTrace();
        }
        
        Destination src1 = (Destination) contexte.lookup("queue1");
        MessageConsumer receiver1 = session.createConsumer(src1);
        Message msg1 = receiver1.receiveNoWait();
        if(msg1 !=null)
          System.out.println("msg1, lu depuis queue1 : " + ((TextMessage)msg1).getText());
        else
          System.out.println("msg1, queue1 est vide ");
        
        Destination src2 = (Destination) contexte.lookup("queue2");
        MessageConsumer receiver2 = session.createConsumer(src2);
        Message msg2 = receiver2.receiveNoWait();
        if(msg2 !=null)
          System.out.println("msg2, lu depuis queue2 : " + ((TextMessage)msg2).getText());
        else
          System.out.println("msg1, queue2 est vide ");


     }finally{
        try{contexte.close();}catch(Exception e){}
        try{connexion.close();}catch(Exception e){}
      }
    
    }
    
 
    public static void main(String[] args) throws Exception{
      try{
        sansTransaction();
        System.out.println();
        System.out.println("____________AVEC TRANSACTION______________");
        avecTransaction();
        
      }finally{

      }
    }
  }
  