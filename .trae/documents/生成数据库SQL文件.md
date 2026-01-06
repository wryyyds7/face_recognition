### 1. 任务目标
生成项目所需的数据库SQL文件，包含所有实体类对应的表结构，放置在项目根目录下。

### 2. 分析实体类
根据收集的代码，需要创建以下表：
- `sys_user` - 用户表（对应 `User` 实体）
- `sys_role` - 角色表（对应 `Role` 实体）
- `sys_user_role` - 用户角色关联表（根据 `User` 实体中的 `roleIds` 推断）
- `sys_permission` - 权限表（根据 `PermittionMapper` 推断）
- `sys_role_permission` - 角色权限关联表（根据 `Role` 实体中的 `permissions` 推断）
- `attendance_record` - 考勤记录表（对应 `AttendanceRecord` 实体）
- `face_recognition_log` - 人脸识别日志表（对应 `FaceRecognitionLog` 实体）

### 3. 实现方案
1. 编写SQL文件，包含所有表的创建语句
2. 遵循MySQL语法规范
3. 包含必要的索引和外键约束
4. 包含 `BaseEntity` 中的公共字段
5. 将SQL文件放置在项目根目录下

### 4. 预期结果
生成完整的数据库SQL文件，确保项目中所有模块都能正常使用。