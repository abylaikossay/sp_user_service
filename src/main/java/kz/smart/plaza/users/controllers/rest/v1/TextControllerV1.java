package kz.smart.plaza.users.controllers.rest.v1;

import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.services.v1.TagServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/text")
@AllArgsConstructor
public class TextControllerV1 extends BaseController {
    private TagServiceV1 tagServiceV1;

    @GetMapping()
    public ResponseEntity<?> getText() {
        return buildResponse(tagServiceV1.getText(), HttpStatus.OK);
    }

    @PutMapping("/message")
    public ResponseEntity<?> editText(@RequestParam String message) {
        tagServiceV1.editText(message);
        return buildSuccessResponse("Сообщение изменено!");
    }

    @PutMapping("/status")
    public ResponseEntity<?> editStatus(@RequestParam Boolean status) {
        tagServiceV1.editStatus(status);
        return buildSuccessResponse("Статус изменен!");
    }
}
