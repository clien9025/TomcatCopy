package org.apache.tomcat.util.threads;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class LimitLatch {

    private class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        Sync() {
        }

        @Override
        protected int tryAcquireShared(int ignored) {
            long newCount = count.incrementAndGet();
            if (!released && newCount > limit) {
                // Limit exceeded
                count.decrementAndGet();
                return -1;
            } else {
                return 1;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            count.decrementAndGet();
            return true;
        }
    }

    private final Sync sync;
    private final AtomicLong count;
    private volatile long limit;
    private volatile boolean released = false;
    /**
     * Instantiates a LimitLatch object with an initial limit.
     * @param limit - maximum number of concurrent acquisitions of this latch
     */
    public LimitLatch(long limit) {
        this.limit = limit;
        this.count = new AtomicLong(0);
        this.sync = new Sync();
    }


    /**
     * Releases all waiting threads and causes the {@link #limit} to be ignored
     * until {@link #reset()} is called.
     * @return <code>true</code> if release was done
     */
    public boolean releaseAll() {
        released = true;
        return sync.releaseShared(0);
    }

    /**
     * Sets a new limit. If the limit is decreased there may be a period where
     * more shares of the latch are acquired than the limit. In this case no
     * more shares of the latch will be issued until sufficient shares have been
     * returned to reduce the number of acquired shares of the latch to below
     * the new limit. If the limit is increased, threads currently in the queue
     * may not be issued one of the newly available shares until the next
     * request is made for a latch.
     *
     * 设置一个新的限制。如果限制减少了，可能会有一段时间内获取的锁的份额超过了限制。
     * 在这种情况下，直到足够的锁份额被返回并使得获取的锁的份额数降低到新限制以下，
     * 否则不会再发放更多的锁份额。如果限制增加了，在队列中的线程可能不会立即获取到新可用的份额，
     * 直到下一个对锁的请求发生。
     *
     * 这个方法用于调整LimitLatch的限制值，这直接影响着可以并发运行的操作数量。
     *
     * @param limit The new limit
     */
    public void setLimit(long limit) {
        this.limit = limit;
    }
}
