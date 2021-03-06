package org.jhove2.module.format.tiff.type;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.io.Input;
import org.jhove2.core.reportable.AbstractReportable;

import com.sleepycat.persist.model.Persistent;


/**
 * AsciiArray - object to store and array of ASCII Strings.
 * Each ASCII string is NUL (binary zero) terminated.
 * A single String is stored as a single element String[] array.
 * 
 * @author MStrong
 *
 */
@Persistent
public class AsciiArray 
extends AbstractReportable {
    public String[] value;

    /*
     * no-arg constructor
     */
    public AsciiArray() {
    }


    /**
     * Tag ASCII Array value 
     * @returns String representation of the String[] */
    @ReportableProperty(order = 1, value="Tag ASCII Array value")
    public String getValue(){
        return this.toString();
    }

    public String[] getAsciiArray(){
        return value;
    }

    /**
     * read in the NUL terminated ASCII values
     * 
     * @param input
     * @param count
     * @throws IOException
     */
    public void setValue(Input input, long count) throws IOException {
        List<String> list = new LinkedList <String>();
        byte[] buf = new byte[(int) count];
        StringBuffer sb = new StringBuffer();


        for (int i=0; i<count; i++) {
            buf[i] = (byte) input.readUnsignedByte();
            int b = buf[i];
            if (b == 0) {
                list.add (sb.toString());
                sb.setLength (0);
            }
            else {
                sb.append ((char) b);
            }
        }
        
        String [] strs = new String[list.size()];
        value = list.toArray(strs);
    }
    
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        if (value.length > 0) {
            result.append(value[0]);
            for (int i=1; i < value.length; i++) {
                result.append(", ");
                result.append(value[i]);
            }
        }
        return result.toString();        
    }
}

