package net.dogesoulseller.cuelib;

/**
 * Pair of elements of any types
 * @param <First> type of first element
 * @param <Second> type of second element
 */
public class Pair<First, Second>
{
	private First first;
	private Second second;

	/**
	 * Pair constructor
	 * @param _first first element
	 * @param _second second element
	 */
	public Pair(First _first, Second _second)
	{
		first = _first;
		second = _second;
	}

	/**
	 * Convenience function for constructing a new Pair
	 * @param <F> type of first element
	 * @param <S> type of second element
	 * @param _first first element
	 * @param _second second element
	 * @return newly constructed Pair
	 */
	public static <F,S> Pair<F,S> makePair(F _first, S _second)
	{
		return new Pair<F,S>(_first, _second);
	}

	/**
	 * Get first element
	 * @return first element
	 */
	public First getFirst()
	{
		return first;
	}

	/**
	 * Get second element
	 * @return second element
	 */
	public Second getSecond()
	{
		return second;
	}

	/**
	 * Set first element
	 * @param _first new first element
	 */
	public void setFirst(First _first)
	{
		first = _first;
	}

	/**
	 * Set second element
	 * @param _second new second element
	 */
	public void setSecond(Second _second)
	{
		second = _second;
	}
}