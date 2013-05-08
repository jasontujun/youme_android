#include "DataUtil.h"

/**
  * byte -> int
  * @param b	index0为最高位
  * @return
  */
int ubyte2int(unsigned char* b) 
{
	int intLength = 4;
	int value = 0;
	for (int i = 0; i < intLength; i++) 
	{
		int shift = (intLength - 1 - i) * 8;
		int tmpB = b[i] & 0x000000FF;// TIP 默认也是转化为int
		value += tmpB << shift;
	}
	return value;
}

/**
  * int -> byte(4)
  * @param value
  * @return signed byte
  */
void int2ubyte(int value, unsigned char* b) {
	int intLength = 4;
	for (int i = 0; i < intLength; i++) 
	{
		int offset = (intLength - 1 - i) * 8;
		b[i] = (unsigned char) ((value >> offset) & 0x000000FF);
	}
}


void intArray2longArray(int* intArr, long* longArr, long arrSize)
{
	for(int i = 0; i<arrSize; i++)
		longArr[i] = intArr[i];
}


void longArray2intArray(long* longArr, int* intArr, long arrSize)
{
	for(int i = 0; i<arrSize; i++)
		intArr[i] = (int) longArr[i];
}