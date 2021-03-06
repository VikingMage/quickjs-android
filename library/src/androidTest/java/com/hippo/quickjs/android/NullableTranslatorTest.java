/*
 * Copyright 2019 Hippo Seven
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

package com.hippo.quickjs.android;

import org.junit.Test;

import static com.hippo.quickjs.android.Utils.assertEquivalent;
import static com.hippo.quickjs.android.Utils.assertException;

public class NullableTranslatorTest {

  @Test
  public void nonNullElement() {
    GenericType<String[]> type = new GenericType<>(
        Types.nonNullOf(
            Types.arrayOf(
                Types.nonNullOf(
                    String.class
                )
            )
        )
    );

    assertEquivalent("['str', 'ing']", new String[] { "str", "ing" }, type);

    assertException(
        "null",
        type,
        JSDataException.class,
        "Unexpected js tag " + JSContext.TYPE_NULL + " for pickle flag " + Translator.PICKLE_FLAG_TYPE_ARRAY
    );

    assertException(
        "['str', null, 'ing']",
        type,
        JSDataException.class,
        "Unexpected js tag " + JSContext.TYPE_NULL + " for pickle flag " + Translator.PICKLE_FLAG_TYPE_STRING
    );
  }
}
