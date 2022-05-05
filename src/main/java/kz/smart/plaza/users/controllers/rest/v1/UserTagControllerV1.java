package kz.smart.plaza.users.controllers.rest.v1;

import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.models.requests.UserTagRequest;
import kz.smart.plaza.users.services.v1.UserTagServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-tag")
@AllArgsConstructor
public class UserTagControllerV1 extends BaseController {
    private UserTagServiceV1 userTagServiceV1;

    @PostMapping("/add")
    public ResponseEntity<?> addTagToUser(@RequestBody UserTagRequest userTagRequest) {
        userTagServiceV1.addTagsToUser(userTagRequest.getUserId(), userTagRequest.getTagIds());
        return buildSuccessResponse("Тэг успешно добавлен");
    }

//    @PutMapping("/edit")
//    public ResponseEntity<?> editUserTags(@RequestBody UserTagRequest userTagRequest) {
//        userTagServiceV1.addTagsToUser(userTagRequest.getUserId(), userTagRequest.getTagIds());
//        return buildSuccessResponse("Тэг успешно добавлен");
//    }
}
