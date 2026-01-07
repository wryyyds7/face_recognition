package com.homework.users.controller;

import com.homework.common.controller.BaseController;
import com.homework.common.service.PermittionService;
import com.homework.users.service.UserService;
import com.homework.users.domain.dto.SearchUserDTO;
import com.homework.users.domain.dto.UserDTO;
import com.homework.common.domain.entity.Result;
import com.homework.common.domain.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.homework.users.domain.dto.ComplaintDTO;
import com.homework.users.domain.entity.UserComplaintAttachment;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private PermittionService permittionService;
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    /**
     * 验证当前用户是否为管理员
     * @return 是否为管理员
     */
    private boolean isAdmin() {
        return permittionService.hasRole("ADMIN");
    }

    @PostMapping("/admin/searchUserByPage")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")

    public Result searchUserBypage(@RequestBody SearchUserDTO userDTO){
        try {
            
            // 继续执行搜索逻辑
            return Result.success(userService.searchUserByPage(userDTO));
        } catch (Exception e) {
            log.error("搜索用户失败", e);
            return Result.error("搜索用户失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/user/searchUser")

    public Result searchUser(@RequestBody SearchUserDTO userDTO){
        return Result.success(userService.searchUser(userDTO));
    }

    @PostMapping("/admin/deleteUser")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")

    public Result deleteUser(@RequestBody UserDTO userDTO){
        try {
            // 继续执行删除逻辑
            if (userService.deleteUser(userDTO)){
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/admin/deleteUser/{userId}")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")

    public Result deleteUserById(@PathVariable Long userId){
        try {
            // 创建UserDTO对象，只设置userId
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userId);
            // 执行删除逻辑
            if (userService.deleteUser(userDTO)){
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@permittionService.hasRole('USER')")
    @PutMapping("/user/updateUser")

    public Result updateUser(@RequestBody UserDTO userDTO){
        return Result.success(userService.updateUser(UserDTO.toUser(userDTO)));
    }

    @PutMapping("/admin/updateUserStatus")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")

    public Result updateUserStatus(@RequestBody UserDTO userDTO){
        try {
            // 继续执行状态更新逻辑
            return Result.success(userService.updateUserStatus(userDTO));
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/admin/add")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")

    public Result add(@RequestBody User user){
        try {
            // 继续执行注册逻辑
            return Result.success(userService.register(user));
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return Result.error("添加用户失败: " + e.getMessage());
        }
    }



    /**
     * 创建投诉
     */
    @PostMapping("/complaint")

    public Result createComplaint(@RequestBody ComplaintDTO complaintDTO) {
        log.info("开始处理创建投诉请求: {}", complaintDTO);
        try {
            Result result = Result.success(userService.createComplaint(complaintDTO));
            log.info("创建投诉请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("创建投诉失败: ", e);
            return Result.error("创建投诉失败: " + e.getMessage());
        }
    }

    /**
     * 获取投诉列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/complaint/list")

    public Result getComplaintList(ComplaintDTO complaintDTO) {
        log.info("开始处理获取投诉列表请求: {}", complaintDTO);
        try {
            Result result = Result.success(userService.getComplaintList(complaintDTO));
            log.info("获取投诉列表请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("获取投诉列表失败: ", e);
            return Result.error("获取投诉列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取投诉详情
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/complaint/{complaintId}")

    public Result getComplaintDetail(@PathVariable("complaintId") Long complaintId) {
        log.info("开始处理获取投诉详情请求，投诉ID: {}", complaintId);
        try {
            Result result = Result.success(userService.getComplaintDetail(complaintId));
            log.info("获取投诉详情请求处理成功，投诉ID: {}, 结果: {}", complaintId, result);
            return result;
        } catch (Exception e) {
            log.error("获取投诉详情失败，投诉ID: {}", complaintId, e);
            return Result.error("获取投诉详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新投诉状态
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PutMapping("/complaint/status/{complaintId}")

    public Result updateComplaintStatus(@PathVariable("complaintId") Long complaintId, 
                                       @RequestBody String status) {
        log.info("开始处理更新投诉状态请求，投诉ID: {}, 状态: {}", complaintId, status);
        try {
            Result result = Result.success(userService.updateComplaintStatus(complaintId, status));
            log.info("更新投诉状态请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("更新投诉状态失败: ", e);
            return Result.error("更新投诉状态失败: " + e.getMessage());
        }
    }

    /**
     * 处理投诉
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PutMapping("/complaint/handle/{complaintId}")

    public Result handleComplaint(@PathVariable("complaintId") Long complaintId, 
                                 @RequestParam String status, 
                                 @RequestParam String handleResult, 
                                 @RequestParam Long handlerId) {
        log.info("开始处理投诉请求，投诉ID: {}, 状态: {}, 处理结果: {}, 处理人ID: {}", 
                 complaintId, status, handleResult, handlerId);
        try {
            Result result = Result.success(userService.handleComplaint(complaintId, status, handleResult, handlerId));
            log.info("处理投诉请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("处理投诉失败: ", e);
            return Result.error("处理投诉失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的投诉列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/complaint/user/{userId}")

    public Result getComplaintsByUserId(@PathVariable("userId") Long userId) {
        log.info("开始处理获取用户投诉列表请求，用户ID: {}", userId);
        try {
            Result result = Result.success(userService.getComplaintsByUserId(userId));
            log.info("获取用户投诉列表请求处理成功，用户ID: {}, 结果: {}", userId, result);
            return result;
        } catch (Exception e) {
            log.error("获取用户投诉列表失败，用户ID: {}", userId, e);
            return Result.error("获取用户投诉列表失败: " + e.getMessage());
        }
    }

    /**
     * 上传投诉附件
     */
    @PostMapping("/complaint/attachment")

    public Result uploadComplaintAttachment(@RequestBody UserComplaintAttachment attachment) {
        log.info("开始处理上传投诉附件请求: {}", attachment);
        try {
            Result result = Result.success(userService.uploadComplaintAttachment(attachment));
            log.info("上传投诉附件请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("上传投诉附件失败: ", e);
            return Result.error("上传投诉附件失败: " + e.getMessage());
        }
    }

    /**
     * 获取投诉附件列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/complaint/{complaintId}/attachments")

    public Result getComplaintAttachments(@PathVariable("complaintId") Long complaintId) {
        log.info("开始处理获取投诉附件列表请求，投诉ID: {}", complaintId);
        try {
            Result result = Result.success(userService.getComplaintAttachments(complaintId));
            log.info("获取投诉附件列表请求处理成功，投诉ID: {}, 结果: {}", complaintId, result);
            return result;
        } catch (Exception e) {
            log.error("获取投诉附件列表失败，投诉ID: {}", complaintId, e);
            return Result.error("获取投诉附件列表失败: " + e.getMessage());
        }
    }
}
