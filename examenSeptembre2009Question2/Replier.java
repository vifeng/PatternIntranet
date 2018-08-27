package examenSeptembre2009Question2;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Hashtable;

public class Replier implements MessageListener {

	private Session session;
	private static Context context;

	protected Replier() {
		super();
	}

	public static Replier newReplier(Connection connection, String requestQueueName)
	  throws JMSException, NamingException {

		Replier replier = new Replier();
		replier.initialize(connection, requestQueueName);
		return replier;
	}

	protected void initialize(Connection connection, String requestQueueName)
		throws NamingException, JMSException {

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		    // look up the Destination
    Destination requestQueue = (Destination) context.lookup(requestQueueName);
		MessageConsumer requestConsumer = session.createConsumer(requestQueue);
		requestConsumer.setMessageListener(this);
   // start the connection, to enable message receipt
    connection.start();
	}

	public void onMessage(Message message) {
		try {
			if ((message instanceof TextMessage) && (message.getJMSReplyTo() != null)) {
				TextMessage requestMessage = (TextMessage) message;

				System.out.println("Received request");
				System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
				System.out.println("\tMessage ID: " + requestMessage.getJMSMessageID());
				System.out.println("\tCorrel. ID: " + requestMessage.getJMSCorrelationID());
				System.out.println("\tReply to:   " + requestMessage.getJMSReplyTo());
				System.out.println("\tContents:   " + requestMessage.getText());

				String contents = requestMessage.getText();
				Destination replyDestination = message.getJMSReplyTo();
				MessageProducer replyProducer = session.createProducer(replyDestination);

				TextMessage replyMessage = session.createTextMessage();
				replyMessage.setText(contents);
				replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
				replyProducer.send(replyMessage);

				System.out.println("Sent reply");
				System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
				System.out.println("\tMessage ID: " + replyMessage.getJMSMessageID());
				System.out.println("\tCorrel. ID: " + replyMessage.getJMSCorrelationID());
				System.out.println("\tReply to:   " + replyMessage.getJMSReplyTo());
				System.out.println("\tContents:   " + replyMessage.getText());
			} else {
				System.out.println("Invalid message detected");
				System.out.println("\tType:       " + message.getClass().getName());
				System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
				System.out.println("\tMessage ID: " + message.getJMSMessageID());
				System.out.println("\tCorrel. ID: " + message.getJMSCorrelationID());
				System.out.println("\tReply to:   " + message.getJMSReplyTo());

			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
        // create the JNDI initial context
    // nsy102 lignes extraites de http://openjms.sourceforge.net/usersguide/using.html
    Hashtable<String,String> properties = new Hashtable<String,String>();
    properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
    properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");

    Replier.context = new InitialContext(properties);

    // look up the ConnectionFactory
    ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

      // create the connection
    Connection connection = factory.createConnection();

	  Replier replier = Replier.newReplier(connection, "RequestChannel");
	  
	  Thread.sleep(Long.MAX_VALUE);
	}
}
