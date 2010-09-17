/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 * <p>
 * Copyright (c) 2010 by The Regents of the University of California. All rights reserved.
 * </p>
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * </p>
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.</li>
 * <li>Neither the name of the University of California/California Digital
 * Library, Ithaka Harbors/Portico, or Stanford University, nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.</li>
 * </ul>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </p>
 */
package org.jhove2.module.format.tiff;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.annotation.ReportableProperty.PropertyType;
import org.jhove2.config.spring.SpringConfigInfo;
import org.jhove2.core.I8R;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.Message;
import org.jhove2.core.Message.Context;
import org.jhove2.core.Message.Severity;
import org.jhove2.core.format.FormatIdentification;
import org.jhove2.core.format.FormatIdentification.Confidence;
import org.jhove2.core.io.Input;
import org.jhove2.core.reportable.AbstractReportable;
import org.jhove2.core.source.ByteStreamSource;
import org.jhove2.core.source.Source;
import org.jhove2.module.format.Validator.Validity;
import org.jhove2.module.format.tiff.type.Ascii;
import org.jhove2.module.format.tiff.type.AsciiArray;
import org.jhove2.module.format.tiff.type.Byte;
import org.jhove2.module.format.tiff.type.ByteArray;
import org.jhove2.module.format.tiff.type.Double;
import org.jhove2.module.format.tiff.type.FloatObject;
import org.jhove2.module.format.tiff.type.Long;
import org.jhove2.module.format.tiff.type.LongArray;
import org.jhove2.module.format.tiff.type.Rational;
import org.jhove2.module.format.tiff.type.RationalArray;
import org.jhove2.module.format.tiff.type.SByte;
import org.jhove2.module.format.tiff.type.SByteArray;
import org.jhove2.module.format.tiff.type.SLong;
import org.jhove2.module.format.tiff.type.SLongArray;
import org.jhove2.module.format.tiff.type.SShort;
import org.jhove2.module.format.tiff.type.SShortArray;
import org.jhove2.module.format.tiff.type.Short;
import org.jhove2.module.format.tiff.type.ShortArray;
import org.jhove2.module.format.tiff.type.desc.Compression;

/** TIFF IFD entry.
 * 
 * @author mstrong
 */
