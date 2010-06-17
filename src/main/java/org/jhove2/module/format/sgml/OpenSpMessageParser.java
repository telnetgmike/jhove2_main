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

import java.io.IOException;
import java.util.ArrayList;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.util.CopyUtils;

/**
 * Class to process err (error, warning, info and other message) output from ongmls.
 * Uses lexer and parser classes generated by ANTLR.
 * Also contains static utility methods used by generated
 * parser code to accumulate information about properties in 
 * the err file.
 * @author smorrissey
 */
public class OpenSpMessageParser {
	
	/** prefix for parser error messages to indicate which grammar generated parse errors */
	public static final String OSPMESSAGEERR = "SPMESSAGE: ";
	// fragments for formatting OpenSP message
	public static final String  MESSAGE_LEVEL = "MESSAGE LEVEL: ";
	public static final String NA = "NA";
	public static final String  MESSAGE_CODE = ":   MESSAGE CODE: ";
	public static final String LINE = ":   LINE: ";
	public static final String POSITION =  ":   POSITION: ";
	public static final String MESSAGE_TEXT = ":  MESSAGE TEXT:\n\t";
	
	/**
	 * Invokes ANTLR-generated grammar class to parse OpenSP-generated .err file
	 * @param messageFilePath path to OpenSP-generated .err file
	 * @return SgmlParseMessagesParser with extracted message info
	 * @throws JHOVE2Exception
	 */
	public SgmlParseMessagesParser parseMessageFile(String messageFilePath)
	throws JHOVE2Exception {
		SgmlParseMessagesLexer lex = null;
		SgmlParseMessagesParser parser = null;
		try {
			lex = new SgmlParseMessagesLexer(new ANTLRFileStream(messageFilePath, "UTF8"));
		} catch (IOException e) {
			throw new JHOVE2Exception(
					"IO Exception thrown creating SgmlParseMessages lexer for file path " 
					+ messageFilePath,
					e);
		}
		CommonTokenStream tokens = new CommonTokenStream(lex);
		parser = new SgmlParseMessagesParser(tokens);
		try {
			parser.errMessages();
		} catch (RecognitionException e) {
			throw new JHOVE2Exception(
					"RecognitionException Exception thrown parsing file at file path " 
					+ messageFilePath,
					e);
		}
		return parser;
	}
	/**
	 * Method to extract fields from ANTLR parser and make deep copy into SgmlDocumentProperties object.
	 * Clears those objects in the ANTLR parser.
	 * @param errParser SgmlParseMessagesParser with .err properties to be extracted
	 * @param props SgmlDocumentProperties object maintaing information about SGML document
	 */
	public void extractDocProperties(SgmlParseMessagesParser errParser,  SgmlDocumentProperties props){
		if (errParser.getSgmlMessagesParseErrors() != null){
			if (props.getParseErrors() == null){
				props.setParseErrors(new ArrayList<String>());
			}
			props.getParseErrors().addAll(
					CopyUtils.copyAndClearList(errParser.getSgmlMessagesParseErrors()));
		}
		props.setErrorLevelMessageCount(errParser.eLevelMessageCount);
		props.setWarningLevelMessageCount(errParser.wLevelMessageCount);
		props.setInfoLevelMessageCount(errParser.iLevelMessageCount);
		props.setQnameLevelMessageCount(errParser.qLevelMessageCount);
		props.setXrefLevelMessageCount(errParser.xLevelMessageCount);
		props.setTotMessageCount(errParser.totMessageCount);
		props.setSgmlParserMessages(CopyUtils.copyAndClearList(errParser.openSpMessages));
	}
	
	/**
	 * 
     * @param fileName
	 * @param lineNumber
	 * @param posNumber
	 * @param messageText
	 * @return
	 */
	public static String createMessageString(String fileName, String lineNumber, String posNumber, 
			String messageText) {
		return createCodedMessageString(fileName, lineNumber, posNumber, messageText, null, null);
	}
	/**
	 * 
	 * @param fileName
	 * @param lineNumber
	 * @param posNumber
	 * @param messageText
	 * @param messageLevel
	 * @param messageCode
	 * @return
	 */
	public static String createCodedMessageString(String fileName, String lineNumber, String posNumber, 
			String messageText, String messageLevel, String messageCode) {
		StringBuffer msg = new StringBuffer(MESSAGE_LEVEL);
		if (messageLevel != null){
			msg.append(messageLevel);
		}
		else {
			msg.append(NA);
		}
		msg.append(MESSAGE_CODE);
		if (messageCode != null){
			msg.append(messageCode);
		}
		else {
			msg.append(NA);
		}
		msg.append(LINE);
		msg.append(lineNumber);
		msg.append(POSITION);
		msg.append(posNumber);
		msg.append(MESSAGE_TEXT);
		msg.append(messageText);
		return msg.toString();
	}
}
