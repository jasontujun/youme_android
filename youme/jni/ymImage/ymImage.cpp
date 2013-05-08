#include <stdio.h>
#include "ymImage.h"
#include "DataUtil.h"

CxImage* loadImage(const char* imagePath, DWORD imageType)
{
	CxImage* image = new CxImage;
	if (!image->Load(imagePath, imageType))
	{
		printf("get image error!\n");
		return NULL; 
	}
	return image;
}

bool saveImage(CxImage* image, const char* imagePath, DWORD imageType)
{
	return image->Save(imagePath, imageType);
}

int* getPixels(CxImage* image) 
{
	DWORD width = image->GetWidth();
	DWORD height = image->GetHeight();
	int* pixels = new int[width * height];
	BYTE* tmpARGB = new BYTE[4];
	for (unsigned int i = 0; i < width; i++) 
	{
		for (unsigned int j = 0; j < height; j++)
		{
			RGBQUAD rgba = image->GetPixelColor(i, j, true);
			tmpARGB[0] = rgba.rgbReserved;
			tmpARGB[1] = rgba.rgbRed;
			tmpARGB[2] = rgba.rgbGreen;
			tmpARGB[3] = rgba.rgbBlue;
			pixels[i * width + j] = ubyte2int(tmpARGB);
		}
	}
	delete[] tmpARGB;
	return pixels;
}

void setPixels(CxImage* image, int* pixels)
{
	DWORD width = image->GetWidth();
	DWORD height = image->GetHeight();
	BYTE* tmpARGB = new BYTE[4];
	for (unsigned int i = 0; i < width; i++) 
	{
		for (unsigned int j = 0; j < height; j++)
		{
			int2ubyte(pixels[i * width + j], tmpARGB);
			RGBQUAD rgba;
			rgba.rgbReserved = tmpARGB[0];
			rgba.rgbRed = tmpARGB[1];
			rgba.rgbGreen = tmpARGB[2];
			rgba.rgbBlue = tmpARGB[3];
			image->SetPixelColor(i, j, rgba, true);
		}
	}
	delete[] tmpARGB;
}