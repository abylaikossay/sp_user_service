package kz.smart.plaza.users.services.v1;

import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserTag;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.UserTagEditRequest;
import kz.smart.plaza.users.models.requests.UserTagRequest;
import kz.smart.plaza.users.models.responses.TagResponse;

import java.util.List;

public interface UserTagServiceV1 {
    void addTags(User user, List<UserTagRequest> userTagRequests) throws ServiceException;
    void addTagsToUser(Long userId, List<Long> tagIds);
    void editUserTags(User user, List<Long> tagIds);
    List<TagResponse> getUserTags(Long userId);


}
