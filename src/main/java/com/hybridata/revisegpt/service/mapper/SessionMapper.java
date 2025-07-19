package com.hybridata.revisegpt.service.mapper;

import com.hybridata.revisegpt.domain.Session;
import com.hybridata.revisegpt.domain.User;
import com.hybridata.revisegpt.service.dto.SessionDTO;
import com.hybridata.revisegpt.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Session} and its DTO {@link SessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SessionMapper extends EntityMapper<SessionDTO, Session> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    SessionDTO toDto(Session s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
