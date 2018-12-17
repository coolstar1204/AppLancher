 class AndroidCallCallback : AndroidJavaProxy
 {
     public AndroidCallCallback()
    : base("com.cool.star.timerlancher.IUnityCallBack")
    {
    }
      public void onTimeRunFinish(AndroidJavaObject result)
      {
      }
 
  }