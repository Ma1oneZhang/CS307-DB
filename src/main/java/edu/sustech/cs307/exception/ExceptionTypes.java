package edu.sustech.cs307.exception;

import edu.sustech.cs307.value.ValueType;

public enum ExceptionTypes {
    WRONG_COMPARISON_TYPE;

    private String error_result;

    public void SetErrorResult(String error_result) {
        this.error_result = error_result;
    }

    public String GetErrorResult() {
        return error_result;
    }

    public ExceptionTypes WrongComparisonError(ValueType left, ValueType right) {
        WRONG_COMPARISON_TYPE.SetErrorResult(
                String.format("The type of value not match, left is %s, right is %s", left, right)
        );
        return WRONG_COMPARISON_TYPE;
    }
}
