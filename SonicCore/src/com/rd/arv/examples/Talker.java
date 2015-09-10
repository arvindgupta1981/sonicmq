package com.rd.arv.examples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Talker {
	private static final String QUEUE_RECEIVE_MSG = "Queue.receiveMsg";
	private static final String QUEUE_SEND_MSG = "Queue.sendMsg";
	//FOREVER 0
	private static final int TIME_TO_LIVE = 0;
	public Talker() {
		ConnectionFactory connectionFactory;
		Connection connection;
		Session sendSession;
		Session receiveSession;
		Queue sendQueue;
		Queue receiveQueue;
		MessageProducer sendProducer;
		MessageConsumer receiveConsumer;
		try {
			connectionFactory = new progress.message.jclient.ConnectionFactory("tcp://localhost:2506");
			System.out.println("1");
			connection = connectionFactory.createConnection("Administrator","Administrator");
			System.out.println("1");
			sendSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			receiveSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			System.out.println("1");
			sendQueue = sendSession.createQueue(QUEUE_SEND_MSG);
			receiveQueue = sendSession.createQueue(QUEUE_RECEIVE_MSG);
			System.out.println("1");
			sendProducer = sendSession.createProducer(sendQueue);
			sendProducer.send(getNewMessage(sendSession, 0), javax.jms.DeliveryMode.PERSISTENT, javax.jms.Message.DEFAULT_PRIORITY, TIME_TO_LIVE);
			System.out.println("1");
			receiveConsumer = receiveSession.createConsumer(receiveQueue);
			receiveConsumer.setMessageListener(new TalkListener());
			System.out.println("1");
			connection.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) {
		new Talker();		
	}
	
	public TextMessage getNewMessage(Session sendSession, int i) {
		TextMessage message = null;
		try {
			message = sendSession.createTextMessage();
			message.setBooleanProperty(progress.message.jclient.Constants.PRESERVE_UNDELIVERED, true);
			message.setBooleanProperty(progress.message.jclient.Constants.PRESERVE_UNDELIVERED, true);
			message.setText("Test Test Test");
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return message;
	}

}


class TalkListener implements javax.jms.MessageListener {

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println("*************" + message.getJMSMessageID());
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
		try {
			System.out.println("++++++++++++++++++++++++++++" + textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
