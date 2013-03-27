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

import java.util.ArrayList;
import org.apache.commons.httpclient.URIException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UrlNormalizerTest {
	
  @Test(dataProvider="initTestDP")
  //Test contructor's obfuscation cleaning
  public void obfuscatedEncodingTest(String inp,String expected) {
	  Assert.assertEquals(new UrlNormalizer(inp).toProcess.toString(),expected);
  }
  @DataProvider
  Object[][] initTestDP() {
	  return new Object[][] {
			  {"%61%62%2D%2E%7E_","ab-.~_"}
			  ,{"%61%21%40%41","a%21%40A"}	  
	  };
  }
  
  
  
  
  @Test(enabled = true,dataProvider="processSchemeDP")
  public void processSchemeHostTest(String in,String expected) throws URLMalformedInputException, URLHostParseException, URIException {
	  UrlNormalizer un = new UrlNormalizer(in);
	  if(expected != null) {
		  un.processSchemeOrHost();
		  UrlNormalizer.URI res = un.getInternalURI();
		  Assert.assertEquals(res.scheme, expected);
		  if (!res.processed)
			  Assert.assertTrue(un.toProcess.length() > 0);
	  } else {
		  boolean excThrown = false;
		  try {
			  un.processSchemeOrHost();
		  } catch (Exception e) {
			  if( e instanceof URLMalformedInputException || e instanceof URLHostParseException)
				  excThrown = true;
		  }
		  Assert.assertTrue(excThrown,"expected exception for input "+in);
	  }
  }
  @DataProvider
  Object [][] processSchemeDP() {
	  return new Object[][] {
			  {"httpabd://test.pl","httpabd"}
			  ,{"httpS://t","https"}
			  ,{"fTp://test" ,"fTp"}
			  ,{"10.10.10.10","http"}
			  ,{"[::ffff:c0a8:1]/asdf","http"}
			  ,{"::ffff:c0a8:1/","http"}
			  ,{"testuser@10.10.10.10/","http"}
			  ,{"user:pass@10.10.10.10","user"} 
			  ,{"http://www.10.com@www.site.com/","http"}
			  ,{"0xc0A80001","http"}
			  ,{"0xc0A80001/test","http"}
			  ,{"someNameą","http"}
			  ,{"about:some","about"}
			  ,{"skype:+46000aad","skype"}
			  ,{"http//abc",null}
			  ,{"https/abc","http"}
			  
	  };
  }
  @Test(dataProvider="getSLDdp")
  public void getSLDtest(String in, String exp) throws URIException, URLMalformedInputException, URLHostParseException, URLParseException {
	  UrlNormalizer un = new UrlNormalizer(in);
	  un.normalize();
	  Assert.assertEquals(un.getSLD(),exp);
	  
  }
  @DataProvider
  Object[][] getSLDdp() {
	  return new Object[][] {
			  {"http://wp.pl/","wp.pl"},
			  {"http://a.b.c.d.e.f.pl","f.pl"},
			  {"http://com",""},
			  {"http://a_.pl","a_.pl"},
			  {"a.b.pl","b.pl"},
			  {"http://user:password@nask.pl:8080","nask.pl"},
			  
	  };
  }
  
  @Test(dataProvider="getNormalizedDP")
  public void getNormalizedTest(String in,String expected) throws URLMalformedInputException, URLHostParseException, URLParseException, URIException {
	  UrlNormalizer un = new UrlNormalizer(in);
	  un.normalize();
	  Assert.assertEquals(un.getNormalized(),expected);
	  
  }
  @Test(dataProvider="getNormalizedDP")
  public void recursiveNormalize(String in,String exp) throws URIException, URLMalformedInputException, URLHostParseException, URLParseException {
	  UrlNormalizer un = new UrlNormalizer(in);
	  un.normalize();
	  String normalized = un.getNormalized();
	  Assert.assertEquals(normalized,exp);
	  un = new UrlNormalizer(normalized);
	  un.normalize();
	  Assert.assertEquals(un.getNormalized(),exp);
  }
  @DataProvider
  Object [][]getNormalizedDP() {
	  return new Object[][] {
			  {"http://fb.com.//?href=http://|www.fb.com/pg/test","http://fb.com/?href=http://%7Cwww.fb.com/pg/test"}
			  ,{"http://test.pl./a%2b%2bc%?a%2bc=+b%ggg%","http://test.pl/a++c%25?a%2bc=%20b%25ggg%25"}
			  ,{"http://www.nask.pl/wydarzeniaID%2fid/811","http://www.nask.pl/wydarzeniaID%2fid/811"}
			  ,{"httpabc://wp.pl","httpabc://wp.pl"}
			  ,{"http://uz/","http://uz/"}
			  ,{"http://user:password@nask.pl:8080","http://user:password@nask.pl:8080/"}
			  ,{"http://user:password@nask.pl:/","http://user:password@nask.pl/"}
			  ,{"http://032423262145","http://212.77.100.101/"}
			  ,{"http://032423262145/a","http://212.77.100.101/a"}
			  ,{"http://0xd44d6465/","http://212.77.100.101/"}
			  ,{"A","http://a/"}
			  ,{"hTTp://a?","http://a/?"}
			  ,{"httpS://a#","https://a/#"}
			  ,{"%41","http://a/"}
			  ,{"htTp://0xc0A80001:8080","http://192.168.0.1:8080/"}
			  ,{"http://test@0xc0A80001:8080","http://test@192.168.0.1:8080/"}
			  ,{"[ff::a]/łapka?ławka","http://[ff:0:0:0:0:0:0:a]/%C5%82apka?%C5%82awka"}
			  ,{"tEst@0xa0.12.12.12/index.html","http://tEst@160.12.12.12/index.html"}
			  ,{"http://test.!@host:8080","http://test.!@host:8080/"}
			  ,{"http://test@10.10.10.10/t/../?a","http://test@10.10.10.10/?a"}
			  ,{"hTtps://[::ffff:c0a8:1]/asdf","https://[0:0:0:0:0:ffff:192.168.0.1]/asdf"}
			  ,{"HTTPS://test@somehost.com:8080/info/../?a","https://test@somehost.com:8080/?a"}
			  ,{"http://abc.com:/%7esmith/home.html","http://abc.com/~smith/home.html"}
			  ,{"0xc0A80001/","http://192.168.0.1/"}
			  ,{"[::ffff:c0a8:1]/asdf","http://[0:0:0:0:0:ffff:192.168.0.1]/asdf"}
			  ,{"wp.pl/","http://wp.pl/"}
			  ,{"skyp%65:+48111","skype:+48111"}
			  ,{"10.10.10.10","http://10.10.10.10/"}
			  ,{"0xc0A80001","http://192.168.0.1/"}
			  ,{"scheme://%61sdfa?asdfd","scheme://asdfa?asdfd"}
			  ,{"http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwyborcza.pl%2F1%2C75478%2C12880742%2CPasikowski__Nie_bede_przepraszal_za__Poklosie_.html&layout=button_count&show_faces=true&width=400&action=like&font&colorscheme=dark&height=21"
				  ,"http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwyborcza.pl%2F1%2C75478%2C12880742%2CPasikowski__Nie_bede_przepraszal_za__Poklosie_.html&layout=button_count&show_faces=true&width=400&action=like&font&colorscheme=dark&height=21"},
			{"http://[0:0:0:0:0:ffff:212.77.100.101]/","http://[0:0:0:0:0:ffff:212.77.100.101]/"},
			{"[0:0:0:0:0:ffff:212.77.100.101]","http://[0:0:0:0:0:ffff:212.77.100.101]/"},
			{"http://[0:0:0:0:0:0:212.77.100.101]/","http://[0:0:0:0:0:0:212.77.100.101]/"},
			{"[0:0:0:0:0:0:c0a8:1]?","http://[0:0:0:0:0:0:192.168.0.1]/?"},
			{"[0:0:0:0:0:ffff:192.0xA80001]#","http://[0:0:0:0:0:ffff:192.168.0.1]/#"},
			{"[::192.168.0.1]","http://[0:0:0:0:0:0:192.168.0.1]/"},
			{"[::192.0xA80001]","http://[0:0:0:0:0:0:192.168.0.1]/"},
			{"[::FFFF:192.168.0.1]","http://[0:0:0:0:0:ffff:192.168.0.1]/"},
			{"[::1]","http://[0:0:0:0:0:0:0:1]/"},
			{"[::fa:c0a8:1]","http://[0:0:0:0:0:fa:c0a8:1]/"}
	  };
  }
  @Test(enabled = true,dataProvider="urls")
  public void refTest(String in, String exp) throws URLMalformedInputException, URLHostParseException, URLParseException, URIException {
	  UrlNormalizer norm = new UrlNormalizer(in);
	  norm.normalize();
	  Assert.assertEquals(norm.getNormalized(),exp);
  }
  
  @DataProvider
  Object[][] urls() {
		return new Object[][] {
				{"[::2]","http://[0:0:0:0:0:0:0:2]/"},
				{"http://www.08007000.nl","http://www.08007000.nl/"},
				{"http://jjj@www.08007000.nl","http://jjj@www.08007000.nl/"},
				{"http://192.100.200.c9","http://192.100.200.c9/"}, //TODO incorrect TLD - check it
				{"http://192.100.200.0xc9","http://192.100.200.201/"},
				{"http://www.08007000.nl?","http://www.08007000.nl/?"},
				{"http://www.08007000.mobi?","http://www.08007000.mobi/?"},
				{"[1f::f:f]?","http://[1f:0:0:0:0:0:f:f]/?"},
				{"[01F::f:F]?AbCd","http://[1f:0:0:0:0:0:f:f]/?AbCd"},
				{"[::fff0]:90","http://[0:0:0:0:0:0:0:fff0]:90/"},
				{"[::ffff:192.192.192.192]","http://[0:0:0:0:0:ffff:192.192.192.192]/"},
				{"[::193.193.193.103]:8080","http://[0:0:0:0:0:0:193.193.193.103]:8080/"},
				{"[::FFFF:193.193.193.103]","http://[0:0:0:0:0:ffff:193.193.193.103]/"},
				{"[::c0a8:1]","http://[0:0:0:0:0:0:192.168.0.1]/"},
				{"[::FFFF:c0a8:1]#","http://[0:0:0:0:0:ffff:192.168.0.1]/#"},
				{"[::FFFe:c0a8:1]/","http://[0:0:0:0:0:fffe:c0a8:1]/"},
				{"0Xf01afaf","http://15.1.175.175/"},
				{"0xA0.0xa0.0xa0.0x10","http://160.160.160.16/"},
				{"FTP://[::FFFF:ff:ff:193.193.193.103]:8080/Test","FTP://[::FFFF:ff:ff:193.193.193.103]:8080/Test"},
				{"HTTP://0x12.12.12.12/u/../inde x.html","http://18.12.12.12/inde"},
				{"HTTPS://0x12.12.12.12/u/../inde? x.html","https://18.12.12.12/inde?"},
				{"tEst@0xa0.12.12.12/index.html","http://tEst@160.12.12.12/index.html"},
				{"tEst@0xa0.12.12.12/u/../index.html","http://tEst@160.12.12.12/index.html"},
				{"test:pASSSS@0Xa0.12.12.12/u/../INDEX.html","test:pASSSS@0Xa0.12.12.12/u/../INDEX.html"},
				{"teSt:paSsword@0Xf0f0f0f0:65553/?test/../test","teSt:paSsword@0Xf0f0f0f0:65553/?test/../test"},
				{"http://teSt0XF0F0F0F0:65553/test/../../../../test/../test","http://test0xf0f0f0f0:65553/test"},
				{"https://teSt@w.PL/test1/../?info=test&t=/./","https://teSt@w.pl/?info=test&t=/./"},
				{"w.PL/test1/../?q1=test&q2={}&q3=' '&q4=","http://w.pl/?q1=test&q2=%7B%7D&q3='"},
				{"0xff.0x10.0x10.0x10/SSr/../DS/..","http://255.16.16.16/"},
				{"WIRED-Prot://test/../../../index","WIRED-Prot://test/../../../index"},
				{"FTp://T@łóżko12.śróćmą.cz:101/t1/../t2","FTp://T@łóżko12.śróćmą.cz:101/t1/../t2"},
				{"[f0f0::f0f0]/test/../../","http://[f0f0:0:0:0:0:0:0:f0f0]/"},
				{"[ff::a]/łapka?ławka","http://[ff:0:0:0:0:0:0:a]/%C5%82apka?%C5%82awka"},
				{"localhost:8080/","localhost:8080/"},
				{"0Xf00000f0:65553/test1/info1/./INFO2/./INFO3/./info4?/./t1/t2/a1/../../../test#t","0Xf00000f0:65553/test1/info1/./INFO2/./INFO3/./info4?/./t1/t2/a1/../../../test#t"},
				{"https://włapka.pl/test1/../info1/./info2/./info3/./info4/./t1/t2/t3/t4/../../../?test","https://xn--wapka-k7a.pl/info1/info2/info3/info4/t1/?test"},
				{"łóżko12.śróćmą.pl/../../?łóśćź=''&łóśćź=''","http://xn--ko12-pqa78b9m.xn--rm-5ja7ei51c.pl/?%C5%82%C3%B3%C5%9B%C4%87%C5%BA=''&%C5%82%C3%B3%C5%9B%C4%87%C5%BA=''"},
				{"xn--ko12-pqa78b9m.xn--rm-5ja7ei51c.pl/?łóśćź=''&łóśćź=''","http://xn--ko12-pqa78b9m.xn--rm-5ja7ei51c.pl/?%C5%82%C3%B3%C5%9B%C4%87%C5%BA=''&%C5%82%C3%B3%C5%9B%C4%87%C5%BA=''"},
				{"  git://github.com/user/project-name.git   ","git://github.com/user/project-name.git"},
				{"http://!$^&*()_+`-={}|;@www.pc-help.org/obscur%65ł.htm","http://!$%5E&*()_+%60-=%7B%7D%7C%3B@www.pc-help.org/obscure%C5%82.htm"},
				{"hTtp://www.playboy.com@3468664375.pl/obscure.htm","http://www.playboy.com@3468664375.pl/obscure.htm"},
				{"maIlto:j@test.pl/","maIlto:j@test.pl/"},
				{"abOut:config","abOut:config"},
				{"te%3A@wp.pl:","http://te%3A@wp.pl/"},
				{"http://:test@wp.pl:","http://%3Atest@wp.pl/"}
				
				//WebClient doesn't handle '|' in  URL but browsers do
//				,{"http://uk.org/styles.php?s=Comic%20Sans%20MS|Marker%20Felt|8pt|7pt|333333|999999","http://uk.org/styles.php?s=Comic%20Sans%20MS|Marker%20Felt|8pt|7pt|333333|999999"}
		};
	}
  @Test(dataProvider="getTldDP")
  public void getTldTest(String in, String [] expected) throws URIException, URLMalformedInputException, URLHostParseException, URLParseException {
	  UrlNormalizer un = new UrlNormalizer(in);
	  un.normalize();
	  Assert.assertEquals(un.getNormalized(),expected[0]);
	  Assert.assertEquals(un.getTLD(),expected[1]);
  }
  @DataProvider
  Object [][] getTldDP() {
	  return new Object[][] {
			  {"china.xn--fiqs8s",genExpected("http://china.xn--fiqs8s/","xn--fiqs8s")},
			  {"test:@%61.pl",genExpected("test:@a.pl","")}, // it is URI with schema 'test'
			  {"http://www.nask.pl:80/r",genExpected("http://www.nask.pl:80/r","pl")},
			  {"http://www.nask.pl.:80/r",genExpected("http://www.nask.pl:80/r","pl")},
			  {"nask.pl.:80? ",genExpected("http://nask.pl:80/?","pl")}
	  };
  }
  private String[]genExpected(String ... t){
	  ArrayList<String> ret = new ArrayList<String>();
	  for(String s : t)
		  ret.add(s);
	  return ret.toArray(new String[] {});
  }
  
  
  
  @Test(dataProvider="testUserInfoDP")
  public void testUserProcessing(String in,String exp) throws URIException, URLMalformedInputException, URLHostParseException, URLParseException {
	  UrlNormalizer un = new UrlNormalizer(in);
	  un.normalize();
	  Assert.assertEquals(un.getNormalized(), exp);
	  Assert.assertTrue(un.getUserInfo().contains("test"));
	  
  }
  @DataProvider
  Object [][] testUserInfoDP() {
	  return new Object[][] {
			  {"test@WP.Pl","http://test@wp.pl/"},
			  {"http://test@wP.pl:/","http://test@wp.pl/"},
			  {"http://test:@wp.Pl:80","http://test:@wp.pl:80/"},
			  {"http://:test@wP.pl","http://%3Atest@wp.pl/"},
			  {"test@localhost","http://test@localhost/"},
//			  {"test:test@Localhost","test:test@Localhost"} // this is URI with scheme test
	  };
  }
}
