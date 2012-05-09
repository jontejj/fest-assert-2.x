/*
 * Created on 9 May, 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright @2010-2011 the original author or authors.
 */
package org.fest.assertions.internal;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Collections.unmodifiableSortedMap;
import static java.util.Collections.unmodifiableSortedSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class Unmodifiables {
  private Unmodifiables() {
  }

  // Never exposed so no need to serialize
  @SuppressWarnings("serial")
  private static Set<Class<?>> UNMODIFIABLE_CLASSES = new HashSet<Class<?>>() {
    {
      add(unmodifiableCollection(emptyList()).getClass());
      add(unmodifiableList(emptyList()).getClass());
      add(unmodifiableMap(emptyMap()).getClass());
      add(unmodifiableSet(emptySet()).getClass());
      add(unmodifiableSortedMap(new TreeMap<Object, Object>()).getClass());
      add(unmodifiableSortedSet(new TreeSet<Object>()).getClass());
      add(emptyList().getClass());
      add(emptySet().getClass());
      add(emptyMap().getClass());

    }
  };

  /**
   * 
   * @param collection the collection to check
   * @return true if the given collection is/is wrapped in one of the {@link Collections} unmodifiable types
   */
  public static <T> boolean isBuiltInUnmodifiableCollection(Collection<T> collection) {
    return UNMODIFIABLE_CLASSES.contains(collection.getClass());
  }
}
