package basic;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

public class SerialConnectionTest {
	public SerialConnectionTest() {
		
	}
	
	public void connect(String portName) {
		try {
			// COM포트가 실제 존재하고 사용가능한 상태인지 확인
			CommPortIdentifier commPortIdentifier =
					CommPortIdentifier.getPortIdentifier(portName);
			// 포트가 사용중인지 아닌지 체크
			if(commPortIdentifier.isCurrentlyOwned()) {
				System.out.println("포트를 사용할 수 없습니다.");
			}else {
				System.out.println("포트 사용 가능");
				// port가 사용 가능하면 포트를 열고 포트객체 얻어오기
				// 포트를 사용할 수 있게 모델링한 객체 - CommPort
				// 매개변수1: 포트를 열고 사용하는 프로그램의 이름(문자열)(아무렇게나 줘도 됨)
				// 매개변수2: 포트를 열고 통신하기 위해서 기다리는 시간(밀리세컨드)
				//			ex) 톰캣의 timeout과 같음
				// 접속, 통신 -> Exception
				CommPort commPort 
						= commPortIdentifier.open("basic_serial", 3000);
				System.out.println(commPort);
			}
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}catch (PortInUseException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// 아두이노와 통신할 수 있는 각자의 포트 번호
		new SerialConnectionTest().connect("COM11");
		
	}
}
