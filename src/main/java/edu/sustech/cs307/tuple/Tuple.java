package edu.sustech.cs307.tuple;

import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.value.Value;

public abstract class Tuple {
    public abstract Value getValue(TabCol tabCol);

    public abstract TabCol[] getTupleSchema();

    public abstract Value[] getValues();
}
