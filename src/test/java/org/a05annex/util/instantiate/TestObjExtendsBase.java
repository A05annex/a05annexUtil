package org.a05annex.util.instantiate;

public class TestObjExtendsBase extends TestObjBase implements ITestInterface{
    public TestObjExtendsBase() {
        super();
    }
    public TestObjExtendsBase(Boolean boolField, Integer intField, String strField) {
        super(boolField, intField, strField);
    }
    @Override
    public boolean getTestInterfaceValue() {
        return true;
    }
}

