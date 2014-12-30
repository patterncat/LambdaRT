/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.service.mongo;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import st.malike.elasticsearch.service.DemographicElasticSearchService;
import st.malike.model.Demographic;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author malike_st
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-mongo-config.xml"})
public class DemographicServiceTest {

    @Autowired
    @Qualifier(value = "demographicService")
    DemographicService demographicService;
    static DemographicElasticSearchService demographicBufferService;
    static PodamFactory podamFactory;
    static final String DATEFORMAT = "MMM dd, yyyy HH:mm:ss";
    static String SHARDINDEX = "lambda_lns_test";
    static String TYPENAME = "demographic_summary";

    @BeforeClass
    public static void setUp() {
        podamFactory = new PodamFactoryImpl();
        demographicBufferService = new DemographicElasticSearchService();
    }

    @Test
    @Ignore
    public void saveDemographic() {
        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        Demographic dSaved = demographicService.saveDemographic(d);
        assertEquals(d.getEvent(), dSaved.getEvent());
        assertEquals(d.getId(), dSaved.getId());
    }

    @Test
    @Ignore
    public void saveDemographicList() {
        List<Demographic> d = Arrays.asList(podamFactory.manufacturePojo(Demographic[].class));
        List<Demographic> dSaved = demographicService.saveDemographic(d);
        assertEquals(d.size(), dSaved.size());
        assertEquals(d.get(0).getId(), dSaved.get(0).getId());
    }

    @Test
    @Ignore
    public void getDemographicById() {
        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        demographicService.saveDemographic(d);
        Demographic de = demographicService.getDemographicById(d.getId());
        assertEquals(d.getEvent(), de.getEvent());
    }

    @Test
    @Ignore
    public void getDemographicWithNullResult() {
        assertEquals(null, demographicService.getDemographicById(1L));
    }

    @Test
    @Ignore
    public void deleteDemographic() {
        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        Demographic dSaved = demographicService.saveDemographic(d);

        assertEquals(d.getEvent(), dSaved.getEvent());
        assertEquals(d.getId(), dSaved.getId());

        demographicService.deleteDemographic(d.getId());

        assertEquals(null, demographicService.getDemographicById(d.getId()));
    }

    @Test
    @Ignore
    public void getDemographicByEvent() {
        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        demographicService.saveDemographic(d);
        List<Demographic> de = demographicService.getDemographicByEvent(d.getEvent());
        assertEquals(d.getEvent(), de.get(0).getEvent());

    }

    @Test
    @Ignore
    public void getDemographicByDate() {

        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        demographicService.saveDemographic(d);
        List<Demographic> de = demographicService.getDemographicByDate(d.getDateCreated());
        assertEquals(d.getDateCreated(), de.get(0).getDateCreated());
    }

    @Test
    @Ignore
    public void getDemographicByEventAndDate() {
        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        demographicService.saveDemographic(d);
        Demographic de = demographicService.getDemographicByEventAndDate(d.getEvent(), d.getDateCreated());
        assertEquals(d.getEvent(), de.getEvent());
        assertEquals(d.getDateCreated(), de.getDateCreated());
    }

    @Test
    public void testMongoRiverForElasticSearch() throws ParseException {
        //save to mongodb
        Demographic d = podamFactory.manufacturePojo(Demographic.class);
        demographicService.saveDemographic(d);
        //retrieve from elastic search
        List<Demographic> des = demographicBufferService.getRecords(SHARDINDEX, TYPENAME, 0, 20);

        List<Demographic> de = demographicBufferService.findByEventAndDate(SHARDINDEX, TYPENAME, d.getEvent(), d.getDateCreated());

        assertNotEquals(0, de.size());
        assertEquals(d.getId(), de.get(0).getId());
        assertEquals(d.getEvent(), de.get(0).getEvent());
        assertEquals(d.getDateCreated(), de.get(0).getDateCreated());
    }
}
