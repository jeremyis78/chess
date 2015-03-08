package com.jeremybrooks.chess.util;

public class OutputBuilder
{
    private StringBuilder sb = new StringBuilder();
    
    public OutputBuilder append(String s)
    {
        sb.append(s);
        sb.append(AbstractDisplayer.EOL);
        return this;
    }
    
    @Override
    public String toString()
    {
        return sb.toString();
    }
}