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

import java.net.MalformedURLException;

import org.apache.commons.httpclient.URIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import pl.nask.hsn2.normalizers.URLNormalizerUtils.EncodingType;

public class URLNormalizerUtilsTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(URLNormalizerUtilsTest.class);
	
  @Test(dataProvider = "obfuscated")
  public void removeAllObfuscatedEncodingTest(String in, String expected) {
	  LOG.debug("[{}] {}",in,expected);
	  StringBuilder sb = new StringBuilder(in);
	  URLNormalizerUtils.removeObfuscatedEncoding(sb,new EncodingType[] {EncodingType.ALL_ASCII});
	  LOG.debug("{} == [{}]",sb.toString(),in);
	  Assert.assertEquals(sb.toString(), expected);
  }
  
  @Test(enabled=true,dataProvider="encodePathDP")
  public void encodePathTest(String in,String expected) throws URLParseException {
	  StringBuilder sb = new StringBuilder(in);
	  URLNormalizerUtils.encodePath(sb, 0, sb.length());
	  Assert.assertEquals(sb.toString(),expected);
	  
  }
  @DataProvider
  Object [][] encodePathDP(){
		  return new Object[][] {
				  {"/%31ua","/1ua"}
				  ,{"łac","%C5%82ac"}
				  ,{"ą/!_ć/%/%ł","%C4%85/!_%C4%87/%25/%25%C5%82"}
				  ,{"+ %2B/t","+%20+/t"}
				  ,{"/%2F/a/!","/%2F/a/!"}
				  ,{"%hf%2b.-_~!$&'()*+,;=:@","%25hf+.-_~!$&'()*+,;=:@"}
		  };
  }
  @Test(dataProvider="normalizePathDP")
  public void normalizePathTest(String in,String expected) throws URLParseException {
	  StringBuilder sb = new StringBuilder(in);
	  URLNormalizerUtils.processIISenc(false);
	  URLNormalizerUtils.normlizePath(sb, 0, sb.length());
	  Assert.assertEquals(sb.toString(),expected);
  }
  
  
  @DataProvider
  Object [][] normalizePathDP() {
	  return new Object [][] {
			  {"","/"}
			  ,{"asd/?","/asd/?"}
			  ,{"ł/asdf/../?ł","/%C5%82/?ł"}
			  ,{"../../testł/?","/test%C5%82/?"}
			  ,{"/a/b/../../testł/?ł","/test%C5%82/?ł"}
			  ,{"ł/asdf/../../?","/?"}
			  ,{"/ł/asdf/../?","/%C5%82/?"}
			  ,{"as/././../test!/#ł","/test!/#ł"}
			  ,{"","/"}
			  ,{"%2F","/%2F"}
			  ,{"%/%25/%u0142","/%25/%25/%C5%82"}
			  ,{"+ %2B/t","/+%20+/t"}
			  ,{"%b8%f7%fe/#","/%b8%f7%fe/#"}
			  
	  };
  }
  @Test(dataProvider = "decodeIPv4DP")
  public void decodeIpv4TestSB(String in,int end) throws URLHostParseException {
	 StringBuilder sb = new StringBuilder(in);
	 if(end < 0)
		 end = sb.length();
	 String res = URLNormalizerUtils.decodeIPv4(sb, 0, end);
	 Assert.assertEquals(res,"192.168.0.1");
  }
  @DataProvider
  Object[][] decodeIPv4DP(){
	  return new Object[][] {
			  {"192.168.0.1/",11}
				,{"192.0xA80001",-1}
				,{"0xc0A80001/test",10}
				,{"192.052000001",-1}
				,{"0xc0A80001",-1}

			  
	  };
		  
  }
  
  @Test
  public void decodeIpv4Test() throws URLHostParseException {
	  String[] d = {
		"192.168.0.1",
		"192.0xA80001",
		"0xc0A80001",
		"192.0xA8.1",//TODO check whether this behavior is OK
		"192.0xa8.0x0.00000001",
		"0xC0.168.000.0x1",
		"192.052000001"
		,"0xc0A80001"
	  };
	  for(String s : d) {
		  long[] res = URLNormalizerUtils.decodeIPv4(s);
		  Assert.assertEquals(res[0], 192);
		  Assert.assertEquals(res[1], 168);
		  Assert.assertEquals(res[2], 0);
		  Assert.assertEquals(res[3],1);	 
	  }
		  
  }
 
  
  @Test(dataProvider="ipv6DP")
  public void decodeIPv6Test(String inp,String expected) throws URLHostParseException, URLMalformedInputException {
	  if(expected != null)
		  Assert.assertEquals(URLNormalizerUtils.decodeIPv6(new StringBuilder(inp)), expected);
	  else {
		  boolean exceptionThrown = false;
		  try {
			URLNormalizerUtils.decodeIPv6(new StringBuilder(inp));
		} catch (URLHostParseException e) {
			exceptionThrown = true;
		} catch (URLMalformedInputException e) {
			exceptionThrown = true;
		}
		  Assert.assertTrue(exceptionThrown,"Exception must have been thrown");
	  }
  }
  @Test(dataProvider="ipv6DP")
  public void recursiveDecodeIPv6Test(String in, String exp) throws URLHostParseException, URLMalformedInputException {
	  if ( exp != null) {
		  String res = URLNormalizerUtils.decodeIPv6(new StringBuilder(in));
		  Assert.assertEquals(URLNormalizerUtils.decodeIPv6(new StringBuilder(res)), exp);
	  }
  }
  @DataProvider
  Object [][] ipv6DP() {
	  return new Object[][] {
			  {"[:::2]",null}
			  ,{"[0:0:0:0:0:FA:C0a8:1]","[0:0:0:0:0:fa:c0a8:1]"}
			  ,{"::ffff:c0a8:1","[0:0:0:0:0:ffff:192.168.0.1]"}
			  ,{"asdf:[::192.168.0.1]:8080","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"adf:[::192.168.0.1:8080",null}
			  ,{"adf:192.168.0.1:8080",null}
			  ,{"::c0a8:1","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"[::192.168.0.1]","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"[::192.0xA80001]","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"[::FFFF:192.168.0.1]","[0:0:0:0:0:ffff:192.168.0.1]"}
			  ,{"[::1]","[0:0:0:0:0:0:0:1]"}
			  ,{"::fa:c0a8:1","[0:0:0:0:0:fa:c0a8:1]"}
			  ,{"[ffff::1]","[ffff:0:0:0:0:0:0:1]"}
			  ,{"10::1/","[10:0:0:0:0:0:0:1]"}
			  ,{"1:1::1","[1:1:0:0:0:0:0:1]"}
			  ,{"::192.0xA80001","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"[ffff::1",null}
			  ,{"[:1:192.168.0.1]",null}
			  ,{"[1:1:1:1]",null}
			  ,{"1:1:1:1:1:1:1:1:0}",null}
			  ,{"[1::]","[1:0:0:0:0:0:0:0]"}
			  ,{"[::1:192.168.0.1]",null}
			  ,{"[1::192.168.0.1]",null} 
			  ,{"[0:0:0:0:0:ffff:212.77.100.101]","[0:0:0:0:0:ffff:212.77.100.101]"}
			  ,{"0:0:0:0:0:0:192.168.0.1","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"[0:0:0:0:0:0:212.77.100.101]","[0:0:0:0:0:0:212.77.100.101]"}
			  ,{"0:0:0:0:0:ffff:212.77.100.101","[0:0:0:0:0:ffff:212.77.100.101]"}
			  ,{"0:0:0:0:0:ffff:c0a8:1","[0:0:0:0:0:ffff:192.168.0.1]"}
			  ,{"1:0:0:0:0:ffff:c0a8:1","[1:0:0:0:0:ffff:c0a8:1]"}
			  ,{"[0:0:0:0:0:ffff:192.168.0.1]","[0:0:0:0:0:ffff:192.168.0.1]"}
			  ,{"1:1:1:1:1:1:1:1","[1:1:1:1:1:1:1:1]"}
			  ,{"0:0:0:0:0:0:c0a8:1","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"0:0:0:0:0:ffff:192.0xA80001","[0:0:0:0:0:ffff:192.168.0.1]"}
			  ,{"0:0:0:0:0:0:c0a8:1?","[0:0:0:0:0:0:192.168.0.1]"}
			  ,{"0:0:0:0:0:ffff:192.0xA80001/","[0:0:0:0:0:ffff:192.168.0.1]"}
			  		  
	  };
  }
  
  
  
  
  @Test(dataProvider=("multiLineDP"))
  public void multiLineTest (String in,String expected) throws MalformedURLException {
	  UrlNormalizer test = new UrlNormalizer(in);
	  Assert.assertEquals(test.toProcess.toString(), expected);
  }
  @DataProvider
  Object[][] multiLineDP() {
	  return new Object [][] {
			  {"http://\r\nwww.wp.pl\n/","http://www.wp.pl/"},
			  {"a \nb","a"},
			  {"a\rc","ac"}
	  };
  }
 
 
  
  
  @Test
  public void DNStoIDNTest1() throws URLHostParseException {
	  StringBuilder sb = new StringBuilder("!!!łąć.k_.pl/a");
	  String s = URLNormalizerUtils.dnsToIDN(sb,3,12);
	  Assert.assertEquals(s, "xn--2dae9o.k_.pl");
	  Assert.assertTrue(sb.toString().startsWith("!!!"));
	  Assert.assertTrue(sb.toString().endsWith("/a"));
  }
  
  @Test(dataProvider="dnsConvDP")
  public void DNStoIDNTest2(String in,String expected) throws URLHostParseException {
	  if(expected!=null)
		  Assert.assertEquals(URLNormalizerUtils.dnsToIDN(new StringBuilder(in)),expected);
	  else {
		  boolean exceptionThrown = false;
		  try {
			  URLNormalizerUtils.dnsToIDN(new StringBuilder(in));
		  } catch (URLHostParseException e) {
			  exceptionThrown = true;  
		  }
		  Assert.assertTrue(exceptionThrown,"exception had to be thrown");
	  }
  }
  @DataProvider
  Object[][] dnsConvDP() {
	  return new Object[][] {
			  {"łąć.k_.pl","xn--2dae9o.k_.pl"},
			  {"!%test.pl",null},
			  {"ó_.com.nl","xn--_-uga.com.nl"},
			  {"",null},
			  {"http://~test.pl/",null},
			  {"012.pl","012.pl"}
			  
	  };
	  
  }
  
 @Test(dataProvider="rmObfuscatedEnc")
 public void removeObfuscatedEnc(String s,EncodingType[] encs) {
	 StringBuilder sb = new StringBuilder(s);
	 String res = URLNormalizerUtils.removeObfuscatedEncoding(sb, encs);
	 Assert.assertEquals(res.indexOf("%"),-1);
	 
 }
 @Test
 public void removeObfuscatedEnc1() {
	 StringBuilder sb = new StringBuilder("%2b%3F");
	 Assert.assertEquals(URLNormalizerUtils.removeObfuscatedEncoding(sb, 0, sb.length(), new EncodingType[] {EncodingType.SCHEME_ALLOWED}),"+%3F");
 }
 @DataProvider
 Object [][] rmObfuscatedEnc() {
	 return new Object[][] {
			 {"%41",new EncodingType[] {EncodingType.ALL_ASCII}},
			 {"%41%u0041",new EncodingType[] {EncodingType.ALL_ASCII,EncodingType.IIS_ENC} },
			 {"|%7C", new EncodingType[] {EncodingType.QUERY_ALLOWED}}
	 };
 }
  
  
  
 @Test(dataProvider="obfuscatedEncodingBasic")
 public void removeObfuscatedEncodingBasicTest(String in,String expected) {
	 Assert.assertEquals(URLNormalizerUtils.removeObfuscatedEncoding(new StringBuilder(in),new EncodingType[] {EncodingType.URI_BASE}),expected);
 }
 @DataProvider
 Object[][] obfuscatedEncodingBasic() {
	 return new Object[][] {
			 {"%61%62%63%64%65%66%67%2D%5F%2E","abcdefg-_."}
			 ,{"%2F.-/","%2F.-/"}
			 ,{"%%%41","%%A"}
	 };
 }
  
 
 
 @Test
 public void numIPv4SBTest() {
	 StringBuilder sb = new StringBuilder("acsd0xf0f0f0f0:8080");
	 Assert.assertEquals(URLNormalizerUtils.numToIPv4(sb,4,100),"240.240.240.240");
	 
 }
 
  @Test(dataProvider="numIPv4DP")
  public void numIPv4Test(String in,String expected) {
	  Assert.assertEquals(URLNormalizerUtils.numToIPv4(new StringBuilder(in)),expected);
  }
  @DataProvider
  Object[][] numIPv4DP() {
	  return new Object[][] {
			  {"0xf0f0f0f0:8080","240.240.240.240"},
			  {"0xC0A80101:80","192.168.1.1"},
			  {"3232235777:80","192.168.1.1"},
			  {"03232235777","26.105.59.255"},
			  {"00000000030052000401:80","192.168.1.1"},
			  {"0x00000000000000000C0A80101:80","192.168.1.1"},
			  {"020202",null}, 
			  {"3232235777/","192.168.1.1"},
			  {"032423262145","212.77.100.101"}
	  };
  }
  
 
  
  @DataProvider(name="obfuscated")
  Object [][] genObfuscatedStrings() {
	  return new String[][] {
			  {"%61%62%63","abc"},
			  {"%61%C5%82A","a%C5%82A"},
			  {"%A%61%U","%Aa%U"},
			  {"aaaa%", "aaaa%"},
			  {"%C5%82%2e%5f%310%C4%87","%C5%82._10%C4%87"},
			  {"%68%74%74%70://%6e%63%63%63%6e%6e%6e%63%2e%63%6e/%69%6d%67/%69%6e%64%65%78%2e%70%68%70%27%20%73%74%79%6c%65%3d%27%64%69%73%70%6c%61%79%3a%6e%6f%6e%65%3b%27%3e%3c%2f%69%66%72%61%6d%65%3e"
				  ,"http://ncccnnnc.cn/img/index.php' style='display:none;'></iframe>"}	  
	  };  
  }
  
  @Test(dataProvider="normalizeQueryDP")
  public void normalizeQueryTest(String in,String exp) throws URLMalformedInputException {
	  StringBuilder sb = new StringBuilder(in);
	  Assert.assertEquals(URLNormalizerUtils.normalizeQuery(sb, 0, sb.length()), exp);
  }
  @DataProvider
  Object [][] normalizeQueryDP() {
	  return new Object[][] {
			  {"?test=asdf#frag","?test=asdf"}
			  ,{"?a=%;b=c;c=d&e=&","?a=%25&b=c&c=d&e=&"}
			  ,{"?c=ł;","?c=%C5%82&"}
	  };
  }
  @Test(dataProvider="userInfoDP")
  public void normalizeUserInfo(String inp,String exp) throws URIException {
	  StringBuilder sb = new StringBuilder(inp);
	  String cmp = URLNormalizerUtils.normalizeUserInfo(sb, 0, sb.indexOf("@") >= 0 ? sb.indexOf("@") : sb.length());
	  Assert.assertEquals(cmp,exp);
  }
  @DataProvider
  Object [][] userInfoDP() {
	  return new Object[][] {
			  {":test@asdf","%3Atest"},
			  {"test:test@test.pl","test:test"},
			  {"%3Atest@test","%3Atest"}
	  };
  }
  
  @Test(dataProvider="normalizeFragmentDP")
  public void normalizeFragmentTest(String in,String expected) throws URLMalformedInputException {
	  StringBuilder sb = new StringBuilder(in);
	  Assert.assertEquals(URLNormalizerUtils.normalizeFragment(sb, 0, sb.length()), expected);
  }
  @DataProvider
  Object [][] normalizeFragmentDP() {
	  return new Object [][] {
			  {"#fra.gm-2.4","#fra.gm-2.4"}
			  ,{"#!$&'() * + ;=","#!$&'() * + ;="}
			  ,{"#f%2Bra%","#f+ra%25"}
			  
	  };
  }
  
}
