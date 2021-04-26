package com.hippo.aws;

public class AsyncTaskResult<T> {
    Throwable throwable;

    public AsyncTaskResult(Exception e) {
        throwable = e;
    }

    public boolean hasError() {
        if (throwable == null)
            return false;
        return true;
    }

    public Throwable getError() {
        return throwable;
    }
}
