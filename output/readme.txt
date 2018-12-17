TimerLauncher工具类使用说明：
    1.printDebugLog：是否输出调试日志：日志TAG是  TimerLauncher
    2.startDelayLaunch：启动延时启动任务：context:app的上下文，startPackage：要启动的app的包名字符串，delayTime：延时时长（秒）
    3.clearDelaylaunch：清除延时启动任务：context:app的上下文
    4.newStartAppByPackage：全新启动一个app，而不管原来是否在运行中：context:app的上下文，startPackage：要启动的app的包名字符串
    5.restartAppByPackage：复用启动一个app，如果后台没有，则全新启动，后台存在，则把后台app转到前台：context:app的上下文，startPackage：要启动的app的包名字符串
    6.tryCloseApp：尝试去杀死后台指定包名app，使用killBackgroundProcesses和restartPackage方式，不同的android手机系统兼任性不同，不保证可靠性。