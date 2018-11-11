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
	// 클라이언트로 부터 메시지를 받는 메소드
	public void recevie() {
		
		// 일반적으로 하나의 쓰레드를 한들떄는 Runable 객체를 이용
		Runnable thread = new Runnable() {	
			@Override
			public void run() {
				try {
				while(true) {
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[512];
					int length = in.read(buffer);
					while(length ==-1) throw new IOException();
					System.out.println("[메시지 수신 성공]"
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
					   System.out.println("[메시지 수신 오류]"
								+ socket.getRemoteSocketAddress()  //네트워크 주소
								+ " : " + Thread.currentThread().getName());  //해당 쓰레드  이름);                         
				} catch (Exception e2) {
			          e2.printStackTrace();
				}
					
					
				}
			
			}
		};
		Main.threadPool.submit(thread);
		
	}
	// 해당 클라이언트에게 메시지를 전송하는 메소드
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
				   System.out.println("[메시지 수신 오류]"
							+ socket.getRemoteSocketAddress()  //네트워크 주소
							+ " : " + Thread.currentThread().getName());  //해당 쓰레드  이름); 
				   Main.clients.remove(Client.this); //오류가 생긴 해당 클라이언트를 제거
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
