/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 * <p>
 * Copyright (c) 2010 by The Regents of the University of California, Ithaka
 * Harbors, Inc., and The Board of Trustees of the Leland Stanford Junior
 * University. All rights reserved.
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
package org.jhove2.module.format.sgml;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.format.Format;
import org.jhove2.core.source.Source;
import org.jhove2.module.format.BaseFormatModule;
import org.jhove2.module.format.Validator;

/**
 * JHOVE2 SGML module.  This module will parse and validate an SGML document instance,
 *  and capture selected characterization information.
 *  
 * @author smorrissey
 */
public class SgmlModule extends BaseFormatModule implements Validator {
	/** Directory module version identifier. */
	public static final String VERSION = "1.9.6";

	/** Directory module release date. */
	public static final String RELEASE = "2010-05-19";

	/** Directory module rights statement. */
	public static final String RIGHTS = "Copyright 2010 by Ithaka Harbors, Inc."
		+ "Available under the terms of the BSD license.";
	/** Module validation coverage. */
	public static final Coverage COVERAGE = Coverage.Inclusive;
   
	/** SGML validation status. */
	protected Validity validity;

    /** The JHOVE2 object passed in by the parse method */
    protected JHOVE2 jhove2; 
    
    /** The Source object passed in by the parse method */
    protected  Source source;

    
    /** parser directive -- should sgmlnorm be run in order to extract doctype statement; default is false */
    protected boolean shouldFindDoctype;
    
    /** DOCTYPE statement for SGML instance */
    protected String docTypeString = null;
     
    /** Parser engine for parsing SGML files and extracting significant properties */
    protected SgmlParser sgmlParser;


	/**
     * Instantiates a new SgmlModule instance.
     * 
     * @param format
     *            the Format object
     */
    public SgmlModule(Format format) {
        super(VERSION, RELEASE, RIGHTS, format);
    }
    
	/** Parse the format.
	 * @param jhove2 JHOVE2 framework
	 * @param source Source unit
	 */
	@Override
	public long parse(JHOVE2 jhove2, Source source)
	throws EOFException, IOException, JHOVE2Exception
	{
        this.jhove2 = jhove2;
        this.source = source;
        this.validity = Validity.Undetermined;
        sgmlParser.parseFile(this);
        if (this.isShouldFindDoctype()){
        	sgmlParser.determineDoctype(this);
        }     
		return 0;
	}


	/* (non-Javadoc)
	 * @see org.jhove2.module.format.Validator#validate(org.jhove2.core.JHOVE2, org.jhove2.core.source.Source)
	 * There are no profiles of the SGML format; this method will return the validation status of the SGML document
	 */
	@Override
	public Validity validate(JHOVE2 jhove2, Source source)
	throws JHOVE2Exception {
		 if (this.isSgmlValid()){
	        	this.validity = Validity.True;
	        }
	        else {
	        	this.validity = Validity.False;
	        }
		return this.validity;
	}

	/* (non-Javadoc)
	 * @see org.jhove2.module.format.Validator#getCoverage()
	 */
	@Override
	public Coverage getCoverage() {
		return COVERAGE;
	}

	/* (non-Javadoc)
	 * @see org.jhove2.module.format.Validator#isValid()
	 */
	@Override
	public Validity isValid() {
		if (validity == null) {
			try {
				validate(jhove2, source);
			}
			catch (JHOVE2Exception e) {
			}
		}
		return validity;
	}

    /**
	 * @return the sgmlParser
	 */
	public SgmlParser getSgmlParser() {
		return sgmlParser;
	}

	/**
	 * @param sgmlParser the sgmlParser to set
	 */
	public void setSgmlParser(SgmlParser sgmlParser) {
		this.sgmlParser = sgmlParser;
	}

	/**
	 * @return the findDoctype
	 */
	@ReportableProperty(order = 25, value = "Parser setting:  Run normalizer to construct DOCTYPE statement")
	public boolean isShouldFindDoctype() {
		return shouldFindDoctype;
	}

