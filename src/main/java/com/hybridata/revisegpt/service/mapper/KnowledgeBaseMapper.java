package com.hybridata.revisegpt.service.mapper;

import com.hybridata.revisegpt.domain.KnowledgeBase;
import com.hybridata.revisegpt.domain.User;
import com.hybridata.revisegpt.service.dto.KnowledgeBaseDTO;
import com.hybridata.revisegpt.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KnowledgeBase} and its DTO {@link KnowledgeBaseDTO}.
 */
@Mapper(componentModel = "spring")
public interface KnowledgeBaseMapper extends EntityMapper<KnowledgeBaseDTO, KnowledgeBase> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    KnowledgeBaseDTO toDto(KnowledgeBase s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
