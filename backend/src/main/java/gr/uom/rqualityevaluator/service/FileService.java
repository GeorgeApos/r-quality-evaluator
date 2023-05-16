package gr.uom.rqualityevaluator.service;

import gr.uom.rqualityevaluator.models.FileAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Configuration
public class FileService {

    public static FileAnalysis startMainAnalysis(MultipartFile file, String owner) throws IOException {
        FileAnalysis fileAnalysis = new FileAnalysis();
        fileAnalysis.setName(file.getOriginalFilename());
        fileAnalysis.setOwner(owner);

        Path path = Paths.get(System.getProperty("user.dir"));
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        saveFileToDirectory(file, path);

        executeAnalysis(fileAnalysis, path);

        deleteFileFromDirectory(file, path);
        return new FileAnalysis();
    }

    private static void executeAnalysis(FileAnalysis fileAnalysis, Path path) {
        executeLinter(fileAnalysis, path);
        executeCycloComplexity(fileAnalysis, path);
        executeStyler(fileAnalysis, path);
        executeGoodPractises(fileAnalysis, path);
    }

    private static void executeGoodPractises(FileAnalysis fileAnalysis, Path path) {
    }

    private static void executeStyler(FileAnalysis fileAnalysis, Path path) {
    }

    private static void executeCycloComplexity(FileAnalysis fileAnalysis, Path path) {
    }

    private static void executeLinter(FileAnalysis fileAnalysis, Path path) {
        
    }

    private static void deleteFileFromDirectory(MultipartFile file, Path path) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path uploadPath = path.resolve(filename);
        try {
            Files.delete(uploadPath);
        }
        catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + filename + ". Please try again!", ex);
        }
    }

    private static void saveFileToDirectory(MultipartFile file, Path path) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path uploadPath = path.resolve(filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex) {
            throw new RuntimeException("Could not store file " + filename + ". Please try again!", ex);
        }
    }

    public void saveFile(FileAnalysis fileAnalysis) {
    }
}
