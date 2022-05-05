package kz.smart.plaza.users.controllers.rest.v1;

import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.models.requests.TagRequest;
import kz.smart.plaza.users.services.v1.TagServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/tag")
@AllArgsConstructor
public class TagControllerV1 extends BaseController {
    private TagServiceV1 tagServiceV1;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return buildResponse(tagServiceV1.getAll(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTag(@RequestBody Tag tag) {
        return buildResponse(tagServiceV1.addNewTag(tag), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@RequestBody Tag tag, @PathVariable Long id) {
        return buildResponse(tagServiceV1.update(tag, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        return buildResponse(tagServiceV1.delete(id), HttpStatus.OK);
    }
}
