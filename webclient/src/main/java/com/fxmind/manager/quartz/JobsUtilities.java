package com.fxmind.manager.quartz;

import com.fxmind.quartz.JobDescription;
import org.quartz.simpl.SimpleClassLoadHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * JobsUtilities helps you to call the methods:
 * <ul>
 * <li>willDelete</li>
 * <li>willSave</li>
 * <li>validateForDelete</li>
 * <li>validateForSave</li>
 * </ul>
 * 
 * by instantiating an object based on the job description class path.<p>
 * 
 * You can call directly JobsUtilities.willSave(myJobDescriptionEO) for example but an instance of the job will be create each time.
 * You can also call createJobInstance(myJobDescriptionEO) and call the above methods yourself.<p>
 * Because the job class is not necessarily a subclass of Job (it can be a sub class of AbstractJob or just implement
 * the interface Job), the methods willDelete, willSave, validateForDelete, validateForSave checks if the instantiated object
 * is a Job object. If not the object is just returned.
 * @author Sergei Zhuravlev
 */
public class JobsUtilities
{
	/**
	 * This exception is thrown if the class to be instantiate doesn't exist or if it can't be instantiate like a wrong constructor
	 * for example.<p>
	 * Rather than create a hierarchy of classes corresponding to each different errors, we preferred to add an error type
	 * that gives more information about the error.
	 * 
	 * @author Philippe Rabier
	 * @see ErrorType
	 */
	public static class COJobInstanciationException extends Exception
	{
		public enum ErrorType
		{
			CLASS_NOT_FOUND,
			CONSTRUCTOR_ERROR,
			INSTANCE_ERROR;
		}
		private static final long serialVersionUID = 1L;
		private final ErrorType errorType;

		public COJobInstanciationException(final String message, final ErrorType type) 
		{
			super(message);
			errorType = type;
		}

		public COJobInstanciationException(final String msg, final ErrorType type, final Throwable cause)
		{
			super(msg, cause);
			errorType = type;
		}

		public Throwable getUnderlyingException()
		{
			return super.getCause();
		}

		public ErrorType getErrorType()
		{
			return errorType;
		}

		@Override
		public String toString()
		{
			Throwable cause = getUnderlyingException();
			if (cause == null || cause == this)
				return super.toString();
			else
				return (new StringBuilder()).append(super.toString()).append(" [See nested exception: ").append(cause).append("]").toString();
		}
	}

	protected static final Logger log = LoggerFactory.getLogger(JobsUtilities.class);

	public static org.quartz.Job createJobInstance(final JobDescription jobDescription) throws COJobInstanciationException
	{
		if (jobDescription == null)
			throw new IllegalArgumentException("jobDescription can't be null");

		SimpleClassLoadHelper loader = new SimpleClassLoadHelper();
		Class<? extends org.quartz.Job> aJobClass = null;
		try 
		{
			aJobClass = (Class<? extends org.quartz.Job>) loader.loadClass(jobDescription.classPath());
		} catch (ClassNotFoundException e) 
		{
			throw new COJobInstanciationException("Class " + jobDescription.classPath() + " not found.", COJobInstanciationException.ErrorType.CLASS_NOT_FOUND);
		}

		Constructor<? extends org.quartz.Job> constructor = null;
        Object ErrorType;
        try
		{
			constructor = aJobClass.getConstructor();
		} catch (Exception e) 
		{
			throw new COJobInstanciationException("Class " + jobDescription.classPath() + " not found.", COJobInstanciationException.ErrorType.CONSTRUCTOR_ERROR, e);
		}

		org.quartz.Job aJob = null;
		try 
		{
			aJob = constructor.newInstance();
		} catch (Exception e) 
		{
			throw new COJobInstanciationException("Class " + jobDescription.classPath() + " not found.", COJobInstanciationException.ErrorType.INSTANCE_ERROR, e);
		}
		return aJob;
	}

	public static org.quartz.Job willDelete(final JobDescription jobDescription) throws COJobInstanciationException
	{
		org.quartz.Job aJob = createJobInstance(jobDescription);
		if (aJob instanceof Job)
			((Job)aJob).willDelete(jobDescription);
		return aJob;
	}

	public static org.quartz.Job willSave(final JobDescription jobDescription) throws COJobInstanciationException
	{
		org.quartz.Job aJob = createJobInstance(jobDescription);
		if (aJob instanceof Job)
			((Job)aJob).willSave(jobDescription);
		return aJob;
	}

	public static org.quartz.Job validateForDelete(final JobDescription jobDescription) throws COJobInstanciationException
	{
		org.quartz.Job aJob = createJobInstance(jobDescription);
		if (aJob instanceof Job)
			((Job)aJob).validateForDelete(jobDescription);
		return aJob;
	}

	public static org.quartz.Job validateForSave(final JobDescription jobDescription) throws COJobInstanciationException
	{
		org.quartz.Job aJob = createJobInstance(jobDescription);
		if (aJob instanceof Job)
			((Job)aJob).validateForSave(jobDescription);
		return aJob;
	}
}
