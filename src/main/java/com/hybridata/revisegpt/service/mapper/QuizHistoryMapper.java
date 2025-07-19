package com.hybridata.revisegpt.service.mapper;

import com.hybridata.revisegpt.domain.QuizHistory;
import com.hybridata.revisegpt.domain.Session;
import com.hybridata.revisegpt.domain.User;
import com.hybridata.revisegpt.service.dto.QuizHistoryDTO;
import com.hybridata.revisegpt.service.dto.SessionDTO;
import com.hybridata.revisegpt.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuizHistory} and its DTO {@link QuizHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuizHistoryMapper extends EntityMapper<QuizHistoryDTO, QuizHistory> {
    @Mapping(target = "session", source = "session", qualifiedByName = "sessionId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    QuizHistoryDTO toDto(QuizHistory s);

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
