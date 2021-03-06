package basic;

import java.io.IOException;
import java.io.OutputStream;

// 키보드로 입력하는 값을 아두이노로 내보내기 위한 스레드 - Serial 통신
public class SerialArduinoWriterThread extends Thread{
	OutputStream out;
	
	public SerialArduinoWriterThread(OutputStream out) {
		super();
		this.out = out;
	}

	@Override
	public void run() {
		int ledstate = 0;
		// 값이 없으면 -1을 리턴
		try {
			// System.in.read(): 키보드 입력(스캐너 대신 사용)
			while((ledstate = System.in.read()) > -1){
				out.write(ledstate);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
