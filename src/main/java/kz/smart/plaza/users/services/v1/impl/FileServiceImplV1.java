package kz.smart.plaza.users.services.v1.impl;

import kz.smart.plaza.users.configurations.FileStorageProperties;
import kz.smart.plaza.users.models.errors.FileStorageException;
import kz.smart.plaza.users.models.errors.MyFileNotFoundException;
import kz.smart.plaza.users.services.v1.FileServiceV1;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Transactional
public class FileServiceImplV1 implements FileServiceV1 {

    private final Path fileStorageLocation;

    @Autowired
    public FileServiceImplV1(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.fileStorageLocation.resolve("avatars"));
            Files.createDirectories(this.fileStorageLocation.resolve("certificate-barcodes"));
            Files.createDirectories(this.fileStorageLocation.resolve("certificate-qrcodes"));
            Files.createDirectories(this.fileStorageLocation.resolve("certificates"));
            Files.createDirectories(this.fileStorageLocation.resolve("qr_certificates"));
            Files.createDirectories(this.fileStorageLocation.resolve("qr's"));
            Files.createDirectories(this.fileStorageLocation.resolve("buttons"));
        } catch (Exception ex) {
            throw new FileStorageException("Ошибка при создании папок.", ex);
        }
    }
    @Override
    public String storeFile(MultipartFile file, String type) {
        String directory = "";
        switch (type) {
            case "avatar": directory = "avatars/"; break;
            case "qr": directory = "qr's/"; break;
            case "certificate-barcode": directory = "certificate-barcodes/"; break;
            case "certificate": directory = "certificates/"; break;
            case "qr_certificate": directory = "qr_certificates/"; break;
            case "certificate-qrcode": directory = "certificate-qrcodes/"; break;
            case "button": directory = "buttons/"; break;

        }
        String fileName = StringUtils.cleanPath(type);

        try {
            if (Objects.requireNonNull(file.getOriginalFilename()).contains("..")) {
                throw new FileStorageException("Неправильный формат " + fileName);
            }
            fileName = fileName + "_" + System.currentTimeMillis() + "."
                    + FilenameUtils.getExtension(file.getOriginalFilename());
            Path targetLocation = this.fileStorageLocation.resolve(directory + fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Невозможно сохранить файл " + fileName + ". Попробуйте еще раз!", ex);
        }
    }

    @Override
    public Resource getFile(String fileName, String type) {
        String directory = "";
        switch (type) {
            case "avatar": directory = "avatars/"; break;
            case "certificate-barcode": directory = "certificate-barcodes/"; break;
            case "certificate-qrcode": directory = "certificate-qrcodes/"; break;
            case "certificate": directory = "certificates/"; break;
            case "qr_certificate": directory = "qr_certificates/"; break;
            case "qr": directory = "qr's/"; break;
            case "excel": directory = "excels/"; break;
            case "button": directory = "buttons/"; break;
        }
        try {
            Path filePath;
            if (type.equals("excel")) {
                filePath = this.fileStorageLocation.resolve(fileName).normalize();
            } else {
                filePath = this.fileStorageLocation.resolve(directory + fileName).normalize();
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                filePath = this.fileStorageLocation.resolve("default.png").normalize();
                resource = new UrlResource(filePath.toUri());;
                if(resource.exists()) {
                    return resource;
                }
                else {
                    throw new MyFileNotFoundException("FileResource not found " + fileName);
                }
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("FileResource not found " + fileName, ex);
        }
    }

    public Boolean delete(String fileName, String type) {
        String directory = "";
        switch (type) {
            case "avatar": directory = "avatars/"; break;
            case "qr": directory = "qr's/"; break;
            case "certificate-barcode": directory = "certificate-barcodes/"; break;
            case "certificate-qrcode": directory = "certificate-qrcodes/"; break;
            case "qr_certificate": directory = "qr_certificates/"; break;
            case "certificate": directory = "certificates/"; break;
            case "button": directory = "buttons/"; break;
        }
        Path filePath = this.fileStorageLocation.resolve(directory + fileName).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            resource.getFile().delete();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Path generateQR(String qrCode) {
        String qrPath = qrCode +  "_" + System.currentTimeMillis();
        Path targetLocation = this.fileStorageLocation.resolve("qr's/");
        try {
            targetLocation = Files.createTempFile(targetLocation, qrPath, ".gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetLocation;
    }

    @Override
    public Path generateCertificateQr(String qrCode) {
        String qrPath = qrCode +  "_" + System.currentTimeMillis();
        Path targetLocation = this.fileStorageLocation.resolve("certificate-qrcodes/");
        try {
            targetLocation = Files.createTempFile(targetLocation, qrPath, ".gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetLocation;
    }
}
