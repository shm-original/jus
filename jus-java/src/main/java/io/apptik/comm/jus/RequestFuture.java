/*
 * Copyright (C) 2015 Apptik Project
 * Copyright (C) 2014 Kalin Maldzhanski
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apptik.comm.jus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.apptik.comm.jus.error.JusError;

/**
 * A Future that represents a Jus request.
 *
 * Used by providing as your response and error listeners. For example:
 * <pre>
 * RequestFuture&lt;JSONObject&gt; future = RequestFuture.newFuture();
 * MyRequest request = new MyRequest(URL, future, future);
 *
 * // If you want to be able to cancel the request:
 * future.setRequest(requestQueue.add(request));
 *
 * // Otherwise:
 * requestQueue.add(request);
 *
 * try {
 *   JSONObject response = future.get();
 *   // do something with response
 * } catch (InterruptedException e) {
 *   // handle the error
 * } catch (ExecutionException e) {
 *   // handle the error
 * }
 * </pre>
 *
 * @param <T> The type of parsed response this future expects.
 */
public class RequestFuture<T> implements Future<T>, RequestListener.ResponseListener<T>,
       RequestListener.ErrorListener {
    private Request<T> request;
    private boolean resultReceived = false;
    private T result;
    private JusError exception;

    public static <E> RequestFuture<E> newFuture() {
        return new RequestFuture<E>();
    }

   // public RequestFuture() {}

    public RequestFuture<T> setRequest(Request<T> request) {
        this.request = request;
        this.request.addErrorListener(this);
        this.request.addResponseListener(this);
        return this;
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (request == null) {
            return false;
        }

        if (!isDone()) {
            request.cancel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    private synchronized T doGet(Long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (exception != null) {
            throw new ExecutionException(exception);
        }

        if (resultReceived) {
            return result;
        }

        if (timeoutMs == null) {
            wait(0);
        } else if (timeoutMs > 0) {
            wait(timeoutMs);
        }

        if (exception != null) {
            throw new ExecutionException(exception);
        }

        if (!resultReceived) {
            throw new TimeoutException();
        }

        return result;
    }

    @Override
    public boolean isCancelled() {
        return request != null && request.isCanceled();
    }

    @Override
    public synchronized boolean isDone() {
        return resultReceived || exception != null || isCancelled();
    }

    @Override
    public synchronized void onResponse(T response) {
        resultReceived = true;
        result = response;
        notifyAll();
    }

    @Override
    public synchronized void onError(JusError error) {
        exception = error;
        notifyAll();
    }
}

