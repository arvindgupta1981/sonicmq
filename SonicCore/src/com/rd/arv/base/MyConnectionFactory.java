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
					(ConnectionFactory)getContext().lookup("Conatiner100");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return conFactory;
	}
	
	
	public static Connection getConnection() {
		try {
			if(connection == null) {
				connection = getConnectionFactory().createConnection("Administrator", "Administrator");
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	
	
}
