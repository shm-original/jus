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

import io.apptik.comm.jus.error.JusError;

/**
 * Indicates that the server's response could not be parsed.
 */
@SuppressWarnings("serial")
public class ParseError extends JusError {


    public ParseError(Throwable cause) {
        super(cause);
    }

    public ParseError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public ParseError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ParseError() {
        super();
    }

    public ParseError(NetworkResponse response) {
        super(response);
    }
}
