/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_tj_demo_core_media_image_jni_ImageHandlerJNI */

#ifndef _Included_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI
#define _Included_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI
#ifdef __cplusplus
extern "C" {
#endif

#define WINDOWS_OS 0

/*
 * Class:     com_tj_demo_core_media_image_jni_ImageHandlerJNI
 * Method:    getPixelsFromImage
 * Signature: (Ljava/lang/String;)[I
 */
JNIEXPORT jintArray JNICALL Java_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI_getPixelsFromImage
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_tj_demo_core_media_image_jni_ImageHandlerJNI
 * Method:    setPixelsToImage
 * Signature: (Ljava/lang/String;Ljava/lang/String;[I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_soulware_youme_core_secret_media_image_jni_ImageHandlerJNI_setPixelsToImage
  (JNIEnv *, jobject, jstring, jstring, jintArray);

#ifdef __cplusplus
}
#endif
#endif