	/**
	 * @param findDoctype the findDoctype to set
	 */
	public void setShouldFindDoctype(boolean findDoctype) {
		this.shouldFindDoctype = findDoctype;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}
	/**
	 * @return boolean indicating if DOCTYPE statement was deteremines
	 */
	@ReportableProperty(order = 31, value = "Was doctype statement determined")
	public boolean getDocTypeFound() {
		boolean foundDoctpe = false;
		if (docTypeString != null){
			foundDoctpe = true;
		}
		return foundDoctpe;                           
	}
	/**
	 * @return the rootElementName
	 */
	@ReportableProperty(order = 32, value = "Element name for first element in SGML document")
	public String getRootElementName() {
		return this.sgmlParser.getRootElementName();
	}

	/**
	 * @return the isSgmlValid
	 */
	@ReportableProperty(order = 33, value = "SGML document conforms to its DTD")
	public boolean isSgmlValid() {
		return this.sgmlParser.isSgmlValid();
	}

	/**
	 * @return the publicIdCount
	 */
	@ReportableProperty(order = 34, 
			value = "Count of public identifiers associated with notations or external, text, or subdoc entities ")
	public int getPublicIdCount() {
		return this.sgmlParser.getPublicIdCount();
	}

	/**
	 * @return the fileNamesCount
	 */
	@ReportableProperty(order = 34, 
			value = "Count of system (file) identifiers associated with notations or external, text, or subdoc entities, generated by the entity manager from the specified external identifier and other information about the entity or notation")
	public int getEntityFileNamesCount() {
		return this.sgmlParser.getEntityFileNamesCount();
	}

	/**
	 * @return the sysidsCount
	 */
	@ReportableProperty(order = 35, 
			value = "Count of system identifiers associated with notations or external, text, or subdoc entities")
	public int getSysidsCount() {
		return this.sgmlParser.getSysidsCount();
	}

	/**
	 * @return the extTextEntCount
	 */
	@ReportableProperty(order = 36, 
			value = "Count of external SGML text entities")
	public int getExtTextEntCount() {
		return this.sgmlParser.getExtTextEntCount();
	}

	/**
	 * @return the notatDefCount
	 */
	@ReportableProperty(order = 36, 
			value = "Count of notation names")
	public int getNotatDefCount() {
		return this.sgmlParser.getNotatDefCount();
	}

	/**
	 * @return the extDataEntCount
	 */
	@ReportableProperty(order = 37, 
			value = "Count of  external data entity definitions")
	public int getExtDataEntCount() {
		return this.sgmlParser.getExtDataEntCount();
	}

	/**
	 * @return the entrefCount
	 */
	@ReportableProperty(order = 37, 
			value = "Count of external data entity references")
	public int getEntrefCount() {
		return this.sgmlParser.getEntrefCount();
	}

	/**
	 * @return the intDataEntCount
	 */
	@ReportableProperty(order = 38, 
			value = "Count of internal data entity definitions")
	public int getIntDataEntCount() {
		return this.sgmlParser.getIntDataEntCount();
	}

	/**
	 * @return the subDocEntityDefCount
	 */
	@ReportableProperty(order = 38, 
			value = "Count of sub-document entity definitions")
	public int getSubDocEntityDefCount() {
		return this.sgmlParser.getSubDocEntityDefCount();
	}

	/**
	 * @return the subDocCommandCount
	 */
	@ReportableProperty(order = 38, 
			value = "Count of SGML subdocument entities")
	public int getSubDocCommandCount() {
		return this.sgmlParser.getSubDocCommandCount();
	}

	/**
	 * @return the omitCommandCount
	 */
	@ReportableProperty(order = 39, 
			value = "Count of omitted start-tag, end-tag, or attribue markup instances")
	public int getOmitCommandCount() {
		return this.sgmlParser.getOmitCommandCount();
	}

	/**
	 * @return the elementAttributeCount
	 */
	@ReportableProperty(order = 40, 
			value = "Count of attributes associated with elements")
	public int getElementAttributeCount() {
		return this.sgmlParser.getElementAttributeCount();
	}

	/**
	 * @return the dataAttrCount
	 */
	@ReportableProperty(order = 41, 
			value = "Count of data attributes for a external entities")
	public int getDataAttrCount() {
		return this.sgmlParser.getDataAttrCount();
	}

	/**
	 * @return the linkAttrCount
	 */
	@ReportableProperty(order = 41, 
			value = "Count of link attributes associated with elements")
	public int getLinkAttrCount() {
		return this.sgmlParser.getLinkAttrCount();
	}

	/**
	 * @return the elementCount
	 */
	@ReportableProperty(order = 42, 
			value = "Count of elements in SGML document")
	public int getElementCount() {
		return this.sgmlParser.getElementCount();
	}

