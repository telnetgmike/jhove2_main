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

import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.source.Source;

/**
 * Abstract base class for parsers of OpenSP .err (Message) files
 * @author smorrissey
 *
 */
public abstract class AbstractOpenSpMessageParser implements
		OpenSpErrMessageParser {

	/**
	 * Constructor
	 */
	public AbstractOpenSpMessageParser() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.jhove2.module.format.sgml.OpenSpErrMessageParser#parseMessageFile(java.lang.String, org.jhove2.core.JHOVE2, org.jhove2.core.source.Source, org.jhove2.module.format.sgml.SgmlModule)
	 */
	@Override
	public abstract void parseMessageFile(String messageFilePath, JHOVE2 jhove2,
			Source source, SgmlModule sgm) throws JHOVE2Exception, IOException;
	/**
	 * Same as createCodedMessageString(fileName, lineNumber, posNumber, messageText, null, null)
	 * @param fileName String with SGML or DTD file name
	 * @param lineNumber line in file to which message pertains
	 * @param posNumber  position in line to which message pertains
	 * @param messageText text of message
	 * @return formatted string containing message information
	 */
	public static String createMessageString(String fileName, String lineNumber, String posNumber, 
			String messageText) {
		return createCodedMessageString(fileName, lineNumber, posNumber, messageText, null, null);
	}

	/**
	 * Takes components of message string generated by OpenSp and creates formatted message string
	 * @param fileName String with SGML or DTD file name
	 * @param lineNumber line in file to which message pertains
	 * @param posNumber  position in line to which message pertains
	 * @param messageText text of message
	 * @param messageLevel message Level/Type (Error, warning, info, xref, Name(quantity)length issue)
	 * @param messageCode message code assigned by OpenSp
	 * @return formatted string containing message information
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
	/**
	 * Test to see if String is a message code
	 * @param messageLevel
	 * @return true if String is a message code, else false
	 */
	public static boolean isMessageCode (String messageLevel){
	    boolean isCode = false;
	    if (messageLevel != null){
	        if ((messageLevel.equals("E")) ||
	            (messageLevel.equals("W")) || 
	            (messageLevel.equals("I")) ||
	            (messageLevel.equals("Q")) ||
	            (messageLevel.equals("X"))
	           ){
	              isCode = true;
	           }    
	    }
	    return isCode;
	 }

}
