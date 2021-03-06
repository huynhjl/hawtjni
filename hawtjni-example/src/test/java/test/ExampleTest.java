package test;

import static org.junit.Assert.*;
import static test.Example.*;

import org.fusesource.hawtjni.runtime.Callback;
import org.junit.Test;
import static org.fusesource.hawtjni.runtime.PointerMath.*;

import test.Example.foo;

public class ExampleTest {

    static private int staticCallbackResult;
    private int instanceCallbackResult;

    @Test
    public void testPointerMath() {
        long values[] = new long[]{
                0, Long.MAX_VALUE, Long.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE,
                0+1, Long.MAX_VALUE+1, Long.MIN_VALUE+1, Integer.MAX_VALUE+1, Integer.MIN_VALUE+1,
                0-1, Long.MAX_VALUE-1, Long.MIN_VALUE-1, Integer.MAX_VALUE-1, Integer.MIN_VALUE-1};
        for( long i: values ) {
           assertEquals(char_add(i, 1), add(i, 1) );
           assertEquals(char_add(i, -1), add(i, -1) );
        }
    }

    @Test
    public void test() {
        // Allocate and initialize some memory on the heap.
        long ptr = malloc(foo.SIZEOF);
        memset(ptr, 0, foo.SIZEOF);

        // Configure an object that can be mapped to a C structure.
        foo expected = new foo();
        expected.a = 35;
        expected.b = Integer.MAX_VALUE;
        
        System.arraycopy("Hello World!".getBytes(), 0, expected.c, 0, 5);
        
        // Marshal the object to the allocated heap memory
        memmove(ptr, expected, foo.SIZEOF);
        
        // Unmarshal the object from the allocated heap memory.
        foo acutal = new foo();
        memmove(acutal, ptr, foo.SIZEOF);
        
        assertEquals(expected, acutal); 
     
        Callback callback = new Callback(this, "instanceCallback", 1);
        callmeback(callback.getAddress());
        assertEquals(69, instanceCallbackResult);
        callback.dispose();
        
        
        callback = new Callback(ExampleTest.class, "staticCallback", 1);
        callmeback(callback.getAddress());
        assertEquals(69, staticCallbackResult);
        callback.dispose();
        
        // Heap memory is not GCed, we must manually free it.
        free(ptr);
    }
    
    public long instanceCallback(long value) {
        this.instanceCallbackResult = (int) value;
        return 0;
    }
    
    static public long staticCallback(long value) {
        staticCallbackResult = (int) value;
        return 0;
    }

}
