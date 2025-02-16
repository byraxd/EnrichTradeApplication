package com.example.app.parser.service.impl;

import com.example.app.entity.Product;
import com.example.app.entity.Trade;
import com.example.app.parser.service.TradeParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class XmlTradeProductParserServiceImpl implements TradeParserService {

    private static final String ELEMENT_TRADE = "trade";
    private static final String ELEMENT_DATE = "date";
    private static final String ELEMENT_PRODUCT_ID = "productId";
    private static final String ELEMENT_CURRENCY = "currency";
    private static final String ELEMENT_PRICE = "price";

    @Override
    public Flux<Trade> parse(MultipartFile file) {
        try {
            Document document = buildDocument(file);
            List<Trade> trades = extractTrades(document);
            return Flux.fromIterable(trades);
        } catch (Exception e) {
            log.error("Error parsing XML file: {}", e.getMessage(), e);
            return Flux.empty();
        }
    }

    /**
     * Builds and normalizes a DOM Document from the provided XML file.
     */
    private Document buildDocument(MultipartFile file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file.getInputStream());
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Extracts a list of Trade objects from the DOM Document.
     */
    private List<Trade> extractTrades(Document document) {
        List<Trade> trades = new ArrayList<>();
        NodeList tradeNodes = document.getElementsByTagName(ELEMENT_TRADE);

        for (int i = 0; i < tradeNodes.getLength(); i++) {
            Node node = tradeNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element tradeElement = (Element) node;
                Trade trade = parseTradeElement(tradeElement);
                if (trade != null) {
                    trades.add(trade);
                }
            }
        }
        return trades;
    }

    /**
     * Creates a Trade from an XML element.
     */
    private Trade parseTradeElement(Element element) {
        String date = getTagValue(ELEMENT_DATE, element);
        String productIdText = getTagValue(ELEMENT_PRODUCT_ID, element);
        String currency = getTagValue(ELEMENT_CURRENCY, element);
        String price = getTagValue(ELEMENT_PRICE, element);

        try {
            int productId = Integer.parseInt(productIdText);
            return Trade.builder()
                    .date(date)
                    .productId(productId)
                    .currency(currency)
                    .price(price)
                    .build();
        } catch (NumberFormatException e) {
            log.error("Invalid productId: {}", productIdText, e);
            return null;
        }
    }

    /**
     * Helper method to extract the text content of the specified tag from an element.
     */
    private String getTagValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes != null && nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent();
        }
        return "";
    }
}
