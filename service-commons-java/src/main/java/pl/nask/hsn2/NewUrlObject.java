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

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;

public class NewUrlObject {
    private static final Logger LOG = LoggerFactory.getLogger(NewUrlObject.class);

    private String originalUrl;
    private URI uri;
    private String origin;
    private final String type;

    public NewUrlObject(String url, String origin) throws URIException {
        this(url, origin, null);
    }

    public NewUrlObject(String url, String origin, String type) throws URIException {
        this.originalUrl = url;
        this.origin = origin;
        this.type = type;
        String scheme = getSchemeFromUri();
		if (scheme != null) {
			if (scheme.equals("https")) {
				this.uri = new HttpsURL(originalUrl,Charset.forName("UTF-8").name());//Default charset is UTF-8 but this isn't handled correctly in getters methods!!!
			} else if (scheme.equals("http")) {
				this.uri = new HttpURL(originalUrl,Charset.forName("UTF-8").name());
			} else {
				this.uri = new URI(originalUrl, false,Charset.forName("UTF-8").name());
			}
		} else {
			throw new URIException("Can't find scheme (protocol name).");
		}
    }

    private String getSchemeFromUri() {
		Pattern pattern = Pattern.compile("^([a-zA-Z][a-zA-Z\\-_].+?):");
		Matcher matcher = pattern.matcher(originalUrl);
		String scheme = null;
		if (matcher.find()) {
			scheme = matcher.group(1);
		}
		return scheme;
    }

    public String getDomain() {
        if (uri.isHostname() || uri.isRegName()) {
            return getHost(uri);
        } else {
            return null;
        }
    }

    public String getIp() {
        if (uri.isIPv4address() || uri.isIPv6reference()) {
            return getHost(uri);
        } else {
            return null;
        }
    }

    private String getHost(URI uri) {
        	if(!uri.getProtocolCharset().equals("UTF-8")) {
        		LOG.warn("Encoding should be set to UTF-8");
        		return null;
        	}
            return new String(uri.getRawHost());
    }

    /**
     *
     * @return type of resource this object represents (url, swf, pdf)
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return origin of this object (feeder, web client, 'some other service')
     */
    public String getOrigin() {
        return origin;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public boolean usesIDN() {
        return uri.isRegName();
    }

    @Override
    public String toString() {
        return "NewUrlObject [url=" + uri + "]";
    }

    public boolean usesIPv4() {
        return uri.isIPv4address();
    }

    public boolean usesIPv6() {
        return uri.isIPv6reference();
    }

	public ObjectData asDataObject(Long parentId) {
		ObjectDataBuilder objectBuilder = new ObjectDataBuilder();
		objectBuilder.addStringAttribute("type", "url");
		objectBuilder.addStringAttribute("url_original", getOriginalUrl());
		objectBuilder.addTimeAttribute("creation_time", System.currentTimeMillis());
        objectBuilder.addStringAttribute("origin", getOrigin());
        if (parentId != null) {
        	objectBuilder.addObjAttribute("parent", parentId);      
        }
        if (getType() != null) {
        	objectBuilder.addStringAttribute("type", getType());
        }
      return objectBuilder.build();      
	}
}
