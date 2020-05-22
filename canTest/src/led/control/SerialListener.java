package led.control;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import serial.SerialConnect;

// CAN과 통신할 수 있는 리스너 [CAN에 특화된 객체] => 스레드보다 간편한다!
// 시리얼 포트을 통해서 데이터가 전송되었을 때 실행되는 클래스 
//	1. 전송되어 온 데이터 읽기만 함
public class SerialListener implements SerialPortEventListener{
	BufferedInputStream bis; // 캔 통신으로 input되는 데이터를 읽기 위해 오픈된 스트림
	SerialConnect arduinoConnect; // 아두이노와 시리얼통신을 위한 객체
	OutputStream os; // 아두이노로 시리얼 출력을 위해 작업
	
	public SerialListener(BufferedInputStream bis) {
		this.bis = bis;
		arduinoConnect = new SerialConnect();
		arduinoConnect.connect("COM11", "arduino");
		os = arduinoConnect.getOut();
				
	}
	
	// 데이터가 전송될 때마다 호출되는 메소드
	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			// CAN 통신은 데이터 길이가 정해져 있다. (많이 안 감)
			byte[] readBuffer = new byte[128];
			try {
				while(bis.available() >0) {
					int numbyte = bis.read(readBuffer);
				}
				String data = new String(readBuffer);
				System.out.println("Can시리얼 포트로 전송된 데이터==> "+data);
				// 전송되는 메시지를 검사해서 적절하게 다른 장치를 제어하거나 
				// Car 클라이언트 객체로 전달해서 서버로 전송되도록 처리
				// 캔으로 수신된 데이터가 0000000000000011이면 LED 끄고
				// 				   0000000000000000이면 LED 키기
				/*
				 * 1. 아두이노와 시리얼통신 할 수 있도록 output스트림 얻기
				 * 		=> 생성자에서 한 번 작업할 수 있도록 처리
				 * 2. output스트림으로 '0', '1' 내보내기
				 * 		=> CAN으로 수신된 데이터를 비교해서 
				 * 		응답 이용 - :U280000000000000000000000003F (데이터를 잘 받았다고 응답 옴)
				*/
				if(os != null) {
					// 뒤에 \r이 붙으므로 먼저 잘라줘야 한다.
					if(data.trim().equals(":U280000000000000000000000003F")) {
						os.write('1');
					}else {
						os.write('0');
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}

}
