package com.android.khel247.model;

import java.io.Serializable;

/**
 * Created by Misaal on 02/01/2015.
 */
public class NotificationData implements Serializable {

    /**
     * The type of the message displayed by the notification
     */
    private String messageType;

    /**
     * The body of the message
     */
    private String messageBody;

    /**
     * A flag that is true if the message is a progress message and the progress is negative
     */
    private Boolean isNegativeProgress;

    public NotificationData(String messageType, String messageBody){
        this.messageType = messageType;
        this.messageBody = messageBody;
    }

    /**
     * Constructor
     * @param messageType : messageType
     * @param messageBody : body of the message
     * @param isNegativeProgressMessage : true if the message is a progress message and the progress is negative
     */
    public NotificationData(String messageType, String messageBody, Boolean isNegativeProgressMessage){
        this(messageType, messageBody);
        this.isNegativeProgress = isNegativeProgressMessage;
    }



    public Boolean getIsNegativeProgress() {
        return isNegativeProgress;
    }

    public void setIsNegativeProgress(Boolean isNegativeProgress) {
        this.isNegativeProgress = isNegativeProgress;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
