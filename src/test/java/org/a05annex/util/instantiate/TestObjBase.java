package org.a05annex.util.instantiate;

public class TestObjBase {
    private final Boolean boolField ;
    private final Integer intField;
    private final String strField;
    public TestObjBase() {
        this.boolField = null;
        this.intField = null;
        this.strField = null;
    }
    TestObjBase(Boolean boolField, Integer intField, String strField) {
        this.boolField = boolField;
        this.intField = intField;
        this.strField = strField;
    }
    public Boolean getBoolField() {
        return boolField;
    }
    public Integer getIntField() {
        return intField;
    }
    public String getStrField() {
        return strField;
    }
}
