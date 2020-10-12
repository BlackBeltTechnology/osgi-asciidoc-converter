package hu.blackbelt.asciidoc.converter;

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
import java.io.FileNotFoundException;

import static hu.blackbelt.asciidoc.converter.KarafTestUtil.karafConfig;
import static hu.blackbelt.asciidoc.converter.KarafTestUtil.karafStandardRepo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ServicesTransactionITest {

    @Inject
    LogService log;

    @Inject
    BundleContext bundleContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Configuration
    public Option[] config() throws FileNotFoundException {

        return combine(karafConfig(this.getClass()),

                features(karafStandardRepo(),
                        "scr"),


                editConfigurationFilePut("etc/org.ops4j.pax.web.cfg",
                        "org.osgi.service.http.port", "8181")
        );

    }

    @Test
    public void test() {
        Assert.assertTrue(true);
    }
}