/*
 * Copyright 2008-2009 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.springsource.bundlor.support.propertysubstitution;

/**
 * Identity transformer is a no-op transformer.
 * 
 * @author Andy Clement
 */
final class IdentityTransformer implements Transformer {

    static IdentityTransformer instance = new IdentityTransformer();

    // static shared instance should be used
    private IdentityTransformer() {

    }

    public <T> T transform(T input) {
        return input;
    }

    public String toString() {
        return "=";
    }
}