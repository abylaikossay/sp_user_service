package kz.smart.plaza.users.services.v1.impl;

import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserTag;
import kz.smart.plaza.users.models.errors.ErrorCode;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.UserTagEditRequest;
import kz.smart.plaza.users.models.requests.UserTagRequest;
import kz.smart.plaza.users.models.responses.TagResponse;
import kz.smart.plaza.users.repositories.TagRepository;
import kz.smart.plaza.users.repositories.UserRepository;
import kz.smart.plaza.users.repositories.UserTagRepository;
import kz.smart.plaza.users.services.v1.UserTagServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserTagServiceImplV1 implements UserTagServiceV1 {

    private UserTagRepository userTagRepository;
    private TagRepository tagRepository;
    private UserRepository userRepository;

    @Override
    public void addTags(User user, List<UserTagRequest> userTagRequests) throws ServiceException {
        for (UserTagRequest userTagRequest: userTagRequests) {
            System.out.println(userTagRequest);
//            UserTag userTag = addOrUpdate(userTagRequest, user, false);
//            userTagRepository.save(userTag);
        }
    }

    @Override
    public void addTagsToUser(Long userId, List<Long> tagIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        List<Tag> tags = tagRepository.findAllById_In(tagIds);
        List<UserTag> userTags = userTagRepository.findAllByDeletedAtIsNullAndUser_Id(userId);
        tags.forEach( tag -> {
            userTags.forEach( (e) -> {
                if (e.getTag().getId().equals(tag.getId())){
                    throw ServiceException.builder()
                            .errorCode(ErrorCode.INVALID_EMPLOYEE)
                            .httpStatus(HttpStatus.CONFLICT)
                            .message("У данного пользователя уже имеется этот тэг!")
                            .build();
                }
            });
            UserTag userTag = new UserTag();
            userTag.setTag(tag);
            userTag.setUser(user);
            userTagRepository.save(userTag);
        });
    }

    @Override
    public void editUserTags(User user, List<Long> tagIds) {
        if (tagIds.isEmpty()) {
            List<UserTag> userTags = userTagRepository.findAllByDeletedAtIsNullAndUser_Id(user.getId());
            userTagRepository.deleteAll(userTags);
        } else {
            List<UserTag> userTags = userTagRepository.findAllByDeletedAtIsNullAndUser_Id(user.getId());
            userTagRepository.deleteAll(userTags);
            List<Tag> tags = tagRepository.findAllByDeletedAtIsNullAndId_In(tagIds);
            tags.forEach( tag -> {
                UserTag userTag = new UserTag();
                userTag.setTag(tag);
                userTag.setUser(user);
                userTagRepository.save(userTag);
            });
        }
    }

    @Override
    public List<TagResponse> getUserTags(Long userId) {
        return userTagRepository.findAllByDeletedAtIsNullAndUser_Id(userId)
                .stream().map((e) ->
                        TagResponse.builder()
                                .name(e.getTag().getName())
                                .noTransactionBlocking(e.getTag().getNoTransactionBlocking())
                                .createdAt(e.getTag().getCreatedAt())
                                .updatedAt(e.getTag().getUpdatedAt())
                                .id(e.getTag().getId())
                                .build())
                .collect(Collectors.toList());
    }

//    private UserTag addOrUpdate(UserTagRequest userTagRequest, User user, Boolean update) throws ServiceException {
//        UserTag userTag;
//        if(userTagRequest.getUserId() == null || (!update && userTagRequest.getUserId() != null)) userTag = new UserTag();
//        else {
//            userTag = userTagRepository.findFirstByIdAndBrand_Id(brandCategoryRequest.getId(), brand.getId());
//            if(brandCategory == null) throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT).httpStatus(HttpStatus.BAD_REQUEST)
//                    .message("Категория не существует!").build();
//        }
//        if(brandCategoryRequest.getId() == null) {
//            if (brandCategoryRepository.findFirstByCategory_IdAndBrand_IdAndDeletedAtIsNull(brandCategoryRequest.getCategoryId(), brand.getId()) != null)
//                throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT).httpStatus(HttpStatus.BAD_REQUEST)
//                        .message("Категория бренда уже сохраненa, невозможно дублировать").build();
//        }
//        Category category = new Category();
//        category.setId(brandCategoryRequest.getCategoryId());
//
//        brandCategory.setBrand(brand);
//        brandCategory.setCategory(category);
//        brandCategory.setPercentage(brandCategoryRequest.getPercentage());
//        return brandCategoryRepository.save(brandCategory);
//    }
}
