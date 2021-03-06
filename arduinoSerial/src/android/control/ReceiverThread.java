package android.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

// TCP서버 쪽에서 클라이언트의 요청을 계속 읽는 스레드
public class ReceiverThread extends Thread{
	Socket client;
	BufferedReader br; // 클라이언트의 메시지를 읽는 스트림
	PrintWriter pw; // 클라이언트에게 메시지를 전달하는 스트림 (사용x. but 모든 TCP서버는 이 구조로 가야 한다)
	SerialArduinoLEDControl serialObj; // 시리얼통신을 위한 객체
	OutputStream os; // 시리얼 통신에서 아두이노로 데이터를 내보내기 위한 스트림
	
	public ReceiverThread(Socket client) {
		this.client = client;
		try {
			// 클라이언트가 보내오는 메시지를 읽기 위한 스트림 생성
			br = new BufferedReader(new InputStreamReader(
									client.getInputStream()));
			// 클라이언트에게 메시지를 전송하기 위한 스트림 생성 (사용x)
			// flush: 버퍼에 있는 데이터 비우기
			pw = new PrintWriter(client.getOutputStream(), true);
			// 아두이노와 시리얼통신을 위해서 아두이노로 데이터를 내보내기 위한 스트림 얻기
			// 한 스레드를 한번반 호출하기 때문에 생성자에서 이렇게 받는다.
			serialObj = new SerialArduinoLEDControl();
			serialObj.connect("COM11");
			os = serialObj.getOutput();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		// 클라이언트의 메시지를 받아서 아두이노로 데이터 전송
		while(true) {
			try {
				String msg = br.readLine();
				System.out.println("클라이언트가 보낸 메시지: "+msg);
				if(msg.equals("led_on")){
					os.write('1');
				}else {
					os.write('0');
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
