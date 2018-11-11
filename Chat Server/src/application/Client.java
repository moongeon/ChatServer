package application;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.xml.ws.handler.MessageContext;

public class Client {

	Socket socket ;
	
	public Client (Socket socket)
	{
	  this.socket = socket;
	  recevie();
	  
	};
	// Ŭ���̾�Ʈ�� ���� �޽����� �޴� �޼ҵ�
	public void recevie() {
		
		// �Ϲ������� �ϳ��� �����带 �ѵ鋚�� Runable ��ü�� �̿�
		Runnable thread = new Runnable() {	
			@Override
			public void run() {
				try {
				while(true) {
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[512];
					int length = in.read(buffer);
					while(length ==-1) throw new IOException();
					System.out.println("[�޽��� ���� ����]"
							+ socket.getRemoteSocketAddress()
							+ " : " + Thread.currentThread().getName() );
					String message = new String(buffer,0,length,"UTF-8");
				   for(Client client : Main.clients)
				   {
					   client.send(message);
				   }		
				}

				} catch (Exception e) {
				   try {
					   System.out.println("[�޽��� ���� ����]"
								+ socket.getRemoteSocketAddress()  //��Ʈ��ũ �ּ�
								+ " : " + Thread.currentThread().getName());  //�ش� ������  �̸�);                         
				} catch (Exception e2) {
			          e2.printStackTrace();
				}
					
					
				}
			
			}
		};
		Main.threadPool.submit(thread);
		
	}
	// �ش� Ŭ���̾�Ʈ���� �޽����� �����ϴ� �޼ҵ�
	public void send(String message) {
	Runnable thread = new Runnable() {	
		@Override
		public void run() {
			try {
				OutputStream out = socket.getOutputStream();
				byte[] buffer =   message.getBytes("UTF-8");
				out.write(buffer);							
			} catch (Exception e) {
			try {
				   System.out.println("[�޽��� ���� ����]"
							+ socket.getRemoteSocketAddress()  //��Ʈ��ũ �ּ�
							+ " : " + Thread.currentThread().getName());  //�ش� ������  �̸�); 
				   Main.clients.remove(Client.this); //������ ���� �ش� Ŭ���̾�Ʈ�� ����
				   socket.close();		   
			} catch (Exception e2) {
				e2.getStackTrace();
			}	
			}	
	}	
	};
	Main.threadPool.submit(thread);
	
	
	
}
}
