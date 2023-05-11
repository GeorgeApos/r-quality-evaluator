package gr.uom.rqualityevaluator.controllers;

import gr.uom.rqualityevaluator.models.FileAnalysis;
import gr.uom.rqualityevaluator.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/")
    public ResponseEntity<FileAnalysis> startMainAnalysis() {
        FileAnalysis fileAnalysis = FileService.startMainAnalysis();
        fileService.saveFile(fileAnalysis);
        return ResponseEntity.ok(fileAnalysis);
    }

}
