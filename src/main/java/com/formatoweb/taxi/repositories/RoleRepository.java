package com.formatoweb.taxi.repositories;

import com.formatoweb.taxi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByName(String name);

    //Esto es lo mismo que el de abajo
    /*@Query("SELECT r FROM Role r JOIN r.userHasRoles ur WHERE ur.user.id = :userId")
    List<Role> findRolesByUserId(@Param("userId")Long userId);*/

    List<Role> findAllByUserHasRoles_User_Id(Long idUser);
}
