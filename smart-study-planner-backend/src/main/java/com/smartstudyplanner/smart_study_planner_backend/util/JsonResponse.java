package com.smartstudyplanner.smart_study_planner_backend.util;

/**
 * Standard JSON response format for API
 *
 * @param <T> Type of data
 */
public class JsonResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public JsonResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
