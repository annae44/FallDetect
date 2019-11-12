#include <jni.h>
#include <string>
#include "f1.h"
#include "f2.h"

extern "C" JNIEXPORT jstring JNICALL

Java_aericks1_example_falldetect_PrepareActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

// TODO: all code below will be replaced with the MATLAB->C++ code once that is generated.

extern "C" JNIEXPORT double
JNICALL
Java_aericks1_example_falldetect_PrepareActivity_computeJNI(
        JNIEnv *env,
        jobject /* this */,
        jdouble d) {
    double rtnval = d + 200.57;
    return rtnval;
}

extern "C" JNIEXPORT double
JNICALL
Java_aericks1_example_falldetect_PrepareActivity_doSumJNI(
        JNIEnv *env,
        jobject /* this */,
        jint num,
        jdoubleArray inArray) {
    jdouble* dArr = env->GetDoubleArrayElements( inArray, 0);
    double rtnval = dArr[0] + dArr[1] + dArr[2] + dArr[3];
    rtnval = f2(rtnval);

    env->ReleaseDoubleArrayElements(inArray, dArr, 0);
    return rtnval;
}

extern "C" JNIEXPORT jdoubleArray
JNICALL
Java_aericks1_example_falldetect_PrepareActivity_doProcessingJNI(
        JNIEnv *env,
        jobject /* this */,
        jint num,
        jdoubleArray inArray) {
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

    jdouble* dArr = env->GetDoubleArrayElements( inArray, 0);

    // do the processing
    jdouble *workArr = new jdouble[num];
    for (int i=0; i<num; ++i) {
//    workArr[i] = dArr[i] * 2.0;
        workArr[i] = f1(dArr[i]);
    }

    //test: syntax_error

    env->ReleaseDoubleArrayElements(inArray, dArr, 0);

    env->SetDoubleArrayRegion(result, 0, num, workArr);
    delete[] workArr;

    return result;
}