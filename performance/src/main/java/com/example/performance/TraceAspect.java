
package com.example.performance;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.example.performance.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 截获类名最后含有Activity、Layout的类的所有方法
 * 监听目标方法的执行时间
 */
@Aspect
public class TraceAspect {
  private static Object currentObject = null;

  private static final String POINTCUT_METHOD =
      "(execution(* *..Activity+.*(..)) ||execution(* *..Layout+.*(..))) && target(Object) && this(Object)";

  @Pointcut(POINTCUT_METHOD)
  public void methodAnnotated() {}



    /**
     *  截获原方法，并替换
     * @param joinPoint
     * @return
     * @throws Throwable
     */
  @Around("methodAnnotated()")
  public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

    if (currentObject == null){
        currentObject = joinPoint.getTarget();
    }
      //初始化计时器
    final StopWatch stopWatch = new StopWatch();
      //开始监听
      stopWatch.start();
      //调用原方法的执行。
    Object result = joinPoint.proceed();
      //监听结束
    stopWatch.stop();
      //获取方法信息对象
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      String className;
      //获取当前对象，通过反射获取类别详细信息
      className = joinPoint.getThis().getClass().getName();

      String methodName = methodSignature.getName();
      String msg =  buildLogMessage(methodName, stopWatch.getTotalTime(1));
      Log.e(className,msg);
      /*
    if (currentObject != null && currentObject.equals(joinPoint.getTarget())){
        DebugLog.log(new MethodMsg(className,msg,stopWatch.getTotalTime(1)));
    }else if(currentObject != null && !currentObject.equals(joinPoint.getTarget())){
        DebugLog.log(new MethodMsg(className, msg,stopWatch.getTotalTime(1)));

        currentObject = joinPoint.getTarget();
//        DebugLog.outPut(new Path());    //日志存储
//        DebugLog.ReadIn(new Path());    //日志读取

    }*/
    return result;
  }

  /**
   * 创建一个日志信息
   *
   * @param methodName 方法名
   * @param methodDuration 执行时间
   * @return
   */
  private static String buildLogMessage(String methodName, double methodDuration) {
    StringBuilder message = new StringBuilder();
    message.append(methodName);
    message.append(" --> ");
    message.append("[");
    message.append(methodDuration);
    if (StopWatch.Accuracy == 1){
      message.append("ms");
    }else {
      message.append("mic");
    }
    message.append("]      \n");

    return message.toString();
  }



}
