package vladislavmaltsev.xmltojsontask.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.XML;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vladislavmaltsev.xmltojsontask.entity.MyJsonData;

@RestController
@RequestMapping("/api")
public class XmlController {

    @PostMapping("/get")
    public String get(@RequestBody String xmlData) throws JsonProcessingException {
        String json = XML.toJSONObject(xmlData).toString();
        MyJsonData myJsonData = new ObjectMapper().readValue(json, MyJsonData.class);
        System.out.println(myJsonData);
        return json;
    }
}
