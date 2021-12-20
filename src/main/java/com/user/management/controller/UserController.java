package com.user.management.controller;

import com.user.management.constants.AppConstants;
import com.user.management.dto.UserDTO;
import com.user.management.dto.UserDTOPageResponse;
import com.user.management.entity.User;
import com.user.management.mapper.MapStructMapper;
import com.user.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final MapStructMapper mapper;
    private final UserService userService;

    @Autowired
    public UserController(MapStructMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserDTO> addUserDetails(@Valid @RequestBody UserDTO userDTO) {
        User user = mapper.userDtoToUser(userDTO);
        userDTO = mapper.userToUserDto(userService.addUser(user));
        return ResponseEntity.status(201).body(userDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserDetailsById(@PathVariable String userId) {
        UserDTO userDTO = mapper.userToUserDto(userService.getUserById(userId));
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping()
    public ResponseEntity<UserDTOPageResponse> getAllUsersDetails(
        @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
        @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
        @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        Page<User> userPage = userService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
        UserDTOPageResponse userDTOPageResponse = new UserDTOPageResponse();
        userDTOPageResponse.setContent(userPage.getContent()
            .stream()
            .map(mapper::userToUserDto)
            .collect(Collectors.toList()));
        userDTOPageResponse.setPageNo(userPage.getNumber());
        userDTOPageResponse.setPageSize(userPage.getSize());
        userDTOPageResponse.setTotalElements(userPage.getTotalElements());
        userDTOPageResponse.setTotalPages(userPage.getTotalPages());
        userDTOPageResponse.setLast(userPage.isLast());
        return ResponseEntity.ok(userDTOPageResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUserDetails(@PathVariable String userId, @Valid @RequestBody UserDTO userDTO) {
        User user = mapper.userDtoToUser(userDTO);
        user.setId(userId);
        userDTO = mapper.userToUserDto(userService.updateUser(user));
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserDetails(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User Details successfully deleted");
    }
}
