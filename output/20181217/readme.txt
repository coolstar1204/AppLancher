使用步骤：
	1.请备份现有没问题的对应文件
	2.请覆盖项目中的aar库文件
	3.请更新AndroidManifest.xml，主要是增加一下两行
		<uses-permission android:name="android.permission.WAKE_LOCK"/>
        <service android:name=".service.RegularTimeService" />
	4.请把callbackImpl.cs代码放入项目中，这里是回调触发接受的地方
	5.调用方式：javaClass.CallStatic("regularTimeRun",context, 3,new AndroidMyCallback());
	6.退出定时回调请调用：javaClass.CallStatic("releaseRegularTimeRun", context);