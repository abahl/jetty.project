//
// ========================================================================
// Copyright (c) 1995-2022 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.io;

import java.nio.ByteBuffer;

import org.eclipse.jetty.util.component.Container;

/**
 * <p>A {@link RetainableByteBuffer} pool.</p>
 * <p>Acquired buffers <b>must</b> be released by calling {@link RetainableByteBuffer#release()} otherwise the memory they hold will
 * be leaked.</p>
 */
public interface RetainableByteBufferPool
{
    /**
     * Acquires a memory buffer from the pool.
     * @param size The size of the buffer. The returned buffer will have at least this capacity.
     * @param direct true if a direct memory buffer is needed, false otherwise.
     * @return a memory buffer.
     */
    RetainableByteBuffer acquire(int size, boolean direct);

    /**
     * Finds a {@link RetainableByteBufferPool} implementation in the given container, or wrap the given
     * {@link ByteBufferPool} with an adapter.
     * @param container the container to search for an existing memory pool.
     * @param byteBufferPool Use  {@link ByteBufferPool#asRetainableByteBufferPool()} to convert if no memory pool was found in the container.
     * @return the {@link RetainableByteBufferPool} found or the converted one.
     */
    static RetainableByteBufferPool findOrAdapt(Container container, ByteBufferPool byteBufferPool)
    {
        RetainableByteBufferPool retainableByteBufferPool = container == null ? null : container.getBean(RetainableByteBufferPool.class);
        return retainableByteBufferPool == null ? byteBufferPool.asRetainableByteBufferPool() : retainableByteBufferPool;
    }

    static RetainableByteBufferPool from(ByteBufferPool byteBufferPool)
    {
        return new RetainableByteBufferPool()
        {
            @Override
            public RetainableByteBuffer acquire(int size, boolean direct)
            {
                ByteBuffer byteBuffer = byteBufferPool.acquire(size, direct);
                RetainableByteBuffer retainableByteBuffer = new RetainableByteBuffer(byteBuffer, this::release);
                retainableByteBuffer.acquire();
                return retainableByteBuffer;
            }

            private void release(RetainableByteBuffer retainedBuffer)
            {
                byteBufferPool.release(retainedBuffer.getBuffer());
            }

            @Override
            public String toString()
            {
                return String.format("NonRetainableByteBufferPool@%x{%s}", hashCode(), byteBufferPool.toString());
            }
        };
    }
}
