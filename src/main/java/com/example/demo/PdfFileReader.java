package com.example.demo;

import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class PdfFileReader {
    private final VectorStore vectorStore;

    @Value("classpath:*.pdf")
    private Resource[] pdfResource;

    public PdfFileReader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

 
    @CrossOrigin(origins = "http://127.0.0.1:4200")
    @GetMapping("/demo/process")
    public void init() {

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                                .build())
                .build();
        for (Resource resource : pdfResource) {
            var pdfReader = new PagePdfDocumentReader(resource, config);
            var textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(pdfReader.get()));
        }

    }
}
