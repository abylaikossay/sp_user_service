package kz.smart.plaza.users.methods;

import com.ibm.icu.text.Transliterator;
import kz.smart.plaza.users.models.entities.Certificate;
import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserTag;
import kz.smart.plaza.users.models.requests.BrandPartnerRequest;
import kz.smart.plaza.users.models.requests.EcoBonusRequest;
import kz.smart.plaza.users.models.requests.UserBonusRequest;
import kz.smart.plaza.users.models.responses.TagResponse;
import kz.smart.plaza.users.models.responses.UserBonusResponse;
import kz.smart.plaza.users.models.responses.UserResponse;
import kz.smart.plaza.users.models.requests.BonusRequest;
import kz.smart.plaza.users.models.responses.certificate.CertificateResponse;
import kz.smart.plaza.users.models.responses.cinemaxFc.CinemaxDataResponse;
import kz.smart.plaza.users.models.responses.cinemaxFc.UserCinemaxFcResponse;
import kz.smart.plaza.users.models.responses.laravel.UserLaraResponse;
import kz.smart.plaza.users.models.responses.paginator.PageResponse;
import kz.smart.plaza.users.models.responses.paginator.PageableResponse;
import kz.smart.plaza.users.models.responses.paginator.SortResponse;
import kz.smart.plaza.users.services.v1.UserTagServiceV1;
import lombok.AllArgsConstructor;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Pagination {

    private static ModelMapper modelMapper = new ModelMapper();
    public static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    private CallApi callApi;
    private UserTagServiceV1 userTagServiceV1;

    public PageRequest paginate(Optional<Integer> page,
                                Optional<Integer> size,
                                Optional<String[]> sortBy) {
        Sort sort = Sort.by("id");
        if (sortBy.isPresent()) {
            String[] sorters = sortBy.get();
            List<Sort.Order> sorts = Arrays.stream(sorters)
                    .map(s -> s.split("-")[0].trim().equalsIgnoreCase("asc")
                            ? Sort.Order.asc(s.split("-")[1]) : Sort.Order.desc(s.split("-")[1]))
                    .collect(Collectors.toList());
            sort = Sort.by(sorts);
        }
        return PageRequest.of(page.orElse(0), size.orElse(5), sort);
    }

    public Integer calculateUserAge(Date dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        LocalDate dateofBirth = dateOfBirth.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Period diff = Period.between(dateofBirth, currentDate);
        return diff.getYears();
    }

    public PageResponse userResponses(Page<User> users) {
        Type listType = new TypeToken<List<UserResponse>>() {
        }.getType();
        List<UserResponse> userResponses = modelMapper.map(users.getContent(), listType);
        List<Long> userIds = new ArrayList<>();
        userResponses.forEach(e -> {
            userIds.add(e.getId());
        });

        HashMap<Long, UserBonusResponse> bonusMap = new HashMap<>();
        UserBonusRequest userBonusRequest = UserBonusRequest.builder()
                .userIds(userIds)
                .build();

        List<UserBonusResponse> userBonusResponses = callApi.getUserBonuses(userBonusRequest);
        if (userBonusResponses!=null && userBonusResponses.size() > 0) {
            for (UserBonusResponse userBonusResponse : userBonusResponses) {
                bonusMap.put(userBonusResponse.getUserId(), userBonusResponse);
            }
        }

        for (UserResponse userResponse : userResponses) {
            if (userResponse.getBirthDate() != null) {
                Integer userAge = calculateUserAge(userResponse.getBirthDate());
                userResponse.setAge(userAge);
            } else {
                userResponse.setAge(null);
            }
            List<TagResponse> tagResponses = userTagServiceV1.getUserTags(userResponse.getId());
//            BonusRequest bonusRequest = callApi.getBonus(userResponse.getId());
//            EcoBonusRequest ecoBonusRequest = callApi.getEcoBonus(userResponse.getId());
//            (double) (Math.round(bonusHistory.getBonusPayed() * 100 ) / 100
            userResponse.setBonuses((bonusMap.get(userResponse.getId()) != null && bonusMap.get(userResponse.getId()).getBonus() != null) ? bonusMap.get(userResponse.getId()).getBonus() : 0);
            userResponse.setActiveBonuses((bonusMap.get(userResponse.getId()) != null && bonusMap.get(userResponse.getId()).getActiveBonus() != null) ? bonusMap.get(userResponse.getId()).getActiveBonus() : 0);
            userResponse.setBlockedBonuses((bonusMap.get(userResponse.getId()) != null && bonusMap.get(userResponse.getId()).getBlockedBonus() != null) ? bonusMap.get(userResponse.getId()).getBlockedBonus() : 0);
            userResponse.setEcoBonuses((bonusMap.get(userResponse.getId()) != null && bonusMap.get(userResponse.getId()).getEcoBonus() != null) ? bonusMap.get(userResponse.getId()).getEcoBonus() : 0);
            userResponse.setActiveEcoBonuses((bonusMap.get(userResponse.getId()) != null && bonusMap.get(userResponse.getId()).getActiveEcoBonus() != null) ? bonusMap.get(userResponse.getId()).getActiveEcoBonus() : 0);
            userResponse.setTags(tagResponses);
        }
        return pageResponse(users, userResponses);
    }

    public PageResponse pageResponse(Page<?> page, List<?> responses) {
        SortResponse sortResponse = SortResponse.builder()
                .sorted(page.getSort().isSorted())
                .unsorted(page.getSort().isUnsorted())
                .empty(page.getSort().isEmpty())
                .build();

        PageableResponse pageableResponse = PageableResponse.builder()
                .sortResponse(sortResponse)
                .pageNumber(page.getPageable().getPageNumber())
                .pageSize(page.getPageable().getPageSize())
                .paged(page.getPageable().isPaged())
                .build();

        PageResponse pageResponse =
                PageResponse.builder()
                        .totalPages(page.getTotalPages())
                        .content(responses)
                        .last(page.isLast())
                        .first(page.isFirst())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .pageable(pageableResponse)
                        .numberOfElements(page.getNumberOfElements())
                        .build();
        return pageResponse;
    }

    public UserResponse collect(User user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        BonusRequest bonusRequest = callApi.getBonus(userResponse.getId());
        EcoBonusRequest ecoBonusRequest = callApi.getEcoBonus(userResponse.getId());
        List<TagResponse> tagResponses = userTagServiceV1.getUserTags(user.getId());
        if (user.getBirthDate() != null) {
            Integer userAge = calculateUserAge(userResponse.getBirthDate());
            userResponse.setAge(userAge);
        } else {
            userResponse.setAge(null);
        }
        userResponse.setTags(tagResponses);
//        userResponse.setActiveBonuses(bonusRequest != null ? bonusRequest.getActiveBonuses() : null);
//        userResponse.setBlockedBonuses(bonusRequest != null ? bonusRequest.getBlockedBonuses() : null);
//        userResponse.setBonuses(bonusRequest != null ? bonusRequest.getBonuses() : null);
        userResponse.setActiveBonuses(bonusRequest != null ?
                ((double) Math.floor(bonusRequest.getActiveBonuses() * 100) / 100)
                : null);
        userResponse.setEcoBonuses(ecoBonusRequest != null ?
                ((double) Math.floor(ecoBonusRequest.getEcoBonuses() * 100) / 100)
                : null);
        userResponse.setActiveEcoBonuses(ecoBonusRequest != null ?
                ((double) Math.floor(ecoBonusRequest.getActiveEcoBonuses() * 100) / 100)
                : null);
        userResponse.setBlockedBonuses(bonusRequest != null ?
                ((double) Math.floor(bonusRequest.getBlockedBonuses() * 100) / 100) : null);
        userResponse.setBonuses(bonusRequest != null ?
                ((double) Math.floor(bonusRequest.getBonuses() * 100) / 100) : null);
        return userResponse;
    }

    public CertificateResponse collectCertificate(Certificate certificate) {
        CertificateResponse certificateResponse = modelMapper.map(certificate, CertificateResponse.class);
        BrandPartnerRequest brandPartnerRequest = callApi.getBrandAndPartnerByBrandId(certificate.getBrandId());
        certificateResponse.setBrandLogo(brandPartnerRequest.getBrandLogo());
        certificateResponse.setBrandName(brandPartnerRequest.getBrandName());
        return certificateResponse;
    }

    public CinemaxDataResponse collectCinemaxFc(User user) {
        BonusRequest bonusRequest = callApi.getBonus(user.getId());
        String points = "0";
        if (bonusRequest != null) {
            points = Double.toString(bonusRequest.getActiveBonuses() * 100);
        }
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);

        String firstName = toLatinTrans.transliterate(user.getName());
        String lastName = toLatinTrans.transliterate(user.getSurname());
        UserCinemaxFcResponse userCinemaxFcResponse = UserCinemaxFcResponse.builder()
                .first_name(firstName)
                .last_name(lastName)
                .id(user.getId().toString())
                .points(points)
                .build();
        return CinemaxDataResponse.builder()
                .data(userCinemaxFcResponse
                ).build();
    }

    public UserLaraResponse collectLaravel(User user) {
        BonusRequest bonusRequest = callApi.getBonus(user.getId());
        UserLaraResponse userLaraResponse = UserLaraResponse.builder()
                .id(user.getId())
                .avatar_url((user.getAvatar() != null) ? user.getAvatar() : "")
                .city_id(user.getCityId())
//                .city_title((user.getCityId() == 1) ? "Шымкент" : (user.getCityId() == 2) ? "Алматы" : null)
                .city_title((user.getCityId() != null) ? ((user.getCityId() == 1) ? "Шымкент" : (user.getCityId() == 2) ? "Алматы" : null) : null)
                .first_name(user.getName())
                .last_name(user.getSurname())
                .phone(user.getPhone())
                .points(bonusRequest != null ? bonusRequest.getActiveBonuses() : null)
                .points_returnable(bonusRequest != null ? bonusRequest.getActiveBonuses() : null)
//                .points(bonusRequest != null ? ((double) Math.round(bonusRequest.getActiveBonuses() * 100) / 100) : null)
//                .points_returnable(bonusRequest != null ? ((double) Math.round(bonusRequest.getActiveBonuses() * 100) / 100) : null)
                .session(user.getId().toString())
                .build();
        return userLaraResponse;
    }
}
