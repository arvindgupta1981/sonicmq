package com.rd.arv.base;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

public class Endpoints {
	
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

	
	private static Session createSession() {
		try {
			return MyConnectionFactory.getConnection().createSession(false, 1);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		Queue queue = new Endpoints().createQueue("queue1");
		Topic topic = new Endpoints().createTopic("topic1");
	}
	

}
