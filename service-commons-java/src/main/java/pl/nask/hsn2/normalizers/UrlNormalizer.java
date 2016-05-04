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

package pl.nask.hsn2.normalizers;

import org.apache.commons.httpclient.URIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.normalizers.URLNormalizerUtils.EncodingType;

public class UrlNormalizer {
	private static final Logger LOG = LoggerFactory.getLogger(UrlNormalizer.class);
	public static final String DEFAULT_SCHEMA="http";

	private final String original;
	//access for tests only
	StringBuilder toProcess;
	private URI internalURI;
	URI getInternalURI() {
		return this.internalURI;
	}
	public UrlNormalizer(String uri) {
		this.original = uri.trim();;
		toProcess = new StringBuilder(this.original);
		int i = toProcess.indexOf(" ");
		if (i > 0) {     
			LOG.warn("URL contains unescaped space(s) will be trimmed at position [{}]:({})", i + 1, toProcess.toString());
			toProcess.delete(i, toProcess.length());
		}
		for( int c = 0; c < toProcess.length(); c++) {
			if (Character.isISOControl(toProcess.codePointAt(c))) {
				toProcess.deleteCharAt(c--);
				}
		}
		URLNormalizerUtils.removeObfuscatedEncoding(toProcess, new EncodingType[] {EncodingType.URI_BASE});
		
	}
	public boolean isURL() {
		return internalURI == null ? false:internalURI.isUrl;
	}
	public boolean isNormalized() {
		return internalURI == null? false : internalURI.processed;
	}
	
	public void normalize() throws URLMalformedInputException, URLHostParseException, URLParseException, URIException {
		processSchemeOrHost();
		processURL();
	}
	public String getNormalized() {
		return internalURI.getURIasString();
	}
	public String getOriginalURL() {
		return original;
	}
	public String getPath() {
		return internalURI.path == null ? internalURI.hierPart.toString() : internalURI.path;
	}
	public int getPort() {
		return internalURI == null ? -1 : internalURI.port ;
	}
	public String getProtocol() {
		return internalURI.scheme;
	}
	public String getQuery() {
		if ( internalURI == null || internalURI.query == null) {
			return null;
		}
		return internalURI.query.substring(1);
	}
	public String getUserInfo() {
		if (internalURI.userInfo != null)
			return internalURI.userInfo;
		return "";
	}
	public boolean hasHostName() {
		if (internalURI != null && internalURI.processed && internalURI.isUrl)
		return !(URLNormalizerUtils.ipv4.matcher(internalURI.host).matches() ||
				URLNormalizerUtils.ipv6.matcher(internalURI.host).matches() ||
				URLNormalizerUtils.ipv6v1.matcher(internalURI.host).matches() ||
				URLNormalizerUtils.ipv6v4normalized.matcher(internalURI.host).matches() );
		return false;
	}
	public String getFragment() {
		if (!isURL()) {
			return "";
		}
		return internalURI.fragment == null ? "" :internalURI.fragment;
	}
	public String getHost() {
		if (internalURI == null) {
			return "";
		}
		return internalURI.host == null ? "" : internalURI.host;
	}
	public String getTLD() {
		if (!hasHostName()) {
			return "";
		}
		int last = internalURI.host.lastIndexOf('.');
		if ( last >= 0) {
			return internalURI.host.substring(last+1);
		}
		return internalURI.host;
	}
	
	public String getSLD() {
		if (!hasHostName()) {
			return "";
		}
		int last = URLNormalizerUtils.findLastMatch(internalURI.host, ".", 0, internalURI.host.length());
		if (last <= 0) {
			return "";
		}
		last = URLNormalizerUtils.findLastMatch(internalURI.host, ".", 0, last);
		return internalURI.host.substring(last+1);

	}
	
	
	
