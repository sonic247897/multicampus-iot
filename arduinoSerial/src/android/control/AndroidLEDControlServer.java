package android.control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import basic.SerialArduinoLEDTest;

// 안드로이드(클라이언트)의 요청을 받으면 차 안에서 장치 쪽으로 요청을 전달하는 서버
// 차 안에서 계속 돌고 있는 프로그램
public class AndroidLEDControlServer {
	private ServerSocket server;
	
	public void connect() {
		try {
			server = new ServerSocket(12345);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 언제 요청이 들어올 지 예측이 불가능하므로 스레드로 만듬
		Thread th = new Thread(new Runnable(){
			@Override
			public void run() {
				// 여러명을 받아야 하므로 무한루프(외부 TCP서버)
				// 지금은 User객체 없이 똑같은 port(COM11)로 덮어쓰기 하므로 다른 사용자가 접속하면 에러 발생
				//  - 차 내의 TCP서버는 내 차이므로 포트 하나를 이미 점유해서 다른 사용자가 들어오지 않는다.
				while(true){
					try {
						Socket client = server.accept();
						String ip = client.getInetAddress().getHostAddress();
						System.out.println(ip+"사용자접속!!\n");
						// 클라이언트가 보내는 메시지를 읽고 있는 스레드(언제 누를지 모르므로)
						// 클라이언트를 넘겨줘야 클라이언트랑 통신할 수 있음
						new ReceiverThread(client).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}
	public static void main(String[] args) {
		new AndroidLEDControlServer().connect();
	}

}
