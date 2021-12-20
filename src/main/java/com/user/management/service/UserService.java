package com.user.management.service;

import com.user.management.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {

    User addUser(User user);

    User getUserById(String id);

    Page<User> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    void deleteUser(String id);

    User updateUser(User user);
}
