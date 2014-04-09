
/*Mobo version code
	this file is part of MoboPlayer*/

package com.clov4r.android.nil;

public final class Version{

	public static final String getVersionName ()
	{
		return String.format("%s.%d_%s",mainVersion,versionNum,platform);
	};
	public static final int versionNum =145;
	public static final String mainVersion ="1.2";
	public static final String platform ="universal";
	public static final String buildDate ="Thu, 28 Apr 2011 16:57:38 CST";
}