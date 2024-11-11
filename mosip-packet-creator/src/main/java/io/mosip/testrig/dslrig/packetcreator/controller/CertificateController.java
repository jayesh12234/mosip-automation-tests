package io.mosip.testrig.dslrig.packetcreator.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.testrig.dslrig.dataprovider.util.DataProviderConstants;
import io.mosip.testrig.dslrig.dataprovider.variables.VariableManager;
import io.mosip.testrig.dslrig.packetcreator.dto.PreRegisterRequestDto;
import io.mosip.testrig.dslrig.packetcreator.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "CertificateController", description = "REST API for uploading certificates")
//@RequestMapping("/")
//@CrossOrigin("*")
public class CertificateController {

	@Value("${mosip.test.persona.configpath}")
	private String personaConfigPath;

	@Autowired
	CertificateService certificateService;

	private static final Logger logger = LoggerFactory.getLogger(CertificateController.class);

	@Operation(summary = "Generating and uploading the root certificate")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully updated the root certificate") })
	@PutMapping(value = "/certificate/generate/root/{contextKey}")
	public @ResponseBody String generateAndUploadRootCertificate(
			@RequestParam(value = "issuer", defaultValue = "C=AU, O=The Legion of the Bouncy Castle, OU=Bouncy Primary Certificate") String issuer,
			@RequestParam(value = "alias", defaultValue = "Root CA") String alias,
			@RequestParam(value = "validYears", defaultValue = "3") int validYears,
			@PathVariable("contextKey") String contextKey) {

		try {
			if (personaConfigPath != null && !personaConfigPath.equals("")) {
				DataProviderConstants.RESOURCE = personaConfigPath;
			}
			return certificateService.generateAndUploadRootCertificate(issuer, alias, validYears, contextKey);
		} catch (Exception ex) {
			logger.error("generateAndUploadCACertificate", ex);
		}

		return "failed";

	}

	@Operation(summary = "Generating and uploading the int certificate")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully updated the int certificate") })
	@PutMapping(value = "/certificate/generate/int/{contextKey}")
	public @ResponseBody String generateAndUploadIntCertificate(
			@RequestParam(value = "issuer", defaultValue = "C=IN, O=EFG Company, OU=Certificate, E=abc@efg.com") String issuer,
			@RequestParam(value = "alias", defaultValue = "Int CA") String alias,
			@RequestParam(value = "validYears", defaultValue = "3") int validYears,
			@RequestParam(value = "RootAlias", defaultValue = "Root CA") String rootAlias,
			@PathVariable("contextKey") String contextKey) {

		try {
			if (personaConfigPath != null && !personaConfigPath.equals("")) {
				DataProviderConstants.RESOURCE = personaConfigPath;
			}
			return certificateService.generateAndUploadIntCertificate(issuer, alias, validYears, rootAlias, contextKey);
		} catch (Exception ex) {
			logger.error("generateAndUploadCACertificate", ex);
		}

		return "failed";
    }
    
    @PostMapping(value = "/uploadDeviceCert/{contextKey}")
    public @ResponseBody String uploadDeviceCert( @RequestBody String encodedDeviceCert,@PathVariable("contextKey") String contextKey) {
        try {
            byte[] fileBytes = Base64.getDecoder().decode(encodedDeviceCert);
            String tempDir = System.getProperty("java.io.tmpdir") + File.separator + VariableManager.getVariableValue(contextKey, "db-server");
            File file = new File(tempDir, "device-dsk-partner.p12");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileBytes);
            }
            return "File uploaded successfully and saved as " + file.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Error uploading device certificate", e);
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

	@Operation(summary = "Generating and uploading the partner certificate")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully updated the partner certificate") })
	@PutMapping(value = "/certificate/generate/partner/{contextKey}")
	public @ResponseBody String generateAndUploadPartnerCertificate(
			@RequestParam(value = "issuer", defaultValue = "C=IN, O=ABC Bank,L=Bangalore,CN=ABC Bank, OU=Account Opening,E=bank@efg.com") String issuer,
			@RequestParam(value = "alias", defaultValue = "ABC Bank") String alias,
			@RequestParam(value = "validYears", defaultValue = "3") int validYears,
			@RequestParam(value = "RootAlias", defaultValue = "Int CA") String rootAlias,
			@RequestParam(value = "PartnerID", defaultValue = "9876") String partnerID,
			@PathVariable("contextKey") String contextKey) {

		try {
			if (personaConfigPath != null && !personaConfigPath.equals("")) {
				DataProviderConstants.RESOURCE = personaConfigPath;
			}
			return certificateService.generateAndUploadPartnerCertificate(issuer, alias, validYears, rootAlias,
					partnerID, contextKey);
		} catch (Exception ex) {
			logger.error("generateAndUploadCACertificate", ex);
		}

		return "failed";

	}

}
