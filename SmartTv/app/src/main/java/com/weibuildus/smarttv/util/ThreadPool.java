/** 
 * @copyright  2015, Wu Maojie
 */
package com.weibuildus.smarttv.util;

import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * 
 * @ClassName: ThreadPool
 * @author wumaojie.gmail.com
 * @date 2015-12-23 上午9:47:36
 */
public class ThreadPool {

	// 线程池单核最大线程数量
	private static final int POOL_SIZE = 4;
	// 线程池工具单列对象
	private static ThreadPool threadPool;

	// 固定数量线程池
	private ExecutorService executorServiceFixed;
	// 单线程池
	private ExecutorService executorServiceSingle;

	/**
	 * 创建线程池工具对象
	 */
	private ThreadPool() {
		// 获取当前系统的CPU 数目
		int cpuNums = Runtime.getRuntime().availableProcessors();
		executorServiceFixed = Executors
				.newFixedThreadPool(cpuNums * POOL_SIZE);
		executorServiceSingle = Executors.newSingleThreadExecutor();
	}

	/**
	 * 获取单列对象
	 * 
	 * @Title getInstance
	 * @return
	 */
	private static ThreadPool getInstance() {
		if (threadPool == null) {
			threadPool = new ThreadPool();
		}
		return threadPool;
	}

	/**
	 * 当前线程休眠
	 * 
	 * @Title: sleep
	 * @param time
	 * @throws
	 */
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 休眠
	 * @Title  executeDelay 
	 * @param time
	 * @param callBack
	 */
	public static void executeDelay(final long time,
			final ThreadPoolMethodCallBack callBack) {
		getInstance().executeO(new Runnable() {
			@Override
			public void run() {
				sleep(time);
				getInstance().handler.sendMessage(getInstance().handler
						.obtainMessage(CALLBACK_MOTHOD,
								getInstance().new MethodCallBackParameter(
										callBack, null, null)));
			}
		});
	}

	/**
	 * 单线程池队列执行
	 * 
	 * @Title executeSequence
	 * @param command
	 */
	public static void executeSequence(Runnable command) {
		getInstance().executeSequenceO(command);
	}

	/**
	 * 非队列执行线程
	 * 
	 * @Title execute
	 * @param command
	 */
	public static void execute(Runnable command) {
		getInstance().executeO(command);
	}

	/**
	 * 单线程按队列异步执行方法
	 * 
	 * @Title executorMethodSequence
	 * @param callBack
	 * @param object
	 * @param methodName
	 * @param parameters
	 */
	public static void executorMethodSequence(
			final ThreadPoolMethodCallBack callBack, final Object object,
			final String methodName, final Object... parameters) {
		getInstance().executorMethodSequenceO(callBack, object, methodName,
				parameters);
	}

	/**
	 * 非队列异步执行方法
	 * 
	 * @Title executorMethod
	 * @param callBack
	 * @param object
	 * @param methodName
	 * @param parameters
	 */
	public static void executorMethod(final ThreadPoolMethodCallBack callBack,
			final Object object, final String methodName,
			final Object... parameters) {
		getInstance().executorMethodO(callBack, object, methodName, parameters);
	}

	/**
	 * 单线程池队列执行
	 * 
	 * @Title: executeSequenceO
	 * @Description: TODO
	 * @param command
	 *            void
	 * @throws
	 */
	private void executeSequenceO(Runnable command) {
		executorServiceSingle.execute(command);
	}

	/**
	 * 非队列执行线程
	 * 
	 * @Title: executeO
	 * @param command
	 * @throws
	 */
	private void executeO(Runnable command) {
		executorServiceFixed.execute(command);
	}

	/**
	 * 单线程按队列异步执行方法
	 * 
	 * @Title: executorMethodSequenceO
	 * @param callBack
	 *            同步返回
	 * @param object
	 * @param methodName
	 * @param parameters
	 *            void
	 * @throws
	 */
	private void executorMethodSequenceO(
			final ThreadPoolMethodCallBack callBack, final Object object,
			final String methodName, final Object... parameters) {
		executorMethod(true, callBack, object, methodName, parameters);
	}

	/**
	 * 非队列异步执行方法
	 * 
	 * @Title: executorMethodO
	 * @param callBack
	 *            同步返回
	 * @param object
	 * @param methodName
	 * @param parameters
	 * @throws
	 */
	private void executorMethodO(final ThreadPoolMethodCallBack callBack,
			final Object object, final String methodName,
			final Object... parameters) {
		executorMethod(false, callBack, object, methodName, parameters);
	}

