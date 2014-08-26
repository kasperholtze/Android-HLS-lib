package com.kaltura.hlsplayersdk.subtitles;


import java.util.Vector;

import com.kaltura.hlsplayersdk.URLLoader;
import android.util.Log;

public class SubTitleParser implements URLLoader.DownloadEventListener {
	public Vector<WebVTTRegion> regions = new Vector<WebVTTRegion>();
	
	enum ParseState
	{
		IDLE,
		PARSE_HEADERS,
		PARSE_CUE_SETTINGS,
		PARSE_CUE_TEXT
	}

	public Vector<TextTrackCue> textTrackCues = new Vector<TextTrackCue>();
	public double startTime = -1;
	public double endTime = -1;
	
	private String _url;
	private URLLoader _loader = null;
	
	public SubTitleParser()
	{
		
	}
	
	public SubTitleParser(String url)
	{
		if (url != null) load(url);
	}
	
	public Vector<TextTrackCue> getCuesForTimeRange( double startTime, double endTime)
	{
		Vector<TextTrackCue> result = new Vector<TextTrackCue>();
		
		for (int i = 0; i < textTrackCues.size(); ++i)
		{
			TextTrackCue cue = textTrackCues.get(i);
			if (cue.startTime > endTime) break;
			if (cue.startTime >= startTime) result.add(cue);
		}
		return result;
	}
	
	public void load(String url)
	{
		_url = url;
		_loader = new URLLoader(this, null);
		_loader.get(_url);
	}
	
	public void parse(String input)
	{
		// Normalize line endings.
		input = input.replace("\r\n", "\n");
		
		// split into array
		String[] lines = input.split("\n");
		
		if (lines.length < 1 || lines[0].indexOf("WEBVTT") == -1)
		{
			Log.i("SubTitleParser.parse", "Not a valid WEBVTT file " + _url);
			if (this.mOnParseCompleteListener != null)
				mOnParseCompleteListener.onSubtitleParserComplete(this);
			return;
		}
		
		ParseState state = ParseState.PARSE_HEADERS;
		TextTrackCue textTrackCue = null;
		
		// Process each line
		
		for (int i = 1; i < lines.length; ++i)
		{
			String line = lines[i];
			if (line.equals(""))
			{
				// if new line, we're done with the last parsing step. Make sure we skip all new lines.
				state = ParseState.IDLE;
				if (textTrackCue != null) textTrackCues.add(textTrackCue);
				textTrackCue = null;
			}
			
			switch (state)
			{
			case PARSE_HEADERS:
				// Only support region headers for now
				if (line.indexOf("Region:") == 0) regions.add(WebVTTRegion.fromString(line));
				break;
				
			case IDLE:
				// New text track cue
				textTrackCue = new TextTrackCue();
				
				// If this line is the cue's ID, set it and break. Otherwise proceed to settings with current line.
				if (line.indexOf("-->") == -1)
				{
					textTrackCue.id = line;
					textTrackCue.buffer += line + "\n";
					break;
				}
				
			case PARSE_CUE_SETTINGS:
				textTrackCue.parse(line);
				textTrackCue.buffer += line;
				state = ParseState.PARSE_CUE_TEXT;
				break;
				
			case PARSE_CUE_TEXT:
				if (textTrackCue.text.length() > 0) textTrackCue.text += "\n";
				textTrackCue.text += line;
				textTrackCue.buffer += "\n" + line;
				break;
			}
			
		}
		
		TextTrackCue firstElement = textTrackCues.size() > 0 ? textTrackCues.get(0) : null;
		TextTrackCue lastElement = textTrackCues.size() > 1 ?textTrackCues.get(textTrackCues.size() - 1) : firstElement;
		
		// Set start and end times for this file
		if (firstElement != null)
		{
			startTime = firstElement.startTime;
			endTime = lastElement.endTime;
		}
		
		if (this.mOnParseCompleteListener != null)
			mOnParseCompleteListener.onSubtitleParserComplete(this);
	}
	
	public static double parseTimeStamp(String input)
	{
		// Time string parsed from format 00:00:00.000 and similar
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int milliseconds = 0;
		String[] units = input.split(":");
		

		String secondValues = "";
		
		if (units.length < 3)
		{
			minutes = Integer.parseInt(units[0]);
			secondValues = units[1];
			//secondUnits = units[1].split(".");
		}
		else
		{
			hours = Integer.parseInt(units[0]);
			minutes = Integer.parseInt(units[1]);
			secondValues = units[2];
		}
		
		String[] secondUnits = secondValues.split("[.]");
		
		seconds = Integer.parseInt(secondUnits[0]);
		if (secondUnits.length > 1) milliseconds = Integer.parseInt(secondUnits[1]);
		
		return (double)(hours * 60 * 60 + minutes * 60 + seconds) + ((double)milliseconds / (double)1000);
	}
	
	// Event Listeners
	public void setOnParseCompleteListener(OnSubtitleParseCompleteListener listener)
	{
		mOnParseCompleteListener = listener;
	}
	private OnSubtitleParseCompleteListener mOnParseCompleteListener;

	@Override
	public void onDownloadComplete(URLLoader loader, String response) {
		parse (response);
	}

	@Override
	public void onDownloadFailed(URLLoader loader, String response) {
		Log.i("SubTitleParser", "Download Failed : " + loader.getRequestURI().toASCIIString());
		if (this.mOnParseCompleteListener != null)
			mOnParseCompleteListener.onSubtitleParserComplete(this);
	}

}
