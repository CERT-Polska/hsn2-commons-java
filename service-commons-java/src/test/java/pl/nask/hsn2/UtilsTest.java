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

package pl.nask.hsn2;

import junit.framework.Assert;

import org.testng.annotations.Test;

import pl.nask.hsn2.connector.REST.DSUtils;

public class UtilsTest {

    @Test
    public void testGetAddress() {
        String address = DSUtils.dsAddress("http://localhost:8080/", 1, 1);
        Assert.assertEquals("http://localhost:8080/data/1/1", address);
    }

    @Test
    public void testPostAddress() {
        String address = DSUtils.dsAddress("http://localhost:8080/", 1);
        Assert.assertEquals("http://localhost:8080/data/1", address);
    }
}
