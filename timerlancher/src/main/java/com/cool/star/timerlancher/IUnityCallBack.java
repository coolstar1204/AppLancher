package com.cool.star.timerlancher;

/**
 * 定义Unity中使用的回调接口
 *
 * 下面代码是在Unity中的C#代码
 * class AndroidCallCallback : AndroidJavaProxy
 * {
 *     public AndroidCallCallback()
 *     : base("com.cool.star.timerlancher.IUnityCallBack")
 *     {
 *     }
 *     public void onTimeRunFinish(AndroidJavaObject result)
 *     {
 *     }
 *
 * }
 *
 * 下面是Unity中使用方式
 * javaClass.CallStatic("regularTimeRun", 3,new AndroidMyCallback());
 */
public interface IUnityCallBack {
    void onTimeRunFinish();
}
