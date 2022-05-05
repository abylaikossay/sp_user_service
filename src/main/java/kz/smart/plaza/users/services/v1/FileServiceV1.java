package kz.smart.plaza.users.services.v1;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileServiceV1 {
    String storeFile(MultipartFile file, String type);
    Resource getFile(String fileName, String type);
    Boolean delete(String fileName, String type);
    Path generateQR(String qrCode);
    Path generateCertificateQr(String qrCode);
}