	/**
	 * @return the dataCount
	 */
	@ReportableProperty(order = 43, 
			value = "Count of element data content")
	public int getDataCount() {
		return this.sgmlParser.getDataCount();
	}

	/**
	 * @return the includedSubElementsCount
	 */
	@ReportableProperty(order = 44, 
			value = "Count of included subelements")
	public int getIncludedSubElementsCount() {
		return this.sgmlParser.getIncludedSubElementsCount();
	}

	/**
	 * @return the emptyElementsCount
	 */
	@ReportableProperty(order = 45, 
			value = "Count of empty elements")
	public int getEmptyElementsCount() {
		return this.sgmlParser.getEmptyElementsCount();
	}

	/**
	 * @return the commentsCount
	 */
	@ReportableProperty(order = 46, 
			value = "Count of comments")
	public int getCommentsCount() {
		return this.sgmlParser.getCommentsCount();
	}

	/**
	 * @return the sDataCount
	 */
	@ReportableProperty(order = 47, 
			value = "Count of internal SDATA entities")
	public int getSDataCount() {
		return this.sgmlParser.getSDataCount();
	}

	/**
	 * @return the processing instruction count
	 */
	@ReportableProperty(order = 48, 
			value = "Count of processing instructions")
	public int getProcessingInstructionCount() {
		return this.sgmlParser.getProcessingInstructionsCount();
	}

	/**
	 * @return the appInfo
	 */
	@ReportableProperty(order = 48, 
			value = "Count of APPINFO declared in DTD, and appearing in the text")
	public int getAppInfoCount() {
		return this.sgmlParser.getAppInfoCount();
	}

	/**
	 * @return the pubIds
	 */
	@ReportableProperty(order = 49, 
			value = "Public identifiers associated with notations or external, text, or subdoc entities")
	public SortedSet<String> getPubIds() {
		return this.sgmlParser.getPubIds();
	}

	/**
	 * @return the extEntFileNames
	 */
	@ReportableProperty(order = 50, 
			value = "System (file) identifiers associated with notations or external, text, or subdoc entities, generated by the entity manager from the specified external identifier and other information about the entity or notation")
	public SortedSet<String> getExtEntFileNames() {
		return this.sgmlParser.getExtEntFileNames();
	}

	/**
	 * @return the extEntSysidNames
	 */
	@ReportableProperty(order = 51, 
			value = "System identifiers associated with notations or external, text, or subdoc entities")
	public SortedSet<String> getExtEntSysidNames() {
		return this.sgmlParser.getExtEntSysidNames();
	}

	/**
	 * @return the extTextEntNames
	 */
	@ReportableProperty(order = 52, 
			value = "External SGML text entity names")
	public SortedSet<String> getExtTextEntNames() {
		return this.sgmlParser.getExtTextEntNames();
	}

	/**
	 * @return the notatNames
	 */
	@ReportableProperty(order = 53, 
			value = "Notation names")
	public SortedSet<String> getNotatNames() {
		return this.sgmlParser.getNotatNames();
	}

	/**
	 * @return the extDataEntNames
	 */
	@ReportableProperty(order = 54, 
			value = "External SGML data entity names")
	public SortedSet<String> getExtDataEntNames() {
		return this.sgmlParser.getExtDataEntNames();
	}

	/**
	 * @return the entRefNames
	 */
	@ReportableProperty(order = 55, 
			value = "Names of external data entities referenced in SGML document")
	public SortedSet<String> getEntRefNames() {
		return this.sgmlParser.getEntRefNames();
	}

	/**
	 * @return the subDocEntDefNames
	 */
	@ReportableProperty(order = 56, 
			value = "Names of defined sub-document entities")
	public SortedSet<String> getSubDocEntDefNames() {
		return this.sgmlParser.getSubDocEntDefNames();
	}

	/**
	 * @return the subDocCommandNames
	 */
	@ReportableProperty(order = 56, 
			value = "Names of defined sub-document entities appearing in SGML document")
	public SortedSet<String> getSubDocCommandNames() {
		return this.sgmlParser.getSubDocCommandNames();
	}

	/**
	 * @return the elementNames
	 */
	@ReportableProperty(order = 57, 
			value = "Unique names of elements appearing in SGML document")
	public SortedSet<String> getElementNames() {
		return this.sgmlParser.getElementNames();
	}

