package com.olehzaiets.olxparser.ui;

import com.olehzaiets.olxparser.excel.SheetWrapper;
import com.olehzaiets.olxparser.parser.AdInformation;
import com.olehzaiets.olxparser.parser.OlxParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class UiParserController {

    @Autowired
    private OlxParser olxParser;

    @GetMapping("/")
    public String parserForm(Model model) {
        model.addAttribute("parsingData", new ParsingData());
        return "parser";
    }

    @PostMapping("/")
    public String parserFormSubmit(@ModelAttribute ParsingData parsingData, Model model) {
        model.addAttribute("parsingData", parsingData);
        return "parsed";
    }

    @PostMapping("/parsing")
    public ResponseEntity<ByteArrayResource> parsing(@ModelAttribute ParsingData data) throws IOException {
        Set<AdInformation> adsInfo = olxParser.parse(data.getData());
        SheetWrapper sheet = new SheetWrapper("Parsing result");
        sheet.setTitles("id", "name", "price", "publish time", "url", "description", "images");
        AtomicInteger index = new AtomicInteger(1);
        adsInfo.forEach(adInformation -> {
            sheet.insertRow(
                    index.getAndIncrement(),
                    adInformation.id(),
                    adInformation.name(),
                    adInformation.price(),
                    adInformation.publishTime(),
                    adInformation.url(),
                    adInformation.description(),
                    adInformation.imagesUrls().toString()
            );
        });

        return createResponse("result.xls", sheet.asByteArray());
    }

    private ResponseEntity<ByteArrayResource> createResponse(String filename, byte[] data) {
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .contentLength(data.length)
                .body(resource);
    }
}
