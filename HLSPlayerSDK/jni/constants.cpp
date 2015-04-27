/*
 * constants.cpp
 *
 *  Created on: Apr 24, 2015
 *      Author: Mark
 */

#include "constants.h"

const char* getStateString(int playState)
{
	switch (playState)
	{
	case STOPPED:
		return "STOPPED";
		break;
	case PAUSED:
		return "PAUSED";
		break;
	case PLAYING:
		return "PLAYING";
		break;
	case SEEKING:
		return "SEEKING";
		break;
	case FORMAT_CHANGING:
		return "FORMAT_CHANGING";
		break;
	case FOUND_DISCONTINUITY:
		return "FOUND_DISCONTINUITY";
		break;
	case WAITING_ON_DATA:
		return "WAITING_ON_DATA";
		break;
	case CUE_STOP:
		return "CUE_STOP";
		break;

	}
	return "UNKNOWN";
}


