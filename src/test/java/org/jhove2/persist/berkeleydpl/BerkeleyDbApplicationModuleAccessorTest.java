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
package org.jhove2.persist.berkeleydpl;

import static org.junit.Assert.*;

import org.jhove2.app.JHOVE2CommandLine;
import org.jhove2.module.display.Displayer;
import org.jhove2.module.display.TextDisplayer;
import org.junit.Test;


/**
 * @author smorrissey
 *
 */

public class BerkeleyDbApplicationModuleAccessorTest extends BerkeleyDbTestBase{

	JHOVE2CommandLine bdbapp;
	Displayer bdbisplayer;

	/**
	 * Test method for {@link org.jhove2.persist.berkeleydpl.BerkeleyDbBaseModuleAccessor#persistModule(org.jhove2.module.Module)}.
	 */
	@Test
	public void testPersistModule() {
		try {
			bdbapp = new JHOVE2CommandLine(new BerkeleyDbApplicationModuleAccessor());
			bdbapp = (JHOVE2CommandLine) bdbapp.getModuleAccessor().persistModule(bdbapp);
			assertNotNull(bdbapp.getModuleId());
			long id1 = bdbapp.getModuleId().longValue();			
			bdbapp = (JHOVE2CommandLine) bdbapp.getModuleAccessor().persistModule(bdbapp);
			assertEquals(id1, bdbapp.getModuleId().longValue());
			bdbisplayer = new TextDisplayer(new BerkeleyDbDisplayerAccessor());
			assertNull(bdbisplayer.getModuleId());
			assertNull(bdbisplayer.getParentAppId());
			BerkeleyDbApplicationModuleAccessor accessor = (BerkeleyDbApplicationModuleAccessor) bdbapp.getModuleAccessor();
			bdbisplayer = accessor.setDisplayer(bdbapp, bdbisplayer);
			assertEquals(id1, bdbapp.getModuleId().longValue());
			assertNotNull(bdbisplayer.getModuleId());
			long id2 = bdbisplayer.getModuleId().longValue();
			assertEquals(bdbapp.getModuleId(), bdbisplayer.getParentAppId());
			bdbapp = (JHOVE2CommandLine) bdbapp.getModuleAccessor().persistModule(bdbapp);
			assertEquals(id1, bdbapp.getModuleId().longValue());
			bdbisplayer = (Displayer) bdbisplayer.getModuleAccessor().persistModule(bdbisplayer);
			assertEquals(id2, bdbisplayer.getModuleId().longValue());
			assertEquals(bdbapp.getModuleId(), bdbisplayer.getParentAppId());
			Displayer nullDisplayer = accessor.setDisplayer(bdbapp, null);
			assertNull(nullDisplayer);
			assertEquals(id2, bdbisplayer.getModuleId().longValue());
			assertEquals(bdbapp.getModuleId(), bdbisplayer.getParentAppId());
			BerkeleyDbDisplayerAccessor ma = (BerkeleyDbDisplayerAccessor)bdbisplayer.getModuleAccessor();
			bdbisplayer = (Displayer) ma.retrieveModule(bdbisplayer.getModuleId());
			assertEquals(id2, bdbisplayer.getModuleId().longValue());
			assertNull(bdbisplayer.getParentAppId());
		}
		catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}



}
