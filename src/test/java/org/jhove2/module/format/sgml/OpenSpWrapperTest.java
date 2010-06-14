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

import static org.junit.Assert.*;

import java.io.File;

import javax.annotation.Resource;

import org.jhove2.app.util.FeatureConfigurationUtil;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.source.Source;
import org.jhove2.core.source.SourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Class to test wrapper around OpenSP opensgml tool
 * 
 * THIS CLASS REQUIRES ONSGMLS TO BE INSTALLED TO RUN SUCCESSFULLY!!!
 * The path to ONSGMLS is specified the sgml/test-config.xml file
 * in the shellEnv property of the testExternalProcessHandler bean
 * 
 * @author smorrissey
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:**/test-config.xml", 
"classpath*:**/filepaths-config.xml"})

public class OpenSpWrapperTest {
	protected JHOVE2 JHOVE2;
	protected SgmlModule testSgmlModule;
	protected String catalogFile;
	protected String validSgmlFile;
	protected String sgmlDirBasePath;
	protected String invalidSgmlFile;
	
	protected String sgmlDirPath;
	protected String catalogPath;
	protected Source inputSource;
	protected OpenSpWrapper sp;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testSgmlModule.jhove2 = JHOVE2;
		sp = (OpenSpWrapper) testSgmlModule.sgmlParser;
		try {
			sgmlDirPath = 
				FeatureConfigurationUtil.getFilePathFromClasspath(sgmlDirBasePath, "temp dir");
		} catch (JHOVE2Exception e1) {
			fail("Could not create base directory");
		}
		File fsgml = new File(sgmlDirPath);
		sgmlDirPath = fsgml.getPath();
		if (sp.filepathFilter != null){
			sgmlDirPath = sp.filepathFilter.filter(sgmlDirPath);
		}
		catalogPath = sgmlDirPath.concat(catalogFile);
		sp.setCatalogPath(catalogPath);
		sp.sgmFilePath = null;
		testSgmlModule.source = null;
	}

	/**
	 * Test method for {@link org.jhove2.module.format.sgml.OpenSpWrapper#parseFile(org.jhove2.module.format.sgml.SgmlModule)}.
	 */
	@Test
	public void testParseFile() {
		String goodFilePath = sgmlDirBasePath.concat(validSgmlFile);
		try {
			goodFilePath = 
				FeatureConfigurationUtil.getFilePathFromClasspath(goodFilePath, 
						"valid sgm file");
		} catch (JHOVE2Exception e1) {
			fail("Could not create base directory");
		}
		File fGoodFile = new File(goodFilePath);
		goodFilePath = fGoodFile.getPath();
		try {
			inputSource = SourceFactory.getSource(goodFilePath);
		}catch (Exception e){
			e.printStackTrace();
			fail("Failed to create source for input file");
		}
		testSgmlModule.source = inputSource;
		try {
			sp.parseFile(testSgmlModule);
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
			fail("unable to get esis parser");
		}
		assertTrue(sp.isSgmlValid());
		
		testSgmlModule.source = null;
		sp.sgmFilePath = null;
		String badFilePath = sgmlDirBasePath.concat(invalidSgmlFile);
		try {
			badFilePath = 
				FeatureConfigurationUtil.getFilePathFromClasspath(badFilePath, 
						"invalid sgm file");
		} catch (JHOVE2Exception e1) {
			fail("Could not create base directory");
		}
		File fBadFile = new File(badFilePath);
		badFilePath = fBadFile.getPath();
		try {
			inputSource = SourceFactory.getSource(badFilePath);
		}catch (Exception e){
			e.printStackTrace();
			fail("Failed to create source for input file");
		}
		testSgmlModule.source = inputSource;
		try {
			sp.parseFile(testSgmlModule);
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
			fail("unable to get esis parser");
		}
		assertFalse(sp.isSgmlValid());
	}

	/**
	 * Test method for {@link org.jhove2.module.format.sgml.OpenSpWrapper#parseSgmlFile(org.jhove2.module.format.sgml.SgmlModule)}.
	 */
	@Test
	public void testParseSgmlFile() {
		String goodFilePath = sgmlDirBasePath.concat(validSgmlFile);
		try {
			goodFilePath = 
				FeatureConfigurationUtil.getFilePathFromClasspath(goodFilePath, "valid sgm file");
		} catch (JHOVE2Exception e1) {
			fail("Could not create base directory");
		}
		File fGoodFile = new File(goodFilePath);
		goodFilePath = fGoodFile.getPath();
		try {
			inputSource = SourceFactory.getSource(goodFilePath);
		}catch (Exception e){
			e.printStackTrace();
			fail("Failed to create source for input file");
		}
		testSgmlModule.source = inputSource;
		String[] outputFiles = null;
		try {
			outputFiles = sp.parseSgmlFile(testSgmlModule,OpenSpWrapper.ESIS_SUFFIX,sp.onsgmlsPath,OpenSpWrapper.ESISCOMMANDPARMS);
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
			fail("Failed to parse sgml file");
		}
		assertEquals(2, outputFiles.length);
		File goodEsisFile = new File(outputFiles[0]);
		assertTrue(goodEsisFile.exists());
		long size = goodEsisFile.length();
		assertTrue(size>0);
		File errEsisFile = new File(outputFiles[1]);
		assertTrue(errEsisFile.exists());
		size = errEsisFile.length();
		assertEquals(0,size);
	}

	/**
	 * Test method for {@link org.jhove2.module.format.sgml.OpenSpWrapper#createDoctype(org.jhove2.module.format.sgml.SgmlModule)}.
	 */
	@Test
	public void testCreateDoctype() {
		String goodFilePath = sgmlDirBasePath.concat(validSgmlFile);
		try {
			goodFilePath = 
				FeatureConfigurationUtil.getFilePathFromClasspath(goodFilePath, 
						"valid sgm file");
		} catch (JHOVE2Exception e1) {
			fail("Could not create base directory");
		}
		File fGoodFile = new File(goodFilePath);
		goodFilePath = fGoodFile.getPath();
		try {
			inputSource = SourceFactory.getSource(goodFilePath);
		}catch (Exception e){
			e.printStackTrace();
			fail("Failed to create source for input file");
		}
		testSgmlModule.source = inputSource;
		try {
			sp.determineDoctype(testSgmlModule);
		} catch (JHOVE2Exception e) {
			e.printStackTrace();
			fail("Failed to run createDoctype method");
		}
		assertTrue(sp.getDocTypeFound());
	}

	/**
	 * @return the jHOVE2
	 */
	public JHOVE2 getJHOVE2() {
		return JHOVE2;
	}

	/**
	 * @param jHOVE2 the jHOVE2 to set
	 */
	@Resource
	public void setJHOVE2(JHOVE2 jHOVE2) {
		JHOVE2 = jHOVE2;
	}

	/**
	 * @return the testSgmlModule
	 */
	public SgmlModule getTestSgmlModule() {
		return testSgmlModule;
	}

	/**
	 * @param testSgmlModule the testSgmlModule to set
	 */
	@Resource
	public void setTestSgmlModule(SgmlModule testSgmlModule) {
		this.testSgmlModule = testSgmlModule;
	}

	/**
	 * @return the catalogFile
	 */
	public String getCatalogFile() {
		return catalogFile;
	}

	/**
	 * @param catalogFile the catalogFile to set
	 */
	@Resource
	public void setCatalogFile(String catalogFile) {
		this.catalogFile = catalogFile;
	}

	/**
	 * @return the validSgmlFile
	 */
	public String getValidSgmlFile() {
		return validSgmlFile;
	}

	/**
	 * @param validSgmlFile the validSgmlFile to set
	 */
	@Resource
	public void setValidSgmlFile(String validSgmlFile) {
		this.validSgmlFile = validSgmlFile;
	}

	/**
	 * @return the sgmlDirBasePath
	 */
	public String getSgmlDirBasePath() {
		return sgmlDirBasePath;
	}

	/**
	 * @param sgmlDirBasePath the sgmlDirBasePath to set
	 */
	@Resource
	public void setSgmlDirBasePath(String sgmlDirBasePath) {
		this.sgmlDirBasePath = sgmlDirBasePath;
	}

	/**
	 * @return the invalidSgmlFile
	 */
	public String getInvalidSgmlFile() {
		return invalidSgmlFile;
	}

	/**
	 * @param invalidSgmlFile the invalidSgmlFile to set
	 */
	@Resource
	public void setInvalidSgmlFile(String invalidSgmlFile) {
		this.invalidSgmlFile = invalidSgmlFile;
	}

}