	private void processURL() throws URLMalformedInputException, URLHostParseException, URLParseException, URIException {
		if (internalURI.processed) {
			return;
		}
		LOG.debug("Processing input: '{}'",toProcess.toString());
		if (toProcess.length() == 0) {
			LOG.warn("There is nothing to parse!");
			throw new URLMalformedInputException("Cannot process URL:"+original);
			
		}
		int i = URLNormalizerUtils.findFirstMatch(toProcess, ".[/?#:@", 0);
		if ( i < 0 ) {
			if ( internalURI.host != null) {
				throw new URLMalformedInputException("Cannot parse path,query,fragment:"+toProcess.toString());
			} else {
				String h = null;
				try {
					h = URLNormalizerUtils.decodeIPv4(toProcess, 0,toProcess.length());
				} catch (URLHostParseException e) {
					LOG.debug("Not an IPv4:'{}', trying DNS.",toProcess.toString());
				}
				if ( h == null) {
					h = URLNormalizerUtils.dnsToIDN(toProcess, 0, toProcess.length());
				}
				internalURI.host = h;
				internalURI.path = "/";
				internalURI.processed = true;
				toProcess.delete(0, toProcess.length());
				return;
				
			}
		}
			

			switch (toProcess.codePointAt(i)) {
			case '.':
				int m= URLNormalizerUtils.findFirstMatch(toProcess, "@/:?#", i+1);
				if (m < 0) {
					m = toProcess.length();
				}
				else if (toProcess.codePointAt(m) =='@') {
					internalURI.userInfo = URLNormalizerUtils.normalizeUserInfo(toProcess, 0, m);
					toProcess.delete(0, internalURI.userInfo.length()+1);
					break;
				}
				String host = buildHostname(m);		
				internalURI.host = host;
				toProcess.delete(0, host.length());
				if (toProcess.length() == 0) {
					internalURI.path = "/";
					internalURI.processed = true;
				}
				break;
			case '/':
				if (i==0 && URLNormalizerUtils.findFirstMatch(toProcess, "/", i+1) ==1 ) {
					toProcess.delete(i, 2);
					break;
				} else if (internalURI.host == null && i == 0) {
					throw new URLMalformedInputException("Cannot determine host:"+original);
				}
				if (internalURI.host == null) {
					String s = null;
					try {
					s = URLNormalizerUtils.decodeIPv4(toProcess, 0, i);
					} catch (URLHostParseException e) {
						//ignore
					}
					if (s == null) {
						s = URLNormalizerUtils.dnsToIDN(toProcess,0,i);
					}
					internalURI.host = s;
					toProcess.delete(0, s.length());
					break;
				}
				int tmp = URLNormalizerUtils.findFirstMatch(toProcess, "?#", 0);
				if (tmp < 0) {
					tmp = toProcess.length();
				}
				internalURI.path = URLNormalizerUtils.normlizePath(toProcess, 0, tmp);
				toProcess.delete(0, internalURI.path.length());
				if (toProcess.length() ==0) {
					internalURI.processed = true;
				}
				break;
			case '[': {
				internalURI.host = URLNormalizerUtils.decodeIPv6(toProcess);
				toProcess.delete(0, internalURI.host.length());
				if (toProcess.length()==0) {
					internalURI.path = "/";
					internalURI.processed = true;
				}
			}
			break;
			case '?': {
				checkHostAndPath(i);
				internalURI.query = URLNormalizerUtils.normalizeQuery(toProcess, 0, toProcess.length());
				toProcess.delete(0, internalURI.query.length());
				if (toProcess.length() == 0) {
					internalURI.processed = true;
				}
			}
				break;
			case '@':
					internalURI.userInfo = URLNormalizerUtils.normalizeUserInfo(toProcess, 0, i);
					toProcess.delete(0, internalURI.userInfo.length()+1);
				break;
			case '#':
				checkHostAndPath(i);
				internalURI.fragment = URLNormalizerUtils.normalizeFragment(toProcess, 0, toProcess.length());
				toProcess.delete(0, toProcess.length());
				internalURI.processed = true;
				break;
			case ':':
				if (internalURI.host != null) {
					if (i == toProcess.length()-1) {
						internalURI.path = "/";
					}
					int end = URLNormalizerUtils.findFirstMatch(toProcess, "/?#", i);
					if ( end < 0) {
						end = toProcess.length();
					}
					int port = -1;
					if (i+1 < end) {
						port= Integer.parseInt(toProcess.substring(i+1, end));
					}
					internalURI.port = port;
					toProcess.delete(0, end);
					if (toProcess.length()==0) {
						internalURI.processed = true;
						internalURI.path = "/";
					}
						
				} else {
					if (i > 0) {
						int end = URLNormalizerUtils.findFirstMatch(toProcess, "/@?", i);
						if ( end > 0 && toProcess.codePointAt(end)=='@') {
							internalURI.userInfo = URLNormalizerUtils.normalizeUserInfo(toProcess, 0, end);
							toProcess.delete(0, internalURI.userInfo.length()+1);
							break;
						}
						String h = URLNormalizerUtils.numToIPv4(toProcess, 0, i);
						if (h == null) {
							h = URLNormalizerUtils.dnsToIDN(toProcess, 0, i);
						}
						internalURI.host = h;
						toProcess.delete(0, h.length());
						break;
					}
					int u = URLNormalizerUtils.findFirstMatch(toProcess, "@",i);
					if (u < i) {
						throw new URLMalformedInputException("Cannot process userinfo");
					}
					internalURI.userInfo = URLNormalizerUtils.normalizeUserInfo(toProcess, 0,u);
					toProcess.delete(0, internalURI.userInfo.length()+1);
				}
				break;
			default:
				if (internalURI.host != null) {
					throw new URLMalformedInputException("Cannot process URL:"+toProcess.toString());
				}
				break;
			}
		
		processURL();
		
	}
	private void checkHostAndPath(int i) throws URLHostParseException {
		if ( internalURI.host == null ) { 
			internalURI.host = buildHostname(i);
			toProcess.delete(0, internalURI.host.length());
		}
		if ( internalURI.path == null) {
			internalURI.path = "/";
		}
	}
	private String buildHostname(int m) throws URLHostParseException {
		String host = null;
		try{
			host = URLNormalizerUtils.decodeIPv4(toProcess, 0, m);
		} catch (URLHostParseException e) {
			LOG.debug("Not an IPv4:'{}', trying DNS.",toProcess.toString());
		}
		if ( host == null) {
			host = URLNormalizerUtils.dnsToIDN(toProcess, 0, m);
		}
		if ( host == null)
			throw new URLHostParseException("Cannot process host part from:"+original);
		return host;
	}
	
