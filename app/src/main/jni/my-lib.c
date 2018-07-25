#include <jni.h>

void mapString(char str[26]){
    for (int i = 0; i < 25; ++i)
        str[i] = (char)((int)str[i] - i%7);
}

jstring Java_com_raed_twitterclient_AppTokenAndSecret_BV423(JNIEnv *env, jobject this) {
    char str[26] = "Bee[UTMeTVn;jSfb26zsvS:Gn";
    mapString(str);
    return (**env).NewStringUTF(env, str);
}

jstring Java_com_raed_twitterclient_AppTokenAndSecret_BV424(JNIEnv *env, jobject this) {
    char *str = "t3umaknaSXY4S1ENlVPzU";
    return (**env).NewStringUTF(env, str);
}

jstring Java_com_raed_twitterclient_AppTokenAndSecret_BV425(JNIEnv *env, jobject this, jint i) {
    char *str =  "dyzm40UPMh2GFzBMFyYRCSCEI";
    char *str2 = "z0MG";
    /*int index = 1;
    while (1){
        str2[index - 1] = str[index * 3 - 1];
        if (index == 4)
            break;
        index++;
    }*/
    return (**env).NewStringUTF(env, str2);
}
