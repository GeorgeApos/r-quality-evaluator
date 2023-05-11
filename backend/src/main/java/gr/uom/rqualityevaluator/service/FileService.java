package gr.uom.rqualityevaluator.service;

import gr.uom.rqualityevaluator.models.FileAnalysis;
import org.springframework.stereotype.Service;

@Service
public class FileService {


    public static FileAnalysis startMainAnalysis() {
        FileAnalysis fileAnalysis = new FileAnalysis();

        return new FileAnalysis();
    }

    public void saveFile(FileAnalysis fileAnalysis) {
    }
}
