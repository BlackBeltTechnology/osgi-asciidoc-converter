package hu.blackbelt.asciidoc.converter;

/*-
 * #%L
 * Asciidoc :: OSGi converter services :: Karaf :: ITest
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

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import javax.inject.Inject;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AsciidocConverterITest {

    @Inject
    LogService log;

    @Inject
    BundleContext bundleContext;

    @Inject
    Asciidoctor asciidoctor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Configuration
    public Option[] config() throws MalformedURLException {


        return combine(KarafFeatureProvider.karafConfig(this.getClass()),
                features(maven()
                                .groupId("hu.blackbelt")
                                .artifactId("osgi-asciidoc-converter-karaf-features")
                                .versionAsInProject()
                                .classifier("features")
                                .type("xml"),
                        "asciidoc-converter")
        );
    }

    @Test
    public void testPdf() throws IOException {

        // Unpack the required styles to data directory from the bundle
        File themeDir = bundleContext.getBundle().getDataFile("asciidoctor");
        themeDir.mkdirs();
        copy(this.getClass().getClassLoader().getResourceAsStream("/theme.yaml"),
                new FileOutputStream(new File(themeDir, "custom-theme.yml")));

        // Read content from bundle
        String content = new BufferedReader(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/content.adoc"),
                        StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        // The PDF styles can work from directory only
        Attributes attributes = Attributes.builder()
                .attribute("pdf-stylesdir", themeDir.getAbsolutePath())
                .attribute("pdf-style", "custom")
                .build();

        Options options = Options.builder()
                .inPlace(true)
                .backend("pdf")
                .attributes(attributes)
                .toStream(new FileOutputStream("Test.pdf"))
                .build();

        asciidoctor.convert(content, options);
        Assert.assertTrue(true);
    }

    void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    @Test
    public void testAsciidoc() throws IOException {
        Assert.assertTrue(true);
    }

}
