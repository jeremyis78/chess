package com.jeremybrooks.chess.util;

import java.util.ArrayDeque;

@SuppressWarnings("serial")
public class MoveStack extends ArrayDeque<Integer>
{
	public void push(Integer move)
	{
		addFirst(move);
	}
	
	public Integer pop()
	{
		return removeFirst();
	}
	
}
