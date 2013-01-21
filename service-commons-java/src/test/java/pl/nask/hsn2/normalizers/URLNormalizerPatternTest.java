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

package pl.nask.hsn2.normalizers;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class URLNormalizerPatternTest {

  @Test(dataProvider="patternTestDP")
  public void patternMatcherTest(String s,Object[] d) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	  String patternName = (String)d[0];
	  boolean pass = (Boolean) d[1];
	  Class<?> clazz = Class.forName("pl.nask.hsn2.normalizers.URLNormalizerUtils");
	  Field[] fields = clazz.getFields();
	  for (Field f:fields) {
		  if(f.get(null) instanceof Pattern) {
			  Pattern  p = (Pattern) f.get(null);
			  boolean match = p.matcher(s).matches();
			  if(f.getName().equals(patternName))
				  Assert.assertEquals(match, pass,"pattern should match: "+f.getName());
			  else if (pass) {
				  Assert.assertFalse(p.matcher(s).matches(),"pattern match should be unique: "+ f.getName()+" == "+patternName);
			  }
		  }
	  }
	  
  }
  @DataProvider
  Object[][] patternTestDP() {
	  return new Object[][]{
			  {"%af",new Object[] {"percentEnc",true}},
			  {"%af%AD%11",new Object[] {"percentEnc",true}},
			  {"avd%ab%ab11",new Object[] {"percentEnc",false}},
			  {"%u004f%u0031",new Object[] {"percentEncWithIIS",true}},
			  {"%u004f%u0031",new Object[] {"percentEnc",false}},
			  
			  {"192.0x11.010.10",new Object[] {"ipv4",true}},
			  {"0000192.168.1.1",new Object[] {"ipv4",true}},
			  {"0000192.000000168.01.1",new Object[] {"ipv4",true}},
			  {".pl",new Object [] {"dnsEnd",true}},
			  {"xn--fiqs8s",new Object [] {"dnsEnd",true}},
			  {"xn-_fiqs8s",new Object [] {"dnsEnd",true}},
			  {"[0:0:0:0:0:ffff:212.77.100.101]", new Object [] {"ipv6v4normalized",true}}
	  };
  }
  
  
  
  
  
  
  @Test
  public void findLastMatchTest() {
	  StringBuilder s = new StringBuilder("aaaa]]]]]c");
	  Assert.assertEquals(URLNormalizerUtils.findLastMatch(s, "]a]", 0), 8);
	  Assert.assertEquals(URLNormalizerUtils.findLastMatch(s, "bd0a", 0), 3);
	  Assert.assertEquals(URLNormalizerUtils.findLastMatch(s, "]a]c", 0), 9);
	  
	  Assert.assertEquals(URLNormalizerUtils.findLastMatch(s, "]a]c", 1,3), 2);
	  Assert.assertEquals(URLNormalizerUtils.findLastMatch(s, "bd0a", 3,3), -1);
	  Assert.assertEquals(URLNormalizerUtils.findLastMatch(s, "]a]c", 0,8), 7);
  }
  
  @Test
  public void findFirstMatchTest() {
	  StringBuilder sb = new StringBuilder("ab&ccc://@://&#: @");

	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb,"uf@/:&" , 2),    2);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb,"uf@/:&" , 3),    6);
	  
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb,"uf@/:&" , 3,3),-1);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb,"uf@/:&" , 3,6),-1);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb,"uf@/:&" , 3,7),6);
	 
	  
	  String []sp = new String []{"ab","://"," "};
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp,9,sb.length()),  10);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp,1,sb.length()),  6);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp,1),  6);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp,6),  6);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp,7),  10);

	  sb = new StringBuilder("a://http://a&");
	  sp =new  String[] {"://", "http://"};
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp, 0,3),-1);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp, 0,sb.length()),1);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp, 1,sb.length()),1);
	  Assert.assertEquals(URLNormalizerUtils.findFirstMatch(sb, sp, 2,sb.length()),4);
  }
  
  
  @Test(dataProvider="ipv4dotDP")
  public void ipv4dotTest(String in,String expected) {
	 if(expected.length()>0)
		 Assert.assertTrue(URLNormalizerUtils.ipv4.matcher(in).matches());
  }
  
  @DataProvider
  Object[][] ipv4dotDP() {
	  return new Object[][] {
			  {"0xA0.0xa0.0xa0.0x10","160.160.160.16"},
			  {"0x12.12.12.12","18.12.12.12"},
			  {"03.0111.020.20","3.73.16.20"}
	  };
  }

  
}
