package org.manlier.srapp.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 表示一个错误
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

    private String userMessage;     //  用户所见的信息
    private int code;               // 错误码
    private String internalMassage; // 内部可见的信息
    private String moreInfo;        //  有关错误的更多信息

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInternalMassage() {
        return internalMassage;
    }

    public void setInternalMassage(String internalMassage) {
        this.internalMassage = internalMassage;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public Error(String userMessage, int code) {
        this.userMessage = userMessage;
        this.code = code;
    }

    public Error(String userMessage, int code, String internalMassage) {
        this.userMessage = userMessage;
        this.code = code;
        this.internalMassage = internalMassage;
    }

    public Error(String userMessage, int code, String internalMassage, String moreInfo) {
        this.userMessage = userMessage;
        this.code = code;
        this.internalMassage = internalMassage;
        this.moreInfo = moreInfo;
    }

}
