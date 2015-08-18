package com.rd.arv.base;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import progress.message.jclient.ConnectionFactory;

public class MyConnectionFactory {
	

	private static Connection connection = null;
	
	private static Context getContext() {
		Context context = null;
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sonicsw.jndi.mfcontext.MFContextFactory");
        env.put("com.sonicsw.jndi.mfcontext.domain", "Domain1");
        env.put(Context.PROVIDER_URL, "tcp://localhost:2506");
        env.put(Context.SECURITY_PRINCIPAL, "Administrator");
        env.put(Context.SECURITY_CREDENTIALS, "Administrator");
        try {
			context = new InitialContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
		}
        return context;
        
	}
	//Create a connectionfactory1 lookup by using sonic management console
	public static ConnectionFactory getConnectionFactory() {
		ConnectionFactory conFactory = null;
		try {
			conFactory = 
					(ConnectionFactory)getContext().lookup("ConnectionFactory1");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return conFactory;
	}
	
	
	private static Connection getConnection() {
		try {
			if(connection == null) {
				connection = getConnectionFactory().createConnection("Administrator", "Administrator");
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	private static Session createSession() {
		try {
			return getConnection().createSession(false, 1);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Queue createQueue(String queueName) {
		try {
			return createSession().createQueue(queueName);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Topic createTopic(String topicName) {
		try {
			return createSession().createTopic(topicName);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		Queue queue = new MyConnectionFactory().createQueue("queue1");
		Topic topic = new MyConnectionFactory().createTopic("topic1");
	}
}
