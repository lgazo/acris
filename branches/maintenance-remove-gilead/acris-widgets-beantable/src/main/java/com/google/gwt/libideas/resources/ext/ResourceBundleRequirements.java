/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.libideas.resources.ext;

import com.google.gwt.core.ext.BadPropertyValueException;

/**
 * Allows ResourceGenerators to indicate how their generated resources may be
 * affected by their execution environment. An instance of this type will be
 * provided to the ResourceGenerator via the {@link ResourceGenerator#prepare}
 * method.
 */
public interface ResourceBundleRequirements {
  /**
   * Indicates that the ResourcePrototype implementation generated by a
   * ResourceGenerator is sensitive to the value of the specified
   * deferred-binding property. This method should be called when the behavior
   * of the ResourcePrototype must differ between permutations of the compiled
   * output. For example, some resource implementations may be sensitive to the
   * <code>user.agent</code> deferred-binding property, and would call this
   * method with the literal string <code>user.agent</code>.
   * 
   * @param propertyName the name of the deferred-binding property
   * @throws BadPropertyValueException if <code>propertyName</code> is not a
   *           valid deferred-binding property.
   */
  void addPermutationAxis(String propertyName) throws BadPropertyValueException;
}
