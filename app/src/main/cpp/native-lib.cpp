#include <jni.h>
#include <string>
#include "DCM.h"

extern "C" JNIEXPORT jstring JNICALL

Java_aericks1_example_falldetect_PrepareActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

// TODO: all code below will be replaced with the MATLAB->C++ code once that is generated.

extern "C" JNIEXPORT jdoubleArray
JNICALL
Java_aericks1_example_falldetect_PrepareActivity_doProcessingJNI(
        JNIEnv *env,
        jobject /* this */,
        jint num,
        jdoubleArray inArrayStatic,
        jdoubleArray inArrayDynamic) {
    // assume we have at least one element in the input array
    if (num <= 0) {
        return NULL;
    }

    // allocate return array
    jdoubleArray result;
    result = env->NewDoubleArray(num);
    if (result == NULL) {
        // memory allocation failed
        return NULL;
    }

    jdouble* sArr = env->GetDoubleArrayElements(inArrayStatic, 0);
    jdouble* dArr = env->GetDoubleArrayElements(inArrayDynamic, 0);
    jdouble* rArr = env->GetDoubleArrayElements(result, 0);

    // create size n array to pass in to hold results
    int size;

    emxArray_real_T staticArray;
    staticArray.data = sArr;

    emxArray_real_T dynamicArray;
    dynamicArray.data = dArr;

    emxArray_real_T newArray;
    newArray.data = rArr;

    // do the processing
    jdouble *workArr = new jdouble[num];
    //for (int i=0; i<num; ++i) {
    //    workArr[i] = DCM(staticArray.data[i], dArr[i], inArray);
    //}
    DCM(&staticArray, &dynamicArray, &newArray);

    //test: syntax_error

    env->ReleaseDoubleArrayElements(result, dArr, 0);

    env->SetDoubleArrayRegion(result, 0, num, workArr);
    delete[] workArr;

    return result;
}