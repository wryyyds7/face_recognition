package com.homework.users.service;

import com.homework.common.domain.dto.JobPreferenceDTO;
import com.homework.common.domain.dto.EnterpriseFavoriteDTO;
import com.homework.common.domain.dto.ActivityFavoriteDTO;
import com.homework.common.domain.dto.EventRegistrationDTO;
import com.homework.users.domain.dto.SearchUserDTO;
import com.homework.users.domain.dto.UserDTO;
import com.homework.users.domain.dto.ComplaintDTO;
import com.homework.common.domain.entity.LoginInfo;
import com.homework.common.domain.entity.User;
import com.homework.users.domain.entity.UserJobPreference;
import com.homework.users.domain.entity.UserFavoriteEnterprise;
import com.homework.users.domain.entity.UserFavoriteActivity;
import com.homework.users.domain.entity.UserEventRegistration;
import com.homework.users.domain.entity.UserComplaint;
import com.homework.users.domain.entity.UserComplaintAttachment;
import com.github.pagehelper.Page;
import java.util.List;

public interface UserService {
    public LoginInfo login(User user, String ip);
    public boolean logout(String token);
    public Page<User> searchUserByPage(SearchUserDTO searchUserDTO);
    public User searchUser(SearchUserDTO searchUserDTO);
    public Long updateUser(User user);
    public Boolean deleteUser(UserDTO UserDTO);
    public User updateUserStatus(UserDTO UserDTO);
    public User[] addUser(User[] user);
    public Long register(User user);
    public User updateUserPassword(User user);
    // 投诉相关方法
    public Long createComplaint(ComplaintDTO complaintDTO);
    public List<UserComplaint> getComplaintList(ComplaintDTO complaintDTO);
    public UserComplaint getComplaintDetail(Long complaintId);
    public Long updateComplaintStatus(Long complaintId, String status);
    public Long handleComplaint(Long complaintId, String status, String handleResult, Long handlerId);
    public List<UserComplaint> getComplaintsByUserId(Long userId);
    public Long uploadComplaintAttachment(UserComplaintAttachment attachment);
    public List<UserComplaintAttachment> getComplaintAttachments(Long complaintId);
}
