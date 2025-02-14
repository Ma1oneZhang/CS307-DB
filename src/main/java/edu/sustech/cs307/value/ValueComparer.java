package edu.sustech.cs307.value;

import edu.sustech.cs307.exception.DBException;

/*
*
* ValueComparer::compare
*
* When v1 > v2, return 1
* When v1 = v2, return 0
* When v1 < v2, return -1
* ATTENTION: First: If v1 is null, always return -1; Second: If v2 is null, always return 1.
* */



public class ValueComparer {
    static int compare(Value v1, Value v2) throws DBException {
        if (v1.type != v2.type) {

        }
        if (v1 == null) {
            return -1;
        }
        if (v2 == null) {
            return 1;
        }

    }
}
