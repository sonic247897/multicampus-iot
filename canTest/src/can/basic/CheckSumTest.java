package can.basic;

public class CheckSumTest {
	public static void main(String[] args) {
		String data = "W28000000000000000000000000";
		char[] data_arr = data.toCharArray();
		int sum = 0;
		for (int i = 0; i < data_arr.length; i++) {
			System.out.println(data_arr[i]);
			sum = sum + data_arr[i];
		}
		System.out.println(sum);
		
		sum = (sum & 0xff);
		System.out.println(sum); // 비트 연산 하기 전과 결과 같음
		System.out.println(Integer.toBinaryString(10)); // 2진수로 변환
		System.out.println(Integer.toHexString(10)); // 16진수로 변환
		System.out.println(Integer.toHexString(sum)); // 16진수로 변환
	}
}
