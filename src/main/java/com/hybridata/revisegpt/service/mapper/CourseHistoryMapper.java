package com.hybridata.revisegpt.service.mapper;

import com.hybridata.revisegpt.domain.CourseHistory;
import com.hybridata.revisegpt.domain.Session;
import com.hybridata.revisegpt.domain.User;
import com.hybridata.revisegpt.service.dto.CourseHistoryDTO;
import com.hybridata.revisegpt.service.dto.SessionDTO;
import com.hybridata.revisegpt.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseHistory} and its DTO {@link CourseHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseHistoryMapper extends EntityMapper<CourseHistoryDTO, CourseHistory> {
    @Mapping(target = "session", source = "session", qualifiedByName = "sessionId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    CourseHistoryDTO toDto(CourseHistory s);

    @Named("sessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SessionDTO toDtoSessionId(Session session);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
