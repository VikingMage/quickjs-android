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

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Type;

import static com.hippo.quickjs.android.Utils.assertException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class JSContextTest {

  @Ignore("There is no guarantee that this test will pass")
  @Test
  public void testJSValueGC() {
    QuickJS quickJS = new QuickJS.Builder().build();

    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        int jsValueCount = 3;

        assertEquals(0, context.getNotRemovedJSValueCount());

        for (int i = 0; i < jsValueCount; i++) {
          context.evaluate("1", "unknown.js", int.class);
        }

        assertEquals(jsValueCount, context.getNotRemovedJSValueCount());

        Runtime.getRuntime().gc();
        Runtime.getRuntime().gc();

        context.evaluate("1", "unknown.js", int.class);

        assertEquals(1, context.getNotRemovedJSValueCount());
      }
    }
  }

  @Test
  public void throwException() {
    QuickJS quickJS = new QuickJS.Builder().build();
    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        assertException(
            JSEvaluationException.class,
            "Throw: 1\n",
            () -> context.evaluate("throw 1", "unknown.js")
        );
      }
    }
  }

  @Test
  public void throwExceptionWithReturn() {
    QuickJS quickJS = new QuickJS.Builder().build();
    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        assertException(
            JSEvaluationException.class,
            "Throw: 1\n",
            () -> context.evaluate("throw 1", "unknown.js", int.class)
        );
      }
    }
  }

  @Test
  public void getGlobalObject() {
    QuickJS quickJS = new QuickJS.Builder().build();
    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        context.evaluate("a = 1", "unknown.js", int.class);
        assertEquals(1, context.getGlobalObject().getProperty("a").cast(JSNumber.class).getInt());

        context.getGlobalObject().setProperty("b", context.createJSString("string"));
        assertEquals("string", context.evaluate("b", "unknown.js", String.class));
      }
    }
  }

  @Test
  public void evaluateWithoutReturn() {
    QuickJS quickJS = new QuickJS.Builder().build();
    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        context.evaluate("a = {}", "test.js");
        assertThat(context.getGlobalObject().getProperty("a")).isInstanceOf(JSObject.class);
      }
    }
  }

  @Test
  public void createValueFunctionNoMethod() {
    QuickJS quickJS = new QuickJS.Builder().build();
    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        Method method = new Method(long.class, "atoi", new Type[] { String.class });
        assertException(
            NoSuchMethodError.class,
            "no non-static method \"Ljava/lang/Integer;.atoi(Ljava/lang/String;)J\"",
            () -> context.createJSFunction(1, method)
        );
        assertException(
            NoSuchMethodError.class,
            "no static method \"Ljava/lang/Integer;.atoi(Ljava/lang/String;)J\"",
            () -> context.createJSFunctionS(Integer.class, method)
        );
      }
    }
  }

  @Test
  public void createValueFunctionNull() {
    QuickJS quickJS = new QuickJS.Builder().build();
    try (JSRuntime runtime = quickJS.createJSRuntime()) {
      try (JSContext context = runtime.createJSContext()) {
        Method method = new Method(int.class, "atoi", new Type[] { String.class });

        assertException(
            NullPointerException.class,
            "instance == null",
            () -> context.createJSFunction(null, method)
        );

        assertException(
            NullPointerException.class,
            "method == null",
            () -> context.createJSFunction(1, null)
        );

        assertException(
            NullPointerException.class,
            "clazz == null",
            () -> context.createJSFunctionS(null, method)
        );

        assertException(
            NullPointerException.class,
            "method == null",
            () -> context.createJSFunctionS(Class.class, null)
        );
      }
    }
  }
}
