/*
 * Created on Aug 4, 2010
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

import static org.fest.assertions.error.ShouldBeEqual.shouldBeEqual;
import static org.fest.assertions.error.ShouldBeIn.shouldBeIn;
import static org.fest.assertions.error.ShouldBeInstance.shouldBeInstance;
import static org.fest.assertions.error.ShouldBeInstanceOfAny.shouldBeInstanceOfAny;
import static org.fest.assertions.error.ShouldBeLenientEqualByAccepting.shouldBeLenientEqualByAccepting;
import static org.fest.assertions.error.ShouldBeLenientEqualByIgnoring.shouldBeLenientEqualByIgnoring;
import static org.fest.assertions.error.ShouldBeSame.shouldBeSame;
import static org.fest.assertions.error.ShouldNotBeEqual.shouldNotBeEqual;
import static org.fest.assertions.error.ShouldNotBeIn.shouldNotBeIn;
import static org.fest.assertions.error.ShouldNotBeNull.shouldNotBeNull;
import static org.fest.assertions.error.ShouldNotBeSame.shouldNotBeSame;
import static org.fest.util.Collections.*;
import static org.fest.util.ToString.toStringOf;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.fest.assertions.core.AssertionInfo;
import org.fest.util.ComparatorBasedComparisonStrategy;
import org.fest.util.ComparisonStrategy;
import org.fest.util.IntrospectionError;
import org.fest.util.StandardComparisonStrategy;
import org.fest.util.VisibleForTesting;

/**
 * Reusable assertions for {@code Object}s.
 * 
 * @author Yvonne Wang
 * @author Alex Ruiz
 * @author Nicolas François
 */
public class Objects {

  private static final Objects INSTANCE = new Objects();

  /**
   * Returns the singleton instance of this class based on {@link StandardComparisonStrategy}.
   * @return the singleton instance of this class based on {@link StandardComparisonStrategy}.
   */
  public static Objects instance() {
    return INSTANCE;
  }

  @VisibleForTesting
  Failures failures = Failures.instance();
  
  @VisibleForTesting
  PropertySupport propertySupport = PropertySupport.instance();

  private final ComparisonStrategy comparisonStrategy;
  
  @VisibleForTesting
  Objects() {
    this(StandardComparisonStrategy.instance());
  }

  public Objects(ComparisonStrategy comparisonStrategy) {
    this.comparisonStrategy = comparisonStrategy;
  }
  
  @VisibleForTesting
  public Comparator<?> getComparator() {
    if (comparisonStrategy instanceof ComparatorBasedComparisonStrategy) {
      return ((ComparatorBasedComparisonStrategy)comparisonStrategy).getComparator();
    }
    return null;
  }

  /**
   * Verifies that the given object is an instance of the given type.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param type the type to check the given object against.
   * @throws NullPointerException if the given type is {@code null}.
   * @throws AssertionError if the given object is {@code null}.
   * @throws AssertionError if the given object is not an instance of the given type.
   */
  public void assertIsInstanceOf(AssertionInfo info, Object actual, Class<?> type) {
    if (type == null) throw new NullPointerException("The given type should not be null");
    assertNotNull(info, actual);
    if (type.isInstance(actual)) return;
    throw failures.failure(info, shouldBeInstance(actual, type));
  }

  /**
   * Verifies that the given object is an instance of any of the given types.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param types the types to check the given object against.
   * @throws NullPointerException if the given array is {@code null}.
   * @throws IllegalArgumentException if the given array is empty.
   * @throws NullPointerException if the given array has {@code null} elements.
   * @throws AssertionError if the given object is {@code null}.
   * @throws AssertionError if the given object is not an instance of any of the given types.
   */
  public void assertIsInstanceOfAny(AssertionInfo info, Object actual, Class<?>[] types) {
    checkIsNotNullAndIsNotEmpty(types);
    assertNotNull(info, actual);
    boolean found = false;
    for (Class<?> type : types) {
      if (type == null) {
        String format = "The given array of types:<%s> should not have null elements";
        throw new NullPointerException(String.format(format, toStringOf(types)));
      }
      if (type.isInstance(actual)) {
        found = true;
        break;
      }
    }
    if (found) return;
    throw failures.failure(info, shouldBeInstanceOfAny(actual, types));
  }

  private void checkIsNotNullAndIsNotEmpty(Class<?>[] types) {
    if (types == null) throw new NullPointerException("The given array of types should not be null");
    if (types.length == 0) throw new IllegalArgumentException("The given array of types should not be empty");
  }

  /**
   * Asserts that two objects are equal.
   * @param info contains information about the assertion.
   * @param actual the "actual" object.
   * @param expected the "expected" object.
   * @throws AssertionError if {@code actual} is not equal to {@code expected}. This method will throw a
   *           {@code org.junit.ComparisonFailure} instead if JUnit is in the classpath and the given objects are not
   *           equal.
   */
  public void assertEqual(AssertionInfo info, Object actual, Object expected) {
    if (areEqual(actual, expected)) return;
    throw failures.failure(info, shouldBeEqual(actual, expected, comparisonStrategy));
  }

