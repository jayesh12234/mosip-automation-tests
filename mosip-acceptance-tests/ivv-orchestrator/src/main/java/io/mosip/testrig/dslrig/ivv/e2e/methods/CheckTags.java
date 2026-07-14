package io.mosip.testrig.dslrig.ivv.e2e.methods;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.masterdata.testscripts.SimplePost;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.KernelAuthentication;
import io.mosip.testrig.apirig.utils.SecurityXSSException;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testrig.dslrig.ivv.orchestrator.dslConfigManager;
import io.restassured.response.Response;

public class CheckTags extends BaseTestCaseUtil implements StepInterface {
	private static final Logger logger = Logger.getLogger(CheckTags.class);
	KernelAuthentication kernelAuthLib = new KernelAuthentication();
	private static final String CheckPacketTags = "regproc/GetPacketTagsInfo/GetPacketTagsInfo.yml";
	SimplePost checkPacketTags = new SimplePost();

	static {
		if (dslConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@Override
	public void run() throws RigInternalError {

	}

	public static String comparePacketTags(String jsonFromServer, String jsonFromPacketCreator) {
		String tagMismatched = "";

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode nodeFromServer = objectMapper.readTree(jsonFromServer);
			JsonNode nodePacketCreator = objectMapper.readTree(jsonFromPacketCreator);

			// Convert JSON nodes to Map for easier comparison
			Map<String, String> mapFromServer = objectMapper.convertValue(nodeFromServer, Map.class);
			Map<String, String> mapPacketCreator = objectMapper.convertValue(nodePacketCreator, Map.class);

			// Compare key-value pairs
			for (Map.Entry<String, String> entry : mapFromServer.entrySet()) {
				String key = entry.getKey();
				String valueFromServer = entry.getValue();
				String valuePacketCreator = mapPacketCreator.get(key);

				if (valuePacketCreator != null && valuePacketCreator.equals(valueFromServer)) {
					logger.info("Key :" + key + "has the same value in both JSONs: " + valueFromServer);
				} else {
					logger.info("Key '" + key + "' has different values in the two JSONs.");
					tagMismatched += "Key :" + key + "   Value from server : " + valueFromServer
							+ "    Value from packet creator : " + valuePacketCreator + " ---- ";
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return tagMismatched;
	}
}
