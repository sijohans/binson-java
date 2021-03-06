package org.binson;

import org.binson.lowlevel.Hex;
import org.binson.lowlevel.MaxSizeException;
import org.junit.Test;

/**
 * Tests invalid Binson byte arrays.
 * 
 * @author Frans Lundberg
 */
public class InvalidDataTest {

    @Test(expected=BinsonFormatException.class)
    public void testEmptyByteArray() {
        byte[] bytes = new byte[]{};
        Binson.fromBytes(bytes);
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testBadEndByte1() {
        byte[] bytes = new Binson().put("a", 1).toBytes();
        int last = bytes.length - 1;
        bytes[last] = (byte) (bytes[last] - 1);
        Binson.fromBytes(bytes);
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testBadEndByte2() {
        byte[] small = new byte[]{0x40, 0x40};
        Binson.fromBytes(small);
    }
    
    @Test(expected=BinsonFormatException.class)
    public void testIntWhereFieldNameExpected() {
        // {0x40, 0x14, 1, 'a', 0x44, 0x41} = {"a": true}
        byte[] data = new byte[]{0x40, 0x44, 1, 'a', 0x44, 0x41};
        Binson.fromBytes(data);
    }
    
    @Test(expected=MaxSizeException.class)
    public void testIssue180108() {
        // Issue detected 2018-01-08 by Frans Lundberg when using 
        // abnfgen (http://www.quut.com/abnfgen/).
        // Results in OutOfMemoryException for binson-2.0.jar release.
        
        byte[] data = Hex.toBytes("4016c3bc436726404141");
        Binson.fromBytes(data);
    }
}
