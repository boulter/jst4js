package jst;

import java.util.ArrayList;
import java.util.List;

public class TemplateLineNumbers {
    List<IndexToLineNumber> lines = new ArrayList<IndexToLineNumber>();
    String[] sourceLines;

    public TemplateLineNumbers(StringBuffer template) {
        sourceLines = template.toString().split("\n");
//        int lastIndex = 0;
//        int lineNumber = 1;
//        int index;
//        while( (index = template.indexOf("\n",lastIndex)) >= 0 ) {
//            lines.add( new IndexToLineNumber( lastIndex, index, lineNumber, -1 ) );
//            lastIndex = index + 1;
//            lineNumber++;
//        }
    }

//    public int setScriptLineNumber( int start, int end, int scriptLineNumber, boolean singleLine ) {
//        int startLine = getLineFromIndex( start );
//        int endLine = getLineFromIndex( end );
//        int currentScriptLine = scriptLineNumber;
//        while( startLine <= endLine ) {
//            lines.get( startLine++ ).scriptLineNumber = currentScriptLine;
//            if( !singleLine ) currentScriptLine++;
//        }
//        return currentScriptLine;
//    }

//    public IndexToLineNumber getLineFromScriptNumber( int scriptNumber ) {
//        int left = 0;
//        int right = lines.size();
//        while( left < right ) {
//            int current = (left + right) / 2;
//            IndexToLineNumber l = lines.get( current );
//            if( scriptNumber < l.scriptLineNumber ) {
//                right = current;
//            } else if( scriptNumber > l.scriptLineNumber ) {
//                left = current + 1;
//            } else {
//                return l;
//            }
//        }
//
//        return null;
//    }
//
//    public int getLineFromIndex( int index ) {
//        int left = 0;
//        int right = lines.size();
//        while( left < right ) {
//            int current = left + (right - left) / 2;
//            IndexToLineNumber l = lines.get( current );
//            if( index < l.left ) {
//                right = current;
//            } else if( index > l.right ) {
//                left = current + 1;
//            } else {
//                return current;
//            }
//        }
//
//        return -1;
//    }

    public int size() {
        return lines.size();
    }

    public IndexToLineNumber getLine(int i) {
        return lines.get(i-1);
    }

    public void addScriptLine(int left, int right, boolean singleLine) {
        int leftTemplateLine = getTemplateLineNumber(left);
        int rightTemplateLine = getTemplateLineNumber(right);
        if( singleLine ) {
            lines.add( new IndexToLineNumber( left, right, leftTemplateLine + 1, lines.size() + 1 ) );
        } else {
            int srcLeft = left;
            while( leftTemplateLine <= rightTemplateLine ) {
                String src = sourceLines[leftTemplateLine];
                int srcRight = srcLeft + src.length();
                lines.add( new IndexToLineNumber( srcLeft, right > srcRight ? srcRight : right, leftTemplateLine+1, lines.size() + 1 ) );
                srcLeft += src.length() + 1;
                leftTemplateLine++;
            }
        }
    }

    private int getTemplateLineNumber(int left) {
        int index = 0;
        for( int i = 0; i < sourceLines.length; i++ ) {
            index += sourceLines[i].length() + 1;
            if( index >= left ) {
                return i;
            }
        }
        return -1;
    }
}
