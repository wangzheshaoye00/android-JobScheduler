package com.example.android.baohuo;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobDaemonService extends JobService {
    private int kJobId = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyJobDaemonService", "jobService启动");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    /**
     * 调用表示作业已开始执行。使用您的工作逻辑覆盖此方法。与所有其他组件生命周期回调一样，此方法在应用程序的主线程上执行。
     *
     * true如果您的工作需要继续运行，请从此方法 返回。如果执行此操作，作业将保持活动状态，直到您打电话 jobFinished(android.app.job.JobParameters, boolean)告诉系统已完成其工作，
     * 或者直到不再满足作业所需的约束为止。例如，如果使用调度作业 JobInfo.Builder#setRequiresCharging(boolean)，如果用户从设备上拔下电源，系统将立即停止该作业，
     * onStopJob(android.app.job.JobParameters)将调用作业的回调，并且应用程序将关闭与该作业相关的所有正在进行的工作。
     *
     * 只要您的工作正在执行，系统就会代表您的应用程序保留唤醒锁。在调用此方法之前获取此唤醒锁，并且在您调用之前jobFinished(android.app.job.JobParameters, boolean)
     * 或系统调用 onStopJob(android.app.job.JobParameters)以通知您的作业过早关闭之前不会释放该唤醒锁。
     *
     * false从这个方法 返回意味着你的工作已经完成。系统的作业唤醒锁将被释放，onStopJob(android.app.job.JobParameters) 不会被调用。
     * @param params
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStartJob方法");
        boolean isLocalServiceWork = isServiceWork(this, "com.example.android.baohuo.BaohuoService1");
        boolean isRemoteServiceWork = isServiceWork(this, "com.example.android.baohuo.BaohuoService2");
        if (!isLocalServiceWork ||
                !isRemoteServiceWork) {
            this.startService(new Intent(this, BaohuoService1.class));
            Log.i("onStartJob", "启动service1");
            this.startService(new Intent(this,BaohuoService2.class));
            Log.i("onStartJob", "启动service2");
            Toast.makeText(this, "进程启动", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * 如果系统确定即使在您有机会呼叫之前必须停止执行作业，也会调用此方法jobFinished(android.app.job.JobParameters, boolean)。
     *
     * 如果不再满足在计划时指定的要求，则会发生这种情况。例如，您可能已经请求WiFi JobInfo.Builder.setRequiredNetworkType(int)，但是当您的工作正在执行用户切换WiFi时。
     * 另一个例子是如果你指定了 JobInfo.Builder.setRequiresDeviceIdle(boolean)，手机就会离开它的空闲维护窗口。在收到此消息后，您对应用程序的行为负全部责任; 如果你忽略它，你的应用可能会开始行为不端。
     *
     * 一旦此方法返回，系统将代表作业释放它所持有的唤醒锁。
     * @param params 标识此作业的参数，提供给回调中的作业
     * @return 	true向JobManager表明您是否希望根据创建工作时提供的重试标准重新安排此职位; 或false 完全结束工作。无论返回的值如何，您的工作都必须停止执行。
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStopJob方法");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        Log.i("MyJobDaemonService", "调度job");
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, MyJobDaemonService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        //间隔1000毫秒
//        builder.setPeriodic(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(3 * 1000);
        } else {
            builder.setPeriodic(3 * 1000);
        }
        return builder.build();
    }

    // 判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


}
