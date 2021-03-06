/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.jersey.inject.cdi.se.bean;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Singleton;

import org.glassfish.jersey.internal.inject.InstanceBinding;

/**
 * Creates an implementation of {@link javax.enterprise.inject.spi.Bean} interface using Jersey's {@link InstanceBinding}. Binding
 * provides the information about the bean also called {@link javax.enterprise.inject.spi.BeanAttributes} information.
 * The {@code Bean} does not use {@link org.glassfish.jersey.inject.cdi.se.injector.JerseyInjectionTarget} because serves already
 * created instances, therefore the create operation just return the provided instance and attempt to inject the rest of the
 * fields but omit contextual operations Produce and Destroy. Client has to manage the instance alone.
 * <p>
 * Register example:
 * <pre>
 * AbstractBinder {
 *     &#64;Override
 *     protected void configure() {
 *         bind(new MyBean())
 *              .to(MyBean.class)&#59;
 *     }
 * }
 * </pre>
 * Inject example:
 * <pre>
 *  &#64;Path("/")
 *  public class MyResource {
 *    &#64;Inject
 *    private MyBean myBean&#59;
 *  }
 * </pre>
 *
 * @author Petr Bouda
 */
class InstanceBean<T> extends JerseyBean<T> {

    private final InstanceBinding<T> binding;
    private InjectionTarget<T> injectionTarget;

    /**
     * Creates a new Jersey-specific {@link javax.enterprise.inject.spi.Bean} instance.
     *
     * @param binding {@link javax.enterprise.inject.spi.BeanAttributes} part of the bean.
     */
    InstanceBean(InstanceBinding<T> binding) {
        super(binding);
        this.binding = binding;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Singleton.class;
    }

    @Override
    public T create(CreationalContext<T> context) {
        T service = binding.getService();
        this.injectionTarget.inject(service, context);
        return service;
    }

    @Override
    public Class<?> getBeanClass() {
        return binding.getService().getClass();
    }

    /**
     * Lazy set of an injection target because to create fully functional injection target needs already created bean.
     *
     * @param injectionTarget {@link javax.enterprise.context.spi.Contextual} information belonging to this bean.
     */
    void setInjectionTarget(InjectionTarget<T> injectionTarget) {
        this.injectionTarget = injectionTarget;
    }
}
