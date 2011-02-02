/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.interceptors.interceptorsOrderBeanArchives;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.tests.category.Integration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InterceptorOrderingTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class);
      war.addClasses(InterceptorOrderingTest.class, InterceptorA.class, InterceptorB.class);
      war.addWebResource(EmptyAsset.INSTANCE, "beans.xml");

      BeanArchive jara = ShrinkWrap.create(BeanArchive.class);
      jara.intercept(InterceptorA.class, InterceptorB.class);
      jara.addClass(HelloMessage.class);

      BeanArchive jarb = ShrinkWrap.create(BeanArchive.class);
      jara.intercept(InterceptorB.class, InterceptorA.class);
      jara.addClass(GoodByeMessage.class);

      war.addLibraries(jara, jarb);
      return war;
   }

   @Inject
   private HelloMessage hello;

   @Inject
   private GoodByeMessage bye;

   @Test
   @Category(Integration.class)
   public void testDifferentOrderingAccrossBda()
   {
      Assert.assertEquals("ABhello", hello.method());
      Assert.assertEquals("BAbye", bye.method());
   }

}
