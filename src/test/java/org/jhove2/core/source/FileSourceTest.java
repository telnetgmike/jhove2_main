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
package org.jhove2.core.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.jhove2.app.util.FeatureConfigurationUtil;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.persist.inmemory.InMemorySourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:**/abstractdisplayer-config.xml",
		"classpath*:**/test-config.xml", "classpath*:**/filepaths-config.xml"})
public class FileSourceTest {
    private JHOVE2 jhove2;
	private String utf8DirBasePath;
	private String testFile01;
	/**
	 * Test method for {@link org.jhove2.core.source.AbstractSource#getInputStream()}.
	 * @throws JHOVE2Exception 
	 */
	@Test
	public void testGetInputStream() throws JHOVE2Exception {
		String utf8DirPath = null;
		try {
			utf8DirPath = 
				FeatureConfigurationUtil.getFilePathFromClasspath(utf8DirBasePath, "utf8 dir");
		} catch (JHOVE2Exception e1) {
			fail("Could not create base directory");
		}
		String filePath = utf8DirPath.concat(testFile01);
		try {
			SourceFactory factory = new InMemorySourceFactory();
			Source source = factory.getSource(jhove2, filePath);
			InputStream inputStream = source.getInputStream();
			long fileSize = source.getFile().length();
			long inputStreamCount = 0L;
			while (inputStream.read()!= -1){
				inputStreamCount++;				
			}
			assertEquals(fileSize, inputStreamCount);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	public String getTestFile01() {
		return testFile01;
	}
	@Resource
	public void setTestFile01(String testFile01) {
		this.testFile01 = testFile01;
	}
	public String getUtf8DirBaseBath() {
		return utf8DirBasePath;
	}
	@Resource
	public void setUtf8DirBasePath(String testDir) {
		this.utf8DirBasePath = testDir;
	}

    public JHOVE2 getJHOVE2() {
        return this.jhove2;
    }
    @Resource
    public void setJHOVE2(JHOVE2 jhove2) {
        this.jhove2 = jhove2;
    }
}
