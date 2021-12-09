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

package org.eclipse.equinox.internal.util.impl.tpt.threadpool;

import java.security.*;
import org.eclipse.equinox.internal.util.UtilActivator;
import org.eclipse.equinox.internal.util.impl.tpt.ServiceFactoryImpl;
import org.eclipse.equinox.internal.util.threadpool.ThreadContext;

/**
 * @author Pavlin Dobrev
 */
public class Executor extends Thread implements ThreadContext {

	public static final String iname = "[ThreadPool Manager] - Idle Thread";
	public static final String nullname = "[ThreadPool Manager] - Occupied Thread ";
	public static final String xname = "ThreadPool Manager Thread";

	ThreadPoolFactoryImpl factory;
	boolean accessed = false;

	private Runnable job = null;

	private boolean terminated = false;

	AccessControlContext acc;
	PEA pea;
	static ClassLoader defaultTCCL;

	static {
		defaultTCCL = Thread.currentThread().getContextClassLoader();
	}

	public synchronized void setRunnable(Runnable job, String name, ThreadPoolFactoryImpl factory, AccessControlContext acc) {
		this.job = job;
		this.factory = factory;

		if (ServiceFactoryImpl.useNames)
			setName(name == null ? nullname : name);

		this.acc = acc;

		notify();
	}

	@Override
	public void run() {
		while (!terminated) {
			if (job != null) {
				try {
					if (UtilActivator.debugLevel == 2 && UtilActivator.LOG_DEBUG) {
						UtilActivator.log.debug(0x0100, 10003, getName(), null, false);
					}
					accessed = true;
					if (acc != null) {
						if (pea == null)
							pea = new PEA();
						pea.job = job;
						AccessController.doPrivileged(pea, acc);
					} else
						job.run();
				} catch (Throwable t) {
					try { // fix memory leak
						throw new Exception();
					} catch (Exception _) {
					}
					if (ServiceFactoryImpl.log != null) {
						ServiceFactoryImpl.log.error("[ThreadPool Manager]\r\nException while executing: \r\nNAME: " + this + "\r\nJOB: " + job + "\r\n", t);
					}

				} finally {
					if (getContextClassLoader() != defaultTCCL) {
						setContextClassLoader(defaultTCCL);
					}
				}
				if (UtilActivator.debugLevel == 2 && UtilActivator.LOG_DEBUG) {
					UtilActivator.log.debug(0x0100, 10004, getName(), null, false);
				}
				job = null;
				if (ServiceFactoryImpl.useNames)
					setName(iname);
				if (ThreadPoolManagerImpl.threadPool == null || (!ThreadPoolManagerImpl.threadPool.releaseObject(this))) {
					terminated = true;
					continue;
				}
			}

			if (this.job != null) {
				continue;
			}
			synchronized (this) {
				if (this.job != null || terminated) {
					continue;
				}

				try {
					wait();
				} catch (InterruptedException ie) {
				}
			}
		}

		clear();
	}

	void clear() {
		job = null;
	}

	public synchronized void terminate() {
		terminated = true;
		notify();
	}

	public Executor() {
		super(ServiceFactoryImpl.useNames ? iname : xname);
		if (getContextClassLoader() != defaultTCCL) {
			setContextClassLoader(defaultTCCL);
		}
		start();
	}

	@Override
	public Runnable getRunnable() {
		return job;
	}

	public void setPriorityI(int p) {
		if (this.getPriority() != p) {
			setPriority(p);
		}
	}
}

class PEA implements PrivilegedAction<Object> {

	Runnable job;

	@Override
	public Object run() {
		job.run();
		return null;
	}
}
