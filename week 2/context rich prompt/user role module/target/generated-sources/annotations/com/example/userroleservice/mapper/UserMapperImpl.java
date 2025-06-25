package com.example.userroleservice.mapper;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.entity.Role;
import com.example.userroleservice.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T10:58:22+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User toEntity(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( request.getEmail() );
        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setPassword( request.getPassword() );
        user.setUsername( request.getUsername() );

        return user;
    }

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setActive( user.isActive() );
        userDto.setEmail( user.getEmail() );
        userDto.setFirstName( user.getFirstName() );
        userDto.setId( user.getId() );
        userDto.setKeycloakUserId( user.getKeycloakUserId() );
        userDto.setLastName( user.getLastName() );
        userDto.setRoles( roleSetToRoleDtoSet( user.getRoles() ) );
        userDto.setUsername( user.getUsername() );

        return userDto;
    }

    @Override
    public void updateEntity(UserDto dto, User user) {
        if ( dto == null ) {
            return;
        }

        user.setActive( dto.isActive() );
        user.setEmail( dto.getEmail() );
        user.setFirstName( dto.getFirstName() );
        user.setLastName( dto.getLastName() );
        user.setUsername( dto.getUsername() );
    }

    protected Set<RoleDto> roleSetToRoleDtoSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleDto> set1 = new LinkedHashSet<RoleDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleMapper.toDto( role ) );
        }

        return set1;
    }
}
