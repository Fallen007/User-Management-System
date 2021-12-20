package com.user.management.mapper;

import com.user.management.dto.UserDTO;
import com.user.management.entity.User;
import org.mapstruct.Mapper;

/**
 *MapStructMapper contains method for conversion UserDTO to User entity and vice versa
 */
@Mapper(componentModel = "spring")
public interface MapStructMapper {
    User userDtoToUser(UserDTO userDTO);

    UserDTO userToUserDto(User user);
}
