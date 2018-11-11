package application;
	
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {
	
	//������ Ǯ --> �������� �����带 ȿ�������� ó���ϱ�����
	public static ExecutorService threadPool;
	//Ŭ���̾�Ʈ ����
	public static Vector<Client> clients = new Vector<Client>();
	
	ServerSocket serverSocket;
	
	
	// ������  �������Ѽ� Ŭ���̾�Ʈ�� ������ ��ٸ��� �޼ҵ�
	public void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));	
		} catch (Exception e) {
			e.printStackTrace();
			if(!serverSocket.isClosed())
			{
				stopServer();
			}
			return;	
		}
		 //Ŭ���̾�Ʈ�� �����Ҷ� ���� ��ٸ��� ������
	   Runnable thread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
			try {
				
					Socket socket = serverSocket.accept();
					clients.add(new Client(socket)); //�ش�������� Ŭ���̾�Ʈ ��ü �ʱ�ȭ
					   System.out.println("[Ŭ���̾�Ʈ ����]"
								+ socket.getRemoteSocketAddress()  //��Ʈ��ũ �ּ�
							+ " : " + Thread.currentThread().getName());  //�ش� ������  �̸�); 
				}
			 catch (Exception e) {
				if(!serverSocket.isClosed())
				{
					stopServer();
				}
				  break;
			     }
			}
		}
	};
	threadPool = Executors.newCachedThreadPool(); //������ Ǯ �ʱ�ȭ
	threadPool.submit(thread);
	}
	// ������ �۵��� ������Ű�� �޼ҵ� 
	public void stopServer() {
		try {
			//���� �۵����� ��� ���ϴݱ�
			Iterator<Client> iterator = clients.iterator();
			while(iterator.hasNext())
			{
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			//���� ���� ��ü �ݱ�
			if(serverSocket != null && !serverSocket.isClosed())
			{
				serverSocket.close();			
			}
			// ������ Ǯ �����ϱ�
			if(threadPool !=null && ! threadPool.isShutdown())
			{
				threadPool.shutdown();
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	
	
	
	//UI�����ϰ� ������ ���α׷��� �۵���Ű�� �޼ҵ�
	@Override
	public void start(Stage primaryStage) {
		
	
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		
		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("�������", 15));
		root.setCenter(textArea);
		
		Button toggleButton = new Button("�����ϱ�");
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton, new Insets(1,0,0,0));
		root.setBottom(toggleButton);
		
		String IP = "127.0.0.1";
		int port = 9876;
		
		toggleButton.setOnAction(event ->
		{
			if(toggleButton.getText()=="�����ϱ�")
			{
				startServer(IP, port);
				Platform.runLater(() ->
				{
					String message = String.format("[���� ����]\n", IP,port);
				    textArea.appendText(message);
				    toggleButton.setText("�����ϱ�");
		
				});	
			}
			else {
				
				stopServer();
				Platform.runLater(() ->
				{
					String message = String.format("[���� ����]\n", IP,port);
				    textArea.appendText(message);
				    toggleButton.setText("�����ϱ�");
				});		
			}
		}
				);
		
		Scene scene = new Scene(root,400,400);
		primaryStage.setTitle("[ä�� ����]");
		primaryStage.setOnCloseRequest(event-> stopServer());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	// ���α׷��� ������..
	public static void main(String[] args) {
		launch(args);
	}
}
