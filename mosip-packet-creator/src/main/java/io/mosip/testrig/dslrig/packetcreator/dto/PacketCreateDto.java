package io.mosip.testrig.dslrig.packetcreator.dto;

import lombok.Data;

@Data
public class PacketCreateDto {

    private String idJsonPath;
    private String templatePath;
    private String source;
    private String process;
    private String additionalInfoReqId;
}
