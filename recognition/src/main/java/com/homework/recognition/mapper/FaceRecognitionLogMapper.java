package com.homework.recognition.mapper;

import com.homework.recognition.domain.entity.FaceRecognitionLog;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 人脸识别人物识别日志Mapper
 *
 * @author homework
 */
@Mapper
public interface FaceRecognitionLogMapper {
    @Insert("INSERT INTO face_recognition_log(recognition_time, status, recognized_name, photo_path, confidence, face_count) VALUES(#{recognitionTime}, #{status}, #{recognizedName}, #{photoPath}, #{confidence}, #{faceCount})")
    @Options(useGeneratedKeys = true, keyProperty = "logId")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recognitionTime", column = "recognition_time"),
            @Result(property = "recognizedName", column = "recognized_name"),
            @Result(property = "photoPath", column = "photo_path"),
            @Result(property = "confidence", column = "confidence"),
            @Result(property = "faceCount", column = "face_count")
    })
    int insert(FaceRecognitionLog log);

    @Select("SELECT * FROM face_recognition_log WHERE log_id = #{logId}")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recognitionTime", column = "recognition_time"),
            @Result(property = "recognizedName", column = "recognized_name"),
            @Result(property = "photoPath", column = "photo_path"),
            @Result(property = "confidence", column = "confidence"),
            @Result(property = "faceCount", column = "face_count")
    })
    FaceRecognitionLog selectById(Long logId);

    @Select("SELECT * FROM face_recognition_log WHERE status = #{status}")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recognitionTime", column = "recognition_time"),
            @Result(property = "recognizedName", column = "recognized_name"),
            @Result(property = "photoPath", column = "photo_path"),
            @Result(property = "confidence", column = "confidence"),
            @Result(property = "faceCount", column = "face_count")
    })
    List<FaceRecognitionLog> selectByStatus(String status);

    @Select("SELECT * FROM face_recognition_log WHERE recognition_time BETWEEN #{startTime} AND #{endTime}")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recognitionTime", column = "recognition_time"),
            @Result(property = "recognizedName", column = "recognized_name"),
            @Result(property = "photoPath", column = "photo_path"),
            @Result(property = "confidence", column = "confidence"),
            @Result(property = "faceCount", column = "face_count")
    })
    Page<FaceRecognitionLog> selectByRecognitionTimeBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("SELECT * FROM face_recognition_log WHERE recognized_name = #{recognizedName}")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recognitionTime", column = "recognition_time"),
            @Result(property = "recognizedName", column = "recognized_name"),
            @Result(property = "photoPath", column = "photo_path"),
            @Result(property = "confidence", column = "confidence"),
            @Result(property = "faceCount", column = "face_count")
    })
    List<FaceRecognitionLog> selectByRecognizedName(String recognizedName);

    @Select("SELECT * FROM face_recognition_log")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recognitionTime", column = "recognition_time"),
            @Result(property = "recognizedName", column = "recognized_name"),
            @Result(property = "photoPath", column = "photo_path"),
            @Result(property = "confidence", column = "confidence"),
            @Result(property = "faceCount", column = "face_count")
    })
    Page<FaceRecognitionLog> selectAll();
}