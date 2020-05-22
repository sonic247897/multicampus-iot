package can.basic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import gnu.io.SerialPort;
import serial.SerialConnect;

public class CANReadWriteTest {
	SerialConnect serialConnect; // CAN시리얼 포트 연결
	OutputStream out; // CAN과 output통신할 스트림
	
	public CANReadWriteTest(String portname) {
		// 시리얼 통신을 위해 연결
		serialConnect = new SerialConnect();
		serialConnect.connect(portname, this.getClass().getName());
				
		// input, output 작업을 하기 위해 리스너를 port에 연결
		SerialPort commport = (SerialPort)serialConnect.getCommPort();
		SerialListener listener = new SerialListener(serialConnect.getBis());
		try {
			commport.addEventListener(listener);
			commport.notifyOnDataAvailable(true);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		// output스트림 얻기
		out = serialConnect.getOut();
		
		// 처음 CAN통신을 위한 준비 작업을 할 때는 수신가능한 메시지를 보낼 수 있도록
		new Thread(new CANWriteThread()).start();
	}// end 생성자
	
	public void send(String msg) {
		new Thread(new CANWriteThread(msg)).start();
	}
	
	class CANWriteThread implements Runnable{
		String data; // 송신할 데이터를 저장할 변수
		
		CANWriteThread(){ // ---------------------------- 처음에 수신가능 설정
			// 먼저 수신 가능하게 만들어야 함
			this.data = ":G11A9\r";
		}
		
		CANWriteThread(String msg){ // ------------------ 메시지 보낼 때마다 사용
			this.data = convert_data(msg);
		}
		
		
		// 통신: 내가 보내고 싶은 데이터를 hexa String으로 만들어서 보낸다.
		// msg = msg의 id + 데이터
		public String convert_data(String msg) {
			msg = msg.toUpperCase(); // 메시지를 대문자로 변환
			msg = "W28"+msg; // 송신 데이터의 구분기호를 추가
			// msg: W28 00000000(메시지 ID 8개 들어옴) 0000000000000000(데이터=프로토콜 16개 들어옴)
			// 16자리 프로토콜: 마음대로 정하면 됨! ex) LED: 00, 서보모터: 01, ..
			// 데이터프레임에 대한 체크섬을 생성 - 앞뒤문자 빼고 나머지를 더한 후 0xff로 &연산
			char[] data_arr = msg.toCharArray();
			int sum = 0;
			for (int i = 0; i < data_arr.length; i++) {
				System.out.println(data_arr[i]);
				sum = sum + data_arr[i];
			}
			sum = (sum & 0xff);
			// 보낼 메시지를 최종 완성
			String result = ":"
							+msg
							+Integer.toHexString(sum).toUpperCase()
							+"\r";
			return result;
		}
		
		@Override
		public void run() {
			byte[] outputdata = data.getBytes();
			try {
				out.write(outputdata);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		String id = "00000000"; // 송신할 메시지의 구분 ID
		String data = "0000000000000011"; // 송신할 데이터(마음대로 입력)
		String msg = id+data;
		CANReadWriteTest canObj = new CANReadWriteTest("COM8");
		canObj.send(msg);
	}

}
