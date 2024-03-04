package vladislavmaltsev.xmltojsontask.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vladislavmaltsev.xmltojsontask.entity.JsonData;
import vladislavmaltsev.xmltojsontask.service.JsonDataService;

@RestController
@RequestMapping("/xmlapi")
@RequiredArgsConstructor
public class XmlParserController {
    private final JsonDataService jsonDataService;
    @PostMapping("/put")
    public String put(@RequestBody String xmlData) throws JsonProcessingException {

        return jsonDataService.saveIntoLog(xmlData);
    }

}