	/**
	 * @return the sdataNames
	 */
	@ReportableProperty(order = 58, 
			value = "Unique SDATA entity names for SDATA entities appearing in SGML document")
	public SortedSet<String> getSdataNames() {
		return this.sgmlParser.getSdataNames();
	}

	/**
	 * @return the intEnt2Value
	 */
	@ReportableProperty(order = 59, 
			value = "Map from internal data entity name to entity value")
	public HashMap<String, String> getInternalDataEntitytName2Value() {
		return this.sgmlParser.getInternalDataEntitytName2Value();
	}

	/**
	 * @return the intEnt2Type
	 */
	@ReportableProperty(order = 60, 
			value = "Map from internal data entity name to entity type")
	public HashMap<String, String> getIntEnt2Type() {
		return this.sgmlParser.getIntEnt2Type();
	}

	/**
	 * @return the intEntType2Countt
	 */
	@ReportableProperty(order = 61, 
			value = "Map from internal data entity types to count of that type in document")
	public HashMap<String, Integer> getInternalEntType2Count(){
		return this.sgmlParser.getInternalEntType2Count();
	}
	
	/**
	 * @return the elemAttributeType2Count
	 */
	@ReportableProperty(order = 62, 
			value = "Map from element attribute types to count of that type in document")
	public HashMap<String, Integer> getElemAttributeType2Count() {
		return this.sgmlParser.getDataAttributeType2Count();
	}

	/**
	 * @return the dataAttributeType2Count
	 */
	@ReportableProperty(order = 63, 
			value = "Map from external entity data attribute types to count of that type in document")
	public HashMap<String, Integer> getDataAttributeType2Count() {
		return this.sgmlParser.getDataAttributeType2Count();
	}

	/**
	 * @return the linkAttributeType2Count
	 */
	@ReportableProperty(order = 64, 
			value = "Map from link attribute types to count of that type in document")
	public HashMap<String, Integer> getLinkAttributeType2Count() {
		return this.sgmlParser.getLinkAttributeType2Count();
	}

	/**
	 * @return the extEntName2dataAttrNames
	 */
	@ReportableProperty(order = 65, 
			value = "Map from external entity name to data attribute names associated with entity in SGML document")
	public HashMap<String, List<String>> getExtEntName2dataAttrNames() {
		return this.sgmlParser.getExtEntName2dataAttrNames();
	}

	/**
	 * @return the progInstructions
	 */
	@ReportableProperty(order = 65, 
			value = "Processing instructions in SGML document")
	public List<String> getProcessingInstructions() {
		return this.sgmlParser.getProcessingInstructions();
	}

	/**
	 * @return the appInfos
	 */
	@ReportableProperty(order = 66, 
			value = "APPINFOs in SGML document")
	public List<String> getAppInfos() {
		return this.sgmlParser.getAppInfos();
	}

	/**
	 * @return list of errors from parse of SGML file
	 */
	@ReportableProperty(order = 67, 
			value = "List of parse errors encountered when parsing file")
	public List<String> getParseErrors(){
		return this.sgmlParser.getParseErrors();
	}
	
	/**
	 * Return public identifier for SGML document
	 * @return public identifier for SGML document if found, else null
	 */
	@ReportableProperty(order = 68, 
			value = "Public identifier for SGML document")
	public String getPublicIdentifier() {
		return this.sgmlParser.getPublicIdentifier();
	}

	/**
	 * Return boolean indicator whether public identifier for SGML document was found
	 * @return true if public identifier found, else false
	 */
	@ReportableProperty(order = 69, 
			value = "Indicates if public identifier for SGML document was found")
	public boolean getPublicIdentifierFound() {
		return this.sgmlParser.getPublicIdentifierFound();
	}

	/**
	 * Return system identifier for SGML document
	 * @return system identifier for SGML document if found, else null
	 */
	@ReportableProperty(order = 70, 
			value = "System identifier for SGML document")
	public String getSystemIdentifier() {
		return this.sgmlParser.getSystemIdentifier();
	}

	/**
	 * Return boolean indicator whether system identifier for SGML document was found
	 * @return true if system identifier found, else false
	 */
	@ReportableProperty(order = 71, 
			value = "Indicates if system identifier for SGML document was found")
	public boolean getSystemIdentifierFound() {
		return this.sgmlParser.getSystemIdentifierFound();
	}
}
