package vladislavmaltsev.xmltojsontask.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vladislavmaltsev.xmltojsontask.service.FileReaderService;
import vladislavmaltsev.xmltojsontask.service.FileReaderServiceCopy;

@Controller
@RequestMapping("/readerapi")
@RequiredArgsConstructor
public class FileReaderController {
    private final FileReaderService fileReaderService;
    private final FileReaderServiceCopy fileReaderServiceCopy;
    @GetMapping("/read/{filename}")
    public String read(@PathVariable String filename) {
//        fileReaderService.readData(filename);
        fileReaderServiceCopy.readData(filename);
        return null;
    }
}
