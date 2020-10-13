package hu.blackbelt.asciidoc.converter.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.commons.classloader.DynamicClassLoaderManager;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.jruby.javasupport.JavaEmbedUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

@Component(immediate = true)
@Slf4j
public class AsciidoctorServiceActivator {

    ServiceRegistration serviceRegistration;
    Ruby runtime;

    @Reference
    DynamicClassLoaderManager dynamicClassLoaderManager;

    @Activate
    public void activate(BundleContext bundleContext) {

//        final OSGiScriptingContainer container = new OSGiScriptingContainer(bundleContext.getBundle());
//        container.addClassLoader(dynamicClassLoaderManager.getDynamicClassLoader());
//        container.addClassLoader(Asciidoctor.class.getClassLoader());
//        container.addClassLoader(Ruby.getClassLoader());
        Asciidoctor asciidoctor = Asciidoctor.Factory.create(dynamicClassLoaderManager.getDynamicClassLoader());
        serviceRegistration = bundleContext.registerService(Asciidoctor.class.getName(), asciidoctor, null);

        /*
        Creator creator = new Creator(this, bundleContext);

        OSGiScriptingContainer scriptingContainer = new OSGiScriptingContainer(bundleContext.getBundle());
        scriptingContainer.addClassLoader(dynamicClassLoaderManager.getDynamicClassLoader());
        scriptingContainer.addClassLoader(Asciidoctor.class.getClassLoader());
        scriptingContainer.addClassLoader(Ruby.class.getClassLoader());

        creator.setContextClassLoader(scriptingContainer.getClassLoader());
        creator.start(); // background thread creating the instance

         */




        /*
        CompositeClassLoader compositeClassLoader = new CompositeClassLoader();
        compositeClassLoader.addClassLoader(AsciidoctorServiceActivator.class.getClassLoader());
        compositeClassLoader.addClassLoader(Asciidoctor.class.getClassLoader());
//        compositeClassLoader.addClassLoader(AsciidoctorJRuby.class.getClassLoader());
//        compositeClassLoader.addClassLoader(Options.class.getClassLoader());
        compositeClassLoader.addClassLoader(Ruby.class.getClassLoader());

        RubyInstanceConfig config = new RubyInstanceConfig();
        config.setLoader(compositeClassLoader);

        JavaEmbedUtils.initialize(Arrays.asList("META-INF/jruby.home/lib/ruby/2.0", "uri:classloader:/gems/asciidoctor-1.6.2/lib"), config);

        Asciidoctor asciidoctor = Asciidoctor.Factory.create(compositeClassLoader);
        serviceRegistration = bundleContext.registerService(
                Asciidoctor.class, asciidoctor, new Hashtable()); */

    }

    @Deactivate
    public void deactivate() {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
        if (runtime != null) {
            runtime.tearDown();
        }
    }

    private static class Creator extends Thread {
        BundleContext bundleContext;
        AsciidoctorServiceActivator asciidoctorServiceActivator;

        public Creator(AsciidoctorServiceActivator asciidoctorServiceActivator, BundleContext bundleContext) {
           this.bundleContext = bundleContext;
           this.asciidoctorServiceActivator = asciidoctorServiceActivator;
       }

        @Override
        public void run() {
            /*
            final RubyInstanceConfig config = new RubyInstanceConfig();
            config.setLoader(Thread.currentThread().getContextClassLoader()); // TCCL == AsciidoctorService.class.getClassLoader()
            try {
                asciidoctorServiceActivator.runtime = JavaEmbedUtils.initialize(Arrays.asList("uri:classloader:/gems/asciidoctor-2.0.10/lib"), config);
                // Asciidoctor asciidoctor = JRubyAsciidoctor.create(Arrays.asList("uri:classloader:/gems/asciidoctor-2.0.10/lib"));
                Asciidoctor asciidoctor = JRubyAsciidoctor.create(Thread.currentThread().getContextClassLoader());
                // Asciidoctor asciidoctor = JRubyAsciidoctor.create();
                asciidoctorServiceActivator.serviceRegistration = bundleContext.registerService(
                        Asciidoctor.class, asciidoctor, new Hashtable());
            } catch (final Throwable e) {
                log.error("Could not register asciidoctor", e);
            } */
            try {

                // OSGiIsolatedScriptingContainer
//                OSGiScriptingContainer scriptingContainer = new OSGiScriptingContainer(bundleContext.getBundle());
//                CompositeClassLoader compositeClassLoader = new CompositeClassLoader();
//                compositeClassLoader.addClassLoader(Asciidoctor.class.getClassLoader());
//                compositeClassLoader.addClassLoader(Ruby.class.getClassLoader());

                /*
                RubyInstanceConfig config = new RubyInstanceConfig();
                config.setLoader(scriptingContainer.getOSGiBundleClassLoader());

                JavaEmbedUtils.initialize(Arrays.asList("META-INF/jruby.home/lib/ruby/2.0", "uri:classloader:/gems/asciidoctor-2.0.10/lib"), config);

                Asciidoctor asciidoctor = AsciidoctorJRuby.Factory.create(scriptingContainer.getOSGiBundleClassLoader());
                */

                //Asciidoctor asciidoctor = AsciidoctorJRuby.Factory.create(currentThread().getContextClassLoader()); // Asciidoctor.Factory.create();


                final RubyInstanceConfig config = new RubyInstanceConfig();
                config.setLoader(Thread.currentThread().getContextClassLoader()); // TCCL == AsciidoctorService.class.getClassLoader()

                asciidoctorServiceActivator.runtime = JavaEmbedUtils.initialize(Arrays.asList("META-INF/jruby.home/lib/ruby/2.0", "uri:classloader:/gems/asciidoctor-1.5.8/lib"), config);

                Asciidoctor asciidoctor = Asciidoctor.Factory.create(currentThread().getContextClassLoader());
                asciidoctorServiceActivator.serviceRegistration = bundleContext.registerService(
                        Asciidoctor.class, asciidoctor, new Hashtable());

            } catch (final Throwable e) {
                log.error("Could not register asciidoctor", e);
            }

        }
    }

    private static class CompositeClassLoader extends ClassLoader {
        private Vector<ClassLoader> classLoaders = new Vector<ClassLoader>();



        @Override
        public URL getResource(String name) {
            for (ClassLoader cl : classLoaders) {

                URL resource = cl.getResource(name);
                if (resource != null)
                    return resource;

            }

            return null;
        }

        @Override
        public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

            for (ClassLoader cl : classLoaders) {
                try {
                    return cl.loadClass(name);
                } catch (ClassNotFoundException ex) {

                }
            }

            throw new ClassNotFoundException(name);
        }

        public void addClassLoader(ClassLoader cl) {
            classLoaders.add(cl);
        }


    }
}

    /*
    private final Options options = OptionsBuilder.options().backend("html").headerFooter(false)
            .attributes(AttributesBuilder.attributes().attribute("showtitle")).get();

    public String convertSynchronous(final String adoc) {
        try {
            return creator.instance.get().convert(adoc, options);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public CompletionStage<String> convertAsynchronous(final String adoc) {
        try {
            return creator.instance
                    .thenApply(instance -> instance.convert(adoc, options));
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
    */