  /**
   * Asserts that two objects are not equal.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param other the object to compare {@code actual} to.
   * @throws AssertionError if {@code actual} is equal to {@code other}.
   */
  public void assertNotEqual(AssertionInfo info, Object actual, Object other) {
    if (!areEqual(actual, other)) return;
    throw failures.failure(info, shouldNotBeEqual(actual, other, comparisonStrategy));
  }

  /**
   * Compares actual and other with standard strategy (null safe equals check).
   * @param actual the object to compare to other
   * @param other the object to compare to actual
   * @return true if actual and other are equal (null safe equals check), false otherwise.
   */
  private boolean areEqual(Object actual, Object other) {
    return comparisonStrategy.areEqual(other, actual);
  }

  /**
   * Asserts that the given object is {@code null}.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @throws AssertionError if the given object is not {@code null}.
   */
  public void assertNull(AssertionInfo info, Object actual) {
    if (actual == null) return;
    throw failures.failure(info, shouldBeEqual(actual, null, comparisonStrategy));
  }

  /**
   * Asserts that the given object is not {@code null}.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @throws AssertionError if the given object is {@code null}.
   */
  public void assertNotNull(AssertionInfo info, Object actual) {
    if (actual != null) return;
    throw failures.failure(info, shouldNotBeNull());
  }

  /**
   * Asserts that two objects refer to the same object.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param expected the expected object.
   * @throws AssertionError if the given objects do not refer to the same object.
   */
  public void assertSame(AssertionInfo info, Object actual, Object expected) {
    if (actual == expected) return;
    throw failures.failure(info, shouldBeSame(actual, expected));
  }

  /**
   * Asserts that two objects do not refer to the same object.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param other the object to compare {@code actual} to.
   * @throws AssertionError if the given objects refer to the same object.
   */
  public void assertNotSame(AssertionInfo info, Object actual, Object other) {
    if (actual != other) return;
    throw failures.failure(info, shouldNotBeSame(actual));
  }

  /**
   * Asserts that the given object is present in the given array.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param values the given array.
   * @throws NullPointerException if the given array is {@code null}.
   * @throws IllegalArgumentException if the given array is empty.
   * @throws AssertionError if the given object is not present in the given array.
   */
  public void assertIsIn(AssertionInfo info, Object actual, Object[] values) {
    checkIsNotNullAndNotEmpty(values);
    assertNotNull(info, actual);
    if (isItemInArray(actual, values)) return;
    throw failures.failure(info, shouldBeIn(actual, values, comparisonStrategy));
  }

  /**
   * Asserts that the given object is not present in the given array.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param values the given array.
   * @throws NullPointerException if the given array is {@code null}.
   * @throws IllegalArgumentException if the given array is empty.
   * @throws AssertionError if the given object is present in the given array.
   */
  public void assertIsNotIn(AssertionInfo info, Object actual, Object[] values) {
    checkIsNotNullAndNotEmpty(values);
    assertNotNull(info, actual);
    if (!isItemInArray(actual, values)) return;
    throw failures.failure(info, shouldNotBeIn(actual, values, comparisonStrategy));
  }

  private void checkIsNotNullAndNotEmpty(Object[] values) {
    if (values == null) throw new NullPointerException("The given array should not be null");
    if (values.length == 0) throw new IllegalArgumentException("The given array should not be empty");
  }

  /**
   * Returns <code>true</code> if given item is in given array, <code>false</code> otherwise. 
   * @param item the object to look for in arrayOfValues 
   * @param arrayOfValues the array of values
   * @return <code>true</code> if given item is in given array, <code>false</code> otherwise.
   */
  private boolean isItemInArray(Object item, Object[] arrayOfValues) {
    for (Object value : arrayOfValues)
      if (areEqual(value, item)) return true;
    return false;
  }

  /**
   * Asserts that the given object is present in the given collection.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param values the given iterable.
   * @throws NullPointerException if the given collection is {@code null}.
   * @throws IllegalArgumentException if the given collection is empty.
   * @throws AssertionError if the given object is not present in the given collection.
   */
  public <A> void assertIsIn(AssertionInfo info, Object actual, Iterable<? extends A> values) {
    checkIsNotNullAndNotEmpty(values);
    assertNotNull(info, actual);
    if (isActualIn(actual, values)) return;
    throw failures.failure(info, shouldBeIn(actual, values, comparisonStrategy));
  }

  /**
   * Asserts that the given object is not present in the given collection.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param values the given collection.
   * @throws NullPointerException if the given iterable is {@code null}.
   * @throws IllegalArgumentException if the given collection is empty.
   * @throws AssertionError if the given object is present in the given collection.
   */
  public <A> void assertIsNotIn(AssertionInfo info, Object actual, Iterable<? extends A> values) {
    checkIsNotNullAndNotEmpty(values);
    assertNotNull(info, actual);
    if (!isActualIn(actual, values)) return;
    throw failures.failure(info, shouldNotBeIn(actual, values, comparisonStrategy));
  }

