package org.binson;

import java.util.ArrayList;
import java.util.Arrays;

import org.binson.lowlevel.ValueType;

/**
 * <p>A Binson array is a list of heterogeneous values.</p>
 * 
 * <p>The getX() methods gets a value of the type X. If the expected value does not exist, 
 * a FormatException is thrown. To check whether a field of a particular type exists, 
 * use the hasX() methods.</p>
 * 
 * @author Frans Lundberg
 */
public class BinsonArray {
    private final ArrayList<Object> list;

    /**
     * Creates an empty Binson array.
     */
    public BinsonArray() {
        super();
        list = new ArrayList<Object>();
    }
    
    public int size() {
        return list.size();
    }
    
    public String toString() {
        return new Binson().put("array", this).toString();
    }
    
    /**
     * Returns a copy of this object that shares no data with the 
     * original object.
     */
    public BinsonArray copy() {
        Binson b1 = new Binson().put("a", this);
        Binson b2 = b1.copy();
        return b2.getArray("a");
    }
    
    /**
     * Returns a Java Object representing the element.
     */
    public Object getElement(int index) {
        return list.get(index);
    }
    
    /**
     * Adds an element to the array.
     * 
     * @throws IllegalArgumentException
     *          If the element is not of a supported type.
     */
    public BinsonArray addElement(Object element) {
        ValueType.fromObject(element);
        list.add(element);
        return this;
    }
    
    /**
     * Adds an element to the array. Any Object may be accepted (incorrectly).
     * This method is intended for internal use.
     */
    public void addElementNoChecks(Object element) {
        list.add(element);
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!(obj instanceof BinsonArray)) {
            return false;
        }
        
        BinsonArray array = (BinsonArray) obj;
        return Arrays.equals(this.toBytes(), array.toBytes());
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.toBytes());
    }
    
    private byte[] toBytes() {
        return new Binson().put("a", this).toBytes();
    }
    
    // boolean
    
    public BinsonArray add(boolean value) {
        list.add((Boolean) value);
        return this;
    }
    
    public boolean isBoolean(int index) {
        Object obj = list.get(index);
        return obj instanceof Boolean;
    }
    
    public boolean getBoolean(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Boolean)) {
            throw new BinsonFormatException("No boolean in Binson array at index " + index + ".");
        }
        return ((Boolean) obj).booleanValue();
    }
    
    // long
    
    public BinsonArray add(long value) {
        list.add((Long) value);
        return this;
    }

    public boolean isInteger(int index) {
        Object obj = list.get(index);
        return obj instanceof Long;
    }
    
    public long getInteger(int index) {   
        Object obj = list.get(index);
        if (!(obj instanceof Long)) {
            throw new BinsonFormatException("No integer in Binson array at index " + index + ".");
        }
        return ((Long) obj).longValue();
    }
    
    // double
    
    public BinsonArray add(double value) {
        list.add((Double) value);
        return this;
    }
    
    public boolean isDouble(int index) {
        Object obj = list.get(index);
        return obj instanceof Double;
    }
    
    public double getDouble(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Double)) {
            throw new BinsonFormatException("No Double in Binson array at index " + index + ".");
        }
        return ((Double) obj).doubleValue();
    }
    
    // string
    
    public BinsonArray add(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        list.add(value);
        return this;
    }
    
    public boolean isString(int index) {
        Object obj = list.get(index);
        return obj instanceof String;
    }
    
    public String getString(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof String)) {
            throw new BinsonFormatException("No String in Binson array at index " + index + ".");
        }
        return (String) obj;
    }
        
    // bytes
    
    public BinsonArray add(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        list.add(value);
        return this;
    }
    
    public boolean isBytes(int index) {
        Object obj = list.get(index);
        return obj instanceof byte[];
    }
    
    public byte[] getBytes(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof byte[])) {
            throw new BinsonFormatException("No bytes element in Binson array at index " + index + ".");
        }
        return (byte[]) obj;
    }
    
    // array
    
    public BinsonArray add(BinsonArray value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        list.add(value);
        return this;
    }
    
    public boolean isArray(int index) {
        Object obj = list.get(index);
        return obj instanceof BinsonArray;
    }
    
    public BinsonArray getArray(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof BinsonArray)) {
            throw new BinsonFormatException("No BinsonArray in Binson array at index " + index + ".");
        }
        return (BinsonArray) obj;
    }
        
    // object
    
    public BinsonArray add(Binson value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null not allowed");
        }
        list.add(value);
        return this;
    }

    public boolean isObject(int index) {
        Object obj = list.get(index);
        return obj instanceof Binson;
    }
    
    public Binson getObject(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Binson)) {
            throw new BinsonFormatException("No Binson object in Binson array at index " + index + ".");
        }
        return (Binson) obj;
    }
}
