package vladislavmaltsev.xmltojsontask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vladislavmaltsev.xmltojsontask.service.JsonDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ConverterTaskApplicationTests {
	@Autowired
	JsonDataService jsonDataService;
	@Test
	void convertToJsonTest() {
		String xmlData = "<root><name>John</name><age>30</age></root>";
		String expectedJsonData = "{\"root\":{\"name\":\"John\",\"age\":30}}";
		String convertedJsonData = jsonDataService.convertToJson(xmlData);
		assertEquals(expectedJsonData, convertedJsonData);
	}

}
