/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
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

import org.apache.commons.httpclient.URIException;
import org.testng.annotations.Test;

@Test
public class NewObjectTest {

    public void testSimpleURL() throws URIException {
        String url = "http://nask.pl/";
        NewUrlObject obj = new NewUrlObject(url, "file", null);
        Assert.assertNotNull(obj.getDomain());
        Assert.assertNull(obj.getIp());
        Assert.assertFalse(obj.usesIDN());
        Assert.assertFalse(obj.usesIPv4());
        Assert.assertFalse(obj.usesIPv6());
    }

    public void testIPv4URL() throws URIException {
        String url = "http://127.0.0.1/";
        NewUrlObject obj = new NewUrlObject(url, "file", null);
        Assert.assertNull(obj.getDomain());
        Assert.assertNotNull(obj.getIp());
        Assert.assertFalse(obj.usesIDN());
        Assert.assertTrue(obj.usesIPv4());
        Assert.assertFalse(obj.usesIPv6());
    }

    public void testIdnURL() throws URIException {
        String url = "http://skąpiec.pl/";
        NewUrlObject obj = new NewUrlObject(url, "file", null);
        Assert.assertTrue(obj.usesIDN());
        Assert.assertNull(obj.getIp());
        Assert.assertEquals("skąpiec.pl",obj.getDomain());
        Assert.assertFalse(obj.usesIPv4());
        Assert.assertFalse(obj.usesIPv6());
    }

    public void testIPv6URL() throws URIException {
        String url = "http://[fe80::226:b9ff:fece:dbea]/64/";
        NewUrlObject obj = new NewUrlObject(url, "file", null);
        Assert.assertNull(obj.getDomain());
        Assert.assertNotNull(obj.getIp());
        Assert.assertFalse(obj.usesIDN());
        Assert.assertFalse(obj.usesIPv4());
        Assert.assertTrue(obj.usesIPv6());
    }
}
