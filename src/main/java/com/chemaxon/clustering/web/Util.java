/*
 * Copyright 2016 ChemAxon Ltd.
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
 *
 */
package com.chemaxon.clustering.web;

import java.util.Map;

/**
 * Various small utilities.
 *
 * @author Gabor Imre
 */
public final class Util {
    /**
     * No constructor exposed.
     */
    public Util() {}

    /**
     * Construct a unique key.
     *
     * @param aMap A {@code map}.
     * @param suggestion Suggestion for a unique key.
     * @return A key not contained by the passed {@code map}. The passed {@code suggestion} with an optional
     * sequence number is returned.
     */
    public static String constructUniqueKey(Map<String, ?> aMap, String suggestion) {
        if (!aMap.containsKey(suggestion)) {
            return suggestion;
        }
        int count = 1;
        while (true) {
            final String candidate = suggestion + '-' + count;
            if (!aMap.containsKey(candidate)) {
                return candidate;
            }
            count ++;
        }
    }

}
