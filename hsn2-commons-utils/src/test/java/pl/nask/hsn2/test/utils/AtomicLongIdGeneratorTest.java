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

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.utils.AtomicLongIdGenerator;
import pl.nask.hsn2.utils.IdGenerator;
import pl.nask.hsn2.utils.IdGeneratorException;

public class AtomicLongIdGeneratorTest {

	@Test
	public void simpleTest() throws IdGeneratorException {
		IdGenerator gen = new AtomicLongIdGenerator();
		Assert.assertEquals(gen.nextId(), 1L);
		Assert.assertEquals(gen.nextId(), 2L);
		Assert.assertEquals(gen.nextId(), 3L);
		Assert.assertEquals(gen.nextId(), 4L);
	}

	@Test
	public void simpleTestWithReset() throws IdGeneratorException {
		IdGenerator gen = new AtomicLongIdGenerator();
		
		Assert.assertEquals(gen.nextId(), 1L);
		Assert.assertEquals(gen.nextId(), 2L);
		gen.reset();
		Assert.assertEquals(gen.nextId(), 1L);
		Assert.assertEquals(gen.nextId(), 2L);
		Assert.assertEquals(gen.nextId(), 3L);
		Assert.assertEquals(gen.nextId(), 4L);
	}

}
