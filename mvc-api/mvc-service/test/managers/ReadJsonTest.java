package managers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GetContentDefinition.class})
public class ReadJsonTest {
private String req = "{\"request\":{\"content\":[{\"board\":\"State(Tamil Nadu)\",\"subject\":[\"English\"],\"medium\":[\"Telegu\"],\"gradeLevel\":[\"Class 4\",\"Class 5\",\"Class 6\"],\"textbook_name\":[\"Science\"],\"level1Name\":[\"Sorting Materials Into Groups\"],\"level1Concept\":[\"Materials\"],\"source\":[\"Diksha 1\"],\"sourceURL\":[\"https://diksha.gov.in/play/content/do_31283180221267968012331\"]}]}}";
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
  public void testReadJson()  {
        PowerMockito.mockStatic(GetContentDefinition.class);
        Mockito.when(GetContentDefinition.validateSourceURL(Mockito.anyString())).thenReturn(true);
        ReadJson readJson = new ReadJson();
        readJson.read(req);
    }
}
