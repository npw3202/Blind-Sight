#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lab_blindsight_Stream_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Halo from C++";

    return env->NewStringUTF(hello.c_str());

}
