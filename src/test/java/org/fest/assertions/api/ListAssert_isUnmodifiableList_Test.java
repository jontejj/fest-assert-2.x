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
package org.fest.assertions.api;

import static java.util.Collections.emptyList;
import static junit.framework.Assert.assertSame;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.util.Collections.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingList;

/**
 * Tests for <code>{@link ListAssert#isUnmodifiableList()}</code>.
 * 
 * @author Jonatan Jönsson
 */
public class ListAssert_isUnmodifiableList_Test {

  @Test(expected = AssertionError.class)
  public void should_verify_that_modifiable_list_is_not_unmodifiable() {
    List<String> modifiableList = list("foo", "bar");
    assertThat(modifiableList).isUnmodifiableList();
  }

  @Test
  public void should_verify_that_unmodifiable_list_is_unmodifiable() {
    List<String> unmodifiableList = Collections.unmodifiableList(list("foo", "bar"));
    assertThat(unmodifiableList).isUnmodifiableList();
  }

  @Test
  public void should_verify_that_a_homebrewed_unmodifiable_list_is_unmodifiable() {
    UnmodifiableList<String> unmodifiableList = new UnmodifiableList<String>(Collections.unmodifiableList(Arrays.asList("foo")));
    assertThat(unmodifiableList).isUnmodifiableList();
  }

  @Test(expected = AssertionError.class)
  public void should_verify_that_modifying_remove_is_detected() {
    UnmodifiableList<String> unmodifiableList = new InvalidRemoveList<String>(Collections.unmodifiableList(Arrays.asList("foo")));
    assertThat(unmodifiableList).isUnmodifiableList();
  }

  @Test(expected = AssertionError.class)
  public void should_verify_that_modifying_removeAll_is_detected() {
    UnmodifiableList<String> unmodifiableList = new InvalidRemoveAllList<String>(Collections.unmodifiableList(Arrays.asList("foo")));
    assertThat(unmodifiableList).isUnmodifiableList();
  }

  @Test(expected = AssertionError.class)
  public void should_verify_that_modifying_iterator_is_detected() {
    UnmodifiableList<String> unmodifiableList = new InvalidIteratorRemove<String>(Collections.unmodifiableList(Arrays.asList("foo")));
    assertThat(unmodifiableList).isUnmodifiableList();
  }

  @Test
  public void should_return_this() {
    ListAssert assertions = new ListAssert(emptyList());
    assertSame(assertions, assertions.isUnmodifiableList());
  }

  private static class UnmodifiableList<E> extends ForwardingList<E> {

    List<E> wrappedList;

    public UnmodifiableList(List<E> list) {
      wrappedList = list;
    }

    @Override
    protected List<E> delegate() {
      return wrappedList;
    }
  }

  private static class InvalidRemoveList<E> extends UnmodifiableList<E> {

    public InvalidRemoveList(List<E> list) {
      super(list);
    }

    @Override
    public boolean remove(Object object) {
      return true;
    }
  }

  private static class InvalidRemoveAllList<E> extends UnmodifiableList<E> {

    public InvalidRemoveAllList(List<E> list) {
      super(list);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
      return true;
    }
  }

  private static class InvalidIteratorRemove<E> extends UnmodifiableList<E> {

    public InvalidIteratorRemove(List<E> list) {
      super(list);
    }

    @Override
    public Iterator<E> iterator() {
      final Iterator<E> realIterator = super.iterator();
      return new ForwardingIterator<E>() {
        @Override
        protected Iterator<E> delegate() {
          return realIterator;
        }

        @Override
        public void remove() {
        }
      };
    }
  }
}
