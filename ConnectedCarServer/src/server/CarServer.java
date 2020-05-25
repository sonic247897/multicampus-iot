package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;


public class CarServer {
	private ServerSocket server;
	// @ 해시맵을 사용해서 가장 최근걸로 덮어쓰기 되므로 이전의 겹치는 클라이언트는 종료해야 한다. 
	// car, user 중복작업 하기 싫어서 같은 클래스 사용
	// DB 연동을 하지 않으므로 HashMap으로 get할 수 있게 만든 것
	HashMap<String,User> userlist = new HashMap<String,User>();	 
	HashMap<String,User> carlist = new HashMap<String,User>();	 
	public void connect() {
		try {
			server = new ServerSocket(12345);
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Thread th = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					try {
						Socket client = server.accept();
						String ip = client.getInetAddress().getHostAddress();
						System.out.println(ip+"접속!!\n");
						// 브로드캐스트 - DB 연동 시에는 리스트 넘길 필요x 
						User user = new User(client, userlist,carlist);
						user.start();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}
	public static void main(String[] args) {
		new CarServer().connect();
	}
}