  private void checkIsNotNullAndNotEmpty(Iterable<?> values) {
    if (values == null) throw new NullPointerException("The given iterable should not be null");
    if (!values.iterator().hasNext()) throw new IllegalArgumentException("The given iterable should not be empty");
  }

  private <A> boolean isActualIn(Object actual, Iterable<? extends A> values) {
    for (Object value : values)
      if (areEqual(value, actual)) return true;
    return false;
  }
  
  /**
   * Assert that the given object is lenient equals by ignoring null fields value on other object.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param other the object to compare {@code actual} to.
   * @throws NullPointerException if the actual type is {@code null}.
   * @throws NullPointerException if the other type is {@code null}.
   * @throws AssertionError if the actual and the given object are not lenient equals.
   * @throws AssertionError if the other object is not an instance of the actual type.
   */
  public void assertIsLenientEqualsToByIgnoringNullFields(AssertionInfo info, Object actual, Object other){
	assertIsInstanceOf(info, other, actual.getClass());
	List<String> fieldsNames = new LinkedList<String>();
	List<Object> values = new LinkedList<Object>();
	List<String> nullFields = new LinkedList<String>();
	for (Field field : actual.getClass().getDeclaredFields()) {
		try {
			Object otherFieldValue = propertySupport.propertyValue(field.getName(), other, field.getType());
			if(otherFieldValue != null){
				Object actualFieldValue = propertySupport.propertyValue(field.getName(), actual, field.getType());
				if(!otherFieldValue.equals(actualFieldValue)){
					fieldsNames.add(field.getName());
					values.add(otherFieldValue);
				}
			} else {
				nullFields.add(field.getName());
			}
		} catch (IntrospectionError e) {
			// Not readeable field, skip.
		}
	}
	if(fieldsNames.isEmpty()) return;
	throw failures.failure(info, shouldBeLenientEqualByIgnoring (actual, fieldsNames, values, nullFields));	  
  }
  
  /**
   * Assert that the given object is lenient equals by ignoring null fields value on other object.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param other the object to compare {@code actual} to.
   * @param fields accepted fields
   * @throws NullPointerException if the actual type is {@code null}.
   * @throws NullPointerException if the other type is {@code null}.
   * @throws AssertionError if the actual and the given object are not lenient equals.
   * @throws AssertionError if the other object is not an instance of the actual type.
   * @throws IntrospectionError if a field does not exist in actual.
   */
  public void assertIsLenientEqualsToByAcceptingFields(AssertionInfo info, Object actual, Object other, String... fields){
	assertIsInstanceOf(info, other, actual.getClass());
	List<String> fieldsNames = new LinkedList<String>();
	List<Object> values = new LinkedList<Object>();
	for (String fieldName : fields) {
		Object actualFieldValue = propertySupport.propertyValue(fieldName, actual, Object.class);
		Object otherFieldValue = propertySupport.propertyValue(fieldName, other, Object.class);
		if(!(actualFieldValue == otherFieldValue || (actualFieldValue != null && actualFieldValue.equals(otherFieldValue)))){
			fieldsNames.add(fieldName);
			values.add(otherFieldValue);				
		}
	}
	if(fieldsNames.isEmpty()) return;
	throw failures.failure(info, shouldBeLenientEqualByAccepting(actual, fieldsNames, values, list(fields)));	  
  }

  /**
   * Assert that the given object is lenient equals by ignoring fields.
   * @param info contains information about the assertion.
   * @param actual the given object.
   * @param other the object to compare {@code actual} to.
   * @param fields ignore fields
   * @throws NullPointerException if the actual type is {@code null}.
   * @throws NullPointerException if the other type is {@code null}.
   * @throws AssertionError if the actual and the given object are not lenient equals.
   * @throws AssertionError if the other object is not an instance of the actual type.
   */  
	public void assertIsLenientEqualsToByIgnoringFields(AssertionInfo info, Object actual, Object other, String... fields) {
		assertIsInstanceOf(info, other, actual.getClass());
		List<String> fieldsNames = new LinkedList<String>();
		List<Object> values = new LinkedList<Object>();
		Set<String> ignoredFields = set(fields);
		for (Field field : actual.getClass().getDeclaredFields()) {
			try {
				if(!ignoredFields.contains(field.getName())){
					Object otherFieldValue = propertySupport.propertyValue(field.getName(), other, field.getType());
					if(otherFieldValue != null){
						Object actualFieldValue = propertySupport.propertyValue(field.getName(), actual, field.getType());
						if(!otherFieldValue.equals(actualFieldValue)){
							fieldsNames.add(field.getName());
							values.add(otherFieldValue);
						}
					}
				}	
			} catch (IntrospectionError e) {
				// Not readeable field, skip.
			}
		}		
		if(fieldsNames.isEmpty()) return;
		throw failures.failure(info,shouldBeLenientEqualByIgnoring(actual, fieldsNames, values, list(fields)));	 		
	}  
}
