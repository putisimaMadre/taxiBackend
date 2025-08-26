package com.formatoweb.taxi.repositories;

import com.formatoweb.taxi.models.UserHasRoles;
import com.formatoweb.taxi.models.id.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHasRolesRepository extends JpaRepository<UserHasRoles, UserRoleId> {
}
