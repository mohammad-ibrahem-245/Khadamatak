package org.example.userapi.Repositories;

import org.example.userapi.Model.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<SiteUser,Long> {

    SiteUser findByEmail(String userid);
    boolean existsByEmail(String userid);

    List<SiteUser> findAllByIsProvider(boolean isProvider);

}
