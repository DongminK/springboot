package dmk.undertow;

import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UndertowApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {
		
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("personal",  "100");
		map.put("group",  "100");
		
	}
}
