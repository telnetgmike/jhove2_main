/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 * <p>
 * Copyright (c) 2009 by The Regents of the University of California, Ithaka
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
package org.jhove2.module.format.utf8;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.jhove2.ConfigTestBase;
import org.jhove2.app.util.FeatureConfigurationUtil;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.io.Input;
import org.jhove2.core.source.FileSource;
import org.jhove2.module.display.TextDisplayer;
import org.jhove2.persist.PersistenceManagerUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests of utf-8 Module
 * 
 * @see org.jhove2.module.utf8.utf8Module
 * @author mstrong
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {		
		"classpath*:**/persist-test-config.xml",
        "classpath*:**/test-config.xml", 
        "classpath*:**/utf8-test-config.xml",
        "classpath*:**/filepaths-config.xml" })
public class UTF8ModuleTestBase extends ConfigTestBase {

    protected UTF8Module testUtf8Module;
    private JHOVE2 JHOVE2;
    private String utf8DirBasePath;
    protected FileSource fileSource;
    protected Input input;
    protected boolean initialized;
    protected String persistenceFactoryClassName;
    protected TextDisplayer textDisplayer;
    
    public TextDisplayer getTextDisplayer(){
    	return textDisplayer;
    }
    
    @Resource(name = "Text")
    public void setTextDisplayer(TextDisplayer td){
    	this.textDisplayer = td;
    }

    public UTF8Module getTestUtf8Module() {
        return testUtf8Module;
    }

    @Resource(name = "TestUtf8Module")
    public void setTestUtf8Module(UTF8Module Utf8Module) {
        this.testUtf8Module = Utf8Module;
    }


    public JHOVE2 getJHOVE2() {
        return JHOVE2;
    }

    @Resource (name="JHOVE2")
    public void setJHOVE2(JHOVE2 jHOVE2) {
        JHOVE2 = jHOVE2;
    }

    public String getUtf8DirBasePath() {
        return utf8DirBasePath;
    }

    @Resource
    public void setUtf8DirBasePath(String utf8DirBasePath) {
        this.utf8DirBasePath = utf8DirBasePath;
    }

    @Resource (name="PersistenceManagerFactoryClassName")
	public void setPersistenceFactoryClassName(String persistenceFactoryClassName) {
		this.persistenceFactoryClassName = persistenceFactoryClassName;
	}
    
    @BeforeClass 
	public static void setUpBeforeClass() throws Exception {
    	ArrayList<String> paths = new ArrayList<String>();   	
    	paths.add("classpath*:**/persist-test-config.xml");
    	paths.add("classpath*:**/test-config.xml");
    	paths.add("classpath*:**/utf8-test-config.xml");
    	ConfigTestBase.setCONTEXT_PATHS(paths);
    	ConfigTestBase.setUpBeforeClass();
    }
    @Before
    public void setUp()
        throws Exception
    {
    	PersistenceManagerUtil.createPersistenceManagerFactory(persistenceFactoryClassName);
		PersistenceManagerUtil.getPersistenceManagerFactory().getInstance().initialize();
    }
    
    protected void parse(String relativePath) {
        String Utf8ExampleDirPath = null;
        try {
            Utf8ExampleDirPath = FeatureConfigurationUtil
                    .getFilePathFromClasspath(utf8DirBasePath,
                            "Utf8 examples base directory");
        }
        catch (JHOVE2Exception e1) {
            fail("Could not create base directory");
        }
        File Utf8ExampleDir = new File(Utf8ExampleDirPath);
        assertTrue(Utf8ExampleDir.exists());
        File testFile = new File(Utf8ExampleDirPath, relativePath);
        assertTrue(testFile.exists());
        try {
            fileSource = (FileSource) JHOVE2.getSourceFactory().getSource(JHOVE2, testFile);
            input      = fileSource.getInput(JHOVE2);
            testUtf8Module.parse(JHOVE2, fileSource, input);
        }
        catch (Exception e) {
            // fail("Exception thrown: " + e.getMessage());
        }
    }

    public void display() {
        try {
            input.setPosition(0);
            JHOVE2.characterize(fileSource, input);
            textDisplayer.display(fileSource);          
        }
        catch (Exception e){
            e.printStackTrace();
            fail("exception");
        }
    }
    /**
     * Test method for UTF8 Declaration information
     */
    @Test
    public void testTruth() {
        assertTrue(true);
    }

 }