	//access for tests only
	void processSchemeOrHost() throws URLMalformedInputException, URLHostParseException {
		LOG.debug("Extracting scheme from input: {}",toProcess.toString());
		if (toProcess.length() == 0) {
			throw new URLMalformedInputException("URL for processing cannot be empty");
		}
		int i = URLNormalizerUtils.findFirstMatch(toProcess, ":[]@./?#", 0);
		if (i == 0 && !(toProcess.codePointAt(i) == '[' || toProcess.codePointAt(i) == ':' || toProcess.codePointAt(i) == '.') ) {
			throw new URLMalformedInputException();
		}
		// numeric IP or hostname
		if (i < 0) { 
			String ip = null;
			try {
				ip = URLNormalizerUtils.decodeIPv4(toProcess,0,toProcess.length());
			} catch (URLHostParseException e) {
				//ignore
			}
			this.internalURI = new URI();
			if (ip == null) {
				ip = URLNormalizerUtils.dnsToIDN(toProcess);
			}
			internalURI.scheme=DEFAULT_SCHEMA;
			internalURI.isUrl = true;
			internalURI.host = ip;
			internalURI.path = "/";
			internalURI.processed = true;
			return;
		}
		
		switch(toProcess.codePointAt(i)) {
		case ':' :
			if (i > 0) {
				internalURI = new URI(toProcess,i);
			}
			else {
				internalURI = new URI();
				internalURI.scheme = DEFAULT_SCHEMA;
				internalURI.isUrl = true;
				internalURI.host = URLNormalizerUtils.decodeIPv6(toProcess);
				if (toProcess.length() == toProcess.indexOf("]")+1) {
					internalURI.processed = true;
					internalURI.path="/";
				} else {
					int rem = toProcess.indexOf("]");
					toProcess.delete(0, rem+1);
				}
			}
			break;


		case '[':
			int clBr = URLNormalizerUtils.findFirstMatch(toProcess, "]", i);
			if (i > 0 || clBr < 0) {
				throw new URLMalformedInputException("Cannot find matched bracket for IPv6");
			}
			StringBuilder sb = new StringBuilder(toProcess.substring(i, clBr+1));
			if (internalURI == null)
				internalURI = new URI();
			internalURI.host = URLNormalizerUtils.decodeIPv6(sb);
			internalURI.scheme = DEFAULT_SCHEMA;
			internalURI.isUrl = true;
			if (toProcess.indexOf("]") == toProcess.length()-1) {
				internalURI.path = "/";
				internalURI.processed = true;
			} else {
				int rem = toProcess.indexOf("]");
				toProcess.delete(i, rem+1);
			}
			break;
		
		
		case '@' :
			internalURI = new URI();
			internalURI.scheme = DEFAULT_SCHEMA;
			try {
				internalURI.userInfo = URLNormalizerUtils.normalizeUserInfo(toProcess,0,i);
			}catch (URIException e) {
				throw new URLMalformedInputException("Cannot process userinfo:"+toProcess.substring(0,i),e);
			}
			toProcess.delete(0,i+1);
			break;
		
		
		case '.' :
			String delim = "/?#:";
			int hEnd = URLNormalizerUtils.findFirstMatch(toProcess, delim, i);
			if (hEnd < 0) {
				hEnd = toProcess.length();
			}
			String enc ;
			if (URLNormalizerUtils.dnsEnd.matcher(toProcess.substring(0, hEnd)).matches() || toProcess.codePointAt(hEnd-1) == '.') {
				enc = URLNormalizerUtils.dnsToIDN(toProcess,0,hEnd);
			} else  {
				enc = URLNormalizerUtils.decodeIPv4(toProcess, 0, hEnd);
			}
			internalURI = new URI();
			internalURI.scheme = DEFAULT_SCHEMA;
			internalURI.isUrl = true;
			internalURI.host = enc;
			int rem = URLNormalizerUtils.findFirstMatch(toProcess, delim, 0);
			if ( rem < 0) {
				internalURI.path = "/";
				internalURI.processed = true;
			}
			else {
				toProcess.delete(0, rem);
			}
			break;
//		either numericIP or hostname with path
		case '/':
			String ip = null;
			if (i < 2 || (toProcess.length() > i+1 && toProcess.codePointAt(i+1) == '/') ) {
				throw new URLMalformedInputException("Cannot process URL:"+toProcess.toString());
			}
			try {
				ip = URLNormalizerUtils.decodeIPv4(toProcess,0,i);
			} catch (URLHostParseException e) {
				//ignore
			}
			if (this.internalURI == null) {
				this.internalURI = new URI();
			}
			if (ip == null) {
				ip = URLNormalizerUtils.dnsToIDN(toProcess,0,i);
			}
			internalURI.scheme=DEFAULT_SCHEMA;
			internalURI.isUrl = true;
			internalURI.host = ip;
			int toDel = toProcess.indexOf("/");
			toProcess.delete(0, toDel);
			break;
		default:
			throw new URLHostParseException("Cannot normalize:"+toProcess.toString());
				
		}	
		
	}
	
	
	
	
	public static class URI {
		public boolean processed;
		boolean isUrl = false;
		URI() { }
		URI(StringBuilder sb, int end) {
// 			according to RFC scheme part is case-insensitive but is different from datacontract.
//			this.scheme = sb.substring(0,div).toLowerCase();
			this.scheme = URLNormalizerUtils.removeObfuscatedEncoding(sb,0,end, new EncodingType[] {EncodingType.SCHEME_ALLOWED});
			int delIndx = this.scheme.length();
			if (scheme.equalsIgnoreCase(DEFAULT_SCHEMA) || scheme.equalsIgnoreCase("https")) {
				scheme = scheme.toLowerCase();
				sb.delete(0, delIndx+1);
				isUrl  = true;
			}
			else {
				processed = true;
				this.hierPart = new StringBuilder( sb.subSequence(delIndx+1, sb.length()));
			}
			
		}
		String scheme;
		StringBuilder hierPart;
		String path;
		String host;
		String userInfo;
		int port =-1;
		String query;
		String fragment;
		public String getURIasString() {
			if (!processed) {
				return null;
			}
			if (scheme.equalsIgnoreCase(DEFAULT_SCHEMA) || scheme.equalsIgnoreCase("https")) {
				StringBuilder sb = new StringBuilder();
				sb.append(scheme).append("://");
				if (userInfo != null) {
					sb.append(userInfo).append("@");
				}
				sb.append(host);
				if (port > 0) {
					sb.append(":").append(port);
				}
				sb.append(path);
				if (query != null) {
					sb.append(query);
				}
				if (fragment != null) {
					sb.append(fragment);
				}
				return sb.toString();
			}
			return scheme+":"+hierPart.toString();
		}
		
		
	}

}