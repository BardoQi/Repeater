package com.weishang.repeater.utils;


/**
 * 简易版AsyncTask,用于执行简单耗时操作,提供UI更新回调
 * 
 * @author momo
 * @Date 2014/1/14
 */
public class HandleTask {

	public static <T> void run(final TaskAction<T> action) {
		RunnableUtils.runWithExecutor(new Runnable() {
			@Override
			public void run() {
				SimpleTaskAction<T> taskAction = new SimpleTaskAction<T>(action);
				taskAction.postRun(taskAction.run());
			}
		});
	}

	/**
	 * 任务执行回执
	 * 
	 * @author momo
	 * @param <T>
	 *            数据
	 */
	public interface TaskAction<T> {
		/**
		 * 子线程任务
		 */
		T run();

		/**
		 * 子线程执行完任务
		 * 
		 * @param t
		 */
		void postRun(T t);
	}
}
