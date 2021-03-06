/***************************************************************************************
* Copyright (c) 2009 Edu Zamora <edu.zasu@gmail.com>                                   *
*                                                                                      *
* This program is free software; you can redistribute it and/or modify it under        *
* the terms of the GNU General Public License as published by the Free Software        *
* Foundation; either version 3 of the License, or (at your option) any later           *
* version.                                                                             *
*                                                                                      *
* This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
* PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
*                                                                                      *
* You should have received a copy of the GNU General Public License along with         *
* this program.  If not, see <http://www.gnu.org/licenses/>.                           *
****************************************************************************************/

package com.ichi2.anki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * Class used to display and handle correctly images
 */
public class Image {
	
	/**
	 * Tag for logging messages
	 */
	private static final String TAG = "AnkiDroid";
	
	/**
	 * Pattern used to identify img tags
	 */
	private static Pattern mImagePattern = Pattern.compile("(?i)<img[^<>(src)]*src\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^'\">]+)[^<>]*>");

	/**
	 * Variables used to track the total time spent
	 */
	private static long mStartTime, mFinishTime;
	
	/**
	 * Parses the content (belonging to deck deckFilename), adding an onload event to the img tags, that will be useful in order to resize them
	 * @param deckFilename Deck's filename whose content is being parsed
	 * @param content HTML content of a card
	 * @return content Content with the onload events for the img tags
	 */
	public static String parseImages(String deckFilename, String content)
	{
		mStartTime = System.currentTimeMillis();

		StringBuilder stringBuilder = new StringBuilder();
		String contentLeft = content;
		
		Log.i(TAG, "parseImages");
		Matcher matcher = mImagePattern.matcher(content);
		while(matcher.find())
		{
			String img = matcher.group(1);
			//Log.i(TAG, "Image " + matcher.groupCount() + ": " + img);
			
			String imgTag = matcher.group();
			int markerStart = contentLeft.indexOf(imgTag);
			stringBuilder.append(contentLeft.substring(0, markerStart));
			stringBuilder.append("<img src=" + img + " onload=\"resizeImage();\">");

			contentLeft = contentLeft.substring(markerStart + imgTag.length());
			//Log.i(TAG, "Content left = " + contentLeft);
		}
		
		stringBuilder.append(contentLeft);
		
		mFinishTime = System.currentTimeMillis();
		Log.i(TAG, "Images parsed in " + (mFinishTime - mStartTime) + " milliseconds");
		
		return stringBuilder.toString();
	}
}
