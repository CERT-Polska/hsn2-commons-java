/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.test.utils;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import pl.nask.hsn2.utils.FileIdGenerator;
import pl.nask.hsn2.utils.IdGeneratorException;

public class FileIdGeneratorTest {

	@AfterTest
	public void cleanUp() {
		new File("./jobId.seq").delete();
	}
	
	@Test
	public void simpleTest() throws IdGeneratorException {
		FileIdGenerator gen = new FileIdGenerator();
		new File("./jobId.seq").delete();
		gen.setSequenceFile(".", "jobId.seq");
		
		Assert.assertEquals(gen.nextId(), 1L);
		Assert.assertEquals(gen.nextId(), 2L);
		Assert.assertEquals(gen.nextId(), 3L);
		Assert.assertEquals(gen.nextId(), 4L);
	}

	@Test
	public void simpleTestWithReset() throws IdGeneratorException {
		FileIdGenerator gen = new FileIdGenerator();
		new File("./jobId.seq").delete();
		gen.setSequenceFile(".", "jobId.seq");
		
		Assert.assertEquals(gen.nextId(), 1L);
		Assert.assertEquals(gen.nextId(), 2L);
		gen.reset();
		Assert.assertEquals(gen.nextId(), 1L);
		Assert.assertEquals(gen.nextId(), 2L);
		Assert.assertEquals(gen.nextId(), 3L);
		Assert.assertEquals(gen.nextId(), 4L);
		
		gen.setSequenceFileDirPath("/dfdsfdsfdsf/");
	}

	@Test(expectedExceptions=IdGeneratorException.class)
	public void simpleTestWithResetAndException() throws IdGeneratorException {
		FileIdGenerator gen = new FileIdGenerator();
		gen.setSequenceFileDirPath("/dfdsfdsfdsf/");
		gen.nextId();
	}
	
	@Test(expectedExceptions=IdGeneratorException.class)
	public void simpleTestWithException() throws IdGeneratorException {
		FileIdGenerator gen = new FileIdGenerator();
		gen.setSequenceFile("/dsfdsf/", "jobId.seq");
		gen.nextId();
	}
}
