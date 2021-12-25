package hu.blackbelt.asciidoc.converter.impl;

import lombok.extern.slf4j.Slf4j;
import org.asciidoctor.Asciidoctor;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

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

