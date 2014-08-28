package com.jeremybrooks.chess.util;

public class OutputBuilder
{
    private StringBuilder sb = new StringBuilder();
    
    public void append(String s)
    {
        sb.append(s);
        sb.append(AbstractDisplayer.EOL);
    }
    
    @Override
    public String toString()
    {
        return sb.toString();
    }
}