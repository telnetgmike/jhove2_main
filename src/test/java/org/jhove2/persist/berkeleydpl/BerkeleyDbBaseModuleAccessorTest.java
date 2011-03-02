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

import javax.annotation.Resource;

import org.jhove2.core.JHOVE2Exception;
import org.jhove2.module.display.JSONDisplayer;
import org.jhove2.module.identify.DROIDIdentifier;
import org.jhove2.persist.PersistenceManager;
import org.jhove2.persist.PersistenceManagerUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:**/test-config.xml", "classpath*:**/filepaths-config.xml"})
public class BerkeleyDbBaseModuleAccessorTest {

	String persistenceMgrClassName;
	JSONDisplayer dbdJSON;
	PersistenceManager persistenceManager = null;
	DROIDIdentifier bdbDROIDIdentifier;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		PersistenceManagerUtil.createPersistenceManagerFactory(persistenceMgrClassName);
		persistenceManager = PersistenceManagerUtil.getPersistenceManagerFactory().getInstance();
		persistenceManager.initialize();
	}

	@After
	public void tearDown() throws Exception {
		if (persistenceManager != null){
			try{
				persistenceManager.close();
			}
			catch (JHOVE2Exception je){
				System.err.println(je.getMessage());
				je.printStackTrace(System.err);
			}
		}
	}
	/**
	 * Test method for {@link org.jhove2.persist.berkeleydpl.BerkeleyDbBaseModuleAccessor#persistModule(org.jhove2.module.Module)}.
	 */
	@Test
	public void testPersistModule() {
		dbdJSON = new JSONDisplayer(new BerkeleyDbBaseModuleAccessor());
		assertNull(dbdJSON.getModuleId());
		try {
			dbdJSON = (JSONDisplayer) dbdJSON.getModuleAccessor().persistModule(dbdJSON);
			assertNotNull(dbdJSON.getModuleId());
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
		}		
		try {
			bdbDROIDIdentifier = new DROIDIdentifier(new BerkeleyDbBaseModuleAccessor());
			assertNull(bdbDROIDIdentifier.getModuleId());
			bdbDROIDIdentifier= (DROIDIdentifier) bdbDROIDIdentifier.getModuleAccessor().persistModule(bdbDROIDIdentifier);
			assertNotNull(bdbDROIDIdentifier.getModuleId());
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.jhove2.persist.berkeleydpl.BerkeleyDbBaseModuleAccessor#retrieveModule(java.lang.Object)}.
	 */
	@Test
	public void testRetrieveModule() {
		dbdJSON = new JSONDisplayer(new BerkeleyDbBaseModuleAccessor());
		assertNull(dbdJSON.getModuleId());
		try {
			dbdJSON	= (JSONDisplayer) dbdJSON.getModuleAccessor().persistModule(dbdJSON);
			assertNotNull(dbdJSON.getModuleId());
			long longVal =  dbdJSON.getModuleId().longValue();
			JSONDisplayer displayer = (JSONDisplayer) dbdJSON.getModuleAccessor().retrieveModule(dbdJSON.getModuleId());
			assertEquals(longVal, displayer.getModuleId().longValue());
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.jhove2.persist.berkeleydpl.BerkeleyDbBaseAccessor#getBerkeleyDbPersistenceManager()}.
	 */
	@Test
	public void testGetBerkeleyDbPersistenceManager() {
		assertTrue(persistenceManager instanceof BerkeleyDbPersistenceManager);
	}

	/**
	 * @return the persistenceMgrClass
	 */
	public String getPersistenceMgrClassName() {
		return persistenceMgrClassName;
	}

	/**
	 * @param persistenceMgrClass the persistenceMgrClass to set
	 */
	@Resource
	public void setPersistenceMgrClassName(String persistenceMgrClass) {
		this.persistenceMgrClassName = persistenceMgrClass;
	}


}
