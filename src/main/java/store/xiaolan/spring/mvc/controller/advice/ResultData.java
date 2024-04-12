package store.xiaolan.spring.mvc.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultData<T> {
    private T result;
    private boolean success;
    private Integer errorCode;
    private String errorMsg;

    public static <T> ResultData<T> success(T result) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setResult(result);
        resultData.setSuccess(true);
        return resultData;
    }

    public static ResultData fail(Integer errorCode,String errorMsg) {
        ResultData<Object> resultData = new ResultData<>();
        resultData.setSuccess(false);
        resultData.setErrorCode(errorCode);
        resultData.setErrorMsg(errorMsg);
        return resultData;
    }
}
