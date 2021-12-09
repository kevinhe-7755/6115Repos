/*******************************************************************************
 * Copyright (c) 1997, 2018 by ProSyst Software GmbH
 * http://www.prosyst.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    ProSyst Software GmbH - initial API and implementation
 *******************************************************************************/

package org.eclipse.equinox.internal.util.impl.tpt.timer;

import org.eclipse.equinox.internal.util.impl.tpt.ServiceFactoryImpl;
import org.eclipse.equinox.internal.util.impl.tpt.threadpool.ThreadPoolFactoryImpl;
import org.eclipse.equinox.internal.util.ref.Log;
import org.eclipse.equinox.internal.util.timer.Timer;
import org.eclipse.equinox.internal.util.timer.TimerListener;

/**
 * @author Pavlin Dobrev
 * @version 1.0
 */

public class TimerFactory extends ServiceFactoryImpl<Timer> implements Timer {
	private static TimerImpl timer;

	public TimerFactory(String bundleName, ThreadPoolFactoryImpl factory, Log log) {

		super(bundleName, log);
		timer = new TimerImpl(factory);
	}

	public TimerFactory(String bundleName) {
		super(bundleName);
	}

	@Override
	public Timer getInstance(String bundleName) {
		if (timer == null)
			throw new RuntimeException("ServiceFactory is currently off!");
		return new TimerFactory(bundleName);
	}

	@Override
	@Deprecated
	public void notifyAfterMillis(TimerListener listener, long timePeriod, int event) throws IllegalArgumentException {
		addNotifyListener(listener, Thread.NORM_PRIORITY, Timer.ONE_SHOT_TIMER, timePeriod, event);
	}

	@Override
	@Deprecated
	public void notifyAfterMillis(TimerListener listener, int priority, long timePeriod, int event) throws IllegalArgumentException {
		addNotifyListener(listener, priority, Timer.ONE_SHOT_TIMER, timePeriod, event);
	}

	@Override
	@Deprecated
	public void notifyAfter(TimerListener listener, int timePeriod, int event) throws IllegalArgumentException {
		addNotifyListener(listener, Thread.NORM_PRIORITY, Timer.ONE_SHOT_TIMER, timePeriod * 1000, event);
	}

	@Override
	@Deprecated
	public void notifyAfter(TimerListener listener, int priority, int timePeriod, int event) throws IllegalArgumentException {
		addNotifyListener(listener, priority, Timer.ONE_SHOT_TIMER, timePeriod * 1000, event);
	}

	@Override
	public void addNotifyListener(TimerListener listener, int priority, int timerType, long periodMilis, int event) {
		TimerImpl tmp = timer;
		if (tmp == null)
			throw new RuntimeException("This is a zombie!");
		tmp.addNotifyListener(listener, priority, timerType, periodMilis, event, bundleName);
	}

	public static void stopTimer() {
		if (timer != null) {
			timer.terminate();
			timer = null;
		}
	}

	@Override
	public void removeListener(TimerListener listener, int event) {
		TimerImpl tmp = timer;
		if (tmp == null)
			throw new RuntimeException("This is a zombie!");
		tmp.removeListener(listener, event);
	}
}
