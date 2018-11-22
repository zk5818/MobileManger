package com.zhy.http.okhttp.https;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * Created by Administrator on 2017/1/4.
 */

public class HttpHeaders {
    private static final String TOKEN = "([^ \"=]*)";
    private static final String QUOTED_STRING = "\"([^\"]*)\"";
    private static final Pattern PARAMETER = Pattern.compile(" +([^ \"=]*)=(:?\"([^\"]*)\"|([^ \"=]*)) *(:?,|$)");

    private HttpHeaders() {
    }

    public static long contentLength(Response response) {
        return contentLength(response.headers());
    }

    public static long contentLength(Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }

    private static long stringToLong(String s) {
        if(s == null) {
            return -1L;
        } else {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException var2) {
                return -1L;
            }
        }
    }

    public static boolean varyMatches(Response cachedResponse, Headers cachedRequest, Request newRequest) {
        Iterator var3 = varyFields(cachedResponse).iterator();

        String field;
        do {
            if(!var3.hasNext()) {
                return true;
            }

            field = (String)var3.next();
        } while(Util.equal(cachedRequest.values(field), newRequest.headers(field)));

        return false;
    }

    public static boolean hasVaryAll(Response response) {
        return hasVaryAll(response.headers());
    }

    public static boolean hasVaryAll(Headers responseHeaders) {
        return varyFields(responseHeaders).contains("*");
    }

    private static Set<String> varyFields(Response response) {
        return varyFields(response.headers());
    }

    public static Set<String> varyFields(Headers responseHeaders) {
        Object result = Collections.emptySet();
        int i = 0;

        for(int size = responseHeaders.size(); i < size; ++i) {
            if("Vary".equalsIgnoreCase(responseHeaders.name(i))) {
                String value = responseHeaders.value(i);
                if(((Set)result).isEmpty()) {
                    result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
                }

                String[] var5 = value.split(",");
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String varyField = var5[var7];
                    ((Set)result).add(varyField.trim());
                }
            }
        }

        return (Set)result;
    }

    public static Headers varyHeaders(Response response) {
        Headers requestHeaders = response.networkResponse().request().headers();
        Headers responseHeaders = response.headers();
        return varyHeaders(requestHeaders, responseHeaders);
    }

    public static Headers varyHeaders(Headers requestHeaders, Headers responseHeaders) {
        Set varyFields = varyFields(responseHeaders);
        if(varyFields.isEmpty()) {
            return (new Headers.Builder()).build();
        } else {
            Headers.Builder result = new Headers.Builder();
            int i = 0;

            for(int size = requestHeaders.size(); i < size; ++i) {
                String fieldName = requestHeaders.name(i);
                if(varyFields.contains(fieldName)) {
                    result.add(fieldName, requestHeaders.value(i));
                }
            }

            return result.build();
        }
    }

    public static List<Challenge> parseChallenges(Headers responseHeaders, String challengeHeader) {
        ArrayList challenges = new ArrayList();
        List authenticationHeaders = responseHeaders.values(challengeHeader);
        Iterator var4 = authenticationHeaders.iterator();

        while(true) {
            while(true) {
                String header;
                int index;
                do {
                    if(!var4.hasNext()) {
                        return challenges;
                    }

                    header = (String)var4.next();
                    index = header.indexOf(32);
                } while(index == -1);

                Matcher matcher = PARAMETER.matcher(header);

                for(int i = index; matcher.find(i); i = matcher.end()) {
                    if(header.regionMatches(true, matcher.start(1), "realm", 0, 5)) {
                        String scheme = header.substring(0, index);
                        String realm = matcher.group(3);
                        if(realm != null) {
                            challenges.add(new Challenge(scheme, realm));
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void receiveHeaders(CookieJar cookieJar, HttpUrl url, Headers headers) {
        if(cookieJar != CookieJar.NO_COOKIES) {
            List cookies = Cookie.parseAll(url, headers);
            if(!cookies.isEmpty()) {
                cookieJar.saveFromResponse(url, cookies);
            }
        }
    }

    public static boolean hasBody(Response response) {
        if(response.request().method().equals("HEAD")) {
            return false;
        } else {
            int responseCode = response.code();
            return (responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304?true:contentLength(response) != -1L || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"));
        }
    }

    public static int skipUntil(String input, int pos, String characters) {
        while(pos < input.length() && characters.indexOf(input.charAt(pos)) == -1) {
            ++pos;
        }

        return pos;
    }

    public static int skipWhitespace(String input, int pos) {
        while(true) {
            if(pos < input.length()) {
                char c = input.charAt(pos);
                if(c == 32 || c == 9) {
                    ++pos;
                    continue;
                }
            }

            return pos;
        }
    }

    public static int parseSeconds(String value, int defaultValue) {
        try {
            long e = Long.parseLong(value);
            return e > 2147483647L?2147483647:(e < 0L?0:(int)e);
        } catch (NumberFormatException var4) {
            return defaultValue;
        }
    }

}
