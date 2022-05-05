package kz.smart.plaza.users.controllers.rest.v1;

import io.swagger.annotations.ApiOperation;
import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.services.v1.FileServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class FileControllerV1 extends BaseController {

    private FileServiceV1 fileServiceV1;

    @GetMapping("/{type}/{fileName:.+}")
    @ApiOperation("Получить файл файл")
    public ResponseEntity<byte[]> showFile(@PathVariable String fileName,
                                           @PathVariable String type,
                                           HttpServletRequest request) throws IOException {

        Resource resource = fileServiceV1.getFile(fileName, type);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("could not determine file");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(Files.readAllBytes(resource.getFile().toPath()));
    }
//
//    @GetMapping("/download/{type}/{fileName:.+}")
//    @ApiOperation("Скачать файл")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @PathVariable String type, HttpServletRequest request) {
//
//        Resource resource = fileServiceV1.getFile(fileName, type);
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//            System.out.println("could not determine file");
//        }
//
//        if (contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }



}
