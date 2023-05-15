package gr.uom.rqualityevaluator.service;

import gr.uom.rqualityevaluator.models.FileAnalysis;
import org.springframework.beans.factory.annotation.Value;
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
public class FileService {

    @Value("${upload.directory}")
    private static String uploadDirectory;


    public static FileAnalysis startMainAnalysis(MultipartFile file, String owner) throws IOException {
        FileAnalysis fileAnalysis = new FileAnalysis();
        fileAnalysis.setName(file.getOriginalFilename());
        fileAnalysis.setOwner(owner);

        Path path = Paths.get(uploadDirectory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        saveFileToDirectory(file, path);

        executeAnalysis(fileAnalysis, path);

        deleteFileFromDirectory(file, path);
        return new FileAnalysis();
    }

    private static void executeAnalysis(FileAnalysis fileAnalysis, Path path) {
        
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
