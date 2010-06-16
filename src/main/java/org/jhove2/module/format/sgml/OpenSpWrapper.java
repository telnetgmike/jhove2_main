/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 *
 * Copyright (c) 2009 by The Regents of the University of California,
 * Ithaka Harbors, Inc., and The Board of Trustees of the Leland Stanford
 * Junior University.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * o Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * o Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * o Neither the name of the University of California/California Digital
 *   Library, Ithaka Harbors/Portico, or Stanford University, nor the names of
 *   its contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
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
 */
package org.jhove2.module.format.sgml;


import java.io.File;
import java.io.IOException;


import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.util.externalprocess.ExternalProcessHandler;
import org.jhove2.util.externalprocess.FilepathFilter;

/**
 * Wrapper around OpenSP SGML parser and onsgmlNorm utility. 
 * 
 * After the SGML file is parsed by OpenSp the output (in ESIS format) 
 * is parsed using an ANTLR-generated Java parser class.  The grammar 
 * has been decorated with Java members and methods to accumulate information
 * about the features of interest in the SGML file.  The grammar accepts 
 * the OpenSP indication as to whether or not the SGML instance conforms
 * to its DTD, and hence is to be considered valid.
 * 
 * No DOCTYPE information is returned in the ESIS file.  If the SGML module
 * is configured to ask for doctype, then we run the onsgml "normalization"
 * utility, and extract the doctype from the first line of its output.
 * 
 * @author smorrissey
 *
 */
public class OpenSpWrapper implements SgmlParser {

	/** OpenSp "use catalog" option; followed by path to catalog file for resolving public and system identifiers*/
	public static final String CATALOGOPT = "-c";
	/** OpenSp option to indicate path to errror messages file (would otherwise default to standard err) */
	public static final String ERRFILEOPT = "-f";
	/** options string for onsgml command */
	public static final String ESISCOMMANDPARMS = 
		"-E0 -gnxl -wall -oentity -ocomment -oid -oincluded -ononsgml -onotation-sysid -oomitted -odata-attribute ";
	/** options string for sgmlnorm command */
	public static final String NORMCOMMANDPARMS = 
		"-E0 -gnexmd --error-numbers -wall";
	/** suffix to be used on files generated by onsmgl command */
	public static final String ESIS_SUFFIX = ".esis";
	/** suffix to be used on files generated by sgmlnorm command */
	public static final String NORM_SUFFIX = ".norm";
	/** parser directive: path to catalog file; if empty or null, no catalog is assumed */

	protected String catalogPath;
	/** Handler that will invoke external process to run ongsml utilities */
	protected ExternalProcessHandler processHandler;
	/** filters to be applied to filepaths to enable processing by ExternalProcessHandler on different operating systems*/
	protected FilepathFilter filepathFilter = null;
	/** full path to onsgmls command */
	protected String onsgmlsPath;
	/** full path to sgmlnorm utility */
	protected String sgmlnormPath;


	/**
	 * Invokes onsmgls processor to parse and validate the SGML
	 * source.  Then invokes ANTLR-generated parser to accumulate
	 * feature information about the instance
	 * @param sgm SgmlModule instance invoking this method; module will have 
	 *            Source member
	 * @throws JHOVE2Exception
	 */
	@Override
	public SgmlDocumentProperties parseFile(SgmlModule sgm)
	throws JHOVE2Exception {	
		String [] onsgmlOutputs = this.parseSgmlFile(sgm, ESIS_SUFFIX, this.onsgmlsPath, ESISCOMMANDPARMS);
		String esisFilePath = onsgmlOutputs[0];
		String esisErrFilePath = onsgmlOutputs[1];
		EsisParser esisFileParser = new EsisParser();
		/** ANTLR-generated ESIS file (output from ongmls) parser*/
		ESISCommandsParser esisParser = null;
		if (sgm.getDocumentProperties()==null){
			sgm.setDocumentProperties(new SgmlDocumentProperties());
		}
		esisParser = 
			esisFileParser.parseEsisFile(esisFilePath);
		SgmlDocumentProperties props = sgm.getDocumentProperties();
		if (props==null){
			props = new SgmlDocumentProperties();
		}
		esisFileParser.extractDocProperties(esisParser, props);
		return props;
	}

	/**
	 * Invokes OpenSp sgmlnorm utility to parse the SGML file. If the file can be successfully parsed,
	 * normalized output will contain a DOCTYPE statement
	 * @param sgm SgmlModule instance invoking this method; module will have 
	 *            Source member
	 * @throws JHOVE2Exception
	 */
	@Override
	public void determineDoctype(SgmlModule sgm)
	throws JHOVE2Exception {
		String [] normOutputs = this.parseSgmlFile(sgm, NORM_SUFFIX, this.sgmlnormPath, NORMCOMMANDPARMS);
		String normOutPath = normOutputs[0];
		String normErrPath =normOutputs[1];
		DoctypeParser doctypeParser = new DoctypeParser();
		DoctypeFinderParser doctypeFinderParser = null;
		doctypeFinderParser = 
			doctypeParser.parseNormFile(normOutPath);
		SgmlDocumentProperties props = sgm.getDocumentProperties();
		if (props==null){
			props = new SgmlDocumentProperties();
		}
		doctypeParser.extractDocProperties(doctypeFinderParser, props);
		return;		
	}

