/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef COLOR_CONVERTER_H_
#define COLOR_CONVERTER_H_
#include <sys/types.h>
#include <stdint.h>
#include "androidVideoShim.h"

namespace android_video_shim {

	struct ColorConverter_Local {
    ColorConverter_Local(OMX_COLOR_FORMATTYPE from, OMX_COLOR_FORMATTYPE to);
    ~ColorConverter_Local();

    bool isValid() const;

    void convert(
            size_t width, size_t height,
            const void *srcBits, size_t srcSkip,
            void *dstBits, size_t dstSkip);

//private:
    OMX_COLOR_FORMATTYPE mSrcFormat, mDstFormat;
    uint8_t *mClip;

    uint8_t *initClip();

    void convertCbYCrY(
            size_t width, size_t height,
            const void *srcBits, size_t srcSkip,
            void *dstBits, size_t dstSkip);

    void convertYUV420Planar(
            size_t width, size_t height,
            const void *srcBits, size_t srcSkip,
            void *dstBits, size_t dstSkip);

    void convertQCOMYUV420SemiPlanar(
            size_t width, size_t height,
            const void *srcBits, size_t srcSkip,
            void *dstBits, size_t dstSkip);

    void convertYUV420SemiPlanar(
            size_t width, size_t height,
            const void *srcBits, size_t srcSkip,
            void *dstBits, size_t dstSkip);

    void convertNV12Tile(
        size_t width, size_t height,
        const void *srcBits, size_t srcSkip,
        void *dstBits, size_t dstSkip);

    size_t nv12TileGetTiledMemBlockNum(
        size_t bx, size_t by,
        size_t nbx, size_t nby);

    void nv12TileComputeRGB(
        uint8_t **dstPtr,const uint8_t *blockUV,
        const uint8_t *blockY, size_t blockWidth,
        size_t dstSkip);

    void nv12TileTraverseBlock(
        uint8_t **dstPtr, const uint8_t *blockY,
        const uint8_t *blockUV, size_t blockWidth,
        size_t blockHeight, size_t dstSkip);

    void convertYUV420SemiPlanar32Aligned(
            size_t width, size_t height,
            const void *srcBits, size_t srcSkip,
            void *dstBits, size_t dstSkip,
            size_t alignedWidth);

    //ColorConverter(const ColorConverter &);
    //ColorConverter &operator=(const ColorConverter &);
};
}  // namespace android
#endif  // COLOR_CONVERTER_H_