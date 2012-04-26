package jst;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.text.MessageFormat;

public class StringUtil {

    public static String toCamelCase( String str ) {
        StringBuffer buffer = new StringBuffer(str);
        int i = 0;
        while( i < buffer.length() ) {
            if( buffer.charAt(i) == '_' || Character.isWhitespace( buffer.charAt(i) ) || buffer.charAt(i) == '.' || buffer.charAt(i) == '-' ) {
                buffer.deleteCharAt( i );
                if( Character.isLetter( buffer.charAt(i) ) ) {
                    buffer.setCharAt( i, Character.toUpperCase(buffer.charAt(i)) );
                    i++;
                }
            } else {
                i++;
            }
        }
        return buffer.toString();
    }

    public static String toCamelCase( Class clazz ) {
        String name = onlyName( clazz.getName() );
        StringBuffer buf = new StringBuffer( name );
        buf.setCharAt( 0, Character.toLowerCase( buf.charAt(0) ) );
        return buf.toString();
    }

    public static String onlyName(String qualifiedName) {
        int last = qualifiedName.lastIndexOf('.');
        return qualifiedName.substring( last + 1 );
    }

    public static String join( Object[] arr, String delimeter ) {
        StringBuilder builder = new StringBuilder();
        for( int i = 0; i < arr.length; i++ ) {
            builder.append( arr[i].toString() );
            if( i + 1 < arr.length ) {
                builder.append(delimeter);
            }
        }
        return builder.toString();
    }

    public static String join(Collection s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (iter.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    public static Collection collect( String formatString, Collection collection ) {
        ArrayList list = new ArrayList();
        for (Object obj : collection) {
            list.add( MessageFormat.format(formatString, obj) );
        }
        return list;
    }

    public static Object[] collect(String formatString, Object[] objs) {
        Object[] array = new Object[ objs.length ];
        for( int i = 0; i < objs.length; i++ ) {
            array[i] = MessageFormat.format( formatString, objs );
        }
        return array;
    }

    public static String sanitize( String value ) {
        if( value != null && value.indexOf('<') >= 0 ) {
            value = value.replaceAll("<script>", "&lt;script&gt;");
            return value.replaceAll("</script>", "&lt;/script&gt;");
        } else {
            return value;
        }
    }

    public static String escapeHtml( String value ) {
        return StringEscapeUtils.escapeHtml( value );
    }

    public static String escapeXml( String value ) {
        return StringEscapeUtils.escapeXml( value );
    }

    public static void main(String[] args) {
        System.out.println(StringUtil.sanitize("<script>alert(You dead!)</script>"));
        System.out.println(StringUtil.sanitize("<LINK REL =\"stylesheet\" TYPE=\"text/css\" HREF=\"../../../../../stylesheet.css\" TITLE=\"Style\">"));
        System.out.println(StringUtil.sanitize("<table><tr><td></td></tr><tr><td><script>alert('You dead');</script></td></tr></table>"));
    }
}