	@Override
	public void cleanUp() throws JHOVE2Exception {
		this.setCatalogPath(null);
		this.setProcessHandler(null);
		this.setFilepathFilter(null);
		this.setOnsgmlsPath(null);
		this.setSgmlnormPath(null);
	}
	/**
	 * Apply an OpenSp utility (onsmgls, sgmlnorm) to the SGML file
	 * @param sgm SgmlModule object with source information
	 * @param tempFileSuffix base suffix for onsgmls output and error temp files
	 * @param openSpCommand  String containing full path to OpenSp command
	 * @param commandParms string containing parameters for OpenSp command
	 * @return String[] containing path to .out and .err files
	 * @throws JHOVE2Exception
	 */
	protected String[] parseSgmlFile(SgmlModule sgm, String tempFileSuffix, 
			String openSpCommand, String commandParms)
	throws JHOVE2Exception {
		String parseOutputFilePath = null;
		String parseErrFilePath = null;
		File tempOutFile = null;
		File tempErrFile = null;
		String sgmFilePath = null;
		File sgmFile = sgm.source.getFile();
		try {
			sgmFilePath = sgmFile.getCanonicalPath();
			if (this.filepathFilter != null){
				sgmFilePath = this.filepathFilter.filter( sgmFilePath);
			}
		} catch (IOException e) {
			throw new JHOVE2Exception(
					"IO Exception thrown attempting to determine canonical path for SGML source",
					e);
		}

		// create path names for the 2 output (output and err messages)
		// files generated by OpenSp
		try {
			tempOutFile = File.createTempFile(
					sgm.jhove2.getInvocation().getTempPrefix(),
					sgm.jhove2.getInvocation().getTempSuffix().concat(tempFileSuffix), 
					new File(sgm.jhove2.getInvocation().getTempDirectory()));
		} catch (IOException e) {
			throw new JHOVE2Exception(
					"IOException attemtping to create temporary OpenSp output file",
					e);
		}
		if (sgm.jhove2.getInvocation().getDeleteTempFiles()){
			tempOutFile.deleteOnExit();
		}
		parseOutputFilePath = tempOutFile.getPath();
		if (this.filepathFilter != null){
			parseOutputFilePath = this.filepathFilter.filter(parseOutputFilePath);
		}
		try {
			tempErrFile = File.createTempFile(
					sgm.jhove2.getInvocation().getTempPrefix(),
					sgm.jhove2.getInvocation().getTempSuffix().concat(tempFileSuffix).concat(".err"), 
					new File(sgm.jhove2.getInvocation().getTempDirectory()));
		} catch (IOException e) {
			throw new JHOVE2Exception(
					"IOException attemtping to create temporary OpenSp error file",
					e);
		}
		if (sgm.jhove2.getInvocation().getDeleteTempFiles()){
			tempErrFile.deleteOnExit();
		}
		parseErrFilePath = tempErrFile.getPath();
		if (this.filepathFilter != null){
			parseErrFilePath = this.filepathFilter.filter(parseErrFilePath);
		}
		StringBuffer sbCommand = new StringBuffer(openSpCommand);
		sbCommand.append(" ");
		sbCommand.append(commandParms);
		sbCommand.append(" ");
		if (this.getCatalogPath()!= null){
			String filteredCatalogPath =this.getCatalogPath();
			if (this.filepathFilter != null){
				filteredCatalogPath = this.filepathFilter.filter(filteredCatalogPath);
			}
			sbCommand.append(CATALOGOPT);
			sbCommand.append(filteredCatalogPath);
			sbCommand.append(" ");
		}
		sbCommand.append(ERRFILEOPT);
		sbCommand.append(parseErrFilePath);
		sbCommand.append(" ");
		sbCommand.append(sgmFilePath);
		sbCommand.append(" > ");
		sbCommand.append(parseOutputFilePath);
		String command = sbCommand.toString();
		this.getProcessHandler().executeCommand(command);
		return new String[]{parseOutputFilePath, parseErrFilePath};
	}

	/**
	 * @return the catalogPath
	 */
	@ReportableProperty(order = 20, value = "Parser setting:  Full path to catalog file")
	public String getCatalogPath() {
		return catalogPath;
	}
	/**
	 * @param catalogPath the catalogPath to set
	 */
	public void setCatalogPath(String catalogPath) {
		this.catalogPath = catalogPath;
	}

	/**
	 * @return the processHandler
	 */
	public ExternalProcessHandler getProcessHandler() {
		return processHandler;
	}
	/**
	 * @param processHandler the processHandler to set
	 */
	public void setProcessHandler(ExternalProcessHandler processHandler) {
		this.processHandler = processHandler;
	}
	/**
	 * @return the onsgmlsPath
	 */
	public String getOnsgmlsPath() {
		return onsgmlsPath;
	}
	/**
	 * @param onsgmlsPath the onsgmlsPath to set
	 */
	public void setOnsgmlsPath(String onsgmlsPath) {
		this.onsgmlsPath = onsgmlsPath;
	}
	/**
	 * @return the filepathFilter
	 */
	public FilepathFilter getFilepathFilter() {
		return filepathFilter;
	}
	/**
	 * @param filepathFilter the filepathFilter to set
	 */
	public void setFilepathFilter(FilepathFilter filepathFilter) {
		this.filepathFilter = filepathFilter;
	}

	/**
	 * @return the sgmlnormPath
	 */
	public String getSgmlnormPath() {
		return sgmlnormPath;
	}
	/**
	 * @param sgmlnormPath the sgmlnormPath to set
	 */
	public void setSgmlnormPath(String sgmlnormPath) {
		this.sgmlnormPath = sgmlnormPath;
	}

}