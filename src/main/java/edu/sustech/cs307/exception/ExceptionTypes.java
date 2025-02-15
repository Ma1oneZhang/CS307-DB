package edu.sustech.cs307.exception;

import edu.sustech.cs307.value.ValueType;

public enum ExceptionTypes {
    WRONG_COMPARISON_TYPE,
    BAD_IO_TYPE;

    private String error_result;

    public void SetErrorResult(String error_result) {
        this.error_result = error_result;
    }

    public String GetErrorResult() {
        return error_result;
    }

    static public ExceptionTypes WrongComparisonError(ValueType left, ValueType right) {
        WRONG_COMPARISON_TYPE.SetErrorResult(
                String.format("The type of value not match, left is %s, right is %s", left, right)
        );
        return WRONG_COMPARISON_TYPE;
    }

    static public ExceptionTypes BadIOError(String IOExceptionMessage) {
        BAD_IO_TYPE.SetErrorResult(
                String.format(
                        "I/O is error, please check the message from system.\n Message:%s"
                , IOExceptionMessage)
        );
        return BAD_IO_TYPE;
    }
}
