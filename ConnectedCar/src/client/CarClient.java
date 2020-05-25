package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

// 라떼판다에서 실행하는 프로그램 - 포트를 사용하지 않기 때문에 여러번 실행하면 된다.
public class CarClient {
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;
    Socket socket;
    OutputStream os;
    PrintWriter pw;
    String carId;
    int flag;
	public CarClient() {
		connect();
		
	}
	public void connect() {
		try {
			socket = new Socket("70.12.224.55", 12345);
			System.out.println("접속성공...");
	        if (socket != null) {
	        	System.out.println("널이아니다.");
	        	// 접속한 후에 바로 클라이언트의 아이디를 생성합니다.[DB연동X]
	        	// 로그인을 해서 DB에 접속한 후에(인증 후) 발생된 id를 넘겨받아 작업
	            // - 여기서는 DB와 연동을 안 했기 때문에 랜덤수 발생시켰다.
	        	
	        	// car과 phone의 id숫자가 같은 유저끼리만 통신 가능하게 만들어야 한다.
	            Random r = new Random();
	            flag =r.nextInt(2)+1;
	            if(flag%2==0){
	                // phone1111, car1111
	                carId = "1111";
	            }else{
	                // phone2222, car2222
	                carId = "2222";
	            }
	            ioWork();
	        }
	        Thread t1 = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                while (true) {
	                    String msg;
	                    try {
	                        msg = br.readLine();
	                        System.out.println("서버로 부터 수신된 메시지>>"
	                                + msg);
	                        
	                        // CAN통신할 수 있는 객체로 메시지 넘기기
	                        
	                    } catch (IOException e) {
	                        try {
	                            //2. 서버쪽에서 연결이 끊어지는 경우 사용자는 자원을 반납======
	                            //자원반납
	                            is.close();
	                            isr.close();
	                            br.close();
	                            os.close();
	                            pw.close();
	                            socket.close();
	                          
	
	                        } catch (IOException e1) {
	                            // TODO Auto-generated catch block
	                            e1.printStackTrace();
	                        }
	                        break;//반복문 빠져나가도록 설정
	                    }
	                }
	            }
	        });
	        t1.start();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
   	}
	void ioWork(){
	    try {
	        is = socket.getInputStream();
	        isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);

	        os = socket.getOutputStream();
	        pw = new PrintWriter(os,true);
	        // 서버가 하나(라떼판다)인데 car와 android에서 모두 접속하기 때문에 
	        // car/, phone/으로 구별한다.
	        //여기서 클라이언트의 아이디를 서버에게 전송합니다.
	        pw.println("car/"+carId);
            pw.flush();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	}
	public static void main(String[] args) {
		new CarClient();
	}
}
