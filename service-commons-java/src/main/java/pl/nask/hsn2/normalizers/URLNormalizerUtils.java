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

import java.net.IDN;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class URLNormalizerUtils {

    private URLNormalizerUtils() {
    }
    private static final String DEFAULT_CHARSET = Charset.forName("UTF-8").name();
	private static final Logger LOG = LoggerFactory.getLogger(URLNormalizerUtils.class);
    public static final Pattern ipv4;
    public static final Pattern dnsEnd;
    public static final Pattern ipv6;
    public static final Pattern ipv6v1;
    public static final Pattern ipv6v4normalized;
    public static final Pattern percentEnc;
    public static final Pattern percentEncWithIIS;
    private static int ignoreIISenc = 0;

    static {
        ipv4 = Pattern.compile("(?:0x[a-fA-F0-9]{1,2}\\.|0*[0-7]{1,3}\\.|0*[0-9]{1,3}\\.){3}(?:0x[a-fA-F0-9]{1,2}|0+[0-7]{1,3}|0*[0-9]{1,3}){1}(?::[0-9]{1,5})*");
        dnsEnd = Pattern.compile("(?:.+\\.[a-zA-Z]{2,}$)|[_a-zA-z0-9\\-\\.]+[a-zA-Z\\-]+$");
        ipv6 = Pattern.compile("^\\[(?:[a-fA-F0-9]{1,4}:)+:*(?:[a-fA-F0-9]{1,4}:)*(?:[a-fA-F0-9]{1,4}]$|[a-fA-F0-9]{1,4}]:[0-9]{1,5}$)");//RFC 2732,2373
        ipv6v1 = Pattern.compile("^\\[:*(?:[a-fA-F0-9:]*)(?:]$|]:[0-9]{1,5}$)");
//        ipv6v4 = Pattern.compile("^\\[:*(?:[a-fA-F0-9]{1,4}:*){1,4}(?:[0-9]{1,3}\\.){3}(?:[0-9]{1,3}]$|[0-9]{1,3}]:[0-9]{1,5}$)");
        ipv6v4normalized = Pattern.compile("^\\[(?:[0]:){5}[0,ffff,FFFF]+:(?:[0-9]{1,3}\\.){3}(?:[0-9]{1,3}]$)");
        percentEncWithIIS = Pattern.compile("(?:%u[0-9a-fA-F]{4}).*");
        percentEnc = Pattern.compile("^(?:%[0-9a-fA-F]{2}).*");


    }

    public static String numToIPv4(StringBuilder host) {
        int i = findFirstMatch(host, ":/?", 0);
        if (i < 0) {
            i = host.length();
        }
        return numToIPv4(host, 0, i);
    }

    public static String numToIPv4(StringBuilder sb, int start, int endInd) {
        int end = findFirstMatch(sb, ":/?", start, endInd);
        if (end < 0) {
            end = endInd;
        }
        long ip;
        try {
            ip = Long.decode(sb.substring(start, end));
            if ( ip <= 0xffffff) { //disallow 0.x.x.x format
            	return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            tmp.insert(0, ip & 0xff);
            tmp.insert(0, '.');
            ip = ip >> 8;
        }
        tmp.insert(0, ip & 0xff);
        sb.replace(start, end, tmp.toString());
        return tmp.toString();

    }

    public static String dnsToIDN(StringBuilder sb) throws URLHostParseException {
        return dnsToIDN(sb, 0, sb.length());
    }

    public static String dnsToIDN(StringBuilder str, int startIndx, int endIndx) throws URLHostParseException {
    	if (str.length() == 0 || startIndx == endIndx) {
    		throw new URLHostParseException("Cannot process empty string");
    	}
        if (endIndx > str.length()) {
            endIndx = str.length();
        }
        int dd = str.indexOf("..",startIndx);
        if (dd >= 0 && dd < endIndx) {
        	throw new URLHostParseException("Sequence '..' is forbidden in DNS: '"+str.substring(startIndx,endIndx)+"'");
        }
       
        String host;
        try {
            host = IDN.toASCII(str.substring(startIndx, endIndx), IDN.USE_STD3_ASCII_RULES & IDN.ALLOW_UNASSIGNED);
            if (host.startsWith(".")) {
            	throw new URLHostParseException("DNS name cannot start with .");
            }
        } catch (IllegalArgumentException e) {
            throw new URLHostParseException("Error converting DNS:" + str.toString(), e);
        }

        if (host.length() == str.length() && !EncodingType.DNS_ALLOWED.allowed(host)) {
            throw new URLHostParseException("Error converting DNS: " + str.toString());
        }

        if (host.length() == 0) {
            return host;
        }
        host = host.toLowerCase();
        
        while (host.endsWith(".")) {
        	host = host.substring(0, host.length()-1);
        }
        
        str.replace(startIndx, endIndx, host);
        return host;
    }

    public static int findFirstMatch(StringBuilder sb, String chars, int begIndex) {
        return findFirstMatch(sb, chars, begIndex, sb.length());
    }

    public static int findFirstMatch(StringBuilder sb, String chars, int startIndex, int endIndex) {
        if (sb.length() < endIndex) {
            endIndex = sb.length();
        }
        if (startIndex >= endIndex) {
            return -1;
        }
        char tab[] = chars.toCharArray();
        int first = endIndex;
        for (int i = 0; i < tab.length; i++) {
            for (int j = startIndex; j < first; j++) {
                if (sb.codePointAt(j) == tab[i]) {
                    first = j;
                }
            }
        }
        if (first < endIndex) {
            return first;
        }
        return -1;
    }

    public static int findFirstMatch(StringBuilder sb, String[] ss, int startIndex) {
        return findFirstMatch(sb, ss, startIndex, sb.length());
    }

    public static int findFirstMatch(StringBuilder sb, String[] ss, int startIndex, int endIndex) {
        int first = endIndex;
        for (int i = 0; i < ss.length; i++) {
            int tmp = sb.indexOf(ss[i], startIndex);
            if (tmp >= 0 && tmp < first && tmp + ss[i].length() < endIndex) {
                first = tmp;
            }
        }
        if (first < endIndex) {
            return first;
        }
        return -1;
    }

    public static int findLastMatch(StringBuilder sb, String chars, int startIndex) {
        return findLastMatch(sb, chars, startIndex, sb.length());

    }

    /**
     * @param sb
     * @param chars
     * @param startIndex
     * @param endIndex - it's not included in search!!
     * @return the last index or (-1) if none of the chars were found
     */
    public static int findLastMatch(StringBuilder sb, String chars,
            int startIndex, int endIndex) {
        if (endIndex > sb.length() || startIndex > sb.length()) {
            return -1;
        }
        char ch[] = chars.toCharArray();
        int last = startIndex - 1;
        for (char c : ch) {
            int tmp = endIndex - 1;
            while (tmp > last) {
                if (sb.codePointAt(tmp) == c) {
                    last = tmp;
                    break;
                }
                tmp--;
            }

        }
        if (last < startIndex) {
            return -1;
        }
        return last;
    }
    
    public static int findLastMatch(String s,String chars, int startInd, int endInd) {
    	if (endInd - startInd < 1 || endInd > s.length() || startInd  < 0) {
    		return -1;
    	}
    	int found = -1 ;
    	for ( int i = endInd -1;i >= startInd; i--) {
    		if (chars.indexOf(s.charAt(i)) >=0) {
    			found = i;
    			break;
    		}
    	}
    	return found;
    }

    public static boolean processIISenc() {
        return ignoreIISenc == 0;
    }

    public static void processIISenc(boolean ignoreIIS) {
        ignoreIISenc = ignoreIIS ? 0 : 1;
    }

    public static void removeAllObfuscatedEncoding(StringBuilder sb) {
        removeObfuscatedEncoding(sb, new EncodingType[]{EncodingType.ALL_ASCII});
    }

    public static String removeObfuscatedEncoding(StringBuilder sb, int startInd, int endInd, EncodingType[] type) {
        StringBuilder nsb = new StringBuilder(sb.subSequence(startInd, endInd));
        removeObfuscatedEncoding(nsb, type);
        sb.replace(startInd, endInd, nsb.toString());
        return nsb.toString();
    }

    public static String removeObfuscatedEncoding(StringBuilder sb, EncodingType[] type) {
        if (type == null) {
            return sb.toString();
        }

        for (EncodingType t : type) {
            if (t == EncodingType.PLUS_AS_SPACE) {
                normalizeSpaceEncoding(sb);
            } else if (t == EncodingType.IIS_ENC) {
                removeIISenc(sb);
            } else {
                int i = sb.indexOf("%");
                while (i >= 0) {
                    if (i + 3 <= sb.length()) {
                        try {
                            int v = Integer.parseInt(sb.substring(i + 1, i + 3), 16);
                            if (t.allowed(v)) {
                                sb.replace(i, i + 3, Character.toString((char) v));
                            }
                        } catch (NumberFormatException e) {
                            LOG.debug(e.getMessage());
                        }
                    }
                    i = sb.indexOf("%", i + 1);
                }
            }
        }
        return sb.toString();
    }

    static String removeIISenc(StringBuilder sb) {
        int i = -1;
        String[] pattern = new String[]{"%u"};
        while ((i = findFirstMatch(sb, pattern, i + 1)) >= 0) {
            try {
                Long v = Long.parseLong(sb.substring(i + 2, i + 6), 16);
                char c = (char) v.intValue();
                sb.delete(i, i + 6);
                sb.insert(i, c);
            } catch (NumberFormatException e) {
            }
        }
        return sb.toString();
    }

    static String normalizeSpaceEncoding(StringBuilder sb) {
        int i = -1;
        while ((i = findFirstMatch(sb, new String[]{"+"}, i + 1)) >= 0) {
           
                sb.replace(i, i + 1, " ");
           
        }
        return sb.toString();
    }
    
    
    
    private static int getIPv6EndIndex(StringBuilder sb) throws URLHostParseException {
    	int beg,end;
    	if (findFirstMatch(sb, "[]", 0) >= 0) {
    		beg = sb.indexOf("[");
    		end = sb.indexOf("]");
    		if (beg * end < 0 || beg >= end) {
    			throw new URLHostParseException("Brackets '[]' dont match" + sb.toString());
    		}
    		if (end < 0) {
    			end = sb.length();
    		}
    	} else {
    		beg = -1;
    		int tmp = findFirstMatch(sb, "/?#", 1);
    		if (tmp > 0) {
    			end = tmp;
    		} else {
    			end = sb.length();
    		}
    	}
    	int dc = sb.indexOf("::", beg +1);
    	if ( dc > end -2) {
    		dc = -1;
    	}
    	String t[] = sb.substring(beg+1,end).split(":");
    	if ( t[0].isEmpty() && dc != beg+1) {
    		throw new URLHostParseException("Incorrect IPv6:"+sb.substring(beg+1, end));
    	}
    	if ( dc == -1 ) {
    		int dot = findFirstMatch(sb, ".", beg+1,end);
    		if ( dot ==-1 && t.length != 8) { 
    			throw new URLHostParseException();
    		}
    	}
    	return end;
    }
    
    
    public static String decodeIPv6(StringBuilder sb) throws URLHostParseException, URLMalformedInputException {

        Stack<Long> values = new Stack<Long>();
        
        int endSq = getIPv6EndIndex(sb);
        int i = findFirstMatch(sb, "[", 0,endSq);
        int begSq = i >= 0 ? i : 0;
        while (true) {
        	int prev = i;
        	i = findFirstMatch(sb, ":]/.", prev + 1);
        	try {
        		if (i - prev < 2) {
        			if ( i < 0 && prev +1 < endSq) {
        				values.push(Long.parseLong(sb.substring(prev + 1, endSq), 16));
        			}
        			break;
        		}
        		if (sb.codePointAt(i) == '.') {
        			i = prev;
        			break;
        		}
        		values.push(Long.parseLong(sb.substring(prev + 1, i), 16));
        	} catch (NumberFormatException e) {
                LOG.warn("cannot parse IPv6 ");
                LOG.warn(e.getMessage());
                throw new URLHostParseException("Cannot parse IPv6:" + sb.substring(begSq), e);
            }
            if (i == sb.indexOf("::")) {
                break;
            }
        }
        final int parsedValues = values.size();
        int ipv64 = sb.indexOf(".");
        i = sb.indexOf("::") ;
        if ((ipv64 >= 0 && ipv64 < i)) {
        	throw new URLHostParseException("Error in parsing ipv4 in ipv6");
        }
        long[] ipv4tab = null;
        
        i++;
        while (i > 0) {
            int prev = i + 1;
            i = sb.indexOf(":", prev);
//            if (i < 0 && ipv64 > 0) {
//                break;
//            }
            if (i < 0) {
            	if ( ipv64 > 0) {
            		break;
            	}
                i = endSq;
                if ( i - prev < 1) {
                	break;
                }
                try {
                    values.push(Long.parseLong(sb.substring(prev, i), 16));
                } catch (NumberFormatException e) {
                    LOG.warn("cannot parse IPv6 ");
                    LOG.warn(e.getMessage());
                    throw new URLHostParseException("Cannot parse IPv6:" + sb.substring(begSq, endSq), e);
                }
                break;
            }
            try {
                if (i <= endSq) {
                    values.push(Long.parseLong(sb.substring(prev, i), 16));
                }
            } catch (NumberFormatException e) {
                LOG.warn("Cannot parse IPv6 " + e.getMessage());
                LOG.warn(e.getMessage());
                throw new URLHostParseException("Cannot parse IPv6:" + sb.substring(begSq, endSq), e);
            }
        }
        if (ipv64 > 0) {
            i = findLastMatch(sb, ":", 0, endSq);
            ipv4tab = decodeIPv4(sb.substring(i + 1, endSq));
            ipv64 = 4;
        } else {
            ipv64 = 0;
        }

        long[] ret = validateAndBuildArray(values, parsedValues, ipv64, ipv4tab);
        
        if (ret[0] > 0xfc00l) {
            LOG.warn("non routable address (ULA) {}", sb.toString());
        }
        StringBuilder retSb = arrayToIPv6(ret, ipv64 > 0);
        i = retSb.indexOf("::");
        if ( i > 0) {
        	if ( retSb.indexOf("ffff") > 0) {
        		retSb.replace(i, i+1, "0:0:0:0:0");
        	} else {
        		retSb.replace(i, i+1, "0:0:0:0:0:0");
        	}
        }
        	
        if (endSq == URLNormalizerUtils.findFirstMatch(sb, "]/", 0)) {
            endSq++;
        }
        sb.replace(begSq, endSq, retSb.toString().toLowerCase());
        return retSb.toString();
    }

	private static long[] validateAndBuildArray(Stack<Long> values, final int count, int ipv64, long[] ipv4tab) throws URLMalformedInputException {
		long[] ret = new long[8];
        for (int i = 0; i < 8; i++) {
            if (ipv64 > 0 && i > 3) {
                ret[i] = ipv4tab[i - 4];
                continue;
            }
            if (i < count) {
                ret[i] = values.elementAt(i);
            } else {
                if (i < 8 - values.size() + count - ipv64) {
                    ret[i] = 0l;
                } else {
                    ret[i] = values.elementAt(i - (8 - values.size() - ipv64));
                }
            }
        }
        if ( count > 4 && ipv64 != 0) {
        	if ( values.size() != 6)
        		throw new URLMalformedInputException("Too long ipv4 in ipv6");
        	for (int i=0; i < values.size(); i++) {
        		if ( i < count -1 && values.elementAt(i) != 0) {
        			throw new URLMalformedInputException("Incorrect ipv4 in ipv6");
        		} else if ( i == count -1) {
        			ret[3] = values.elementAt(count -1 );
        		} 
        	}
        }
		return ret;
	}

    /**
     * @param sb
     * @return
     * @throws URLHostParseException
     */
    public static String decodeIPv4(StringBuilder sb, int startIndx, int endIndx) throws URLHostParseException {
        long[] ip = decodeIPv4(sb.substring(startIndx, endIndx));
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < ip.length; i++) {
            str.append(ip[i]);
            if (i < ip.length - 1) {
                str.append(".");
            }
        }
        sb.replace(startIndx, endIndx, str.toString());
        return str.toString();

    }

    public static long[] decodeIPv4(String st) throws URLHostParseException {
        long[] ipv4Tab = new long[4];
        int i = 0;
        String[] val = Pattern.compile("\\.").split(st);

        for (String str : val) {
            try {
                ipv4Tab[i++] = Long.decode(str);
            } catch (NumberFormatException e) {
                throw new URLHostParseException("Cannot decode IP:" + str, e);
            }
        }
        if (--i < 3) {
            long ip = ipv4Tab[i];
            int indx = 3;
            while (i++ < 4) {
                ipv4Tab[indx--] = ip % 256l;
                ip /= 256l;
            }
        }
        if (ipv4Tab[0] == 10
                || (ipv4Tab[0] == 192 && ipv4Tab[1] == 168)
                || (ipv4Tab[0] == 172 && ipv4Tab[1] >= 16 && ipv4Tab[1] <= 31)) {
            LOG.warn("non routable IPv4 address: {}.{}.{}.{}", new Object[]{ipv4Tab[0], ipv4Tab[1], ipv4Tab[2], ipv4Tab[3]});
        }
        if ( ipv4Tab[0] == 0) {
        	throw new URLHostParseException("IPv4 in format 0.x.x.x is  incorrect");
        }
        return ipv4Tab;
    }

    private static StringBuilder arrayToIPv6(long[] octets, boolean lastAsIPv4) throws URLHostParseException {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        while (octets[i] == 0l) {
            i++;
        }

        switch (i) {
            case 3:
            case 4:
                if (lastAsIPv4) {
                    sb.append("::");
                } else {
                    i = 0;
                }
                break;
            case 5:
            	if (octets[5] == 0xffffl) {
            		octets[4] = octets[6] >> 8;
                	octets[5] = octets[6] & 0xff;
                	octets[6] = octets[7] >> 8;
            		octets[7] = octets[7] & 0xff;
            		octets[3] = 0xffffl;
            		i = 3;
            		lastAsIPv4 = true;
            		sb.append("::");
            	} else {
                    i = 0;
                }
                break;
            case 6:
            		octets[4] = octets[6] >> 8;
        			octets[5] = octets[6] & 0xff;
        			octets[6] = octets[7] >> 8;
        			octets[7] = octets[7] & 0xff;
        			i = 4;
        			sb.append("::");
        			lastAsIPv4 = true;

                break;
            default:
                i = 0;
                break;
        }
        if ( lastAsIPv4 ) {
        	checkIPv46correctness(octets);
        }
        for (; i < octets.length; i++) {
            if (!lastAsIPv4) {
                sb.append(Long.toHexString(octets[i]))
                        .append(":");
            } else {
                if (i < 4) {
                    sb.append(Long.toHexString(octets[i])).append(":");
                } else {
                    sb.append(Long.toString(octets[i])).append(".");
                }
            }
        }
        i = sb.length();
        sb.replace(i - 1, i, "]");
        return sb;
    }

    private static boolean checkIPv46correctness(long[] octet) throws URLHostParseException {
    	int i = 0;
    	for (; i < 3; i++) {
    		if (octet[i] != 0) {
    			throw new URLHostParseException("Incorrect ipv4 in ipv6");
    		}
    	}
    	if ( !(octet[i] == 0 || octet[i] == 0xffff)) {
    		throw new URLHostParseException("Incorrect ipv4 in ipv6");
    	}
    	i++;
    	for (; i< octet.length; i++) {
    		if (octet[i] > 0xff) {
    			throw new URLHostParseException("Incorrect ipv4 in ipv6");
    		}
    	}
		return true;
		
	}

	public static String normlizePath(StringBuilder pathIn, int startIndx, int endIndx) throws URLParseException {

        int endSq = findFirstMatch(pathIn, "?#", startIndx);
        if (endSq < 0 || endSq > endIndx) {
            endSq = endIndx;
        }
        int endReplace = endSq;
        StringBuilder retPath = new StringBuilder(pathIn.substring(startIndx, endSq));

        String[] strPattern = new String[]{"/./"};

        int dotIndex = findFirstMatch(retPath, strPattern, 0);
        while (dotIndex > 0 && dotIndex < endSq) {
            retPath.delete(dotIndex, dotIndex + 2);
            endSq -= 2;
            dotIndex = findFirstMatch(retPath, strPattern, 0);
        }

        strPattern = new String[]{"/.."};
        while (findFirstMatch(retPath, strPattern, 0) == 0) {
            retPath.delete(0, 3);
            endSq -= 3;
        }

        int iDDot = findFirstMatch(retPath, strPattern, 0, endSq);
        if (iDDot < 0) {
            encodePath(retPath, 0, retPath.length());
            if (retPath.length() == 0 || retPath.codePointAt(0) != '/') {
                retPath.insert(0, '/');
            }
            pathIn.replace(startIndx, endReplace, retPath.toString());
            return retPath.toString();
        }
        int iSS = 0;
        iDDot = retPath.indexOf("/..");

        Stack<String> pathStack = new Stack<String>();
        while (iDDot >= 0) {
            int next = retPath.indexOf("/", iSS + 1);
            while (next <= iDDot && next >= 0) {
                pathStack.push(retPath.substring(iSS, next));
                iSS = next;
                next = retPath.indexOf("/", iSS + 1);

            }
            while (iSS == iDDot) {
                if (!pathStack.isEmpty()) {
                    pathStack.pop();
                }
                iDDot = retPath.indexOf("/..", iSS + 1);

                if (iDDot < 0) {
                    pathStack.push(retPath.substring(iSS + 3));
                    break;
                }
                iSS = retPath.indexOf("/", iSS + 1);
            }
        }

        if (pathStack.isEmpty()) {
            encodePath(retPath, 0, retPath.length());
            if (retPath.codePointAt(0) != '/') {
                retPath.insert(0, '/');
            }
            pathIn.replace(startIndx, endReplace, retPath.toString());
            return retPath.toString();
        } else {
            retPath = new StringBuilder();
            for (int i = 0; i < pathStack.size(); i++) {
                retPath.append(pathStack.elementAt(i));
            }
        }
        if (retPath.length() == 0) {
            retPath.append("/");
        }
        encodePath(retPath, 0, retPath.length());
        if (retPath.indexOf("/") != 0) {
            retPath.insert(0, '/');
        }
        pathIn.replace(startIndx, endReplace, retPath.toString());
        return retPath.toString();
    }
	// '|' char will be percent encoded - browsers don't do this
    public static String normalizeQuery(StringBuilder sb, int startIndx, int endIndx) throws URLMalformedInputException {
    	if (sb.indexOf("?") != 0) {
    		throw new URLMalformedInputException("Query should start with '?'");
    	}
    	int end = findFirstMatch(sb, "#", startIndx);
    	if (end < 0 || end > endIndx) {
    		end = endIndx;
    	}
    	StringBuilder enc = new StringBuilder(sb.substring(startIndx, end));
    	removeObfuscatedEncoding(enc, new EncodingType[] {EncodingType.PLUS_AS_SPACE,EncodingType.QUERY_ALLOWED});
    	int i = findFirstMatch(enc, "%", 0);
    	try {
    		if (i < 0) {
    			String tmp = URIUtil.encodeQuery(enc.toString(),DEFAULT_CHARSET);
    			enc.replace(0, enc.length(), tmp);
    		} else {
    			int start = 0;
    			String tmp = "";
    			while (  (i = findFirstMatch(enc, "%", start)) > 0 ) {
    				if (percentEnc.matcher(enc.substring(i)).matches()) {
    					tmp = URIUtil.encodeQuery(enc.substring(start, i), DEFAULT_CHARSET);
    					enc.replace(start, i, tmp);
    					start = i+1 - (tmp.length()-(i-start));
    				} else {
    					enc.replace(i, i+1, "%25");
    					start = i+1;
    				}
    			}
    			tmp = URIUtil.encodeQuery(enc.substring(start), DEFAULT_CHARSET);
    			enc.replace(start, enc.length(), tmp);
    		}
    	} catch (URIException e) {
    		throw new URLMalformedInputException(e);
    	}
        i = 0;
        while( (i = findFirstMatch(enc, ";", i)) > 0 ) {
        	enc.replace(i, i+1, "&");
        }
        i = 0;
        while( (i = findFirstMatch(enc, "+ ", i)) > 0 ) {
        	enc.replace(i, i+1, "%20");
        } 
        sb.replace(startIndx, end, enc.toString());
        return enc.toString();

    }

    public static String normalizeFragment(StringBuilder sb, int startIndx, int endIndx) throws URLMalformedInputException {
        if (sb.length() == 0 || sb.codePointAt(startIndx) != '#') {
            throw new URLMalformedInputException("Fragment should start with '#'");
        }
        StringBuilder ret = new StringBuilder(sb.substring(startIndx, endIndx));

        sb.replace(startIndx, endIndx, encodeFragment(ret));
        return sb.toString();
    }

    public static String normalizeUserInfo(StringBuilder in, int start, int end) throws URIException {
        int endSq = URLNormalizerUtils.findFirstMatch(in, "@", start, end);
        if (endSq < 0) {
            endSq = end;
        }
        StringBuilder sb = new StringBuilder(in.substring(start, endSq));

        int sep = findFirstMatch(sb, ":", 0);
        String enc = null;
        if (sep > 0) {
            removeObfuscatedEncoding(sb, 0, sep, new EncodingType[]{EncodingType.USERINFO});
            sep = findFirstMatch(sb, ":", 0);
            enc = URIUtil.encodeWithinAuthority(sb.substring(0, sep));
            sb.replace(0, sep, enc);
        } else {
            removeObfuscatedEncoding(sb, new EncodingType[]{EncodingType.USERINFO});
            enc = URIUtil.encodeWithinAuthority(sb.toString());
            sb.replace(0, sb.length(), enc);
        }
        in.replace(start, end, sb.toString());
        return sb.toString();
    }

    static String encodePath(StringBuilder pathIn, int start, int end) throws URLParseException {
        StringBuilder sb = new StringBuilder(pathIn.substring(start, end));
        if (!processIISenc()) {
            removeObfuscatedEncoding(sb, new EncodingType[]{EncodingType.PATH_ALLOWED, EncodingType.IIS_ENC});
        } else {
            removeObfuscatedEncoding(sb, new EncodingType[]{EncodingType.PATH_ALLOWED});
        }
        
        int find = 0;
        while( (find = findFirstMatch(sb, new String[] {"//"}, 0)) >= 0)  {
        	sb.replace(find, find+2, "/");
        }
        String ignore = "%+"; // URIUtils doen't conform to RFC 3986 spec regarding '+' encoding
        find = findFirstMatch(sb, ignore, 0);
        if (find >= 0) {
            ArrayList<Integer> toIgnore = new ArrayList<Integer>();
            while (find >= 0) {
                if (percentEnc.matcher(sb.substring(find)).matches() || sb.codePointAt(find) == '+') {
                    toIgnore.add(find);
                }
                find = findFirstMatch(sb, ignore, find + 1);
            }
            StringBuilder enc = new StringBuilder();
            try {
                if (toIgnore.size() > 0) {
                    int tmpBeg = 0;
                    for (int i = 0; i < toIgnore.size(); i++) {
                        enc.append(URIUtil.encodePath(sb.substring(tmpBeg, toIgnore.get(i)), DEFAULT_CHARSET));
                        tmpBeg = toIgnore.get(i) + 1;
                        enc.append(sb.charAt(toIgnore.get(i)));
                    }
                    enc.append(URIUtil.encodePath(sb.substring(tmpBeg), DEFAULT_CHARSET));
                } else {
                    enc.append(URIUtil.encodePath(sb.toString()));
                }
            } catch (URIException e) {
                LOG.error(e.getMessage(), e);
                throw new URLParseException("Cannot parse path:" + sb.substring(start, end), e);
            }
            pathIn.replace(start, end, enc.toString());
            return enc.toString();
        } else {
            try {
                String enc = URIUtil.encodePath(sb.toString(), DEFAULT_CHARSET);
                pathIn.replace(start, end, enc);
                return enc;
            } catch (URIException e) {
                LOG.error(e.getMessage(), e);
                throw new URLParseException("Cannot parse path:" + sb.substring(start, end), e);
            }

        }
    }
    //Every character is honored in WB, this part is not sent to server, all chars are available via JS window.location.hash variable
    static String encodeFragment(StringBuilder sb) {
        int i = -1;
        URLNormalizerUtils.removeObfuscatedEncoding(sb, new EncodingType [] {EncodingType.FRAGMENT});
        //TODO discuss whether escape '%' char
        while ((i = findFirstMatch(sb, "%", i + 1)) >= 0 ) {
        	if (i+3 > sb.length() || ! percentEnc.matcher(sb.substring(i, i+3)).matches()) {
        		sb.replace(i, i + 1, "%25");
        	}
        }
        return sb.toString();
    }

    public enum EncodingType {

        ALL_ASCII,
        URI_RESERVED(":/?#[]@!$&'()*+,;="),
        SCHEME_ALLOWED("+-."),
        PATH_ALLOWED (".-_~!$&'()*+,;=:@"), //RFC sec.3.3  check '+' coding.
        QUERY_ALLOWED("|"),
        URI_BASE("-.~_"),
        DNS_ALLOWED("-_."),
        PLUS_AS_SPACE,
        IIS_ENC,
        USERINFO("!$^&*()_+`-={}|;"),
        FRAGMENT;
        private BitSet allowed = genAllowed();
        private EncodingType() {
        }

        public boolean allowed(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (!allowed(s.codePointAt(i))) {
                    return false;
                }
            }
            return true;
        }
        private BitSet genAllowed() {
        	BitSet bs = new BitSet(256);
        	for(char c : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()) {
        		bs.set(c);
        	}
        	return bs;
        }

        private EncodingType(String s) {
        	for(char c: s.toCharArray()) {
        		allowed.set(c);
        	}
        }

        public boolean allowed(int c) {
            if (this.ordinal() == EncodingType.ALL_ASCII.ordinal()) {
                return c > 0x1f && c < 0x7b;
            }
            if ( this.ordinal() == EncodingType.FRAGMENT.ordinal()) {
            	return c != '%' && EncodingType.ALL_ASCII.allowed(c);
            	
            }
            return allowed.get(c);
        }

        public boolean allowed(StringBuilder s) {
            for (int i = 0; i < s.length(); i++) {
                if (!this.allowed(s.codePointAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

}