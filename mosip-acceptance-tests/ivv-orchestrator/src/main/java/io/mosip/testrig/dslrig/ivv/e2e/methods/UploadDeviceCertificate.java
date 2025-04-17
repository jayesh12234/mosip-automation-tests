package io.mosip.testrig.dslrig.ivv.e2e.methods;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.dslrig.ivv.core.base.StepInterface;
import io.mosip.testrig.dslrig.ivv.core.exceptions.RigInternalError;
import io.mosip.testrig.dslrig.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testrig.dslrig.ivv.orchestrator.dslConfigManager;
import io.restassured.response.Response;

public class UploadDeviceCertificate extends BaseTestCaseUtil implements StepInterface {
	public static Logger logger = Logger.getLogger(UploadDeviceCertificate.class);

	static {
		if (dslConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@Override
	public void run() throws RigInternalError {
		Response response = null;
		String url = baseUrl + props.getProperty("uploadDeviceCert");

		String certsDir = System.getProperty("java.io.tmpdir") + File.separator + "AUTHCERTS";
		if (System.getProperty("os.name").toLowerCase().contains("windows") == false) {
			certsDir = dslConfigManager.getauthCertsPath();
		}

		String p12 = certsDir + File.separator + "DSL-IDA-" + BaseTestCase.ApplnURI.replace("https://", "") + File.separator
				+ "device-partner.p12";
		File file = new File(p12);

		if (file.exists()) {
			try {
				byte[] fileBytes = Files.readAllBytes(file.toPath());
				String encodedBytes = Base64.getEncoder().encodeToString(fileBytes);
				response = postRequest(url, encodedBytes, "UPLOAD_DEVICE_CERT", step);

				// Log the response
				if (response != null) {
					logger.info("Response Status: " + response.getStatusCode());
					logger.info("Response Body: " + response.getBody().asString());
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.hasError = true;
				throw new RigInternalError("Unable to upload device certificate ");
			}
		} else {
			logger.error("File does not exist: " + p12);
			this.hasError = true;
			throw new RigInternalError("File does not exists: " + p12);
		}
	}
}
