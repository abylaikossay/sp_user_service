package kz.smart.plaza.users.services.v1.impl;

import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.models.entities.Text;
import kz.smart.plaza.users.models.errors.ErrorCode;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.TagRequest;
import kz.smart.plaza.users.repositories.TagRepository;
import kz.smart.plaza.users.repositories.TextRepository;
import kz.smart.plaza.users.services.v1.TagServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagServiceImplV1 implements TagServiceV1 {
    private TagRepository tagRepository;
    private TextRepository textRepository;

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAllByDeletedAtIsNull();
    }

    @Override
    public Tag addNewTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag update(Tag tag, Long id) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if(tagOptional.isPresent()) {
            Tag tag1 = tagOptional.get();
            tag1.setName(tag.getName());
            tag1.setNoTransactionBlocking(tag.getNoTransactionBlocking());
            return tagRepository.save(tag1);
        }
        throw ServiceException.builder()
                .errorCode(ErrorCode.INVALID_ARGUMENT)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Тэг не найден!")
                .build();
    }

    @Override
    public Tag delete(Long id) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if(tagOptional.isPresent()) {
            Tag tag1 = tagOptional.get();
            tag1.setDeletedAt(new Date());
            return tagRepository.save(tag1);
        }
        throw ServiceException.builder()
                .errorCode(ErrorCode.INVALID_ARGUMENT)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Тэг не найден!")
                .build();
    }

    @Override
    public Text getText() {
        return textRepository.findFirstByDeletedAtIsNull();
    }

    @Override
    public void editText( String message) {
        Text text = textRepository.findFirstByDeletedAtIsNull();
        text.setMessage(message);
        text.setUpdatedAt(new Date());
        textRepository.save(text);
    }

    @Override
    public void editStatus(Boolean status) {
        Text text = textRepository.findFirstByDeletedAtIsNull();
        text.setStatus(status);
        textRepository.save(text);
    }
}
