package com.example.userroleservice.mapper;

import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.entity.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T10:58:21+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toEntity(RoleDto dto) {
        if ( dto == null ) {
            return null;
        }

        Role role = new Role();

        role.setDescription( dto.getDescription() );
        role.setId( dto.getId() );
        role.setName( dto.getName() );

        return role;
    }

    @Override
    public RoleDto toDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto roleDto = new RoleDto();

        roleDto.setDescription( role.getDescription() );
        roleDto.setId( role.getId() );
        roleDto.setName( role.getName() );

        return roleDto;
    }

    @Override
    public void updateEntity(RoleDto roleDto, Role role) {
        if ( roleDto == null ) {
            return;
        }

        role.setDescription( roleDto.getDescription() );
        role.setName( roleDto.getName() );
    }
}
