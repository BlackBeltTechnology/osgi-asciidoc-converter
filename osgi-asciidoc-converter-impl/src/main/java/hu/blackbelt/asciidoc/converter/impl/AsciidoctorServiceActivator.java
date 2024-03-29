package hu.blackbelt.asciidoc.converter.impl;

/*-
 * #%L
 * Asciidoc :: OSGi converter services :: Karaf :: Implementation
 * %%
 * Copyright (C) 2018 - 2023 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.asciidoctor.Asciidoctor;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import java.util.Hashtable;

import static org.asciidoctor.jruby.AsciidoctorJRuby.Factory.create;

@Component(immediate = true)
@Slf4j
public class AsciidoctorServiceActivator {

    ServiceRegistration serviceRegistration;
    OSGiScriptingContainer container;
    @Activate
    public void activate(BundleContext bundleContext) {
        container = new OSGiScriptingContainer(bundleContext.getBundle());
        Asciidoctor asciidoctor = create(container.getOSGiBundleClassLoader());
        log.info("Init AsciiDoctorJ version: " + asciidoctor.asciidoctorVersion());

        serviceRegistration = bundleContext.registerService(Asciidoctor.class, asciidoctor, new Hashtable<>());
    }

    @Deactivate
    public void deactivate() {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
        if (container != null) {
            container.terminate();
        }
    }
}

