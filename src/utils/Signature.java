package utils;

/**
 * Signature interface allowing classes to provide their
 * class name and current method name which can be used in debug messages
 * @author davidroussel
 */
public interface Signature
{
	/**
	 * Class name accessor
	 * @return the name of the class calling this method
	 */
	public default String getClassName()
	{
		return getClass().getSimpleName();
	}

	/**
	 * Class name accessor
	 * @param <E> the type of class calling this method
	 * @param type an instance of the class Class to get name
	 * @return the name of the class corresponding to the argument of this method
	 */
	public static <E> String getClassName(Class<E> type)
	{
		return type.getSimpleName();
	}

	/**
	 * Method name accessor.
	 * To be called from static methods
	 * @return the name of the method calling this method based on
	 * the stack trace
	 */
	public static String getStaticMethodName()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length >= 2)
		{
			return stackTrace[2].getMethodName();
		}
		else
		{
			return new String();
		}
	}

	/**
	 * Method name accessor.
	 * To be called from instance methods
	 * @return the name of the method calling this method based on
	 * the stack trace
	 */
	public default String getMethodName()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length >= 2)
		{
			return stackTrace[2].getMethodName();
		}
		else
		{
			return new String();
		}
	}
}
