package sort;

public class Sort {

	public static void main(String[] args) {
		int[] testArr = {5,2,1,9,4};
		doSort(testArr);
		for(int i : testArr) {
			System.out.println(testArr[i]);
		}
	}
	
	static void doSort(int[] arr) {
		for(int i = 1; i < arr.length; i++) {
			int element = arr[i];
			for(int j = 0; j < i; j++) {
				if(element < arr[j]) {jjj
					for(int k = i; k > j; k--) {
						arr[k] = arr[k - 1];
					}
					arr[j] = element;
					break;
				}
			}
		}
	}
	
}
