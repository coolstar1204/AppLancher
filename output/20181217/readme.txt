ʹ�ò��裺
	1.�뱸������û����Ķ�Ӧ�ļ�
	2.�븲����Ŀ�е�aar���ļ�
	3.�����AndroidManifest.xml����Ҫ������һ������
		<uses-permission android:name="android.permission.WAKE_LOCK"/>
        <service android:name=".service.RegularTimeService" />
	4.���callbackImpl.cs���������Ŀ�У������ǻص��������ܵĵط�
	5.���÷�ʽ��javaClass.CallStatic("regularTimeRun",context, 3,new AndroidMyCallback());
	6.�˳���ʱ�ص�����ã�javaClass.CallStatic("releaseRegularTimeRun", context);