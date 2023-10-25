package com.example.Itext.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PdfRequest {
    private String partnerLogo;
    private String bKashLogo;
    private String customerInformationLabel;
    private List<KeyValueDto> customerInformation;
    private String billInformationLabel;
    private List<KeyValueDto> billInformation;
    private String paymentInformationLabel;
    private List<KeyValueDto> paymentInformation;
    private String paymentReceivedLabel;
    private String totalPaid;
    private String wallet;
    private String receiptNumber;
    private String billReferenceNumber;
    private String tokenRetrieverHelpToken;
    private String tokenRetrieverHelpHtml;
}
