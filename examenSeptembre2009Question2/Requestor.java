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

public class Requestor implements MessageListener {
  private static Context context;
  
	private Session session;
	private Destination replyQueue;
	private MessageProducer requestProducer;
	private MessageConsumer replyConsumer;

	protected Requestor() {
		super();
	}

	public static Requestor newRequestor(Connection connection, Destination requestQueue, String replyQueueName)
		throws JMSException, NamingException {
			
		Requestor requestor = new Requestor();
		requestor.initialize(connection, requestQueue, replyQueueName);
		return requestor;
	}

	protected void initialize(Connection connection, Destination requestQueue, String replyQueueName)
		throws NamingException, JMSException {
			
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		replyQueue = (Destination) context.lookup(replyQueueName); //args[0]); // ReplyChannel_1  ou  ReplyChannel_2
		requestProducer = session.createProducer(requestQueue);
		replyConsumer = session.createConsumer(replyQueue);
		replyConsumer.setMessageListener(this);
   // start the connection, to enable message receipt
    connection.start();
	}

	public void send(String message) throws JMSException {
		TextMessage requestMessage = session.createTextMessage();
		requestMessage.setText(message);
		requestMessage.setJMSReplyTo(replyQueue);
		requestProducer.send(requestMessage);
		System.out.println("Sent request");
		System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
		System.out.println("\tMessage ID: " + requestMessage.getJMSMessageID());
		System.out.println("\tCorrel. ID: " + requestMessage.getJMSCorrelationID());
		System.out.println("\tReply to:   " + requestMessage.getJMSReplyTo());
		System.out.println("\tContents:   " + requestMessage.getText());
	}


	public void onMessage(Message msg) {
    try{
  		if (msg instanceof TextMessage) {
  			TextMessage replyMessage = (TextMessage) msg;
  			System.out.println("Received reply ");
  			System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
  			System.out.println("\tMessage ID: " + replyMessage.getJMSMessageID());
  			System.out.println("\tCorrel. ID: " + replyMessage.getJMSCorrelationID());
  			System.out.println("\tReply to:   " + replyMessage.getJMSReplyTo());
  			System.out.println("\tContents:   " + replyMessage.getText());
  		} else {
  			System.out.println("Invalid message detected");
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

    Requestor.context = new InitialContext(properties);

    // look up the ConnectionFactory
    ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

    // look up the Destination
    Destination requestChannel = (Destination) context.lookup("RequestChannel");

      // create the connection
    Connection connection = factory.createConnection();

	  Requestor requestor = Requestor.newRequestor(connection, requestChannel, args[0]);
	  requestor.send(args[1]); // le message
	  Thread.sleep(Long.MAX_VALUE);
	}
}
