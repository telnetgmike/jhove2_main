/**
 * 
 */
package org.jhove2.module.format.tiff;

import static org.junit.Assert.assertTrue;

import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mstrong
 *
 */

public class IntelTiffFileTest extends TiffModuleTestBase{

    private String intelTestFile;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        parse(intelTestFile);
    }

    /**
     * Test methods for TIFF Parser 
     */
    @Test
    public void testByteOrder() {
        ByteOrder byteOrder = testTiffModule.getIFH().getByteOrder();
        assertTrue(byteOrder == ByteOrder.LITTLE_ENDIAN);

    }

    @Test
    public void testIFDZeroEntriesMessages() {
        List<IFD> ifdList = testTiffModule.getIFDs();
        for (IFD ifd : ifdList) {
            assertTrue (ifd.getZeroIFDEntriesMessage() == null);        
        }
    }

    @Test
    public void testIFDEntryParseMessages() {
        List<IFD> ifdList = testTiffModule.getIFDs();
        for (IFD ifd : ifdList) {
            Map<Integer, IFDEntry> ifdEntryList = ifd.getEntries();
            IFDEntry ifdEntry = null;
            if ((ifdEntry = ifdEntryList.get(254)) != null)
                assertTrue("Known tag 254 flagged", ifdEntry.getUnknownTagMessage() == null);
            if ((ifdEntry = ifdEntryList.get(34850)) == null)
                assertTrue("Unknown tag 34850 not flagged", ifdEntry == null);
        }
    }

    public String getIntelTestFile() {
        return intelTestFile;
    }

    @Resource
    public void setIntelTestFile(String intelTestFile) {
        this.intelTestFile = intelTestFile;
    }


}