public class IFDEntry 
extends AbstractReportable
implements Comparable<Object> {

    private static final int 
    DATETIME = 306,
    COMPRESSION = 259,
    CELLLENGTH = 265,
    ORIENTATION = 274,
    PHOTMETRIC_INTERPRETATION = 262,
    TILEWIDTH = 322,
    TILELENGTH = 323,
    TILEOFFSETS = 324,
    TILEBYTECOUNTS = 325,
    XMP = 700,
    ICCProfile = 34675;


    private static final int ARTIST = 315;


    private static final int COLORMAP = 320;


    /** The number of values, Count of the indicated Type */
    protected long count;


    /** compression in descriptive form */
    protected String compression_d;

    /** compression raw */
    protected int compression;

    /**  Name of the TIFF tag */
    protected String name;

    /** the tag that identifies the field */
    protected int tag;

    /** Contains the offset to the value field */
    protected long offsetOfValue;

    /** the previous tag read */
    public static int prevTag;

    /** the field type */
    protected TiffType tiffType;

    /** Contains the value iff the value is 4 or less bytes.  Otherwise is offset to value */
    protected long valueOffset;

    /** TIFF Version - some field types define the TIFF version */
    protected int version = 4;

    /** Tag Sort order error message */
    private Message TagSortOrderErrorMessage;

    /** Byte offset not word aligned message */
    private Message ByteOffsetNotWordAlignedMessage;

    /** Value Offset reference location invalid message */
    private Message ValueOffsetReferenceLocationFileMessage;

    /** Unknown Type Message */
    private List<Message> unknownTypeMessages;

    /** type mismatch message */
    private Message TypeMismatchMessage;

    /** unknown tag message */
    private Message UnknownTagMessage;

    /** invalid count value message */
    private Message InvalidCountValueMessage;

    private Validity isValid = Validity.Undetermined;

    /** Possible TIFF values
     * Each TIFF value can be of a different type so it needs to be stored in a
     * separate object
     */
    /** the value if the value is more than 4 btyes */
    boolean isArray = false;

    protected Ascii asciiValue;

    protected AsciiArray asciiArrayValue;

    protected Byte byteValue;

    protected SByte sByteValue;

    protected ByteArray byteArrayValue;

    protected SByteArray sbyteArrayValue;

    /** 32-bit (4-byte) unsigned integer 
     * @see org.jhove2.module.format.tiff.type.Long */
    protected Long longValue;

    protected LongArray longArrayValue;

    protected SLong sLongValue;

    protected SLongArray sLongArrayValue;

    protected FloatObject floatValue;

    protected Double doubleValue;

    protected Rational rationalValue;

    protected RationalArray rationalArrayValue;

    protected Rational sRationalValue;

    protected RationalArray sRationalArrayValue;

    protected Short shortValue;

    protected ShortArray shortArrayValue;

    protected SShort sShortValue;

    protected SShortArray sShortArrayValue;

    private Message invalidDateTimeMessage;

    private Message invalidDateTimeFormatMessage;

    private Message TileWidthNotMultipleOf16Message;

    private Message TileLengthNotMultipleOf16Message;

    @ReportableProperty(order=5, value = "Entry value/offset.")
    public long getValueOffset() {
        return valueOffset;
    }

    public int getVersion() {
        return version;
    }

    /** no arg constructor */
    public IFDEntry() {
        super();
        this.unknownTypeMessages = new ArrayList<Message>();
    }

    public IFDEntry(int tag, int type, long count, long valueOffset) {
        this.tag = tag;
        this.tiffType = TiffType.getType(type);
        this.count = count;
        this.valueOffset = valueOffset;
    }

    /**
     * parse the IFD Entry 
     * @throws IOException, JHOVE2Exception 
     */
    public void parse(JHOVE2 jhove2, Source source, Input input) throws IOException, JHOVE2Exception{
        this.isValid = Validity.True;
        this.tag = input.readUnsignedShort();
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
        this.tiffType = TiffType.getType(type);

        /* Skip over tags with unknown type; those outside of defined range. */
        if (type < TiffType.BYTE.num()|| type > TiffType.IFD.num()) {
            Object[]messageArgs = new Object[]{type, input.getPosition() };
            this.unknownTypeMessages.add(new Message(Severity.WARNING,
                    Context.OBJECT,
                    "org.jhove2.module.format.tiff.IFD.UnknownTypeMessage",
                    messageArgs, jhove2.getConfigInfo()));               
        }
        else {
            /* set the version */
            if (type <= TiffType.SBYTE.num() && type <= TiffType.IFD.num()) {
                this.version = 6;
            }

            this.count = (int) input.readUnsignedInt();

            /* store the offset of the value field in the IFD Entry */
            this.valueOffset = input.getPosition();
            long saveOffset = this.valueOffset;
            long value = input.readUnsignedInt();

            if (calcValueSize(type, count) > 4) {
                /* the value read is the offset to the value */
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

                /* test offset is word aligned */
                if ((value & 1) != 0){
                    this.isValid = Validity.False;
                    Object[]messageArgs = new Object[]{0, input.getPosition(), valueOffset};
                    this.ByteOffsetNotWordAlignedMessage = (new Message(Severity.ERROR,
                            Context.OBJECT,
                            "org.jhove2.module.format.tiff.IFD.ValueByteOffsetNotWordAlignedMessage",
                            messageArgs, jhove2.getConfigInfo()));               
                    throw new JHOVE2Exception ("Value byte offset is not word aligned");
                }
                /* update the valueOffset to contain the value (which is the offset to the value) */
                this.valueOffset = value;
            }
            /* Parse the ICCProfile tag */
            if (this.tag == ICCProfile) {
                ByteStreamSource bss = new ByteStreamSource(jhove2, source, this.valueOffset, this.count);
                Map<String, Object> i8r = SpringConfigInfo.getObjectsForType(I8R.class);
                I8R identifier = (I8R) i8r.get("ICCIdentifier");
                FormatIdentification iccPresumptiveFormat = new FormatIdentification(identifier, Confidence.PositiveSpecific); 
                bss.addPresumptiveFormat(iccPresumptiveFormat);
                jhove2.characterize(bss, input);
            }
            /* Parse the XMP tag */
            else if (this.tag == XMP) {
                ByteStreamSource bss = new ByteStreamSource(jhove2, source, this.valueOffset, this.count);
                Map<String, Object> i8r = SpringConfigInfo.getObjectsForType(I8R.class);
                I8R identifier = (I8R) i8r.get("XmlIdentifier");
                FormatIdentification xmlPresumptiveFormat = new FormatIdentification(identifier, Confidence.PositiveGeneric); 
                bss.addPresumptiveFormat(xmlPresumptiveFormat);
                jhove2.characterize(bss, input);                
            }
            else {
                readValues(input);
                validate(jhove2, input); 
            }
            /* reset the input position so that the offset is set up correctly since when you read values the
             * input position gets changed from where you want to be in the IFD 
             * the offset of the Value field + 4 bytes will get you to the next Tag field
             */
           input.setPosition(saveOffset + 4);

        }
    }


    /**
     * 
     * validates the IFD Entry by checking: 
     * 
     *  1) if tag is known and defined
     *  2) that the type matches expected type values for that tag definition
     *  3) that count expected matches the count read in
     *  4) performing any other validations for a particular tag
     *  
     * @param jhove2
     * @throws JHOVE2Exception
     * @throws IOException 
     */
    protected void validate(JHOVE2 jhove2, Input input) throws JHOVE2Exception, IOException {

        /* retrieve the definition for the tag read in */
        TiffTag tag = TiffTag.getTag(this.tag);

        /* validate that the type and count read in matches what is expected for this tag */
        if (tag != null) {
            this.name = tag.getName();
            checkType(jhove2, this.tiffType, tag.getType());
            checkCount(jhove2, this.count, tag.getCardinality());
        }
        else {
            this.isValid = Validity.False;
            Object[]messageArgs = new Object[]{this.tag, valueOffset};
            this.UnknownTagMessage = (new Message(Severity.WARNING,
                    Context.OBJECT,
                    "org.jhove2.module.format.tiff.IFDEntry.UnknownTagMessage",
                    messageArgs, jhove2.getConfigInfo()));  
            return;
        }       

        /* Validate specific tags and set version when applicable */

        if (this.tag == ARTIST) {
            if (version < 5) {
                version = 5;
            }
        }
        else if (this.tag == COLORMAP) {
            if (version < 5) {
                version = 5;
            }
        }
        else if (this.tag == COMPRESSION) {
            // get the scheme to determine version
            int scheme = ((Short) this.getValue()).getValue();
            if (scheme == 5 && version < 5) {
                version = 5;
            }
            else if (scheme == 6 && version < 6) {
                version = 6;
            }
            /* set the descriptive format for the Compression Scheme */
            Compression compression =
                Compression.getCompressionValue(scheme, jhove2);
            if (compression != null) {
                this.compression_d = compression.getDescription();
            }
        }

        /* validate Date format is YYYY:MM:DD HH:MM:SS */
        else if (this.tag == DATETIME) {
            SimpleDateFormat date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            String dateTime = (String)((AsciiArray) this.getValue()).toString();
            try {
                date.parse(dateTime);
            }
            catch (ParseException e) {
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{dateTime};
                this.invalidDateTimeFormatMessage = new Message(Severity.ERROR,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.TIFFIFD.invalidDateTimeFormatMessage",
                        messageArgs, jhove2.getConfigInfo());
            }
            /* check that date is a valid date/time */
            date.setLenient(false);
            try {
                date.parse(dateTime);
            }
            catch (ParseException e) {
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{dateTime};
                this.invalidDateTimeMessage = new Message(Severity.ERROR,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.TIFFIFD.invalidDateTimeMessage",
                        messageArgs, jhove2.getConfigInfo());
            }
        }
        /* Validate TILEWIDTH */
        else if (this.tag == TILEWIDTH ) {
            if (version < 6) 
                version = 6;
            long tileWidth = ((Long) this.getValue()).getValue();

            if (tileWidth%16 > 0) {
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{this.tag, valueOffset};
                this.TileWidthNotMultipleOf16Message = (new Message(Severity.WARNING,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.IFDEntry.TileWidthNotMultipleOf16Message",
                        messageArgs, jhove2.getConfigInfo()));  
            }

        }

        /* Validate TILELENGTH */
        if (this.tag == TILELENGTH ) {
            if (version < 6) 
                version = 6;
            long tileLength = ((Long) this.getValue()).getValue();
            if (tileLength%16 > 0) {
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{this.tag, valueOffset};
                this.TileLengthNotMultipleOf16Message = (new Message(Severity.WARNING,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.IFDEntry.TileLengthNotMultipleOf16Message",
                        messageArgs, jhove2.getConfigInfo()));  
                return;
            }
        }

        /* Set version for TILEOFFSETS */
        if (this.tag == TILEOFFSETS ) {
            if (version < 6) 
                version = 6;
        }
        /* Set version for TILEBYTECOUNTS */
        if (this.tag == TILEBYTECOUNTS ) {
            if (version < 6) 
                version = 6;
        }


    }


    /**
     * checks that the count value read in matches the expected count value
     * 
     * @param jhove2
     * @param count
     * @param expectedCount
     * @throws JHOVE2Exception
     */
    private void checkCount(JHOVE2 jhove2, long count, String expectedCount ) throws JHOVE2Exception {
        if (expectedCount != null) {
            int expected = Integer.parseInt(expectedCount);
            if (count < expected) {
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{this.tag, count, expectedCount};
                this.InvalidCountValueMessage = (new Message(Severity.WARNING,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.IFDEntry.InvalidCountValueMessage",
                        messageArgs, jhove2.getConfigInfo()));               
            }

        }        
    }

    /**
     * compares the type that is read in with the type that is defined for the tag
     * For unsigned integers, readers accept BYTE, SHORT, LONG or IFD types
     * 
     * @param jhove2 - JHOVE2 framework
     * @param expected - the list of expected types defined for this tag
     * @param string - the type read in 
     * @throws JHOVE2Exception
     */
    private void checkType(JHOVE2 jhove2, TiffType type, String[] expected) throws JHOVE2Exception {

        int typeNum = type.num();
        String typeReadIn = type.name();
        /* type values of 6-12 were defined in TIFF 6.0 */
        if (typeNum > TiffType.SBYTE.num()) {
            version = 6;
        }

        if ((type.equals(TiffType.BYTE) || (type.equals(TiffType.SHORT)) ||
                (type.equals(TiffType.LONG)) || (type.equals(TiffType.IFD)))) {
            //        if (typeReadIn.equalsIgnoreCase("BYTE") || typeReadIn.equalsIgnoreCase("SHORT") ||
            //                typeReadIn.equalsIgnoreCase("LONG") || typeReadIn.equalsIgnoreCase("IFD")) {
            for (String expectedEntry:expected){
                if (expectedEntry.equalsIgnoreCase("BYTE") || expectedEntry.equalsIgnoreCase("SHORT") || 
                        expectedEntry.equalsIgnoreCase("LONG") || expectedEntry.equalsIgnoreCase("IFD"))             
                    return;  // type is valid
            }
        }
        for (String expectedEntry:expected){
            if (!expectedEntry.equalsIgnoreCase(typeReadIn)) {
                this.isValid = Validity.False;
                Object[]messageArgs = new Object[]{typeReadIn, this.tag, Arrays.toString(expected)};
                this.TypeMismatchMessage = (new Message(Severity.ERROR,
                        Context.OBJECT,
                        "org.jhove2.module.format.tiff.IFDEntry.TypeMismatchMessage",
                        messageArgs, jhove2.getConfigInfo()));               
            }        
        }
    }

    /**
     * read in value based on type
     *   
     * @param input
     * @param entry
     * @throws IOException
     */
    public void readValues(Input input) throws IOException {

        input.setPosition(valueOffset);
        TiffType type = this.tiffType;

        if (count <= 1 ) {
            /* store a single value */
            if (type.equals(TiffType.BYTE) || type.equals(TiffType.UNDEFINED)) 
                byteValue = new Byte(input.readUnsignedByte());
            else if (type.equals(TiffType.ASCII)){
                asciiValue = new Ascii();
                asciiValue.setValue(input, count );
            }
            else if (type.equals(TiffType.SHORT))
                shortValue = new Short(input.readUnsignedShort());
            else if (type.equals(TiffType.LONG))
                longValue = new Long(input.readUnsignedInt());
            else if (type.equals(TiffType.RATIONAL)) {
                long num = input.readUnsignedInt();
                long denom = input.readUnsignedInt();
                rationalValue = new Rational(num, denom);
            }
            else if (type.equals(TiffType.SBYTE)) 
                sByteValue = new SByte(input.readSignedByte());
            else if (type.equals(TiffType.SSHORT))
                sShortValue = new SShort(input.readSignedShort());
            else if (type.equals(TiffType.SLONG))
                sLongValue = new SLong(input.readSignedLong());
            else if (type.equals(TiffType.SRATIONAL)) {
                long num = input.readSignedInt();
                long demon = input.readSignedInt();
                sRationalValue = new Rational(num, demon);
            }
            /*
             *(TODO:  FLOAT & DOUBLE
             */
        }
        else {
            isArray = true;
            /* read into an array */
            if (type.equals(TiffType.BYTE) || type.equals(TiffType.UNDEFINED)) {
                byteArrayValue = new ByteArray();
                byteArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.ASCII)){
                asciiArrayValue = new AsciiArray();
                asciiArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.SHORT)) {
                shortArrayValue = new ShortArray();
                shortArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.LONG)) {
                longArrayValue = new LongArray();
                longArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.RATIONAL)) {
                long num = input.readUnsignedInt();
                long denom = input.readUnsignedInt();
                rationalValue = new Rational(num, denom);
            }
            else if (type.equals(TiffType.SBYTE)) { 
                sbyteArrayValue = new SByteArray();
                sbyteArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.SSHORT)) {
                sShortArrayValue = new SShortArray();
                sShortArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.SLONG)) {
                sLongArrayValue = new SLongArray();
                sLongArrayValue.setValue(input, count);
            }
            else if (type.equals(TiffType.SRATIONAL)) {
                long num = input.readSignedLong();
                long demon = input.readSignedLong();
                rationalValue = new Rational(num, demon);
            }

        }
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
        fieldSize = TiffType.getType(type).size();
        return  count*fieldSize;
    }

    /**
     * reset the previous tag value 
     */
    public static void resetPrevTag(int value) {
        prevTag = value;
    }
    /**
     * The cardinality (number of values) for this TIFF tag  
     * @return long
     */
    @ReportableProperty (order=4, value="Entry number of values.")
    public long getCount(){
        return count;
    }

    /**
     * The name of the tag entry
     * @return String 
     */
    @ReportableProperty (order=1, value = "Entry tag name.")
    public String getName(){
        return name;
    }

    /**
     * The TIFF tag number
     * @return int
     */
    @ReportableProperty(order=2, value="Entry tag.")
    public int getTag(){
        return tag;
    }

    /**
     * Get the unknown tag message
     * @return the unknownTagMessage
     */
    @ReportableProperty(order=3, value = "unknown tag message.")
    public Message getUnknownTagMessage() {
        return UnknownTagMessage;
    }

    /**
     * The field type of the value for this tag
     * @return TiffType
     */
    @ReportableProperty(order=4, value="Tag type.")
    public TiffType getType(){
        return this.tiffType;
    }


    /**
     * @return the value
     */
    @ReportableProperty(order=5, value = "Tiff Tag Value.")
    public Object getValue(){
        TiffType type = this.tiffType;
        if (isArray) {
            if (type.equals(TiffType.BYTE) || (type.equals(TiffType.UNDEFINED)))
                return byteArrayValue;
            else if (type.equals(TiffType.ASCII))
                return asciiArrayValue;
            else if (type.equals(TiffType.SHORT))
                return shortArrayValue;
            else if (type.equals(TiffType.LONG))
                return longArrayValue;
            else if (type.equals(TiffType.RATIONAL))
                return rationalArrayValue;
            else if (type.equals(TiffType.SSHORT))
                return sShortArrayValue;
            else if (type.equals(TiffType.SLONG))
                return sLongArrayValue;
            else if (type.equals(TiffType.SRATIONAL))
                return sRationalArrayValue;
        }
        else  {
            if (type.equals(TiffType.BYTE) || (type.equals(TiffType.UNDEFINED)))
                return byteValue;
            else if (type.equals(TiffType.ASCII))
                return asciiValue;
            else if (type.equals(TiffType.SHORT))
                return shortValue;
            else if (type.equals(TiffType.LONG))
                return longValue;
            else if (type.equals(TiffType.RATIONAL))
                return rationalValue;
            else if (type.equals(TiffType.SBYTE))
                return sByteValue;
            else if (type.equals(TiffType.SLONG))
                return sLongValue;
            else if (type.equals(TiffType.SRATIONAL))
                return sRationalValue;
        }

        return null;
    }



    /**
     * Get the tag sort order error message
     * 
     * @return the tagSortOrderErrorMessage
     */
    @ReportableProperty(order=6, value = "tag sort order error message.")
    public Message getTagSortOrderErrorMessage() {
        return TagSortOrderErrorMessage;
    }

    /**
     * Get the unknown type message
     * 
     * @return the unknownTypeMessage
     */
    @ReportableProperty(order=7, value = "unknown type message.")
    public List<Message> getUnknownTypeMessage() {
        return unknownTypeMessages;
    }
    /**
     * Get byte offset not word aligned message
     * 
     * @return the byteOffsetNotWordAlignedMessage
     */
    @ReportableProperty(order=8, value = "Byte offset not word aligned message.")
    public Message getByteOffsetNotWordAlignedMessage() {
        return ByteOffsetNotWordAlignedMessage;
    }

    /**
     * Get the value offset reference location file message
     * 
     * @return the valueOffsetReferenceLocationFileMessage
     */
    @ReportableProperty(order=9, value = "value offset reference location file message.")
    public Message getValueOffsetReferenceLocationFileMessage() {
        return ValueOffsetReferenceLocationFileMessage;
    }


    /**
     * @return the typeMismatchMessage
     */
    @ReportableProperty(order=10, value="type mismatch message.")
    public Message getTypeMismatchMessage() {
        return TypeMismatchMessage;
    }

    /**
     * @return the invalidCountValueMessage
     */
    @ReportableProperty(order=12, value="Invalid Count Value Message.")
    public Message getInvalidCountValueMessage() {
        return InvalidCountValueMessage;
    }

    /**
     * @return the unknownTypeMessages
     */
    @ReportableProperty(order=13, value="Unknown Type Message.")
    public List<Message> getUnknownTypeMessages() {
        return unknownTypeMessages;
    }

    /**
     * @return the invalidDateTimeMessage
     */
    @ReportableProperty(order=14, value="Invalid Date Time Message.")
    public Message getInvalidDateTimeMessage() {
        return invalidDateTimeMessage;
    }

    /**
     * @return the invalidDateTimeFormatMessage
     */
    @ReportableProperty(order=15, value="Invalid Date Time Format Message.")
    public Message getInvalidDateTimeFormatMessage() {
        return invalidDateTimeFormatMessage;
    }

    @ReportableProperty(order=3, value="Compression Scheme in descriptive form.",
            type=PropertyType.Descriptive)
            public String getCompression_descriptive() {
        return this.compression_d;
    }

    /** Get IFDEntry validity.
     * @return IFDEntry validity
     */
    @ReportableProperty(order=16, value="IFDEntry validity.")
    public Validity isValid() {
        return isValid;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
