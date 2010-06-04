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
import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jhove2.core.JHOVE2Exception;

/**
 * @author smorrissey
 *
 */
public class EsisParser {

	public ESISCommandsParser parseEsisFile(String esisPath)
	throws JHOVE2Exception {
		ESISCommandsLexer lex = null;
		ESISCommandsParser parser = null;
		try {
			lex = new ESISCommandsLexer(new ANTLRFileStream(esisPath, "UTF8"));
		} catch (IOException e) {
			throw new JHOVE2Exception(
					"IO Exception thrown creating ESIS lexer for file path " + esisPath,
					e);
		}
        CommonTokenStream tokens = new CommonTokenStream(lex);
        parser = new ESISCommandsParser(tokens);
        try {
			parser.esis();
		} catch (RecognitionException e) {
			throw new JHOVE2Exception(
					"RecognitionException Exception thrown parsing file at file path " + esisPath,
					e);
		}
        return parser;		
	}


	/**
	 * Utility method to update list in a map; used in ESIS grammar
	 * @param map Map<String, List<String>> of lists to be updated
	 * @param key String key to list to be updated
	 * @param newItem String item to be added to target list
	 */
	public static void updateMapList(HashMap<String, List<String>>map, String key, String newItem){
		List<String> list = map.get(key);
		if (list==null){
			list = new ArrayList<String>();
		}
		list.add(newItem);
		map.put(key,list);
		return;
	}

	/**
	 * Utility method to update counter value in a map; used in ESIS grammar
	 * @param map Map<String, Integer> with counters to be updated
	 * @param key String key to counter to be updated
	 */
	public static void updateMapCounter(HashMap<String, Integer> map, String key){
		Integer val = map.get(key);
		if (val==null){
			val=0;
		}
		int iVal = val.intValue();
		iVal++;
		val = Integer.valueOf(iVal);
		map.put(key, val);
		return;
	}
}
