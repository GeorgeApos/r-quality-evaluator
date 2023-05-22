package gr.uom.rqualityevaluator.service;

import com.github.rcaller.rstuff.*;
import com.github.rcaller.util.Globals;
import gr.uom.rqualityevaluator.models.FileAnalysis;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    public static FileAnalysis startMainAnalysis(MultipartFile file, String owner) throws IOException, InterruptedException {
        FileAnalysis fileAnalysis = new FileAnalysis();
        fileAnalysis.setName(file.getOriginalFilename());
        fileAnalysis.setOwner(owner);

        Path path = Paths.get(System.getProperty("user.dir"));
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        saveFileToDirectory(file, path);

        executeCommands(fileAnalysis, path);

        deleteFileFromDirectory(file, path);
        return fileAnalysis;
    }

    private static void executeCommands(FileAnalysis fileAnalysis, Path path) throws IOException, InterruptedException {
        // Your script
        String script =
                "install.packages(\"lintr\")\n" +
                        "install.packages(\"cyclocomp\")\n" +
                        "library(lintr)\n" +
                        "library(cyclocomp)\n" +
                        "setwd(\"" + path + "\")\n" +
                        "analysis_file <- \"" + fileAnalysis.getName() + "\"\n" +
                        "lintr::lint_dir(\"" + path + "\", linters = with_defaults(line_length_linter(120)))\n" +
                        "cyclocomp::cyclocomp(analysis_file)\n" +
                        "q()\n";

        // create a temp file and write your script to it
        File tempScript = File.createTempFile("test_r_scripts_", "");
        try(OutputStream output = new FileOutputStream(tempScript)) {
            output.write(script.getBytes());
        }

        // build the process object and start it
        List<String> commandList = new ArrayList<>();
        commandList.add("/usr/bin/Rscript");
        commandList.add(tempScript.getAbsolutePath());
        ProcessBuilder builder = new ProcessBuilder(commandList);
        builder.redirectErrorStream(true);
        Process shell = builder.start();

        // read the output and show it
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(shell.getInputStream()))) {
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // wait for the process to finish
        int exitCode = shell.waitFor();

        // delete your temp file
        tempScript.delete();

        // check the exit code (exit code = 0 usually means "executed ok")
        System.out.println("EXIT CODE: " + exitCode);

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
