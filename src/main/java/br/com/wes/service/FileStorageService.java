package br.com.wes.service;

import br.com.wes.config.FileStorageConfig;
import br.com.wes.exception.FileNotFoundException;
import br.com.wes.exception.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

@Service
public class FileStorageService {

    private final Logger logger = Logger.getLogger(FileStorageService.class.getName());
    private final Path fileStorageLocation;

    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored", e);
        }
    }

    public String storeFile(MultipartFile file) {
        logger.info("Storing file to disk");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileStorageException("Original filename does not exists");
        }

        String filename = StringUtils.cleanPath(originalFilename);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + filename + ". Please try again", e);
        }
    }

    public Resource loadFileAsResource(String filename) {
        logger.info("Reading a file from the disk");

        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new FileStorageException("File not found");
            }

            return resource;
        } catch (Exception e) {
            throw new FileNotFoundException("File not found", e);
        }
    }
}
