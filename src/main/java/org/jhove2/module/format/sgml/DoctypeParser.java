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
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.Message;
import org.jhove2.core.Message.Context;
import org.jhove2.core.Message.Severity;
import org.jhove2.core.source.Source;
import org.jhove2.util.CopyUtils;

/**
 * Class to process "normalized" output from ongmls to extract DOCTYPE declaration information.
 * Uses lexer and parser classes generated by ANTLR.
 * the ESIS file.
 * @author smorrissey
 */
public class DoctypeParser {
	
	/** prefix for parser error messages to indicate which grammar generated parse errors */
	  public static final String DOCTYPEERR = "DoctypeFinder: ";
	  
	/**
	 * Parses the output file generated by the OpenSP sgmlnorm utility
	 * @param normFilepath String containing path to OpenSP sgmlnorm utility output file
	 * @param sgm  SgmlModule to which messages can be attached
	 * @param JHOVE2 jhove2 object with ConfigInfo
	 * @param Source object to which messages may be attached
     * @return DoctypeFinderParser object with information about DOCTYPE declaration (if one exists) in the file
	 * @throws JHOVE2Exception
	 * @throws IOException 
	 * @throws RecognitionException 
	 */
	public DoctypeFinderParser parseNormFile(String normFilepath, JHOVE2 jhove2, Source source, SgmlModule sgm)
	throws JHOVE2Exception, IOException, RecognitionException {
		DoctypeFinderLexer lex = null;
		DoctypeFinderParser parser = null;
		try {
			lex = new DoctypeFinderLexer(new ANTLRFileStream(normFilepath, "UTF8"));
		} catch (IOException e) {
			String eMessage = e.getLocalizedMessage();
			if (eMessage==null){
				eMessage = "";
			}
			Object[]messageArgs = new Object[]{normFilepath, eMessage};
			Message message = new Message(
					Severity.ERROR,
					Context.PROCESS,
					"org.jhove2.module.format.sgml.DoctypeParser.IOExceptionForDoctypeParserLexer",
					messageArgs,
					jhove2.getConfigInfo());
			sgm.getSgmlParserErrorMessages().add(message);
			throw e;
		}
		 CommonTokenStream tokens = new CommonTokenStream(lex);
	        parser = new DoctypeFinderParser(tokens);
	        try {
				parser.normdoc();
			} catch (RecognitionException e) {
				String eMessage = e.getLocalizedMessage();
				if (eMessage==null){
					eMessage = "";
				}
				Object[]messageArgs = new Object[]{normFilepath, eMessage};
				Message message = new Message(
						Severity.ERROR,
						Context.PROCESS,
						"org.jhove2.module.format.sgml.DoctypeParser.RecognitionExceptionForDoctypeParser",
						messageArgs,
						jhove2.getConfigInfo());
				sgm.getSgmlParserErrorMessages().add(message);
				throw e;
			}
		return parser;
	}
	/**
	 * Method to extract fields from ANTLR parser and make deep copy into SgmlDocumentProperties object.
	 * Clears those objects in the ANTLR parser. 
	 * @param doctypeFinderParser contains data extracted by ANTLR parser
	 * @param props updated SgmlDocumentProperties object with content of ANTLR parser fields
	 */
	public void extractDocProperties(DoctypeFinderParser doctypeFinderParser, SgmlDocumentProperties props){
		if (doctypeFinderParser.getDoctypeFinderParseErrors()!= null){
			if (props.getParseErrors() == null){
				props.setParseErrors(new ArrayList<String>());
			}
			props.getParseErrors().addAll(
					CopyUtils.copyAndClearList(doctypeFinderParser.getDoctypeFinderParseErrors()));
		}
		props.setFoundDoctype(doctypeFinderParser.foundDoctype);
		props.setPublicIdentifier(doctypeFinderParser.pubid);
		props.setFoundPubid(doctypeFinderParser.foundPubid);
		props.setSystemIdentifier(doctypeFinderParser.systemId);
		props.setFoundSysid(doctypeFinderParser.foundSysid);
		return;
	}
}
