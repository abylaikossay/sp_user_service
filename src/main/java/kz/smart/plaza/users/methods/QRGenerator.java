package kz.smart.plaza.users.methods;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import kz.smart.plaza.users.models.entities.CertificateCode;
import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.repositories.UserRepository;
import kz.smart.plaza.users.services.v1.FileServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class QRGenerator {

    private FileServiceV1 fileServiceV1;
    private UserRepository userRepository;
    public User generateUserQr(User user) throws WriterException, IOException {
        if(user.getQrPath() != null) {
            try{
                fileServiceV1.delete(user.getQrPath(), "qr");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        String qr_code = this.generateQrCode();
        Optional<User> userOptional = userRepository.findFirstByQrOrderByUpdatedAtDesc(qr_code);
        if (userOptional.isPresent()) {
            qr_code = this.generateQrCode();
        }
//        String qr = "user" + user.getId() +  UUID.randomUUID();
        Path path = fileServiceV1.generateQR("QR");
        BitMatrix bitMatrix = new QRCodeWriter().encode(qr_code, BarcodeFormat.QR_CODE, 1000, 800);
        MatrixToImageWriter.writeToPath(bitMatrix, "gif", path);
        user.setQr(qr_code);
        user.setQrPath(path.getFileName().toString());
        user.setUpdatedAt(new Date());
        return user;
    }

    private String generateQrCode() {
        return Integer.toString(ThreadLocalRandom.current().nextInt(10000000, 100000000));
    }

    public CertificateCode generateCertificateQr(CertificateCode certificateCode) throws WriterException, IOException {
        String qr_code = Integer.toString(ThreadLocalRandom.current().nextInt(10000000, 100000000));
        Path path = fileServiceV1.generateCertificateQr("certificate_qr");
        BitMatrix bitMatrix = new QRCodeWriter().encode(qr_code, BarcodeFormat.QR_CODE, 600, 400);
        MatrixToImageWriter.writeToPath(bitMatrix, "gif", path);
        certificateCode.setQrString(qr_code);
        certificateCode.setQrUrl(path.getFileName().toString());
        return certificateCode;
    }
}
