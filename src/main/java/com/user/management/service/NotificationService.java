package com.user.management.service;

import com.user.management.entity.User;

public interface NotificationService {
    void sendWelcomeMail(User user);
}
