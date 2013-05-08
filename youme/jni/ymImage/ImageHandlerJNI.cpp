#include <android/log.h>
#include "ImageHandlerJNI.h"
#include "ymImage.h"
#include "DataUtil.h"

#define TAG "ymImage-JNI" // 这个是自定义的LOG的标识
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) // 定义LOGD类型  
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__) // 定义LOGI类型  
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__) // 定义LOGW类型  
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__) // 定义LOGE类型  
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,TAG,__VA_ARGS__) // 定义LOGF类型 

/*
 * Class:     Java_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI
 * Method:    getPixelsFromImage
 * Signature: (Ljava/lang/String;)[I
 */
JNIEXPORT jintArray JNICALL Java_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI_getPixelsFromImage
  (JNIEnv * env, jobject obj, jstring jfilepath)
{
	LOGD("hello, getPixels!");

	const char * filePath = env -> GetStringUTFChars(jfilepath, NULL); 
	if (filePath == NULL)
	{
		LOGD("file path error!");
		return NULL;
	}

	// load image
	CxImage* image = loadImage(filePath, CXIMAGE_FORMAT_PNG);
	if (!image || !image->IsValid())
	{
		LOGD("load image error!");
		env -> ReleaseStringUTFChars(jfilepath, filePath);
		return NULL;
	}


	// image -> pixel
	int* int_pixels = getPixels(image);

	DWORD width = image->GetWidth();
	DWORD height = image->GetHeight();
	jint pixelsize = width * height;
	jintArray result = env -> NewIntArray(pixelsize);
	#if WINDOWS_OS
		jint* long_pixels = new jint[pixelsize];
		intArray2longArray(int_pixels, long_pixels, pixelsize);// 转换数据类型
		delete[] int_pixels;// 回收数组资源
		env -> SetIntArrayRegion(result, 0, pixelsize, long_pixels); 
	#else
		env -> SetIntArrayRegion(result, 0, pixelsize, int_pixels);
	#endif

	LOGD("get pixels success!\n");
		
	env -> ReleaseStringUTFChars(jfilepath, filePath);
	return result;
}

/*
 * Class:     com_tj_demo_core_media_image_jni_ImageHandlerJNI
 * Method:    setPixelsToImage
 * Signature: (Ljava/lang/String;Ljava/lang/String;[I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI_setPixelsToImage
  (JNIEnv * env, jobject obj, jstring jsrcpath, jstring jdespath, jintArray jpixels)
{
	LOGD("hello, setPixels!");

	const char * srcPath = env -> GetStringUTFChars(jsrcpath, NULL); 
	if (srcPath == NULL)
	{
		LOGD("src path error!");
		return false;
	}

	const char * desPath = env -> GetStringUTFChars(jdespath, NULL);
	if (desPath == NULL)
	{
		LOGD("des path error!");
		env -> ReleaseStringUTFChars(jsrcpath, srcPath);
		return false;
	}

	// load image
	CxImage* srcImage = loadImage(srcPath, CXIMAGE_FORMAT_PNG);
	if (!srcImage || !srcImage->IsValid())
	{
		env -> ReleaseStringUTFChars(jsrcpath, srcPath);
		env -> ReleaseStringUTFChars(jdespath, desPath);
		return false;
	}

	DWORD width = srcImage->GetWidth();
	DWORD height = srcImage->GetHeight();
	DWORD type = srcImage->GetType();
	DWORD bitsperpixel = srcImage->GetBpp();// 1,4,8,24
	LOGD("iamge width=%d\n", width);
	LOGD("iamge height=%d\n", height);
	LOGD("iamge type=%d\n", type);
	LOGD("bitsperpixel=%d\n", bitsperpixel);

	
	CxImage* desImage = new CxImage(width, height, bitsperpixel, type);
	if (srcImage->AlphaIsValid())
		desImage->AlphaCreate();
	
	LOGD("before get java pixels input!\n");
	
	int pixelsize = env -> GetArrayLength(jpixels);
	#if WINDOWS_OS
		jint* long_pixels = env -> GetIntArrayElements(jpixels, 0);
		int* int_pixels = new int[pixelsize];	
		longArray2intArray(long_pixels, int_pixels, pixelsize);// 转换数据类型
		env -> ReleaseIntArrayElements(jpixels, long_pixels, 0);// 释放jni资源
	#else
		int* int_pixels = env -> GetIntArrayElements(jpixels, 0);
	#endif
	
	
	LOGD("before set pixels to image!\n");
	
	// pixel -> image
	setPixels(desImage, int_pixels);
	#if WINDOWS_OS
		delete[] int_pixels;// 回收pixel数组
	#else
		env -> ReleaseIntArrayElements(jpixels, int_pixels, 0);
	#endif

	// save image
	LOGD("save image path: %s \n", desPath);
	if (!saveImage(desImage, desPath, CXIMAGE_FORMAT_PNG))
	{
		LOGD("save iamge error!\n");
		env -> ReleaseStringUTFChars(jsrcpath, srcPath);
		env -> ReleaseStringUTFChars(jdespath, desPath);
		return false;
	}

	
	LOGD("set pixels success!\n");
	env -> ReleaseStringUTFChars(jsrcpath, srcPath);
	env -> ReleaseStringUTFChars(jdespath, desPath);
	return true;
}