	/**
	 * 异步执行方法 实现逻辑
	 * 
	 * @Title: executorMethod
	 * @param callBack
	 * @param object
	 * @param methodName
	 * @param parameters
	 * @throws
	 */
	private void executorMethod(boolean isSequence,
			final ThreadPoolMethodCallBack callBack, final Object object,
			final String methodName, final Object... parameters) {
		ExecutorService executorService = isSequence ? executorServiceSingle
				: executorServiceFixed;
		executorService.execute(new Thread() {
			public void run() {
				Class<?>[] classes = new Class<?>[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == null) {
						classes[i] = null;
					} else {
						classes[i] = parameters[i].getClass();
					}
				}
				try {
					Method method = null;
					Class<?> clas = object.getClass();
					Method[] methods = clas.getDeclaredMethods();
					do {
						for (int i = 0; i < methods.length; i++) {
							// 方法名相同
							if (methods[i].getName().equals(methodName)) {
								// 参数个数相同
								if (methods[i].getParameterTypes().length == classes.length) {
									if (classes.length == 0) {
										method = methods[i];
										break;
									}
									Class<?>[] pcls = methods[i]
											.getParameterTypes();
									boolean isOne = false;
									for (int j = 0; j < pcls.length; j++) {
										if (classes[j] == null) {
											isOne = true;
										} else {
											if (conversionBasicType(pcls[j])
													.isAssignableFrom(
															conversionBasicType(classes[j]))) {
												isOne = true;
											} else {
												isOne = false;
												break;
											}
										}
									}
									if (isOne) {
										method = methods[i];
										break;
									}
								}
							}
						}
						if (method == null) {
							clas = clas.getSuperclass();
							if (clas == Object.class) {
								break;
							} else {
								methods = clas.getDeclaredMethods();
							}
						}
					} while (method == null);
					if (method != null) {
						method.setAccessible(true);
						Object obj = method.invoke(object, parameters);
						handler.sendMessage(handler.obtainMessage(
								CALLBACK_MOTHOD, new MethodCallBackParameter(
										callBack, obj, methodName)));
					} else {
						throw new NoSuchMethodException("没有找到该方法");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 基本类型转换
	 * 
	 * @Title: conversionBasicType
	 * @Description: TODO
	 * @param classO
	 * @return Class<?>
	 * @throws
	 */
	private Class<?> conversionBasicType(Class<?> classO) {
		if (classO.getName().hashCode() == int.class.getName().hashCode()) {
			return Integer.class;
		}
		if (classO.getName().hashCode() == byte.class.getName().hashCode()) {
			return Byte.class;
		}
		if (classO.getName().hashCode() == short.class.getName().hashCode()) {
			return Short.class;
		}
		if (classO.getName().hashCode() == float.class.getName().hashCode()) {
			return Float.class;
		}
		if (classO.getName().hashCode() == double.class.getName().hashCode()) {
			return Double.class;
		}
		if (classO.getName().hashCode() == char.class.getName().hashCode()) {
			return Character.class;
		}
		if (classO.getName().hashCode() == long.class.getName().hashCode()) {
			return Long.class;
		}
		if (classO.getName().hashCode() == boolean.class.getName().hashCode()) {
			return Boolean.class;
		}
		return classO;
	}

	/** 以下是异步线程同步返回代码 */
	private static final int CALLBACK_MOTHOD = 1;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CALLBACK_MOTHOD:
				if (msg.obj != null
						&& msg.obj instanceof MethodCallBackParameter) {
					((MethodCallBackParameter) msg.obj).callback();
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 返回数据组装对象类
	 * 
	 * @ClassName: MethodCallBackParameter
	 * @author wumaojie.gmail.com
	 * @date 2015-12-23 上午9:56:08
	 */
	private class MethodCallBackParameter {

		private Object object;
		private String methodName;

		private ThreadPoolMethodCallBack callBack;

		public MethodCallBackParameter(ThreadPoolMethodCallBack callBack,
				Object object, String methodName) {
			super();
			this.callBack = callBack;
			this.object = object;
			this.methodName = methodName;
		}

		public void callback() {
			if (callBack != null) {
				callBack.callBack(methodName, object);
			}
		}
	}

	/**
	 * 线程异步执行方法返回
	 * 
	 * @ClassName: ThreadPoolMethodCallBack
	 * @author wumaojie.gmail.com
	 * @date 2015-12-23 下午1:55:26
	 */
	public interface ThreadPoolMethodCallBack {
		void callBack(String methodName, Object object);
	}
}
