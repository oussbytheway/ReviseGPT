package com.hybridata.revisegpt.service.mapper;

import com.hybridata.revisegpt.domain.ChatHistory;
import com.hybridata.revisegpt.domain.Session;
import com.hybridata.revisegpt.domain.User;
import com.hybridata.revisegpt.service.dto.ChatHistoryDTO;
import com.hybridata.revisegpt.service.dto.SessionDTO;
import com.hybridata.revisegpt.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChatHistory} and its DTO {@link ChatHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatHistoryMapper extends EntityMapper<ChatHistoryDTO, ChatHistory> {
    @Mapping(target = "session", source = "session", qualifiedByName = "sessionId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    ChatHistoryDTO toDto(ChatHistory s);

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
