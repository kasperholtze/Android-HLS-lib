/*
 * HLSSegment.cpp
 *
 *  Created on: Apr 29, 2014
 *      Author: Mark
 */
#include <android/native_window.h>
#include <android/window.h>
#include <../android-source/frameworks/av/include/media/stagefright/MediaDefs.h>


#include "constants.h"
#include "debug.h"
#include "HLSSegment.h"

using namespace android;


#define CLASS_NAME APP_NAME"::HLSSegment"


#define METHOD CLASS_NAME"::HLSSegment()"
HLSSegment::HLSSegment(int32_t quality, double time) : mWidth(0), mHeight(0), mBitrate(0), mExtractorFlags(0), mActiveAudioTrackIndex(0), mStartTime(time), mQuality(quality)
{

}

#define METHOD CLASS_NAME"::~HLSSegment()"
HLSSegment::~HLSSegment()
{

}

#define METHOD CLASS_NAME"::SetDataSource()"
bool HLSSegment::SetDataSource(android::sp<android::DataSource> dataSource)
{
	status_t err = dataSource->initCheck();
	if (err != OK)
	{
		LOGERROR(METHOD, "DataSource is invalid: %s", strerror(-err));
		return false;
	}

	mFileSource = dataSource;
	mExtractor = MediaExtractor::Create(mFileSource);
	if (mExtractor == NULL)
	{
		LOGERROR(METHOD, "Could not create MediaExtractor from DataSource %0x", dataSource.get());
		return false;
	}

	if (mExtractor->getDrmFlag())
	{
		LOGERROR(METHOD, "This datasource has DRM - not implemented!!!");
		return false;
	}

	int64_t totalBitRate = 0;
	for (size_t i = 0; i < mExtractor->countTracks(); ++i)
	{

		sp<MetaData> meta = mExtractor->getTrackMetaData(i); // this is likely to return an MPEG2TSSource

		int32_t bitrate = 0;
		if (!meta->findInt32(kKeyBitRate, &bitrate))
		{
			const char* mime = "[unknown]";
			meta->findCString(kKeyMIMEType, &mime);

			LOGINFO(METHOD, "Track #%d of type '%s' does not publish bitrate", i, mime );
			continue;
		}
		LOGINFO(METHOD, "bitrate for track %d = %d bits/sec", i , bitrate);
		totalBitRate += bitrate;
	}

	mBitrate = totalBitRate;



	LOGINFO(METHOD, "mBitrate = %lld bits/sec", mBitrate);

	bool haveAudio = false;
	bool haveVideo = false;

	for (size_t i = 0; i < mExtractor->countTracks(); ++i)
	{
		sp<MetaData> meta = mExtractor->getTrackMetaData(i);
		meta->dumpToLog();

		const char* cmime;
		if (meta->findCString(kKeyMIMEType, &cmime))
		{
			String8 mime = String8(cmime);

			if (!haveVideo && !strncasecmp(mime.string(), "video/", 6))
			{
				SetVideoTrack(mExtractor->getTrack(i));
				haveVideo = true;

				// Set the presentation/display size
				int32_t width, height;
				bool res = meta->findInt32(kKeyWidth, &width);
				if (res)
				{
					res = meta->findInt32(kKeyHeight, &height);
				}
				if (res)
				{
					mWidth = width;
					mHeight = height;
					LOGINFO(METHOD, "Video Track Width = %d, Height = %d, %d", width, height, __LINE__);
				}
			}
			else if (!haveAudio && !strncasecmp(mime.string(), "audio/", 6))
			{
				SetAudioTrack(mExtractor->getTrack(i));
				haveAudio = true;

				mActiveAudioTrackIndex = i;

			}
			else if (!strcasecmp(mime.string(), MEDIA_MIMETYPE_TEXT_3GPP))
			{
				//addTextSource_l(i, mExtractor->getTrack(i));
			}
		}
	}

	if (!haveAudio && !haveVideo)
	{
		return UNKNOWN_ERROR;
	}



	mExtractorFlags = mExtractor->flags();

	return true;
}

#define METHOD CLASS_NAME"::SetAudioTrack()"
void HLSSegment::SetAudioTrack(sp<MediaSource> source)
{
	mAudioTrack = source;
}

#define METHOD CLASS_NAME"::SetVideoTrack()"
void HLSSegment::SetVideoTrack(sp<MediaSource> source)
{
	mVideoTrack = source;
}

#define METHOD CLASS_NAME"::GetAudioTrack()"
sp<MediaSource> HLSSegment::GetAudioTrack()
{
	return mAudioTrack;
}

#define METHOD CLASS_NAME"::GetVideoTrack()"
sp<MediaSource> HLSSegment::GetVideoTrack()
{
	return mVideoTrack;
}

#define METHOD CLASS_NAME"::GetWidth()"
int32_t HLSSegment::GetWidth()
{
	return mWidth;
}

#define METHOD CLASS_NAME"::GetHeight()"
int32_t HLSSegment::GetHeight()
{
	return mHeight;
}

#define METHOD CLASS_NAME"::GetQuality()"
int32_t HLSSegment::GetQuality()
{
	return mQuality;
}

#define METHOD CLASS_NAME"::GetStartTime()"
double HLSSegment::GetStartTime()
{
	return mStartTime;
}
#define METHOD CLASS_NAME"::GetBitrate()"
int64_t HLSSegment::GetBitrate()
{
	return mBitrate;
}
