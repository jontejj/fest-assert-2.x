/*
 * Created on Sep 17, 2010
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
package org.fest.assertions.error;

import static junit.framework.Assert.assertEquals;

import static org.fest.assertions.error.ShouldContain.shouldContain;
import static org.fest.util.Collections.*;

import org.junit.Before;
import org.junit.Test;

import org.fest.assertions.description.Description;
import org.fest.assertions.description.TextDescription;
import org.fest.assertions.util.CaseInsensitiveStringComparator;
import org.fest.util.ComparatorBasedComparisonStrategy;

/**
 * Tests for <code>{@link ShouldContain#create(Description)}</code>.
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class ShouldContain_create_Test {

  private ErrorMessageFactory factory;

  @Before
  public void setUp() {
    factory = shouldContain(list("Yoda"), list("Luke", "Yoda"), set("Luke"));
  }

  @Test
  public void should_create_error_message() {
    String message = factory.create(new TextDescription("Test"));
    assertEquals("[Test] expecting:<['Yoda']> to contain:<['Luke', 'Yoda']> but could not find:<['Luke']>", message);
  }

  @Test
  public void should_create_error_message_with_custom_comparison_strategy() {
    factory = shouldContain(list("Yoda"), list("Luke", "Yoda"), set("Luke"), new ComparatorBasedComparisonStrategy(
        CaseInsensitiveStringComparator.instance));
    String message = factory.create(new TextDescription("Test"));
    assertEquals("[Test] expecting:<['Yoda']> to contain:<['Luke', 'Yoda']> but could not find:<['Luke']> "
        + "according to 'CaseInsensitiveStringComparator' comparator", message);
  }
  
  @Test
  public void should_create_error_message_differentiating_long_from_integer() {
    factory = shouldContain(list(5L, 7L), list(5, 7), set(5, 7));
    String message = factory.create(new TextDescription("Test"));
    assertEquals("[Test] expecting:<[5L, 7L]> to contain:<[5, 7]> but could not find:<[5, 7]>", message);
  }
  
}
