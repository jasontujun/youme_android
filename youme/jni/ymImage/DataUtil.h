#ifndef DATAUTIL_H
#define DATAUTIL_H
/** @defgroup ymImage
 *  This is the Youme Image handler itself.
 *  @{
 */




/**
  * byte -> int
  * @param b	index0为最高位
  * @return
  */
int ubyte2int(unsigned char* b);

/**
  * int -> byte(4)
  * @param value
  * @return signed byte
  */
void int2ubyte(int value, unsigned char* b);


void intArray2longArray(int* intArr, long* longArr, long arrSize);


void longArray2intArray(long* longArr, int* intArr, long arrSize);


/** @}*/
#endif