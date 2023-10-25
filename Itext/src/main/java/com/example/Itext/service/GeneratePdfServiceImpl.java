package com.example.Itext.service;

import com.example.Itext.models.KeyValueDto;
import com.example.Itext.models.PdfRequest;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneratePdfServiceImpl  implements GeneratePdfService {
    private static float leftGap = 64;

    private final StorageService storageService;

    private final float tableCellFontSize = 10;
    private final float tableHeaderFontSize = 14;
    private final float tableHeaderMargin = 6;
    private final float tableCellMargin = 4;
    private final float tableRightColumnLeftPadding = 10;
    private final float titleFontSize = 24;
    private final float totalPaymentFontSize = 12;

    private final float padding = 52.5f;

    private final float docHeight = 842;
    private final float docWidth = 595;

    private final DeviceRgb borderColor = new DeviceRgb(166, 168, 171);

    private final DeviceRgb tableValueFontColor = new  DeviceRgb(109, 110, 114);
    private final DeviceRgb totalPaymentColor = new DeviceRgb(1, 132, 108);
    private final DeviceRgb tableTitleFontColor = new DeviceRgb(65, 65, 67);
    private final Border border = new SolidBorder(borderColor,0.5F);

    private final float logoImageHeight = 48f;
    private final float partnerLogoWith = 48f;
    private final float bkashLogoWith = 64f;

    @Override
    public void generatePdf(PdfRequest request) throws IOException {
        test("./pdf/" + UUID.randomUUID().toString() + ".pdf");

    }


    void test(String path) throws IOException {
        var request = getPdfRequest();
        long startTime = System.currentTimeMillis();


        var pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        PdfPage pdfPage = pdfDocument.addNewPage(PageSize.A4);
        Document document = new Document(pdfDocument);


        makeTable(document, request);



        document.close();

        long endTime = System.currentTimeMillis();
        log.info("Elapsed time: {} milliseconds", endTime - startTime);
        log.info("tada");
    }

    private void addBottomInfo(Document document, float x,  float y) {
        String bottomLabel = "This receipt has been generated electronically";

        var bottom = new Text(bottomLabel).setFontSize(12).setFontColor(new DeviceRgb(166, 168, 171));
        document.add(new Paragraph(bottom).setFixedPosition(x, y, 490).setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER));
    }

    private void makeTable(Document document, PdfRequest request) {
        float[] pointColumnWidths = {255F, 235F};
        Table table = new Table(pointColumnWidths);
        var tableHeight = 0;
        tableHeight += setLogosAndLabel(table, request);
        tableHeight += setTableData(table,request.getBillInformationLabel(),request.getBillInformation());
        tableHeight += setTableData(table, request.getPaymentInformationLabel(), request.getPaymentInformation());
        tableHeight += setBottomTotalPayment(table, request);

        table.setFixedPosition(padding, docHeight - tableHeight - padding, 490);


        addBottomInfo(document, padding, docHeight - tableHeight - padding*2);

        var pdfCanvas = new PdfCanvas(document.getPdfDocument().getFirstPage());
        pdfCanvas.roundRectangle(padding - 4, docHeight - tableHeight - 8 - padding*2, 490 + 16, tableHeight + 32 + padding, 10).setStrokeColor(borderColor);
        document.add(table);

    }

    private float setLogosAndLabel(Table table, PdfRequest request) {

        var bytes2 = storageService.getGetFile(request.getPartnerLogo());
        var partnerImageData = ImageDataFactory.create(bytes2);
        var partnerImg = new Image(partnerImageData);
        partnerImg.setHeight(logoImageHeight);
        partnerImg.setMarginLeft(tableCellMargin);
        partnerImg.setMarginRight(24);



        var bytes = storageService.getGetFile(request.getBKashLogo());
        var bkImgData = ImageDataFactory.create(bytes);
        var bkImg = new Image(bkImgData);
        bkImg.setHeight(logoImageHeight);
        bkImg.setMarginLeft(tableCellMargin);


        Paragraph p = new Paragraph();
        p.add(partnerImg);
        p.add(bkImg);

        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setBorderRight(border).setBorderBottom(border));
        table.addCell(new Cell().add("RECEIPT")
                .setFontSize(titleFontSize)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(border)
                .setBorderLeft(border)
                .setMargin(12)
                .setFontColor(tableTitleFontColor)
                .setTextAlignment(TextAlignment.RIGHT));


        return logoImageHeight  + tableHeaderMargin * 4;

    }

    private float setBottomTotalPayment(Table table, PdfRequest request) {
        table.addCell(new Cell().add(request.getPaymentReceivedLabel())
                .setFontSize(totalPaymentFontSize)
                .setMargin(tableHeaderMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(border)
                .setBorderRight(border)
                .setFontColor(totalPaymentColor));

        table.addCell(new Cell().add(request.getTotalPaid())
                .setFontSize(totalPaymentFontSize)
                .setMargin(tableHeaderMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(border)
                .setPaddingLeft(tableRightColumnLeftPadding)
                .setFontColor(totalPaymentColor));

        return tableHeaderMargin * 2 + totalPaymentFontSize;

    }

    private float setTableData(Table table, String label, List<KeyValueDto> data) {
        setTableHeader(label, table);
        var infos = getTabledata(data);
        setTopRow(table, infos);
        setMiddleRows(table, infos);
        setBottomRow(table, infos);

        return tableHeaderMargin * 4 + tableHeaderFontSize + (tableCellFontSize + tableCellMargin * 4) * data.size();
    }

    private void setBottomRow(Table table, List<KeyValueDto> infos) {
        table.addCell(getLeftBottomCell(infos.get(infos.size()-1)));
        table.addCell(getRightBottomCell(infos.get(infos.size()-1)));
    }

    private Cell getRightBottomCell(KeyValueDto info) {
        return new Cell().add(info.getValue())
                .setFontSize(tableCellFontSize)
                .setMargin(tableCellMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(border)
                .setPaddingLeft(tableRightColumnLeftPadding)
                .setFontColor(tableValueFontColor);
    }

    private Cell getLeftBottomCell(KeyValueDto info) {
        return new Cell().add(info.getKey())
                .setFontSize(tableCellFontSize)
                .setMargin(tableCellMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderRight(border)
                .setBorderBottom(border);
    }

    private void setTopRow(Table table, List<KeyValueDto> infos) {
        table.addCell(getTopLeftCell(infos));
        table.addCell(getTopRightCell(infos));
    }

    private static List<KeyValueDto> getTabledata(List<KeyValueDto> data) {
        return data
                .stream()
                .filter(keyValueDto -> keyValueDto.isEnable())
                .sorted(Comparator.comparing(KeyValueDto::getOrder))
                .collect(Collectors.toList());
    }

    private void setTableHeader(String header, Table table) {
        table.addCell(new Cell().add(header).setFontSize(tableHeaderFontSize).setMarginLeft(tableCellMargin).setMarginTop(tableHeaderMargin).setMarginBottom(tableHeaderMargin).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setBorder(Border.NO_BORDER));
    }

    private void setMiddleRows(Table table, List<KeyValueDto> infos) {
        for (int i = 1; i < infos.size()-1; i++) {

            table.addCell(getLetfMiddleCell(infos.get(i)));
            table.addCell(getRightMiddleCell(infos.get(i)));
        }
    }

    private Cell getRightMiddleCell(KeyValueDto info) {
        return new Cell().add(info.getValue())
                .setFontSize(tableCellFontSize)
                .setMargin(tableCellMargin)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(tableRightColumnLeftPadding)
                .setFontColor(tableValueFontColor);
    }

    private Cell getLetfMiddleCell(KeyValueDto info) {
        return new Cell().add(info.getKey())
                .setFontSize(tableCellFontSize)
                .setMargin(tableCellMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderRight(border);
    }

    private Cell getTopRightCell(List<KeyValueDto> infos) {
        return new Cell().add(infos.get(0).getValue())
                .setFontSize(tableCellFontSize)
                .setMargin(tableCellMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(border)
                .setPaddingLeft(tableRightColumnLeftPadding)
                .setFontColor(tableValueFontColor);
    }

    private Cell getTopLeftCell(List<KeyValueDto> infos) {

        var cell = new Cell().add(infos.get(0).getKey())
                .setFontSize(tableCellFontSize)
                .setMargin(tableCellMargin)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(border)
                .setBorderRight(border);
        return cell;
    }

    private PdfRequest getPdfRequest() {
        return PdfRequest.
                builder()
                .bKashLogo("bkash-logo")
                .partnerLogo("partner-logo")
                .totalPaid("BDT 500.00")
                .billInformationLabel("Bill Information")
                .billInformation(getBillInfos())
                .paymentInformationLabel("Payment Information")
                .paymentInformation(getPaymentInfos())
                .paymentReceivedLabel("Payment Information")
                .build();
        //                .partnerLogo("https://images.uatcapp.bka.sh/images/public/biller/DESCO.png")
//                .bKashLogo("https://images.uatcapp.bka.sh/images/public/provider/pay_bill_receipt.png")
    }

    private List<KeyValueDto> getPaymentInfos() {
        return List.of(
                new KeyValueDto("bKash Account", "BDT 500.0", true, 1),
                new KeyValueDto("Payment Date", "04 February, 2020", true, 2),
                new KeyValueDto("Transaction Id", "7B4301VTA", true, 3),
                new KeyValueDto("Paid to Organization", "BDT 500.0", true, 4),
                new KeyValueDto("bKash Fee", "0.0", true, 5),
                new KeyValueDto("Extra property1", "Extra", true, 6)
        );
    }

    private List<KeyValueDto> getBillInfos() {
        return List.of(
                new KeyValueDto("Organization Name", "DESCO (Prepaid) Test", true, 1),
                new KeyValueDto("Account Number", "663110000616", true, 2),
                new KeyValueDto("Bill Amount", "BDT 500.0", true, 3),
                new KeyValueDto("Extra property1", "Extra", true, 4),
                new KeyValueDto("Extra property2", "Extra", true, 5)
        );
    }

    private void drawLine(PdfPage pdfPage) throws IOException {
        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
        //line between logos
        pdfCanvas.moveTo(112f, 794f);
        pdfCanvas.lineTo(112f, 730f);

        //line in before receipt
        pdfCanvas.moveTo(422f, 788f);
        pdfCanvas.lineTo(422f, 746f);

        //line under header
        pdfCanvas.moveTo(32f, 722f);
        pdfCanvas.lineTo(563f, 722f);

        pdfCanvas.roundRectangle(64, 64, docWidth - padding * 2, docHeight - padding*2, 10).setStrokeColor(borderColor);
        pdfCanvas.stroke();

    }

    private void addBkashLogo(Document document, String path) throws MalformedURLException {
        long startTime = System.currentTimeMillis();
        var bytes = storageService.getGetFile(path);
        var bkImgData = ImageDataFactory.create(bytes);
        var bkImg = new Image(bkImgData);
        bkImg.setHeight(64f);
        bkImg.setWidth(128f);
        bkImg.setFixedPosition(padding + tableCellMargin, docHeight - padding - tableCellMargin);
        document.add(bkImg);

        long endTime = System.currentTimeMillis();
        log.info("time to set bkash logo: {} milliseconds", endTime - startTime);
    }

    private void addPartnerLogo(Document document, String path) throws MalformedURLException {
        long startTime = System.currentTimeMillis();
        var bytes = storageService.getGetFile(path);
        var partnerImgData = ImageDataFactory.create(bytes);
        var partnerImg = new Image(partnerImgData);
        partnerImg.setHeight(50f);
        partnerImg.setWidth(50f);
        partnerImg.setFixedPosition(padding + tableCellMargin, docHeight - padding - tableCellMargin);
        partnerImg.setMarginRight(tableCellMargin);
        partnerImg.setBorderRight(border);
        document.add(partnerImg);

        long endTime = System.currentTimeMillis();
        log.info("time to set partner logo: {} milliseconds", endTime - startTime);
    }

}


