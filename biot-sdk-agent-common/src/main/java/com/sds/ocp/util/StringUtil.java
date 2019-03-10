package com.sds.ocp.util;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 문자열 처리 관련 유틸 클래스
 * 
 * @author 박준수 <trust.park@samsung.com>
 * @since 2010. 3. 5.
 * 
 */
public final class StringUtil {
	
	private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+-]?\\d*(\\.\\d+)?");
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	// FileUtil, CharArrayBuffer
	public static final int CR = 13; // <US-ASCII CR, carriage return (13)>
	public static final int LF = 10; // <US-ASCII LF, linefeed (10)>
	public static final int SP = 32; // <US-ASCII SP, space (32)>
	public static final int HT = 9; // <US-ASCII HT, horizontal-tab (9)>

	public static final String EMPTY_STR = "";
	public static final String MCAST_PREFIX = "MCAST-";


	private StringUtil() {
		throw new AssertionError();
	}

	/**
	 * 문자열이 null 이거나, 공백이면 true 리턴
	 * 
	 * @param s
	 * @return boolean true if text is null or empty, false otherwise
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
	/**
	 * 문자열이 null 이거나, trim 한 문자열이 공백이면 true 리턴
	 * 
	 * @param s
	 * @return boolean true if text is null or empty, false otherwise
	 */
	public static boolean isEmptyWithTrim(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static Object nvl(Object value, String alterStr) {
		if (value == null || EMPTY_STR.equals(value)) {
			return alterStr;
		} else {
			return value;
		}
	}

	public static String nvl(String value) {
		return nvl(value, EMPTY_STR);
	}

	public static String nvl(String value, String alterStr) {
		if (value == null || EMPTY_STR.equals(value)) {
			return alterStr;
		} else {
			return value;
		}
	}

	/**
	 * @brief regular expression 을 사용하지 않고, <br/>
	 *        단순히 delimiter 에 의해 split 을 하는 경우 사용
	 * @note regular expression 을 사용하는 것보다 성능이 좋음
	 * @param text
	 *            : 전체 문자열
	 * @param delim
	 *            : 구분자 문자열
	 * @return 리턴 값은 null 이 아님. <br/>
	 *         text 가 null 이거나 공백인 경우, element 가 0 인 List 리턴.<br/>
	 *         그 외에는 반드시 1개 이상의 element 를 가진 List 리턴. <br/>
	 *         <b>구분자 사이에 문자열이 없는 경우에도 공백 문자열 항목으로 리스트에 포함</b>
	 */
	public static List<String> splitSimple(String text, String delim) {
		List<String> list = new ArrayList<String>();

		if (text == null || EMPTY_STR.equals(text)) {
			return list;
		}

		int startPos = 0;
		int endPos;
		while (true) {
			endPos = text.indexOf(delim, startPos);
			if (endPos >= 0) {
				list.add(text.substring(startPos, endPos));
				startPos = endPos + delim.length();
			} else {
				// last item
				list.add(text.substring(startPos));
				break;
			}
		}
		return list;
	}

	/**
	 * @brief regular expression 을 사용하지 않고, <br/>
	 *        단순히 delimiter 에 의해 split 을 하는 경우 사용
	 * @note regular expression 을 사용하는 것보다 성능이 좋음
	 * @param text
	 *            : 전체 문자열
	 * @param delim
	 *            : 구분자
	 * @return 리턴 값은 null 이 아님. <br/>
	 *         text 가 null 이거나 공백인 경우, element 가 0 인 List 리턴.<br/>
	 *         그 외에는 반드시 1개 이상의 element 를 가진 List 리턴. <br/>
	 *         <b>구분자 사이에 문자열이 없는 경우에도 공백 문자열 항목으로 리스트에 포함</b>
	 */
	public static List<String> splitSimple(String text, char delim) {
		return splitSimple(text, String.valueOf(delim));
	}

	/**
	 * @brief regular expression 을 사용하지 않고, <br/>
	 *        startDelim 과 endDelim 사이의 문자열을 추출
	 * @param text
	 * @param startDelim
	 *            : 시작 구분자
	 * @param endDelim
	 *            : 종료 구분자
	 * @return
	 */
	public static List<String> splitBetween(String text, String startDelim, String endDelim) {
		if (text == null || EMPTY_STR.equals(text)) {
			return null;
		}

		List<String> list = new ArrayList<String>();
		int startPos = 0;
		int endPos = 0;
		while (true) {

			startPos = text.indexOf(startDelim, endPos);
			if (startPos < 0) {
				break;
			}
			endPos = text.indexOf(endDelim, startPos + startDelim.length());
			if (endPos > 0) {
				list.add(text.substring(startPos + startDelim.length(), endPos));
			} else {
				// LOG.warn("Invalid start-end pair");
				break;
			}
			endPos = endPos + endDelim.length();
		}
		return list;
	}


	public static boolean isNumeric(String s) {
		return NUMERIC_PATTERN.matcher(s).matches();
	}

	public static boolean isBigdecimalZero(String s) {
		BigDecimal zero = BigDecimal.ZERO;
		BigDecimal b = new BigDecimal(s);
		if (b.compareTo(zero) == 0) {
			return true;
		}

		return false;
	}

    /**
     * <p>Checks if the CharSequence contains only Unicode letters or digits.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = false
     * StringUtils.isAlphanumeric("  ")   = false
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains letters or digits,
     *  and is non-null
     */
    public static boolean isAlphanumeric(final String cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphanumericUnderscore(final String cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i)) && '_' != cs.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
	/**
	 * Replace all occurences of a substring within a string with another string.
	 * 
	 * @param str
	 *            String to examine
	 * @param oldPattern
	 *            String to replace
	 * @param newPattern
	 *            String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String str, String oldPattern, String newPattern) {
		if (!(str != null && str.length() > 0) || !(oldPattern != null && oldPattern.length() > 0)
				|| newPattern == null) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = str.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(str.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = str.indexOf(oldPattern, pos);
		}
		sb.append(str.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	/**
	 * Delete all occurrences of the given substring.
	 * 
	 * @param str
	 *            the original String
	 * @param pattern
	 *            the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(String str, String pattern) {
		return replace(str, pattern, EMPTY_STR);
	}

	/**
	 * CAMELCASE 형태로 변경 <br/>
	 * 단, _ 문자가 없는 경우 변환하지 않으므로 .toLowerCase() 한 문자열을 넣을 것
	 * 
	 * @param str
	 * @return
	 */
	public static String convCamelCaseString(String str) {
		if (str.indexOf('_') == -1 && str.indexOf('-') == -1) {
			return str;
		}
		String str2 = str.replace('-', '_');
		str2 = str2.replace(' ', '_');
		String[] values = str2.split("_");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			// 첫 단어는 전부 소문자
			// 다음 단어부터는 첫 글자만 대문자, 나머지는 소문자
			String word = values[i];
			if (word.isEmpty()) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append(word.toLowerCase(Locale.getDefault()));
			} else {
				sb.append(word.substring(0, 1).toUpperCase(Locale.getDefault()));
				sb.append(word.substring(1).toLowerCase(Locale.getDefault()));
			}
		}

		return sb.toString();
	}

	/**
	 * UNDERSCORE 형태로 변경
	 * 
	 * @param str
	 * @return
	 */
	public static String convUnderscoreString(String str) {
		if (StringUtil.isEmpty(str)) {
			return EMPTY_STR;
		}
		StringBuilder result = new StringBuilder();
		result.append(str.substring(0, 1).toUpperCase(Locale.getDefault()));
		for (int i = 1; i < str.length(); i++) {
			String s = str.substring(i, i + 1);
			String slc = s.toLowerCase(Locale.getDefault());
			if (!s.equals(slc)) {
				result.append("_").append(slc.toUpperCase(Locale.getDefault()));
			} else {
				result.append(s.toUpperCase(Locale.getDefault()));
			}
		}
		return result.toString();
	}
	
	public static String rpad(String str, int len) {
		return rpad(str, len, ' ');
	}
	
	public static String rpad(String str, int size, char padChar) 
	{
		str = nvl(str);
		
	    int strLength = str.length();
		if (strLength < size)
	    {
	        char[] temp = new char[size];
	        int i = 0;

	        while (i < strLength )
	        {
	            temp[i] = str.charAt(i);
	            i++;
	        }

	        while (i < size)
	        {
	            temp[i] = padChar;
	            i++;
	        }

	        str = new String(temp);
	    }

	    return str;	
	}
	
	public static String lpad(String str, int len) {
		return lpad(str, len, ' ');
	}
	
	public static String lpad(String str, int size, char padChar) 
	{
		str = nvl(str);
		
	    int strLength = str.length();
		if (strLength < size)
	    {
	        char[] temp = new char[size];
	        int i = 0;

	        int padLen = size - strLength;
			while (i < padLen)
	        {
	            temp[i] = padChar;
	            i++;
	        }

	        while (i < size)
	        {
	            temp[i] = str.charAt(i - padLen);
	            i++;
	        }

	        str = new String(temp);
	    }
		
	    return str;	
	}
	
}
