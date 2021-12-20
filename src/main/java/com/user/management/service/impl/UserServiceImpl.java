package com.user.management.service.impl;

import com.user.management.constants.ErrorMessages;
import com.user.management.entity.User;
import com.user.management.exception.DuplicatePropertyException;
import com.user.management.exception.ResourceNotFoundException;
import com.user.management.repository.UserRepository;
import com.user.management.service.NotificationService;
import com.user.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * addUser() method adds the user details to the repository.
     * Before inserting the details checks are made to make sure there's no existing user with the email specified
     * After successfully saving the user a welcome mail is sent to the specified email
     */
    @Override
    public User addUser(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent())
            throw new DuplicatePropertyException(ErrorMessages.DUPLICATE_EMAIL_ERROR_MESSAGE);
        user.setCreatedOn(LocalDate.now());
        user =  userRepository.save(user);
        notificationService.sendWelcomeMail(user);
        return user;
    }

    /**
     *Gets the user from the database using the id parameter, if no user is found
     * then we are throwing ResourceNotFound Exception
     */
    @Override
    public User getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND_ERROR_MESSAGE);
        return user.get();
    }

    /**
     *getAllUser() method returns all the users present in database and has support for pagination, sorting
     */
    @Override
    public Page<User> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return userRepository.findAll(pageable);
    }

    /**
     *Deletes the user from the database using the id parameter, if no user is found
     *then we are throwing ResourceNotFound Exception
     */
    @Override
    public void deleteUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND_ERROR_MESSAGE);
        userRepository.deleteById(id);
    }

    /**
     *updateUser() method updates the user details in database, before updating the data in DB
     *it's made sure that there's some existing user with the specified id and the email that is
     *provided is not being used by some other user than the existing user with specified id or is not
     * in the database at all
     */
    @Override
    public User updateUser(User updatedUserDetails) {
        Optional<User> existingUserDetails = userRepository.findById(updatedUserDetails.getId());
        if (existingUserDetails.isEmpty())
            throw new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND_ERROR_MESSAGE);

        Optional<User> existingUserWithSpecifiedEmail = userRepository.findByEmail(updatedUserDetails.getEmail());
        if (existingUserWithSpecifiedEmail.isPresent() && !existingUserWithSpecifiedEmail.get().getId().equalsIgnoreCase(updatedUserDetails.getId()))
            throw new DuplicatePropertyException(ErrorMessages.DUPLICATE_EMAIL_ERROR_MESSAGE);

        updatedUserDetails.setCreatedOn(existingUserDetails.get().getCreatedOn());
        updatedUserDetails.setLastModifiedOn(LocalDate.now());
        return userRepository.save(updatedUserDetails);
    }
}
