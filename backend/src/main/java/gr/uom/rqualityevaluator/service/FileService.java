package gr.uom.rqualityevaluator.service;

import gr.uom.rqualityevaluator.models.FileAnalysis;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        setupREnvironment(fileAnalysis, path);

        executeAnalysis(fileAnalysis, path);

        deleteFileFromDirectory(file, path);
        return fileAnalysis;
    }

    private static void setupREnvironment(FileAnalysis file, Path path) {
        executeCommand(file, "R", path);
        executeCommand(file, "install.packages(\"styler\")", path);
        executeCommand(file, "install.packages(\"lintr\")", path);
        executeCommand(file, "install.packages(\"goodpractice\")", path);
        executeCommand(file, "install.packages(\"cyclocomp\")", path);
        executeCommand(file, "library(styler)", path);
        executeCommand(file, "library(lintr)", path);
        executeCommand(file, "library(goodpractice)", path);
        executeCommand(file, "library(cyclocomp)", path);
        executeCommand(file, "setwd(\"" + path + "\")", path);
    }

    private static void executeCommand(FileAnalysis file, String command, Path path) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(path.toFile());

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                //edw bgainoun ta results
                System.out.println(line);
                processResults(file, line, command);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Command executed successfully.");
            } else {
                System.out.println("Command failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void processResults(FileAnalysis file, String line, String command) {
        List lintList = new ArrayList<>();
        if(command.startsWith("lint(") && line.startsWith(System.getProperty("user.dir"))) {
            lintList.add(line);
        } else if(command.startsWith("cyclocomp(")) {
            file.setCycloComplexity(Integer.parseInt(line));
        } else if(command.startsWith("styler::style_file(")) {
            file.setStylerResult(line);
        } else if(command.startsWith("goodpractice::gp(") && line.startsWith(System.getProperty("user.dir"))) {

        }
        file.setLinterComments(lintList);
    }

    private static void executeAnalysis(FileAnalysis fileAnalysis, Path path) {
        executeLinter(fileAnalysis, path);
        executeCycloComplexity(fileAnalysis, path);
        executeStyler(fileAnalysis, path);
        executeGoodPractises(fileAnalysis, path);
    }

    private static void executeGoodPractises(FileAnalysis fileAnalysis, Path path) {
        executeCommand(fileAnalysis,"goodpractice::gp(" + fileAnalysis.getName() + ")", path);
    }

    private static void executeStyler(FileAnalysis fileAnalysis, Path path) {
        executeCommand(fileAnalysis,"styler::style_file(" + fileAnalysis.getName() + ")", path);
    }

    private static void executeCycloComplexity(FileAnalysis fileAnalysis, Path path) {
        executeCommand(fileAnalysis,"cyclocomp(" + fileAnalysis.getName() + ")", path);
    }

    private static void executeLinter(FileAnalysis fileAnalysis, Path path) {
        executeCommand(fileAnalysis, "lint(" + fileAnalysis.getName() + ")", path);
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
