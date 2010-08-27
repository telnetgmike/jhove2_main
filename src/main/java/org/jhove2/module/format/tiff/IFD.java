/**
 * 
 */
package org.jhove2.module.format.tiff;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.config.ConfigInfo;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.Message;
import org.jhove2.core.Message.Context;
import org.jhove2.core.Message.Severity;
import org.jhove2.core.io.Input;
import org.jhove2.core.reportable.AbstractReportable;
import org.jhove2.module.format.Validator.Validity;

/**
 * @author mstrong
 *
 */
public abstract class IFD 
extends AbstractReportable {

    /** IFD Entries in the IFD */
   // protected List<IFDEntry> entries = new ArrayList<IFDEntry>();
    
    /** IFD Entries in the IFD */
    protected HashMap<Integer, IFDEntry> entries = new HashMap<Integer, IFDEntry>();

    /** True if this is the first IFD. */
    private boolean first;

    /** validity of IFD */
    protected Validity isValid;

    /** offset to the next IFD */
    protected long nextIFD;

    /** number of IFD Entries in the IFD */ 
    protected int numEntries;

    /** offset of the IFD */ 
    protected long offset;

    /** True if this is the "thumbnail" IFD. */
    private boolean thumbnail;

    /** TIFF version determined by data in IFDEntry */
    private int version;

    /** Tag sort order error message */
    private Message TagSortOrderErrorMessage;

    /** Message for Zero IFD Entries */
    private Message zeroIFDEntriesMessage;

    /** Byte offset not word aligned message */
    private Message ByteOffsetNotWordAlignedMessage;

    /** Value Offset reference location invalid message */
    private Message ValueOffsetReferenceLocationFileMessage;

    /** Unknown Type Message */
    private List<Message> unknownTypeMessages;

    protected static Properties tiffTagProps;

    protected static Properties tiffTypeProps;


    /** 
     * Instantiate an IFD Class with the Input source
     * @param input
     */
    public IFD() {
        super();
        this.unknownTypeMessages = new ArrayList<Message>();
    }


    /**
     * Parse a source unit input. Implicitly set the start and end elapsed time.
     * 
     * @param input
     *            JHOVE2 framework
     * @param input
     *            Input
     * @throws EOFException
     *             If End-of-File is reached reading the source unit
     * @throws IOException
     *             If an I/O exception is raised reading the source unit
     * @throws JHOVE2Exception
     */
    public void parse(JHOVE2 jhove2, Input input) throws EOFException,
    IOException, JHOVE2Exception {

        this.isValid = Validity.Undetermined;
        long offsetInIFD = offset;
        nextIFD = 0L;

        try {
            /* Read the first byte. */
            input.setPosition(offsetInIFD);
            numEntries = input.readUnsignedShort();
            offsetInIFD += 2;

            if (numEntries < 1){
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{0, input.getPosition(), numEntries};
                this.zeroIFDEntriesMessage = new Message(Severity.ERROR,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.IFD.zeroIFDEntriesMessage",
                        messageArgs, jhove2.getConfigInfo());  
            }

            long length = numEntries * 12;
            /* TODO: read the buffer from the file input stream containing the IFD Entries as
             * opposed to manipulating the Input object */

            /* go to the field that contains the offset to the next IFD - 0 if none */
            offsetInIFD += length;
            nextIFD = 0L;
        }
        catch (IOException e) {
            throw new JHOVE2Exception("Premature EOF" + offsetInIFD, e);
        }

        try {
            /* parse through the list of IFDs */
            int prevTag = 0;
            for (int i=0; i<numEntries; i++) {
                int tag = input.readUnsignedShort();
                if (tag > prevTag)
                    prevTag = tag;
                else {
                    this.isValid = Validity.False;
                    Object[]messageArgs = new Object[]{tag, input.getPosition()};
                    this.TagSortOrderErrorMessage = (new Message(Severity.ERROR,
                            Context.OBJECT,
                            "org.jhove2.module.format.tiff.IFD.TagSortOrderErrorMessage",
                            messageArgs, jhove2.getConfigInfo()));
                }               

                int type = input.readUnsignedShort();

                /* Skip over tags with unknown type; those outside of defined range. */
                if (type < TiffType.types.first().getNum() || type > TiffType.types.last().getNum()) {
                    Object[]messageArgs = new Object[]{type, offset };
                    this.unknownTypeMessages.add(new Message(Severity.ERROR,
                            Context.OBJECT,
                            "org.jhove2.module.format.tiff.IFD.UnknownTypeMessage",
                            messageArgs, jhove2.getConfigInfo()));               
                }
                else {
                    /* set the version */
                    if (type <= TiffType.Type.SBYTE.num() && type <= TiffType.Type.IFD.num()) {
                        this.version = 6;
                    }

                    int count = (int) input.readUnsignedInt();

                    /* keep track of the value offset in the file */
                    long valueOffset = input.getPosition(); 
                    long value = input.readUnsignedInt();

                    if (calcValueSize(type, count) > 4) {
                        /* the value is the offset to the value */
                        long size = input.getSize();

                        /* test that the value offset is within the file */
                        if (value > size) {
                            this.isValid = Validity.False;
                            Object[]messageArgs = new Object[]{tag, value, size};
                            this.ValueOffsetReferenceLocationFileMessage = (new Message(Severity.ERROR,
                                    Context.OBJECT,
                                    "org.jhove2.module.format.tiff.IFD.ValueOffsetReferenceLocationFileMessage",
                                    messageArgs, jhove2.getConfigInfo()));  
                            throw new JHOVE2Exception ("Value offset is not within the file");
                        }
 
                        /* test off set is word aligned */
                        if ((value & 1) != 0){
                            this.isValid = Validity.False;
                            Object[]messageArgs = new Object[]{0, input.getPosition(), valueOffset};
                            this.ByteOffsetNotWordAlignedMessage = (new Message(Severity.ERROR,
                                    Context.OBJECT,
                                    "org.jhove2.module.format.tiff.IFD.ValueByteOffsetNotWordAlignedMessage",
                                    messageArgs, jhove2.getConfigInfo()));               
                            throw new JHOVE2Exception ("Value byte offset is not word aligned");
                        }
                    }
                    else {
                        /* the value is the actual value */
                        value = offset + 10 + 12*i;

                        /* value = valueOffset; */
                    }            
                    IFDEntry ifdEntry = new IFDEntry(tag, type, count, value);

                    ifdEntry.validateEntry(jhove2);
                    ifdEntry = readValues(jhove2, input, ifdEntry);
                    version = ifdEntry.getVersion();
                    entries.put(tag, ifdEntry);

                    /* reset the input position so that the offset is set up correctly since when you read values the
                     * input position gets changed from where you want to be in the IFD 
                     * the offset of the Value field + 4 bytes will get you to the next Tag field
                     */
                    input.setPosition(valueOffset + 4);
                }
            }
        }
        catch (IOException e) {
            throw new JHOVE2Exception ("IOException while reading IFD " + (offset + 2), e);
        }
    }

    /*
    public void parse(long offset) {
        this.isValid = Validity.Undetermined;
    }
    */

    /** get the value(s) for an IFD entry 
     * @param input 
     * @throws IOException */
    public abstract IFDEntry readValues (JHOVE2 jhove2, Input input, IFDEntry entry) throws JHOVE2Exception, IOException;


    /**
     * get the byte offset of the IFD 
     * @return long
     */
    @ReportableProperty(order = 1, value = "Byte offset of IFD.")
    public long getOffset() {
        return offset;
    }

    /**
     * get the number of IFD entries
     * @return int
     */
    @ReportableProperty (order = 2, value = "Number of IFD entries.")
    public int getNumEntries() {
        return numEntries;
    }

    /**
     * Sort the entries in the HashMap and then return only the IFDEntries
     * @return
     */
    @ReportableProperty(order = 3, value="IFD entries.")
    public List<IFDEntry> getIFDEntries() {
        Map<Integer, IFDEntry> sortedEntries = new TreeMap<Integer, IFDEntry>(entries);
        List<IFDEntry> sortedList = new ArrayList<IFDEntry>(sortedEntries.values());
        return sortedList;
    }

    /**
     * get the offsetof the next IFD 
     * @return long
     */
    @ReportableProperty(order = 4, value = "Offset of next IFD.")
    public long getNextIFD() {
        return nextIFD;
    }

    /**
     * Get the Zero IFD entries message
     */
    @ReportableProperty(order=5, value = "Zero IFD Entries message.")
    public Message getZeroIFDEntriesMessage(){
        return zeroIFDEntriesMessage;
    }
    
    /**
     * Get byte offset not word aligned message
     * 
     * @return the byteOffsetNotWordAlignedMessage
     */
    @ReportableProperty(order=6, value = "Byte offset not word aligned message.")
    public Message getByteOffsetNotWordAlignedMessage() {
        return ByteOffsetNotWordAlignedMessage;
    }

    /**
     * Get the tag sort order error message
     * 
     * @return the tagSortOrderErrorMessage
     */
    @ReportableProperty(order=7, value = "tag sort order error message.")
    public Message getTagSortOrderErrorMessage() {
        return TagSortOrderErrorMessage;
    }

    /**
     * Get the value offset reference location file message
     * 
     * @return the valueOffsetReferenceLocationFileMessage
     */
    @ReportableProperty(order=8, value = "value offset reference location file message.")
    public Message getValueOffsetReferenceLocationFileMessage() {
        return ValueOffsetReferenceLocationFileMessage;
    }

    /**
     * Get the unknown type message
     * 
     * @return the unknownTypeMessage
     */
    @ReportableProperty(order=9, value = "unknown type message.")
    public List<Message> getUnknownTypeMessage() {
        return unknownTypeMessages;
    }
    

    public int getVersion() {
        return this.version;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return first;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isThumbnail() {
        return thumbnail;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * Calculate how many bytes a given number of fields of a given
     * type will require.
     * @param type Field type
     * @param count Field count
     */
    public static long calcValueSize (int type, long count)
    {
        int fieldSize = 0;
        fieldSize = TiffType.getType(type).getSize();
        return  count*fieldSize;
    }

    /**
     * @return Properties the Tiff Tag Properties stored in the Java Properties file
     */
    public static Properties getTiffTags(ConfigInfo config)  throws JHOVE2Exception {
        if (tiffTagProps==null){
            tiffTagProps = config.getProperties("TiffTags");
        }
        return tiffTagProps;
    }

    /**
     * @return Properties the Tiff Type Properties stored in the Java Properties file
     */
    public static Properties getTiffType(ConfigInfo config)  throws JHOVE2Exception {
        if (tiffTypeProps==null){
            tiffTypeProps = config.getProperties("TiffTypes");
        }
        return tiffTypeProps;
    }

}
