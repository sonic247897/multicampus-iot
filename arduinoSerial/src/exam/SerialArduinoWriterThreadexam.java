package exam;

import java.io.IOException;
import java.io.OutputStream;

// 키보드로 입력하는 값을 아두이노로 내보내기 위한 스레드 - Serial 통신
public class SerialArduinoWriterThreadexam extends Thread{
	OutputStream out;
	int ledstate;
	
	public SerialArduinoWriterThreadexam(OutputStream out, int ledstate) {
		super();
		this.out = out;
		this.ledstate = ledstate;
	}



	@Override
	public void run() {
		//int ledstate = 0;
		// 값이 없으면 -1을 리턴
		try {
			out.write(ledstate);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
