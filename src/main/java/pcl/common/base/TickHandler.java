package pcl.common.base;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.ITickAgent;
import cpw.mods.fml.common.ITickHandler;

public abstract class TickHandler implements ITickHandler {

	protected ReentrantLock childLock = new ReentrantLock();
	protected ArrayList<ITickAgent> children = new ArrayList<ITickAgent>();
	protected ArrayList<ITickAgent> newChildren = new ArrayList<ITickAgent>();

	public void registerTickHost(ITickAgent host) {
		try {
			childLock.lock();
			if (!newChildren.contains(host))
				newChildren.add(host);
			childLock.unlock();
		} catch (Throwable t) {
		} finally {
			if (childLock.isLocked())
				childLock.unlock();
		}
	}

	protected void updateChildren() {
		// Only allow modification of the child stack when the child input
		// stack is not locked; don't wait to obtain the lock either.
		if (!childLock.isLocked())
			if (newChildren.size() > 0)
				try {
					childLock.lock();
					for (ITickAgent host : newChildren)
						children.add(host);
					newChildren.clear();
					childLock.unlock();
				} catch (Throwable t) {
				} finally {
					if (childLock.isLocked())
						childLock.unlock();
				}
	}

	protected void tickChildren() {
		for (ITickAgent host : children)
			try {
				host.advance();
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARNING, "Unhandled exception in ITickAgent.", t);
			}
	}

}
