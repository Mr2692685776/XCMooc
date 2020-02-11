package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author newHeart
 * @Create 2020/2/11 19:30
 */
@Data
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {

    String previewUrl;

    public CoursePublishResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);
        this.previewUrl = previewUrl;
    }
}
