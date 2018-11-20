package ta_bot;

public class DummyNet {
	
	//heading update, speed update, turret heading update, fire confidence
	int[] data;
	
	public DummyNet() {
		data = new int[]{10, 10, 10, 50};
	}
	
	public int[] getData() {
		return data;
	}

}
