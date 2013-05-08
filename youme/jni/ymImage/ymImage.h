#ifndef YMIMAGE_H
#define YMIMAGE_H
/** @defgroup ymImage
 *  This is the Youme Image handler itself.
 *  @{
 */
 
#include "include/CxImage/ximage.h"



CxImage* loadImage(const char* imagePath, DWORD imageType);

bool saveImage(CxImage* image, const char* imagePath, DWORD imageType);

int* getPixels(CxImage* image);

void setPixels(CxImage* image, int* pixels);



/** @}*/
#endif