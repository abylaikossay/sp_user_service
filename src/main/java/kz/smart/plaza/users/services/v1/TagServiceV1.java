package kz.smart.plaza.users.services.v1;

import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.models.entities.Text;
import kz.smart.plaza.users.models.requests.TagRequest;

import java.util.List;

public interface TagServiceV1 {
    List<Tag> getAll();
    Tag addNewTag(Tag Tag);
    Tag update(Tag tag, Long id);
    Tag delete(Long id);

    Text getText();
    void editText( String message);
    void editStatus(Boolean status);



}
