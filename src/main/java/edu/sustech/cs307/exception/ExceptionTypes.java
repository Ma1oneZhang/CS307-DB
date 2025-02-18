package edu.sustech.cs307.exception;

import edu.sustech.cs307.value.ValueType;

public enum ExceptionTypes {
    WRONG_COMPARISON_TYPE,
    BAD_IO_TYPE,
    INVALID_SQL,
    UNSUPPORTED_COMMAND_TYPE;

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

    static public ExceptionTypes InvalidSQL(String sql, String reason) {
        INVALID_SQL.SetErrorResult(
                String.format("Invalid SQL: %s, the reason is: %s", sql, reason)
        );
        return INVALID_SQL;
    }

    static public ExceptionTypes UnsupportedCommand(String type) {
        UNSUPPORTED_COMMAND_TYPE.SetErrorResult(
                String.format("Unsupported command type: %s", type)
        );
        return UNSUPPORTED_COMMAND_TYPE;
    }


}
