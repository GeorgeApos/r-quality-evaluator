package gr.uom.rqualityevaluator.controllers;

import gr.uom.rqualityevaluator.models.FileAnalysis;
import gr.uom.rqualityevaluator.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileAnalysis> handleFileUpload(
            @RequestParam("owner") String owner,
            @RequestParam("file") MultipartFile file) throws IOException {
        FileAnalysis fileAnalysis = FileService.startMainAnalysis(file, owner);
        fileService.saveFile(fileAnalysis);
        return ResponseEntity.ok(fileAnalysis);
    }

}
